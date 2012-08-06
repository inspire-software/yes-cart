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

package org.yes.cart.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 * <p/>
 * Product sku prices per shop, currency and quantity of skus. Quantity of sku shows
 * what price per single sku if shopper gonna buy more that 1 item. Regular price - price without
 * any discounts and promotional. Sale price will be show in particular time frame or everytime if
 * time frame not present. Minimal price will be used in "name-your-price" pricing strategy.
 */
public interface SkuPrice extends Auditable {

    /**
     * Get the product sku.
     *
     * @return {@link ProductSku}
     */
    ProductSku getSku();

    /**
     * Set product sku.
     *
     * @param sku {@link ProductSku}
     */
    void setSku(ProductSku sku);

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
     * set currecny code.
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
     * //TODO Create and use version with time frame check
     *
     * @return sale price.
     * @deprecated Create and use version with time frame check
     */
    BigDecimal getSalePrice();

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
     * @param minimalPrice
     */
    void setMinimalPrice(BigDecimal minimalPrice);

    /**
     * Set sale from date.
     *
     * @return sale from date.
     */
    Date getSalefrom();

    /**
     * Get sale from date.
     *
     * @param salefrom sale from date.
     */
    void setSalefrom(Date salefrom);

    /**
     * Get sale to date.
     *
     * @return sale to date.
     */
    Date getSaleto();

    /**
     * Set sale to date
     *
     * @param saleto sale to date
     */
    void setSaleto(Date saleto);

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

}
