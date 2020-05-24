/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.SkuPriceQuantityComparator;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PriceServiceImpl extends BaseGenericServiceImpl<SkuPrice> implements PriceService {

    private final GenericDAO<SkuPrice, Long> skuPriceDao;

    /**
     * Constructor.
     *
     * @param skuPriceDao         sku price dao service
     */
    public PriceServiceImpl(final GenericDAO<SkuPrice, Long> skuPriceDao) {
        super(skuPriceDao);
        this.skuPriceDao = skuPriceDao;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkuPrice getMinimalPrice(final Long productId,
                                    final String selectedSku,
                                    final long customerShopId,
                                    final Long masterShopId,
                                    final String currencyCode,
                                    final BigDecimal quantity,
                                    final boolean enforceTier,
                                    final String pricingPolicy,
                                    final String supplier) {

        final List<Pair<String, SkuPrice>> skuPrices;
        if (selectedSku == null && productId != null) {
            skuPrices = getSkuPrices(productId, customerShopId, masterShopId, currencyCode, pricingPolicy, supplier);
        } else if (selectedSku != null) {
            skuPrices = getSkuPrices(selectedSku, customerShopId, masterShopId, currencyCode, pricingPolicy, supplier);
        } else {
            skuPrices = Collections.emptyList();
        }

        return getMinimalSkuPrice(skuPrices, selectedSku, quantity, enforceTier);
    }

    private SkuPrice getMinimalSkuPrice(List<Pair<String, SkuPrice>> skuPrices,
                                        final String selectedSku,
                                        final BigDecimal quantity,
                                        final boolean enforceTier) {

        skuPrices = getSkuPricesFilteredByTimeFrame(skuPrices);

        if (quantity != null) {
            skuPrices = getSkuPricesFilteredByQuantity(
                    skuPrices,
                    quantity);
        }


        BigDecimal overallMinimalRegularPrice = null;
        BigDecimal overallMinimalRegularPriceTier = null;
        Pair<String, SkuPrice> rez = null;
        for (Pair<String, SkuPrice> skuPrice : skuPrices) {
            if ((selectedSku == null || skuPrice.getFirst().equals(selectedSku))) {
                final BigDecimal minPrice = MoneyUtils.minPositive(skuPrice.getSecond().getSalePriceForCalculation());
                if (
                        // Starting point of search
                        overallMinimalRegularPrice == null
                        ||
                        // We do not enforce tier and we look for cheapest price
                        (
                                !enforceTier &&
                                MoneyUtils.isFirstBiggerThanSecond(overallMinimalRegularPrice, minPrice)
                        )
                        ||
                        // We enforce tier and look for largest tier with cheapest price
                        (
                                enforceTier &&
                                (
                                        // Tier is higher, so enforce
                                        MoneyUtils.isFirstBiggerThanSecond(skuPrice.getSecond().getQuantity(), overallMinimalRegularPriceTier)
                                )
                                ||
                                (
                                        // Tier is same but cheaper price
                                        MoneyUtils.isFirstEqualToSecond(skuPrice.getSecond().getQuantity(), overallMinimalRegularPriceTier) &&
                                        MoneyUtils.isFirstBiggerThanSecond(overallMinimalRegularPrice, minPrice)
                                )
                        )) {
                    overallMinimalRegularPrice = minPrice;
                    overallMinimalRegularPriceTier = skuPrice.getSecond().getQuantity();
                    rez = skuPrice;
                }
            }
        }
        if (rez == null) {
            return skuPriceDao.getEntityFactory().getByIface(SkuPrice.class);
        }
        return rez.getSecond();
    }

    private static final Comparator<SkuPrice> SORT_PRICE_BY_QUANTITY = new SkuPriceQuantityComparator();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SkuPrice> getAllCurrentPrices(final Long productId,
                                              final String selectedSku,
                                              final long customerShopId,
                                              final Long masterShopId,
                                              final String currencyCode,
                                              final String pricingPolicy,
                                              final String supplier) {

        final List<Pair<String, SkuPrice>> skuPrices;
        if (selectedSku == null && productId != null) {
            skuPrices = getSkuPrices(productId, customerShopId, masterShopId, currencyCode, pricingPolicy, supplier);
        } else if (selectedSku != null) {
            skuPrices = getSkuPrices(selectedSku, customerShopId, masterShopId, currencyCode, pricingPolicy, supplier);
        } else {
            skuPrices = Collections.emptyList();
        }

        final List<Pair<String, SkuPrice>> filtered = getSkuPricesFilteredByTimeFrame(skuPrices);

        final List<SkuPrice> prices = new ArrayList<>();
        for (final Pair<String, SkuPrice> price : filtered) {
            prices.add(price.getSecond());
        }

        prices.sort(SORT_PRICE_BY_QUANTITY);

        final Iterator<SkuPrice> pricesIt = prices.iterator();
        BigDecimal tier = null;
        while (pricesIt.hasNext()) {
            final SkuPrice price = pricesIt.next();
            if (tier == null || !MoneyUtils.isFirstEqualToSecond(price.getQuantity(), tier)) {
                // first item or next tier
                tier = price.getQuantity();
            } else {
                // same tier but with higher price
                pricesIt.remove();
            }
        }

        return prices;
    }

    /**
     * At the moment we can have different price definitions (lowest in list with high priority):
     * price without any time limitations;
     * price, which starts in infinitive past and will be end at some date;
     * price, which has the start date but no end date;
     * price with start and end date.
     *
     * @param skuPrices all prices filtered by currency, and quantity for all skus
     * @return the list of sku prices, which is filtered by time frame
     */
    List<Pair<String, SkuPrice>> getSkuPricesFilteredByTimeFrame(final List<Pair<String, SkuPrice>> skuPrices) {

        final List<Pair<String, SkuPrice>> allPrices = new LinkedList<>();

        final LocalDateTime now = now();
        for (Pair<String, SkuPrice> skuPrice : skuPrices) {

            if (skuPrice.getSecond().isAvailable(now)) {
                allPrices.add(skuPrice);
            }

        }

        return allPrices;
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * Get the sku prices filtered by shop.
     *
     * @param skuCode         SKU code
     * @param customerShopId  shop filter
     * @param masterShopId    optional fallback shop filter
     * @param currencyCode    currency code
     * @param pricingPolicy   optional pricing policy
     * @param supplier        optional supplier
     *
     * @return list of sku prices
     */
    List<Pair<String, SkuPrice>> getSkuPrices(final String skuCode,
                                              final long customerShopId,
                                              final Long masterShopId,
                                              final String currencyCode,
                                              final String pricingPolicy,
                                              final String supplier) {

        return getSkuPriceFilteredByShopCurrency(
                skuCode,
                customerShopId,
                masterShopId,
                currencyCode,
                pricingPolicy,
                supplier
        );

    }

    /**
     * Get the sku prices filtered by shop.
     *
     * @param productId       product PK
     * @param customerShopId  shop filter
     * @param masterShopId    optional fallback shop filter
     * @param currencyCode    currency code
     * @param pricingPolicy   optional pricing policy
     * @param supplier        optional supplier
     *
     * @return list of sku prices
     */
    List<Pair<String, SkuPrice>> getSkuPrices(final long productId,
                                              final long customerShopId,
                                              final Long masterShopId,
                                              final String currencyCode,
                                              final String pricingPolicy,
                                              final String supplier) {

        return getSkuPriceFilteredByShopCurrency(
                productId,
                customerShopId,
                masterShopId,
                currencyCode,
                pricingPolicy,
                supplier
        );

    }

    /**
     * Get the sku prices filtered by quantity. Example:
     * ProductSKU1 has defined price ties 1 - 100 USD, 2 - 87 USD, 5 - 85 USD
     * ProductSKU2 has defined price ties 1 - 100 USD, 2 - 98 USD, 3 - 90 USD
     * <p/>
     * For quantity 4 result will hold only two SkuPrice:
     * ProductSKU1 87 USD
     * ProductSKU2 90 USD
     *
     * @param prices   sku prices
     * @param quantity quantity
     * @return list of sku prices filtered by quantity
     */
    List<Pair<String, SkuPrice>> getSkuPricesFilteredByQuantity(final List<Pair<String, SkuPrice>> prices,
                                                                final BigDecimal quantity) {
        List<Pair<String, SkuPrice>> result = new ArrayList<>();
        for (Pair<String, SkuPrice> skuPrice : prices) {
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(quantity, skuPrice.getSecond().getQuantity())) {
                result.add(skuPrice);
            }
        }
        return result;
    }

    List<Pair<String, SkuPrice>> getSkuPriceFilteredByShopCurrency(final String skuCode,
                                                                   final long customerShopId,
                                                                   final Long masterShopId,
                                                                   final String currencyCode,
                                                                   final String pricingPolicy,
                                                                   final String supplier) {

        final List<SkuPrice> prices;
        if (masterShopId != null) {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.CODE.AND.CURRENCY.AND.SHOPS",
                    skuCode, currencyCode, customerShopId, masterShopId);
        } else {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.CODE.AND.CURRENCY.AND.SHOP",
                    skuCode, currencyCode, customerShopId);
        }

        return getValidPricePairs(prices, pricingPolicy, supplier);

    }

    List<Pair<String, SkuPrice>> getSkuPriceFilteredByShopCurrency(final long productId,
                                                                   final long customerShopId,
                                                                   final Long masterShopId,
                                                                   final String currencyCode,
                                                                   final String pricingPolicy,
                                                                   final String supplier) {

        final List<SkuPrice> prices;
        if (masterShopId != null) {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.PRODUCT.AND.CURRENCY.AND.SHOPS",
                    productId, currencyCode, customerShopId, masterShopId);
        } else {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.PRODUCT.AND.CURRENCY.AND.SHOP",
                    productId, currencyCode, customerShopId);
        }

        return getValidPricePairs(prices, pricingPolicy, supplier);

    }


    private List<Pair<String, SkuPrice>> getValidPricePairs(final List<SkuPrice> prices,
                                                            final String pricingPolicy,
                                                            final String supplier) {

        if (CollectionUtils.isNotEmpty(prices)) {

            final boolean usePolicy = StringUtils.isNotBlank(pricingPolicy);
            final boolean useSupplier = StringUtils.isNotBlank(supplier);
            final List<Pair<String, SkuPrice>> rez = new ArrayList<>(prices.size());

            for (final SkuPrice price : prices) {

                if (    // POLICY:
                        (
                                // Non-specific (available to all)
                                StringUtils.isEmpty(price.getPricingPolicy()) ||
                                // OR enforce policy and policy is exact match
                                (usePolicy && pricingPolicy.equals(price.getPricingPolicy()))
                        ) &&
                        // SUPPLIER:
                        (
                                // Non-specific (available to all)
                                StringUtils.isEmpty(price.getSupplier()) ||
                                // OR enforce supplier and supplier is exact match
                                (useSupplier && supplier.equals(price.getSupplier()))
                        )
                ) {
                    rez.add(new Pair<>(price.getSkuCode(), price));
                }
            }
            return rez;
        }
        return Collections.emptyList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<SkuPrice> getAllPrices(final Long productId, final String selectedSku, final String currencyCode) {

        if (productId != null) {
            return getGenericDao().findByNamedQuery("SKUPRICE.BY.PRODUCT.AND.CURRENCY", productId, currencyCode);
        }

        if (StringUtils.isNotBlank(selectedSku)) {
            return getGenericDao().findByNamedQuery("SKUPRICE.BY.CODE.AND.CURRENCY", selectedSku, currencyCode);
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkuPrice create(final SkuPrice instance) {
        ensureNonZeroPrices(instance);
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkuPrice update(final SkuPrice instance) {
        ensureNonZeroPrices(instance);
        return super.update(instance);
    }


    private void ensureNonZeroPrices(final SkuPrice entity) {
        if (!MoneyUtils.isPositive(entity.getSalePrice())) {
            entity.setSalePrice(null);
        }
        if (!MoneyUtils.isPositive(entity.getMinimalPrice())) {
            entity.setMinimalPrice(null);
        }
    }




    private Pair<String, Object[]> findPriceQuery(final boolean count,
                                                  final String sort,
                                                  final boolean sortDescending,
                                                  final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(p.skuPriceId) from SkuPriceEntity p ");
        } else {
            hqlCriteria.append("select p from SkuPriceEntity p ");
        }

        final List shopIds = currentFilter != null ? currentFilter.remove("shopIds") : null;
        if (shopIds != null) {
            hqlCriteria.append(" where (p.shop.shopId in (?1)) ");
            params.add(shopIds);
        }
        final List currencies = currentFilter != null ? currentFilter.remove("currencies") : null;
        if (currencies != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (p.currency in ?1) ");
            } else {
                hqlCriteria.append(" and (p.currency in ?2) ");
            }
            params.add(currencies);
        }


        HQLUtils.appendFilterCriteria(hqlCriteria, params, "p", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by p." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }




    /**
     * {@inheritDoc}
     */
    @Override
    public List<SkuPrice> findPrices(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findPriceQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findPriceCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findPriceQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}
