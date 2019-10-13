/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.report.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.report.ReportPair;
import org.yes.cart.report.ReportWorker;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.federation.ShopFederationStrategy;
import org.yes.cart.utils.DateUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * User: denispavlov
 * Date: 09/10/2019
 * Time: 09:56
 */
public class SalesByCategoryArrayReportWorker implements ReportWorker {

    private static final Logger LOG = LoggerFactory.getLogger(SalesByCategoryArrayReportWorker.class);

    private static final String SEPARATOR = " -> ";

    private final List<String> INCLUDE_ORDERS = new ArrayList<>(Arrays.asList(
            CustomerOrder.ORDER_STATUS_IN_PROGRESS,
            CustomerOrder.ORDER_STATUS_WAITING_PAYMENT,
            CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED,
            CustomerOrder.ORDER_STATUS_COMPLETED
    ));

    private final CustomerOrderService customerOrderService;
    private final ShopService shopService;
    private final ShopFederationStrategy shopFederationStrategy;
    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final CategoryService categoryService;

    public SalesByCategoryArrayReportWorker(final CustomerOrderService customerOrderService,
                                            final ShopService shopService,
                                            final ShopFederationStrategy shopFederationStrategy,
                                            final ProductService productService,
                                            final ProductCategoryService productCategoryService,
                                            final CategoryService categoryService) {
        this.customerOrderService = customerOrderService;
        this.shopService = shopService;
        this.shopFederationStrategy = shopFederationStrategy;
        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.categoryService = categoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportPair> getParameterValues(final String lang, final String param, final Map<String, Object> currentSelection) {
        if ("shop".equals(param)) {
            try {
                final List<Shop> shops = shopService.getAll();
                final List<ReportPair> select = new ArrayList<>();
                for (final Shop shop : shops) {
                    if (shopFederationStrategy.isShopAccessibleByCurrentManager(shop.getCode())) {
                        select.add(new ReportPair(shop.getCode() + ": " + shop.getName(), String.valueOf(shop.getShopId())));
                    }
                }
                select.sort((a, b) -> a.getLabel().compareToIgnoreCase(b.getLabel()));
                return select;
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getResult(final String lang, final Map<String, Object> currentSelection) {
        final String shop = (String) currentSelection.get("shop");
        final String fromDate = (String) currentSelection.get("fromDate");
        final String tillDate = (String) currentSelection.get("tillDate");

        final long shopId = NumberUtils.toLong(shop);
        final Instant start = NumberUtils.toLong(fromDate) > 0L ? DateUtils.iFrom(NumberUtils.toLong(fromDate)) : DateUtils.iParseSDT(fromDate);
        final Instant end = NumberUtils.toLong(tillDate) > 0L ? DateUtils.iFrom(NumberUtils.toLong(tillDate)) : DateUtils.iParseSDT(tillDate);

        final int count_quantity = 0;
        final int count_cost = 1;
        final int count_amount = 2;
        final int count_profit = 3;

        if (shopId > 0L) {
            try {

                final Map<String, String> skuToCat = new HashMap<>();
                final Map<String, List<String>> catToSku = new HashMap<>();
                final Map<String, BigDecimal[]> catCounters = new TreeMap<>();
                final Map<String, BigDecimal[]> skuCounters = new TreeMap<>();

                final Map<Long, Set<Long>> shopsAndSubshops = shopService.getAllShopsAndSubs();

                final Set<Long> inShops = new HashSet<>();
                inShops.add(shopId);
                if (shopsAndSubshops.containsKey(shopId)) {
                    inShops.addAll(shopsAndSubshops.get(shopId));
                }

                this.customerOrderService.findByCriteriaIterator(
                        " where e.createdTimestamp >= ?1 and e.createdTimestamp < ?2 and e.shop.shopId in (?3) and e.orderStatus in (?4)",
                        new Object[]{ start, end, inShops, INCLUDE_ORDERS },
                        (order) -> {

                            for (final CustomerOrderDet detail : order.getOrderDetail()) {

                                final String productName = determineProductName(detail);

                                final BigDecimal[] skuCounts = skuCounters.computeIfAbsent(
                                        productName,
                                        det -> createCounters()
                                );

                                final Pair<String, I18NModel> cost = detail.getValue("ItemCostPrice");
                                BigDecimal lineCost = BigDecimal.ZERO;
                                if (cost != null) {
                                    lineCost = new BigDecimal(cost.getFirst()).multiply(detail.getQty());
                                }
                                final BigDecimal lineAmount = detail.getGrossPrice().multiply(detail.getQty());
                                final BigDecimal lineProfit = lineAmount.subtract(lineCost);

                                skuCounts[count_quantity] = skuCounts[count_quantity].add(detail.getQty());
                                skuCounts[count_cost] = skuCounts[count_cost].add(lineCost);
                                skuCounts[count_amount] = skuCounts[count_amount].add(lineAmount);
                                skuCounts[count_profit] = skuCounts[count_profit].add(lineProfit);

                                final String categoryName = skuToCat.computeIfAbsent(
                                        productName,
                                        det -> determineCategoryName(catToSku, detail, productName)
                                );

                                final BigDecimal[] categoryCounts = catCounters.computeIfAbsent(
                                        categoryName,
                                        det -> createCounters()
                                );

                                categoryCounts[count_quantity] = categoryCounts[count_quantity].add(detail.getQty());
                                categoryCounts[count_cost] = categoryCounts[count_cost].add(lineCost);
                                categoryCounts[count_amount] = categoryCounts[count_amount].add(lineAmount);
                                categoryCounts[count_profit] = categoryCounts[count_profit].add(lineProfit);

                            }

                            return true; // read fully
                        }
                );

                final List<Object[]> out = new ArrayList<>();

                out.add(new Object[]{
                        "Category 1",
                        "Category 2",
                        "Category 3",
                        "TechData Article",
                        "Product Name",
                        "Model",
                        "Quantity",
                        "Cost",
                        "Revenue",
                        "Profit"
                });

                for (final Map.Entry<String, BigDecimal[]> catCounter : catCounters.entrySet()) {

                    final String[] cats = StringUtils.splitByWholeSeparator(catCounter.getKey(), SEPARATOR);

                    final boolean has3Cats = cats.length >= 3;
                    final boolean has2Cats = cats.length >= 2;

                    final String category1 = has3Cats || has2Cats ? cats[cats.length - (has2Cats ? 2 : 3)] : catCounter.getKey();
                    final String category2 = has3Cats || has2Cats ? cats[cats.length - (has2Cats ? 1 : 2)] : "";
                    final String category3 = has3Cats ? cats[cats.length - 1] : "";

                    out.add(new Object[]{
                            category1,
                            category2,
                            category3,
                            "",
                            "",
                            "",
                            catCounter.getValue()[count_quantity],
                            catCounter.getValue()[count_cost],
                            catCounter.getValue()[count_amount],
                            catCounter.getValue()[count_profit]
                    });

                    for (final String productName : catToSku.get(catCounter.getKey())) {

                        final String[] prods = StringUtils.splitByWholeSeparator(productName, SEPARATOR);

                        final boolean hasModel = prods.length == 3;
                        final String td = prods[0];
                        final String name = prods[1];
                        final String sku = hasModel ? prods[2] : "";


                        final BigDecimal[] skuCounter = skuCounters.get(productName);

                        out.add(new Object[]{
                                category1,
                                category2,
                                category3,
                                td,
                                name,
                                sku,
                                skuCounter[count_quantity],
                                skuCounter[count_cost],
                                skuCounter[count_amount],
                                skuCounter[count_profit]
                        });


                    }

                }


                return (List) out;

            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private String determineProductName(final CustomerOrderDet detail) {

        final Product product = productService.getProductBySkuCode(detail.getProductSkuCode());

        if (product != null && StringUtils.isNotBlank(product.getManufacturerCode())) {

            return detail.getProductSkuCode() + SEPARATOR + detail.getProductName() + SEPARATOR + product.getManufacturerCode();

        }

        return detail.getProductSkuCode() + SEPARATOR + detail.getProductName();

    }

    private String determineCategoryName(final Map<String, List<String>> catToSku, final CustomerOrderDet detail, final String productName) {

        String categoryName = "????";

        final Product product = productService.getProductBySkuCode(detail.getProductSkuCode());

        if (product != null) {

            final List<Long> catIds = productCategoryService.getByProductId(product.getProductId());

            if (CollectionUtils.isNotEmpty(catIds)) {

                final StringBuilder full = new StringBuilder();
                appendFullChain(full, catIds.get(0));
                if (full.length() > 0) {
                    categoryName = full.toString();
                }

            }

        }

        catToSku.computeIfAbsent(categoryName, cat -> new ArrayList<>()).add(productName);

        return categoryName;

    }

    private void appendFullChain(final StringBuilder full, final long categoryId) {

        final Category category = categoryService.getById(categoryId);
        if (category != null && !category.isRoot()) {

            appendFullChain(full, category.getParentId());

            if (full.length() > 0) {
                full.append(SEPARATOR);
            }

            full.append(category.getName());

        }

    }

    private BigDecimal[] createCounters() {
        return new BigDecimal[] {
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getEnhancedParameterValues(final List<Object> result, final Map<String, Object> currentSelection) {
        return new HashMap<>(currentSelection);
    }
}
