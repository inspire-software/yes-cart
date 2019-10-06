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

package org.yes.cart.domain.entity;

import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 * <p/>
 * Product sku prices per shop, currency and quantity of skus. Quantity of sku shows
 * what price per single sku if shopper gonna buy more that 1 item. Regular price - price without
 * any discounts and promotional. Sale price will be show in particular time frame or every time if
 * time frame is not present. Minimal price will be used in "name-your-price" pricing strategy.
 */
public interface SkuPrice extends Auditable, Taggable {

    /**
     * Get SKU code.
     *
     * @return sku
     */
    String getSkuCode();

    /**
     * Set SKU code.
     *
     * @param skuCode SKU code
     */
    void setSkuCode(String skuCode);

    /**
     * Get shop.
     *
     * @return {@link Shop}
     */
    Shop getShop();

    /**
     * Set shop
     *
     * @param shop {@link Shop}
     */
    void setShop(Shop shop);

    /**
     * Get currency code.
     *
     * @return currency code
     */
    String getCurrency();

    /**
     * set currency code.
     *
     * @param currency curr code
     */
    void setCurrency(String currency);

    /**
     * Get price quantity.
     *
     * @return quantity
     */
    BigDecimal getQuantity();

    /**
     * Set quantity.
     *
     * @param quantity quantity to set
     */
    void setQuantity(BigDecimal quantity);


    /**
     * Get price upon request flag. This flag indicates that the product has price in this shop but
     * it is not publicly available and current customer needs to contact the shop directly for the price
     * quote. When such price is resolved the product is not purchasable and effectively is behaving like
     * SHOWROOM product. However the big difference is that it is controlled by price record, so if other
     * customers (say using different pricing policy) has this price resolved they will be able to
     * purchase the product.
     *
     * @return flag
     */
    boolean isPriceUponRequest();

    /**
     * Set price upon request flag.
     *
     * @param priceUponRequest set flag
     */
    void setPriceUponRequest(boolean priceUponRequest);

    /**
     * Get price on offer flag. This flag is used to indicate special (sale) prices when the list price
     * is the same as sale price. This flag is generally used by "limited" third party ERP integrations
     * that cannot provide full list price but can only indicate that price is on offer and is already
     * discounted but it cannot specify by how much.
     *
     * @return flag
     */
    boolean isPriceOnOffer();

    /**
     * Set price on offer flag.
     *
     * @param priceOnOffer set flag
     */
    void setPriceOnOffer(boolean priceOnOffer);

    /**
     * Get regular/list price.
     *
     * @return regular price.
     */
    BigDecimal getRegularPrice();

    /**
     * Set regular price.
     *
     * @param regularPrice regular price.
     */
    void setRegularPrice(BigDecimal regularPrice);

    /**
     * Get sale price.
     *
     * @return sale price.
     */
    BigDecimal getSalePrice();

    /**
     * Get sale price for calculation.
     *
     * This method aims to standardise access to the prices realm, in terms of following
     * rules:
     *
     * 1. If product does not have a price as in price is <code>null</code> it is not purchasable.
     * 2. Zero price is used for things like gift and free of change items
     * 3. Sale price by nature cannot be higher than regular price (at least on frontend)
     *
     * This method will return correct regular and sale price provided settings of this
     * object.
     *
     * Examples:
     *
     * Non purchasable product:
     * R: null, S: null results in [null, null]
     *
     * Standard use case only "Regular" price is provided
     * R: 100, S: null results in [100, null]
     *
     * Typical sale use case "Regular" is higher than "Sale"
     * R: 100, S: 80 results in [100, 80]
     *
     * Gift product:
     * R: 100, S: 0 results in [100, 0]
     *
     * Overpriced:
     * R: 100, S: 120 results in [120, null]
     *
     * @return sale price for calculation.
     */
    Pair<BigDecimal, BigDecimal> getSalePriceForCalculation();

    /**
     * Set sale price.
     *
     * @param salePrice sale price.
     */
    void setSalePrice(BigDecimal salePrice);

    /**
     * Get minimal price to use in discount per day or name your price strategy.
     *
     * @return minimal price
     */
    BigDecimal getMinimalPrice();

    /**
     * Set minimal price
     *
     * @param minimalPrice minimal price
     */
    void setMinimalPrice(BigDecimal minimalPrice);

    /**
     * Set sale from date.
     *
     * @return sale from date.
     */
    LocalDateTime getSalefrom();

    /**
     * Get sale from date.
     *
     * @param salefrom sale from date.
     */
    void setSalefrom(LocalDateTime salefrom);

    /**
     * Get sale to date.
     *
     * @return sale to date.
     */
    LocalDateTime getSaleto();

    /**
     * Set sale to date
     *
     * @param saleto sale to date
     */
    void setSaleto(LocalDateTime saleto);

    /**
     * Returns true if product is enabled and now is within from/to date range.
     *
     * @param now    time now
     *
     * @return true if the product is available now
     */
    boolean isAvailable(LocalDateTime now);

    /**
     * Primary key.
     *
     * @return pk value
     */
    long getSkuPriceId();

    /**
     * Set pk value
     *
     * @param skuPriceId pk value.
     */
    void setSkuPriceId(long skuPriceId);

    /**
     * Tag allows classification of price entries. E.g. It is hard to understand
     * price with salefrom/to: 01/12/12 - 25/12/12, but it is easier if it has a tag
     * Christmas sales 2012.
     *
     * @return tag or null.
     */
    @Override
    String getTag();

    /**
     * Set tag value.
     *
     * @param tag price tag
     */
    @Override
    void setTag(String tag);


    /**
     * Get price type. Price type defines customer segment that has access to this price.
     *
     * @return price type
     */
    String getPricingPolicy();

    /**
     * Set price type. Price type defines customer segment that has access to this price.
     *
     * @param pricingPolicy price type
     */
    void setPricingPolicy(String pricingPolicy);

    /**
     * Mark this price as supplier specific.
     *
     * @return supplier
     */
    String getSupplier();

    /**
     * Mark this price as supplier specific.
     *
     * @param supplier supplier
     */
    void setSupplier(String supplier);

    /**
     * Get reference for this price list.
     *
     * @return reference
     */
    String getRef();

    /**
     * Set reference for this price list
     *
     * @param ref reference (e.g. contract number or special price mark)
     */
    void setRef(String ref);


    /**
     * "AutoGenerated" flag that is set to true denotes that the price has been auto generated by the system and hence
     * can be removed or changed by the system at any time. Usually this means that this price record was generated
     * by price rules.
     *
     * @return flag
     */
    boolean isAutoGenerated();

    /**
     * Set auto generated flag.
     *
     * @param autoGenerated set flag
     */
    void setAutoGenerated(boolean autoGenerated);


}
