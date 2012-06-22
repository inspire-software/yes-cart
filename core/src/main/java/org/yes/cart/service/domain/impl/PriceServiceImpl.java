
package org.yes.cart.service.domain.impl;

import org.yes.cart.cache.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FiteredNavigationRecord;
import org.yes.cart.domain.queryobject.impl.FiteredNavigationRecordImpl;
import org.yes.cart.service.domain.ExchangeRateService;
import org.yes.cart.service.domain.PriceService;
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
    private final GenericDAO<SkuPrice, Long> skuPriceDao;



    /**
     * Constructor.
     *
     * @param exchangeRateService exchange rate service for handle not filled price lists
     * @param priceNavigation     price navigation composer
     * @param skuPriceDao sku proce dao service
     */
    public PriceServiceImpl(final ExchangeRateService exchangeRateService,
                            final PriceNavigation priceNavigation,
                            final GenericDAO<SkuPrice, Long> skuPriceDao
                            ) {
        super(skuPriceDao);
        this.exchangeRateService = exchangeRateService;
        this.priceNavigation = priceNavigation;
        this.skuPriceDao = skuPriceDao;

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceServiceImplMethodCache")
    public SkuPrice getMinimalRegularPrice(
            final Collection<ProductSku> productSkus,
            final Shop shop,
            final String currencyCode,
            final BigDecimal quantity) {

        BigDecimal minimalRegularPrice = null;

        List<SkuPrice> skuPrices = getSkuPrices(productSkus, shop, currencyCode);
        if (quantity != null) {
            skuPrices = getSkuPricesFilteredByQuantity(
                    skuPrices,
                    quantity);
        }
        SkuPrice rez = null;
        for (SkuPrice skuPrice : skuPrices) {
            if (minimalRegularPrice == null
                    || MoneyUtils.isFirstBiggerThanOrEqualToSecond(minimalRegularPrice, skuPrice.getRegularPrice())) {
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
     * {@inheritDoc}
     */
    public BigDecimal getMinimalPrice(
            final Collection<ProductSku> productSkus,
            final String selectedSku,
            final Shop shop,
            final String currencyCode,
            final BigDecimal quantity) {

        final List<SkuPrice> skuPrices =
                getSkuPricesFilteredSkuCode(
                        getSkuPricesFilteredByQuantity(
                                getSkuPrices(productSkus, shop, currencyCode),
                                quantity
                        ),
                        selectedSku
                );

        if (skuPrices.isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            SkuPrice skuPrice = skuPrices.get(0);
            return  MoneyUtils.minPositive(skuPrice.getSalePrice(), skuPrice.getRegularPrice());
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> getSkuPrices(final ProductSku productSku, final Shop shop, final String currencyCode) {
        final Collection<ProductSku> productSkus = new ArrayList<ProductSku>(1);
        productSkus.add(productSku);
        return getSkuPrices(productSkus, shop, currencyCode);
    }


    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> getSkuPrices(final Collection<ProductSku> productSkus, final Shop shop, final String currencyCode) {

        List<SkuPrice> rez = getSkuPriceFilteredByShopCurrency(productSkus, shop, currencyCode);
        
        if (rez.isEmpty() && !currencyCode.equals(shop.getDefaultCurrency())) {

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
     * @param currencyCode given currency code
     * @param baseSkuPrices prices in default currency
     * @param exchangeRate exchange rate
     * @return list of skus with recalculated prices
     */
    private List<SkuPrice> recalculatePrices(final String currencyCode,
                                             final List<SkuPrice> baseSkuPrices,
                                             final BigDecimal exchangeRate) {

        final List<SkuPrice> skuPrices = new ArrayList<SkuPrice>(baseSkuPrices.size());

        for (SkuPrice baseSkuPrice : baseSkuPrices) {

            final SkuPrice skuPrice = skuPriceDao.getEntityFactory().getByIface(SkuPrice.class);


            skuPrice.setRegularPrice(baseSkuPrice.getRegularPrice().multiply(exchangeRate));
            if (baseSkuPrice.getSalePrice() != null) {
                skuPrice.setSalePrice(baseSkuPrice.getSalePrice().multiply(exchangeRate));
            }
            if (baseSkuPrice.getMinimalPrice() != null) {
                skuPrice.setMinimalPrice(baseSkuPrice.getMinimalPrice().multiply(exchangeRate));
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
    public List<SkuPrice> getSkuPrices(final Collection<ProductSku> productSkus,
                                       final Shop shop,
                                       final String currencyCode,
                                       final String selectedSkuCode) {
        final List<SkuPrice> allSkusPrices = getSkuPrices(productSkus, shop, currencyCode);
        final List<SkuPrice> result = new ArrayList<SkuPrice>();
        for (SkuPrice skuPrice : allSkusPrices) {
            if (skuPrice.getSku().getCode().equals(selectedSkuCode)) {
                result.add(skuPrice);
            }
        }
        return result;
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
    public List<FiteredNavigationRecord> getPriceNavigationRecords(
            final PriceTierTree priceTierTree,
            final String currency,
            final Shop shop) {
        final List<PriceTierNode> priceTierNodes = getPriceTierNodes(priceTierTree, currency, shop);
        final List<FiteredNavigationRecord> result = new ArrayList<FiteredNavigationRecord>(priceTierNodes.size());
        for (PriceTierNode priceTierNode : priceTierNodes) {
            result.add(
                    new FiteredNavigationRecordImpl(
                            ProductSearchQueryBuilder.PRODUCT_PRICE,
                            ProductSearchQueryBuilder.PRODUCT_PRICE,
                            priceNavigation.composePriceRequestParams(
                                    currency,
                                    priceTierNode.getPriceRange().getFirst(),
                                    priceTierNode.getPriceRange().getSecond()
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
            final String defaultCurrency = shop.getDefaultCurrency();
            final BigDecimal exchangeRate = exchangeRateService.getExchangeRate(shop, defaultCurrency, currency);
            priceTierNodes = createPriceTierNodes(
                    priceTierTree.getPriceTierNodes(defaultCurrency),
                    MoneyUtils.notNull(exchangeRate, BigDecimal.ZERO));
        }
        return priceTierNodes;
    }

    /**
     * Create new price nodes from default currency and exchange rate
     *
     * @param priceTierNodes price tier in default currency
     * @param exchangeRate   exchange rate
     * @return list of priceTierNodes recalculated with given exchange rate
     */
    private List<PriceTierNode> createPriceTierNodes(final List<PriceTierNode> priceTierNodes, final BigDecimal exchangeRate) {

        for (PriceTierNode priceTierNode : priceTierNodes) {
            priceTierNode.setPriceRange(
                    new Pair<BigDecimal, BigDecimal>(
                            priceTierNode.getPriceRange().getFirst().multiply(exchangeRate),
                            priceTierNode.getPriceRange().getSecond().multiply(exchangeRate)
                    )
            );
        }
        return priceTierNodes;
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
        String sql = MessageFormat.format("delete from tskuprice where shop_id = {0} and currency = ''{1}''",
                shopId,
                derivedCurrency);
        skuPriceDao.executeNativeUpdate(sql);


        sql = MessageFormat.format("insert into tskuprice (sku_id, shop_id, currency, qty, regular_price, sale_price, minimal_price, sale_from, sale_till)" +
                " select o.sku_id, o.shop_id, ''{0}'', o.qty, o.regular_price * {1}, o.sale_price * {1}, o.minimal_price * {1}, sale_from, sale_till from tskuprice o" +
                " where o.shop_id = {2} and o.currency = ''{3}''",
                derivedCurrency,
                decimalFormat.format(exchangeRate),
                shopId,
                defaultCurrency);


        return skuPriceDao.executeNativeUpdate(sql);
    }


}
