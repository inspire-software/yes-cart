package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.queryobject.FiteredNavigationRecord;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Price service.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface PriceService extends GenericService<SkuPrice> {

    /**
     * Get minimal price for given product skus (all), shop, currency and quantity.
     * Exchange rate will be used for recalculate price if price does not present
     * in price list for given currency.
     *
     * @param productSkus  product skus
     * @param shop         shop
     * @param currencyCode desirable currency
     * @param quantity     quantity
     * @return pair of regular and sale price.
     */
    Pair<BigDecimal, BigDecimal> getMinimalRegularPrice(
            Collection<ProductSku> productSkus,
            Shop shop,
            String currencyCode,
            BigDecimal quantity);

    /**
     * Get minimal price for given product skus (will be filtered by selected sku), shop, currency and quantity.
     * Exchange rate will be used for recalculate price if price does not present
     * in price list for given currency.
     *
     * @param productSkus  product skus
     * @param selectedSku  selected sku
     * @param shop         shop
     * @param currencyCode desirable currency
     * @param quantity     quantity
     * @return regular or sale price. for fiven parameters.
     */
    BigDecimal getMinimalPrice(
            Collection<ProductSku> productSkus,
            String selectedSku,
            Shop shop,
            String currencyCode,
            BigDecimal quantity);


    /**
     * Get the sku prices filtered by shop.
     * Exchange rate will be used if shop has not prices
     * for gived curency.
     *
     * @param productSkus  product skus
     * @param shop         shop filter
     * @param currencyCode currency code
     * @return list of sku prices
     */
    List<SkuPrice> getSkuPrices(Collection<ProductSku> productSkus, Shop shop, String currencyCode);


    /**
     * Get the sku prices filtered by shop.
     * Exchange rate will be used if shop has not prices
     * for gived curency.
     *
     * @param productSku   product sku
     * @param shop         shop filter
     * @param currencyCode currency code
     * @return list of sku prices
     */
    List<SkuPrice> getSkuPrices(ProductSku productSku, Shop shop, String currencyCode);

    /**
     * Get the sku prices filtered by quantity. Example:
     * ProductSKU1 has defined price ties 1 - 100 USD, 2 - 97 USD, 5 - 85 USD
     * ProductSKU2 has defined price ties 1 - 100 USD, 2 - 98 USD, 3 - 90 USD
     *
     * For quantity 4 result will hold only two SkuPrice:
     * ProductSKU1 87 USD
     * ProductSKU2 90 USD
     *
     * @param prices   sku prices
     * @param quantity
     * @return list of sku prices filtered by quantity
     */
    List<SkuPrice> getSkuPricesFilteredByQuantity(List<SkuPrice> prices, BigDecimal quantity);


    /**
     * Get the sku prices filtered by shop.
     * Exchange rate will be used if shop has not prices
     * for gived curency.
     *
     * @param productSkus     product skus
     * @param shop            shop filter
     * @param currencyCode    currency code
     * @param selectedSkuCode sku code
     * @return list of sku proces
     */
    List<SkuPrice> getSkuPrices(Collection<ProductSku> productSkus,
                                Shop shop,
                                String currencyCode,
                                String selectedSkuCode);

    /**
     * Get the list of skus prices filtered by currency.
     *
     * @param skuPrices    sku prices with all currencies
     * @param currencyCode currency code filter
     * @return list of skus prices filtered by currency code
     */
    List<SkuPrice> getSkuPriceFilteredByCurrency(List<SkuPrice> skuPrices, String currencyCode);

    /**
     * Get the list of skus prices filtered by shop.
     *
     * @param productSkus produc skus
     * @param shop        shop
     * @return list of skus prices filtered by shop
     */
    List<SkuPrice> getSkuPriceFilteredByShop(Collection<ProductSku> productSkus, Shop shop);

    /**
     * Get navigation records for prices
     *
     * @param priceTierTree given price tier tree
     * @param currency      currence code
     * @param shop          currenct shop
     * @return list of navigation records for given price tree and currecny
     */
    List<FiteredNavigationRecord> getPriceNavigationRecords(
            PriceTierTree priceTierTree,
            String currency,
            Shop shop);

    /**
     * Recalculate derived prices. Derived prices - proces not in default currency, for example default shop currency is
     * USD and sho psupport EUR also, but has not price lists for EUR currency and used currency exchange rate instead.
     * Use delete / insert paragigm instead of insert/update.
     * @param shop shop
     * @param derivedCurrency target currency
     * @return quantity of created records.
     */
    int updateDerivedPrices(Shop shop, String derivedCurrency);

}
