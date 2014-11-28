/*
* Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
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
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
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
    private final ProductService productService;
    private final GenericDAO<SkuPrice, Long> skuPriceDao;


    /**
     * Constructor.
     *
     * @param priceNavigation     price navigation composer
     * @param productService      product service
     * @param skuPriceDao         sku price dao service
     */
    public PriceServiceImpl(final PriceNavigation priceNavigation,
                            final ProductService productService,
                            final GenericDAO<SkuPrice, Long> skuPriceDao
    ) {
        super(skuPriceDao);
        this.priceNavigation = priceNavigation;
        this.productService = productService;
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
                                    final BigDecimal quantity) {

        final List<Pair<String, SkuPrice>> skuPrices;
        if (selectedSku == null && productId != null) {
            skuPrices = getSkuPrices(productId, shopId, currencyCode);
        } else if (selectedSku != null) {
            skuPrices = getSkuPrices(selectedSku, shopId, currencyCode);
        } else {
            skuPrices = Collections.emptyList();
        }

        return getMinimalSkuPrice(skuPrices, selectedSku, quantity);
    }

    private SkuPrice getMinimalSkuPrice(List<Pair<String, SkuPrice>> skuPrices, final String selectedSku, final BigDecimal quantity) {

        BigDecimal overallMinimalRegularPrice = null;

        skuPrices = getSkuPricesFilteredByTimeFrame(skuPrices);

        if (quantity != null) {
            skuPrices = getSkuPricesFilteredByQuantity(
                    skuPrices,
                    quantity);
        }


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

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-allCurrentPrices")
    public List<SkuPrice> getAllCurrentPrices(final Long productId,
                                              final String selectedSku,
                                              final long shopId,
                                              final String currencyCode) {

        final List<Pair<String, SkuPrice>> skuPrices;
        if (selectedSku == null && productId != null) {
            skuPrices = getSkuPrices(productId, shopId, currencyCode);
        } else if (selectedSku != null) {
            skuPrices = getSkuPrices(selectedSku, shopId, currencyCode);
        } else {
            skuPrices = Collections.emptyList();
        }

        final List<Pair<String, SkuPrice>> filtered = getSkuPricesFilteredByTimeFrame(skuPrices);

        final List<SkuPrice> prices = new ArrayList<SkuPrice>();
        for (final Pair<String, SkuPrice> price : filtered) {
            prices.add(price.getSecond());
        }

        Collections.sort(prices, new SkuPriceQuantityComparatorImpl());

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

        final MultiMap qtySkuPriceMap = new MultiValueMap();

        for (Pair<String, SkuPrice> skuPrice : skuPrices) {
            qtySkuPriceMap.put(skuPrice.getFirst() + ":" + skuPrice.getSecond().getQuantity(), skuPrice);
        }


        for (Object o : qtySkuPriceMap.keySet()) {

            final String key = (String) o;

            final List<Pair<String, SkuPrice>> skuPricesForOneSku = new ArrayList<Pair<String, SkuPrice>>(
                    (Collection<Pair<String, SkuPrice>>) qtySkuPriceMap.get(key));

            reorderSkuPrices(skuPricesForOneSku);

            long time = java.lang.System.currentTimeMillis();   //TODO: V2 time machine

            boolean found = false;

            found = addFramedPrice(allPrices, skuPricesForOneSku, time);

            if (!found) {
                found = addEndPrice(allPrices, skuPricesForOneSku, time);
            }

            if (!found) {
                found = addStartPrice(allPrices, skuPricesForOneSku, time);
            }

            if (!found) {
                addAllTimePrice(allPrices, skuPricesForOneSku, time);
            }


        }

        return allPrices;
    }

    void reorderSkuPrices(List<Pair<String, SkuPrice>> skuPricesForOneSku) {
        Collections.sort(
                skuPricesForOneSku,
                new Comparator<Pair<String, SkuPrice>>() {

                    public int compare(final Pair<String, SkuPrice> skuPrice1, final Pair<String, SkuPrice> skuPrice2) {
                        //the new price definition has high priority
                        return skuPrice2.getSecond().getSkuPriceId() < skuPrice1.getSecond().getSkuPriceId() ? -1 :
                                (skuPrice2.getSecond().getSkuPriceId() == skuPrice1.getSecond().getSkuPriceId() ? 0 : 1);
                    }
                }
        );
    }

    /**
     * Try to add all time price (not start and no end)
     * and add it into given result holder - <code>allPrices</code>
     *
     * @param allPrices          result holder for all skus.
     * @param skuPricesForOneSku prices for one sku
     * @param time               current time
     * @return true in case if result was added
     */
    boolean addAllTimePrice(List<Pair<String, SkuPrice>> allPrices, List<Pair<String, SkuPrice>> skuPricesForOneSku, long time) {
        for (Pair<String, SkuPrice> skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSecond().getSalefrom() == null && skuPrice.getSecond().getSaleto() == null) {
                allPrices.add(skuPrice);
                return true;
            }
        }
        return false;
    }

    /**
     * Try to find price has start date
     * and add it into given result holder - <code>allPrices</code>
     *
     * @param allPrices          result holder for all skus.
     * @param skuPricesForOneSku prices for one sku
     * @param time               current time
     * @return true in case if result was added
     */
    boolean addStartPrice(List<Pair<String, SkuPrice>> allPrices, List<Pair<String, SkuPrice>> skuPricesForOneSku, long time) {
        for (Pair<String, SkuPrice> skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSecond().getSalefrom() != null && skuPrice.getSecond().getSaleto() == null) {
                if (skuPrice.getSecond().getSalefrom().getTime() < time) {
                    allPrices.add(skuPrice);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Try to find price started in infinitive past and has some end date,
     * and add it into given result holder - <code>allPrices</code>
     *
     * @param allPrices          result holder for all skus.
     * @param skuPricesForOneSku prices for one sku
     * @param time               current time
     * @return true in case if result was added
     */
    boolean addEndPrice(List<Pair<String, SkuPrice>> allPrices, List<Pair<String, SkuPrice>> skuPricesForOneSku, long time) {
        for (Pair<String, SkuPrice> skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSecond().getSalefrom() == null && skuPrice.getSecond().getSaleto() != null) {
                if (time < skuPrice.getSecond().getSaleto().getTime()) {
                    allPrices.add(skuPrice);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Try to find price with start and dates , which is mach search criteria
     * and add it into given result holder - <code>allPrices</code>
     *
     * @param allPrices          result holder for all skus.
     * @param skuPricesForOneSku prices for one sku
     * @param time               current time
     * @return true in case if result was added
     */
    boolean addFramedPrice(List<Pair<String, SkuPrice>> allPrices, List<Pair<String, SkuPrice>> skuPricesForOneSku, long time) {
        for (Pair<String, SkuPrice> skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSecond().getSalefrom() != null && skuPrice.getSecond().getSaleto() != null) {
                if (skuPrice.getSecond().getSalefrom().getTime() < time && time < skuPrice.getSecond().getSaleto().getTime()) {
                    allPrices.add(skuPrice);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the sku prices filtered by shop.
     *
     * @param skuCode      SKU code
     * @param shopId       shop filter
     * @param currencyCode currency code
     * @return list of sku prices
     */
    List<Pair<String, SkuPrice>> getSkuPrices(final String skuCode, final long shopId, final String currencyCode) {

        return getSkuPriceFilteredByShopCurrency(skuCode, shopId, currencyCode);

    }

    /**
     * Get the sku prices filtered by shop.
     *
     * @param productId    product PK
     * @param shopId       shop filter
     * @param currencyCode currency code
     * @return list of sku prices
     */
    List<Pair<String, SkuPrice>> getSkuPrices(final long productId, final long shopId, final String currencyCode) {

        return getSkuPriceFilteredByShopCurrency(productId, shopId, currencyCode);

    }

    private List<Pair<String, SkuPrice>> getSkuPricesFilteredSkuCode(final List<Pair<String, SkuPrice>> prices, final String selectedSkuCode) {
        List<Pair<String, SkuPrice>> result = new ArrayList<Pair<String, SkuPrice>>();
        for (Pair<String, SkuPrice> skuPrice : prices) {
            if (skuPrice.getFirst().equals(selectedSkuCode)) {
                result.add(skuPrice);
            }
        }
        return result;
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
        final Set<String> uniqueSkuCodes = getUniqueSkuCodes(prices);
        for (String selectedSkuCode : uniqueSkuCodes) {
            final List<Pair<String, SkuPrice>> selectedSkuPrices = getSkuPricesFilteredSkuCode(prices, selectedSkuCode);
            Collections.sort(
                    selectedSkuPrices,
                    new Comparator<Pair<String, SkuPrice>>() {
                        /**
                         * {@inheritDoc}
                         * ReverveComparator by quantity.
                         */
                        public int compare(final Pair<String, SkuPrice> skuPrice1, final Pair<String, SkuPrice> skuPrice2) {
                            return skuPrice2.getSecond().getQuantity().compareTo(skuPrice1.getSecond().getQuantity());
                        }
                    }
            );
            for (Pair<String, SkuPrice> skuPrice : selectedSkuPrices) {
                if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(quantity, skuPrice.getSecond().getQuantity())) {
                    result.add(skuPrice);
                    break;
                }
            }

            if (result.isEmpty()
                    && MoneyUtils.isFirstBiggerThanSecond(BigDecimal.ONE, quantity)
                    && !MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, quantity, Constants.DEFAULT_SCALE)) {
                // float point qty
                return getSkuPricesFilteredByQuantity(prices, BigDecimal.ONE);
            }
        }
        return result;
    }


    private Set<String> getUniqueSkuCodes(final List<Pair<String, SkuPrice>> prices) {
        Set<String> stringSet = new HashSet<String>();
        for (Pair<String, SkuPrice> skuPrice : prices) {
            stringSet.add(skuPrice.getFirst());
        }
        return stringSet;
    }

    private List<Pair<String, SkuPrice>> getSkuPriceFilteredByShopCurrency(final String skuCode,
                                                                           final long shopId,
                                                                           final String currencyCode) {

        final List<Object[]> prices = (List) getGenericDao().findQueryObjectByNamedQuery("SKUPRICE.BY.CODE.AND.CURRENCY.AND.SHOP",
                skuCode, currencyCode, shopId);
        if (CollectionUtils.isNotEmpty(prices)) {
            final List<Pair<String, SkuPrice>> rez = new ArrayList<Pair<String, SkuPrice>>(prices.size());
            for (final Object[] price : prices) {
                rez.add(new Pair<String, SkuPrice>((String) price[1], (SkuPrice) price[0]));
            }
            return rez;
        }
        return Collections.emptyList();

    }

    private List<Pair<String, SkuPrice>> getSkuPriceFilteredByShopCurrency(final long productId,
                                                                           final long shopId,
                                                                           final String currencyCode) {

        final List<Object[]> prices = (List) getGenericDao().findQueryObjectByNamedQuery("SKUPRICE.BY.PRODUCT.AND.CURRENCY.AND.SHOP",
                productId, currencyCode, shopId);
        if (CollectionUtils.isNotEmpty(prices)) {
            final List<Pair<String, SkuPrice>> rez = new ArrayList<Pair<String, SkuPrice>>(prices.size());
            for (final Object[] price : prices) {
                rez.add(new Pair<String, SkuPrice>((String) price[1], (SkuPrice) price[0]));
            }
            return rez;
        }
        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    public List<FilteredNavigationRecord> getPriceNavigationRecords(
            final PriceTierTree priceTierTree,
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
    public void deleteDerivedPrices(final Shop shop, final String derivedCurrency) {
        final String shopId = String.valueOf(shop.getShopId());
        final String sql = MessageFormat.format("delete from tskuprice where shop_id = {0} and currency = ''{1}''",
                shopId,
                derivedCurrency);
        skuPriceDao.executeNativeUpdate(sql);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices"
    }, allEntries = true)
    public SkuPrice create(final SkuPrice instance) {
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices"
    }, allEntries = true)
    public SkuPrice update(final SkuPrice instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices"
    }, allEntries = true)
    public void delete(final SkuPrice instance) {
        super.delete(instance);
    }
}
