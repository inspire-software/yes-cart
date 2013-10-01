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

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierNodeImpl;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.impl.FilteredNavigationRecordImpl;
import org.yes.cart.service.domain.ExchangeRateService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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


    private final ExchangeRateService exchangeRateService;
    private final PriceNavigation priceNavigation;
    private final ProductService productService;
    private final GenericDAO<SkuPrice, Long> skuPriceDao;


    /**
     * Constructor.
     *
     * @param exchangeRateService exchange rate service for handle not filled price lists
     * @param priceNavigation     price navigation composer
     * @param productService      product service
     * @param skuPriceDao         sku price dao service
     */
    public PriceServiceImpl(final ExchangeRateService exchangeRateService,
                            final PriceNavigation priceNavigation,
                            final ProductService productService,
                            final GenericDAO<SkuPrice, Long> skuPriceDao
    ) {
        super(skuPriceDao);
        this.exchangeRateService = exchangeRateService;
        this.priceNavigation = priceNavigation;
        this.productService = productService;
        this.skuPriceDao = skuPriceDao;

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-minimalRegularPrice")
    public SkuPrice getMinimalRegularPrice(
            final long productId,
            final String selectedSku,
            final Shop shop,
            final String currencyCode,
            final BigDecimal quantity) {

        return getMinimalRegularPrice(
                /*
                 * We display prices everywhere but need attributes only on product details page
                 * so in the long run this should be an attribute-less product entity
                 */
                productService.getProductById(productId).getSku(),
                selectedSku,
                shop,
                currencyCode,
                quantity
                );
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-minimalRegularPrice2")
    public SkuPrice getMinimalRegularPrice(
            final Collection<ProductSku> productSkus,
            final String selectedSku,
            final Shop shop,
            final String currencyCode,
            final BigDecimal quantity) {

        BigDecimal minimalRegularPrice = null;

        List<SkuPrice> skuPrices = getSkuPrices(productSkus, shop, currencyCode);
        skuPrices = getSkuPricesFilteredByTimeFrame(skuPrices);

        if (quantity != null) {
            skuPrices = getSkuPricesFilteredByQuantity(
                    skuPrices,
                    quantity);
        }


        SkuPrice rez = null;
        for (SkuPrice skuPrice : skuPrices) {
            if ((selectedSku == null || skuPrice.getSku().getCode().equals(selectedSku))
                    && (minimalRegularPrice == null
                    || MoneyUtils.isFirstBiggerThanOrEqualToSecond(minimalRegularPrice, skuPrice.getRegularPrice())
            )
                    ) {
                minimalRegularPrice = skuPrice.getRegularPrice();
                rez = skuPrice;
            }
        }
        if (rez == null) {
            rez = skuPriceDao.getEntityFactory().getByIface(SkuPrice.class);
        }
        return rez;
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
    public List<SkuPrice> getSkuPricesFilteredByTimeFrame(final Collection<SkuPrice> skuPrices) {

        final List<SkuPrice> allPrices = new LinkedList<SkuPrice>();

        final MultiMap qtySkuPriceMap = new MultiValueMap();

        for (SkuPrice skuPrice : skuPrices) {
            qtySkuPriceMap.put(skuPrice.getSku().getCode() + ":" + skuPrice.getQuantity(), skuPrice);
        }


        for (Object o : qtySkuPriceMap.keySet()) {

            final String key = (String) o;

            final List<SkuPrice> skuPricesForOneSku = new ArrayList<SkuPrice>((Collection<SkuPrice>) qtySkuPriceMap.get(key));

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

    void reorderSkuPrices(List<SkuPrice> skuPricesForOneSku) {
        Collections.sort(
                skuPricesForOneSku,
                new Comparator<SkuPrice>() {

                    public int compare(final SkuPrice skuPrice1, final SkuPrice skuPrice2) {
                        //the new price definition has high priority
                        return skuPrice2.getSkuPriceId() < skuPrice1.getSkuPriceId() ? -1 : skuPrice2.getSkuPriceId() == skuPrice1.getSkuPriceId() ? 0 : 1;
                    }
                }
        );
    }

    /**
     * Try to add all time price (not start and no end)
     * and add it into given result holder - <code>allPrices</code>
     *
     * @param allPrices          result holder for all skus.
     * @param skuPricesForOneSku pricces for one sku
     * @param time               current time
     * @return true in case if result was added
     */
    boolean addAllTimePrice(List<SkuPrice> allPrices, List<SkuPrice> skuPricesForOneSku, long time) {
        boolean found;
        for (SkuPrice skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSalefrom() == null && skuPrice.getSaleto() == null) {
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
     * @param skuPricesForOneSku pricces for one sku
     * @param time               current time
     * @return true in case if result was added
     */
    boolean addStartPrice(List<SkuPrice> allPrices, List<SkuPrice> skuPricesForOneSku, long time) {
        for (SkuPrice skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSalefrom() != null && skuPrice.getSaleto() == null) {
                if (skuPrice.getSalefrom().getTime() < time) {
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
     * @param skuPricesForOneSku pricces for one sku
     * @param time               current time
     * @return true in case if result was added
     */
    boolean addEndPrice(List<SkuPrice> allPrices, List<SkuPrice> skuPricesForOneSku, long time) {
        for (SkuPrice skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSalefrom() == null && skuPrice.getSaleto() != null) {
                if (time < skuPrice.getSaleto().getTime()) {
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
    boolean addFramedPrice(List<SkuPrice> allPrices, List<SkuPrice> skuPricesForOneSku, long time) {
        for (SkuPrice skuPrice : skuPricesForOneSku) {
            if (skuPrice.getSalefrom() != null && skuPrice.getSaleto() != null) {
                if (skuPrice.getSalefrom().getTime() < time && time < skuPrice.getSaleto().getTime()) {
                    allPrices.add(skuPrice);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> getSkuPrices(final Collection<ProductSku> productSkus, final Shop shop, final String currencyCode) {

        List<SkuPrice> rez = getSkuPriceFilteredByShopCurrency(productSkus, shop, currencyCode);

        if (rez.isEmpty() && currencyCode != null && !currencyCode.equals(shop.getDefaultCurrency())) {

            final List<SkuPrice> shopSkuPrices = getSkuPriceFilteredByShop(productSkus, shop);
            List<SkuPrice> skuPrices = getSkuPriceFilteredByCurrency(shopSkuPrices, shop.getDefaultCurrency());
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate(shop, shop.getDefaultCurrency(), currencyCode);
            if (exchangeRate == null) {
                skuPrices.clear();
            } else {
                skuPrices = recalculatePrices(currencyCode, skuPrices, exchangeRate);
            }
            rez = skuPrices;
        }
        return rez;
    }

    /**
     * Recalculate prices from default currency to given.
     *
     * @param currencyCode  given currency code
     * @param baseSkuPrices prices in default currency
     * @param exchangeRate  exchange rate
     * @return list of skus with recalculated prices
     */
    private List<SkuPrice> recalculatePrices(final String currencyCode,
                                             final List<SkuPrice> baseSkuPrices,
                                             final BigDecimal exchangeRate) {

        final List<SkuPrice> skuPrices = new ArrayList<SkuPrice>(baseSkuPrices.size());

        for (SkuPrice baseSkuPrice : baseSkuPrices) {

            final SkuPrice skuPrice = skuPriceDao.getEntityFactory().getByIface(SkuPrice.class);


            skuPrice.setRegularPrice(baseSkuPrice.getRegularPrice().multiply(exchangeRate).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
            if (baseSkuPrice.getSalePriceForCalculation() != null) {
                skuPrice.setSalePrice(baseSkuPrice.getSalePriceForCalculation().multiply(exchangeRate).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
            }
            if (baseSkuPrice.getMinimalPrice() != null) {
                skuPrice.setMinimalPrice(baseSkuPrice.getMinimalPrice().multiply(exchangeRate).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
            }
            skuPrice.setCurrency(currencyCode);
            skuPrice.setQuantity(baseSkuPrice.getQuantity());
            skuPrice.setSku(baseSkuPrice.getSku());

            skuPrices.add(skuPrice);
        }

        return skuPrices;
    }


    private List<SkuPrice> getSkuPricesFilteredSkuCode(final List<SkuPrice> prices, final String selectedSkuCode) {
        List<SkuPrice> result = new ArrayList<SkuPrice>();
        for (SkuPrice skuPrice : prices) {
            if (skuPrice.getSku().getCode().equals(selectedSkuCode)) {
                result.add(skuPrice);
            }
        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> getSkuPricesFilteredByQuantity(final List<SkuPrice> prices, final BigDecimal quantity) {
        List<SkuPrice> result = new ArrayList<SkuPrice>();
        final Set<String> uniqueSkuCodes = getUniqueSkuCodes(prices);
        for (String selectedSkuCode : uniqueSkuCodes) {
            final List<SkuPrice> selectedSkuPrices = getSkuPricesFilteredSkuCode(prices, selectedSkuCode);
            Collections.sort(
                    selectedSkuPrices,
                    new Comparator<SkuPrice>() {
                        /**
                         * {@inheritDoc}
                         * ReverveComparator by quantity.
                         */
                        public int compare(final SkuPrice skuPrice1, final SkuPrice skuPrice2) {
                            return skuPrice2.getQuantity().compareTo(skuPrice1.getQuantity());
                        }
                    }
            );
            for (SkuPrice skuPrice : selectedSkuPrices) {
                if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(quantity, skuPrice.getQuantity())) {
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


    private Set<String> getUniqueSkuCodes(final List<SkuPrice> prices) {
        Set<String> stringSet = new HashSet<String>();
        for (SkuPrice skuPrice : prices) {
            stringSet.add(skuPrice.getSku().getCode());
        }
        return stringSet;
    }


    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> getSkuPriceFilteredByCurrency(final List<SkuPrice> skuPrices, final String currencyCode) {
        List<SkuPrice> skuPricesFiltered = new ArrayList<SkuPrice>();
        for (SkuPrice skuPrice : skuPrices) {
            if (skuPrice.getCurrency().equals(currencyCode)) {
                skuPricesFiltered.add(skuPrice);
            }
        }
        return skuPricesFiltered;
    }

    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> getSkuPriceFilteredByShop(final Collection<ProductSku> productSkus, final Shop shop) {
        List<SkuPrice> skuPrices = new ArrayList<SkuPrice>();
        for (ProductSku sku : productSkus) {
            for (SkuPrice skuPrice : sku.getSkuPrice()) {
                if (shop.getShopId() == skuPrice.getShop().getShopId()) {
                    skuPrices.add(skuPrice);
                }
            }
        }
        return skuPrices;
    }

    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> getSkuPriceFilteredByShopCurrency(final Collection<ProductSku> productSkus, final Shop shop, final String currencyCode) {
        List<SkuPrice> skuPrices = new ArrayList<SkuPrice>();
        for (ProductSku sku : productSkus) {
            for (SkuPrice skuPrice : sku.getSkuPrice()) {
                if (shop.getShopId() == skuPrice.getShop().getShopId() && skuPrice.getCurrency().equals(currencyCode)) {
                    skuPrices.add(skuPrice);
                }
            }
        }
        return skuPrices;
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
            AttrValueShop attrValueShop = shop.getAttributeByCode("PRICE_NAVIGATION_STRATEGY");

            if (attrValueShop != null && "DYNAMIC".equalsIgnoreCase(attrValueShop.getVal())) {

                final String defaultCurrency = shop.getDefaultCurrency();
                final List<PriceTierNode> defTiers = priceTierTree.getPriceTierNodes(defaultCurrency);
                final BigDecimal exchangeRate = exchangeRateService.getExchangeRate(shop, defaultCurrency, currency);
                final List<PriceTierNode> rez = createPriceTierNodes(
                        defTiers,
                        MoneyUtils.notNull(exchangeRate, BigDecimal.ONE));
                priceTierTree.addPriceTierNode(currency, rez);
                return rez;
            }
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
     * Create new price nodes from default currency and exchange rate
     *
     * @param priceTierNodes price tier in default currency
     * @param exchangeRate   exchange rate
     * @return list of priceTierNodes recalculated with given exchange rate
     */
    List<PriceTierNode> createPriceTierNodes(final List<PriceTierNode> priceTierNodes, final BigDecimal exchangeRate) {

        if (priceTierNodes != null) {
            final List<PriceTierNode> rez = new ArrayList<PriceTierNode>(priceTierNodes.size());
            for (PriceTierNode priceTierNode : priceTierNodes) {
                rez.add(new PriceTierNodeImpl(
                        niceBigDecimal(priceTierNode.getFrom().multiply(exchangeRate)),
                        niceBigDecimal(priceTierNode.getTo().multiply(exchangeRate))
                ));
            }
            return rez;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public int updateDerivedPrices(final Shop shop, final String derivedCurrency) {
        final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        final DecimalFormat decimalFormat = new DecimalFormat(Constants.MONEY_FORMAT, formatSymbols);


        final String defaultCurrency = shop.getDefaultCurrency();
        final BigDecimal exchangeRate = exchangeRateService.getExchangeRate(shop, defaultCurrency, derivedCurrency);
        final String shopId = String.valueOf(shop.getShopId());
        deleteDerivedPrices(shop, derivedCurrency);
        String sql;


        /**
         * Native sql is used, because i have got from hibernate
         * "number of select types did not match those for insert" this exeption is incorrect.
         */

        sql = MessageFormat.format("insert into tskuprice (sku_id, shop_id, currency, qty, regular_price, sale_price, minimal_price, sale_from, sale_to, guid)" +
                " select o.sku_id, o.shop_id, ''{0}'', o.qty, o.regular_price * {1}, o.sale_price * {1}, o.minimal_price * {1}, o.sale_from, o.sale_to, o.guid from tskuprice o" +
                " where o.shop_id = {2} and o.currency = ''{3}''",
                derivedCurrency,
                decimalFormat.format(exchangeRate),
                shopId,
                defaultCurrency);


        return skuPriceDao.executeNativeUpdate(sql);
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
    @CacheEvict(value = {"imageService-seoImage" , "priceService-minimalRegularPrice"   }, allEntries = true)
    public SkuPrice create(final SkuPrice instance) {
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {"imageService-seoImage" , "priceService-minimalRegularPrice"   }, allEntries = true)
    public SkuPrice update(final SkuPrice instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {"imageService-seoImage" , "priceService-minimalRegularPrice"   }, allEntries = true)
    public void delete(final SkuPrice instance) {
        super.delete(instance);
    }
}
