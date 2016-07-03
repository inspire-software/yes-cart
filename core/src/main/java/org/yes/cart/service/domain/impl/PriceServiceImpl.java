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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SkuPriceQuantityComparatorImpl;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.impl.FilteredNavigationRecordImpl;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PriceServiceImpl
        extends BaseGenericServiceImpl<SkuPrice>
        implements PriceService {


    private final PriceNavigation priceNavigation;
    private final GenericDAO<SkuPrice, Long> skuPriceDao;


    /**
     * Constructor.
     *
     * @param priceNavigation     price navigation composer
     * @param skuPriceDao         sku price dao service
     */
    public PriceServiceImpl(final PriceNavigation priceNavigation,
                            final GenericDAO<SkuPrice, Long> skuPriceDao
    ) {
        super(skuPriceDao);
        this.priceNavigation = priceNavigation;
        this.skuPriceDao = skuPriceDao;

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-minimalPrice")
    public SkuPrice getMinimalPrice(final Long productId,
                                    final String selectedSku,
                                    final long shopId,
                                    final String currencyCode,
                                    final BigDecimal quantity,
                                    final String pricingPolicy) {

        final List<Pair<String, SkuPrice>> skuPrices;
        if (selectedSku == null && productId != null) {
            skuPrices = getSkuPrices(productId, shopId, currencyCode, pricingPolicy);
        } else if (selectedSku != null) {
            skuPrices = getSkuPrices(selectedSku, shopId, currencyCode, pricingPolicy);
        } else {
            skuPrices = Collections.emptyList();
        }

        return getMinimalSkuPrice(skuPrices, selectedSku, quantity);
    }

    private SkuPrice getMinimalSkuPrice(List<Pair<String, SkuPrice>> skuPrices, final String selectedSku, final BigDecimal quantity) {

        skuPrices = getSkuPricesFilteredByTimeFrame(skuPrices);

        if (quantity != null) {
            skuPrices = getSkuPricesFilteredByQuantity(
                    skuPrices,
                    quantity);
        }


        BigDecimal overallMinimalRegularPrice = null;
        Pair<String, SkuPrice> rez = null;
        for (Pair<String, SkuPrice> skuPrice : skuPrices) {
            if ((selectedSku == null || skuPrice.getFirst().equals(selectedSku))) {
                final BigDecimal minPrice = MoneyUtils.minPositive(skuPrice.getSecond().getRegularPrice(), skuPrice.getSecond().getSalePrice());
                if (overallMinimalRegularPrice == null ||
                        MoneyUtils.isFirstBiggerThanSecond(overallMinimalRegularPrice, minPrice)) {
                    overallMinimalRegularPrice = minPrice;
                    rez = skuPrice;
                }
            }
        }
        if (rez == null) {
            return skuPriceDao.getEntityFactory().getByIface(SkuPrice.class);
        }
        return rez.getSecond();
    }

    private static final Comparator<SkuPrice> SORT_PRICE_BY_QUANTITY = new SkuPriceQuantityComparatorImpl();

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-allCurrentPrices")
    public List<SkuPrice> getAllCurrentPrices(final Long productId,
                                              final String selectedSku,
                                              final long shopId,
                                              final String currencyCode,
                                              final String pricingPolicy) {

        final List<Pair<String, SkuPrice>> skuPrices;
        if (selectedSku == null && productId != null) {
            skuPrices = getSkuPrices(productId, shopId, currencyCode, pricingPolicy);
        } else if (selectedSku != null) {
            skuPrices = getSkuPrices(selectedSku, shopId, currencyCode, pricingPolicy);
        } else {
            skuPrices = Collections.emptyList();
        }

        final List<Pair<String, SkuPrice>> filtered = getSkuPricesFilteredByTimeFrame(skuPrices);

        final List<SkuPrice> prices = new ArrayList<SkuPrice>();
        for (final Pair<String, SkuPrice> price : filtered) {
            prices.add(price.getSecond());
        }

        Collections.sort(prices, SORT_PRICE_BY_QUANTITY);

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
     * Atm we can have different price definitions (lowest in list with high priority):
     * price without any time limitations;
     * price, which starts in infinitive past and will be end at some date;
     * price, which has the start date but no end date;
     * price with start and end date.
     *
     * @param skuPrices all prices filtered by currency, and quantity for all skus
     * @return the list of sku prices, which is filtered by time frame
     */
    List<Pair<String, SkuPrice>> getSkuPricesFilteredByTimeFrame(final List<Pair<String, SkuPrice>> skuPrices) {

        final List<Pair<String, SkuPrice>> allPrices = new LinkedList<Pair<String, SkuPrice>>();

        final Date now = new Date(); //TODO: V2 time machine
        for (Pair<String, SkuPrice> skuPrice : skuPrices) {

            if (DomainApiUtils.isObjectAvailableNow(true, skuPrice.getSecond().getSalefrom(), skuPrice.getSecond().getSaleto(), now)) {
                allPrices.add(skuPrice);
            }

        }

        return allPrices;
    }

    /**
     * Get the sku prices filtered by shop.
     *
     * @param skuCode      SKU code
     * @param shopId       shop filter
     * @param currencyCode currency code
     * @param pricingPolicy optional pricing policy
     *
     * @return list of sku prices
     */
    List<Pair<String, SkuPrice>> getSkuPrices(final String skuCode, final long shopId, final String currencyCode, final String pricingPolicy) {

        return getSkuPriceFilteredByShopCurrency(skuCode, shopId, currencyCode, pricingPolicy);

    }

    /**
     * Get the sku prices filtered by shop.
     *
     * @param productId    product PK
     * @param shopId       shop filter
     * @param currencyCode currency code
     * @param pricingPolicy optional pricing policy
     *
     * @return list of sku prices
     */
    List<Pair<String, SkuPrice>> getSkuPrices(final long productId, final long shopId, final String currencyCode, final String pricingPolicy) {

        return getSkuPriceFilteredByShopCurrency(productId, shopId, currencyCode, pricingPolicy);

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
        List<Pair<String, SkuPrice>> result = new ArrayList<Pair<String, SkuPrice>>();
        for (Pair<String, SkuPrice> skuPrice : prices) {
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(quantity, skuPrice.getSecond().getQuantity())) {
                result.add(skuPrice);
            }
        }
        return result;
    }

    private List<Pair<String, SkuPrice>> getSkuPriceFilteredByShopCurrency(final String skuCode,
                                                                           final long shopId,
                                                                           final String currencyCode,
                                                                           final String pricingPolicy) {

        final List<SkuPrice> prices;
        if (StringUtils.isNotBlank(pricingPolicy)) {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.CODE.AND.CURRENCY.AND.SHOP.AND.POLICY",
                    skuCode, currencyCode, shopId, pricingPolicy);
        } else {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.CODE.AND.CURRENCY.AND.SHOP",
                    skuCode, currencyCode, shopId);
        }
        if (CollectionUtils.isNotEmpty(prices)) {
            final List<Pair<String, SkuPrice>> rez = new ArrayList<Pair<String, SkuPrice>>(prices.size());
            for (final SkuPrice price : prices) {
                rez.add(new Pair<String, SkuPrice>(price.getSkuCode(), price));
            }
            return rez;
        }
        return Collections.emptyList();

    }

    private List<Pair<String, SkuPrice>> getSkuPriceFilteredByShopCurrency(final long productId,
                                                                           final long shopId,
                                                                           final String currencyCode,
                                                                           final String pricingPolicy) {

        final List<SkuPrice> prices;
        if (StringUtils.isNotBlank(pricingPolicy)) {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.PRODUCT.AND.CURRENCY.AND.SHOP.AND.POLICY",
                    productId, currencyCode, shopId, pricingPolicy);
        } else {
            prices = getGenericDao().findByNamedQuery("SKUPRICE.BY.PRODUCT.AND.CURRENCY.AND.SHOP",
                    productId, currencyCode, shopId);
        }
        if (CollectionUtils.isNotEmpty(prices)) {
            final List<Pair<String, SkuPrice>> rez = new ArrayList<Pair<String, SkuPrice>>(prices.size());
            for (final SkuPrice price : prices) {
                rez.add(new Pair<String, SkuPrice>(price.getSkuCode(), price));
            }
            return rez;
        }
        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-allPrices")
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
    public List<FilteredNavigationRecord> getPriceNavigationRecords(final PriceTierTree priceTierTree,
                                                                    final String currency,
                                                                    final Shop shop) {
        final List<PriceTierNode> priceTierNodes = getPriceTierNodes(priceTierTree, currency, shop);
        final List<FilteredNavigationRecord> result = new ArrayList<FilteredNavigationRecord>(priceTierNodes.size());
        for (PriceTierNode priceTierNode : priceTierNodes) {
            result.add(
                    new FilteredNavigationRecordImpl(
                            ProductSearchQueryBuilder.PRODUCT_PRICE,
                            ProductSearchQueryBuilder.PRODUCT_PRICE,
                            priceNavigation.composePriceRequestParams(
                                    currency,
                                    priceTierNode.getFrom(),
                                    priceTierNode.getTo()
                            ),
                            0
                    )
            );
        }
        return result;
    }

    private List<PriceTierNode> getPriceTierNodes(final PriceTierTree priceTierTree, final String currency, final Shop shop) {
        List<PriceTierNode> priceTierNodes = priceTierTree.getPriceTierNodes(currency);
        if (priceTierNodes == null) {
            return Collections.emptyList();
        }
        return priceTierNodes;
    }


    /**
     * Nice rounding for digits.
     *
     * @param toNicefy digit to make it nice
     * @return nicefied digit
     */
    BigDecimal niceBigDecimal(final BigDecimal toNicefy) {
        Integer intValue = toNicefy.intValue();
        String intAsString = intValue.toString();

        int tailZeroCount = intAsString.length() / 2;
        if (tailZeroCount == 0) {
            tailZeroCount = intAsString.length();

        }

        return new BigDecimal(intValue).setScale(-1 * tailZeroCount, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public SkuPrice create(final SkuPrice instance) {
        ensureNonZeroPrices(instance);
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public SkuPrice update(final SkuPrice instance) {
        ensureNonZeroPrices(instance);
        return super.update(instance);
    }


    private void ensureNonZeroPrices(final SkuPrice entity) {
        if (entity.getSalePrice() != null && MoneyUtils.isFirstEqualToSecond(entity.getSalePrice(), BigDecimal.ZERO)) {
            entity.setSalePrice(null);
        }
        if (entity.getMinimalPrice() != null && MoneyUtils.isFirstEqualToSecond(entity.getMinimalPrice(), BigDecimal.ZERO)) {
            entity.setMinimalPrice(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public void delete(final SkuPrice instance) {
        super.delete(instance);
    }
}
