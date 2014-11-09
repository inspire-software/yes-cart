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

import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;


/**
 * Represent sku in order with quantity and price in order currency.
 * At this moment prices in shopiing cart items list and here without
 * catalog promotions. TODO: V2 add catalog promotion sku price modificators
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrderDet extends Auditable, CartItem {

    /**
     * Get order detail pk value.
     *
     * @return detail pk value
     */
    long getCustomerOrderDetId();

    /**
     * Set detail pk value
     *
     * @param customerOrderDetId set pk value.
     */
    void setCustomerOrderDetId(long customerOrderDetId);

    /**
     * Get quantity of skus.
     *
     * @return set quantity of skus.
     */
    BigDecimal getQty();

    /**
     * Set quantity of skus.
     *
     * @param qty quantity of skus.
     */
    void setQty(BigDecimal qty);

    /**
     * Get the final price after all promotion have been applied.
     *
     * @return price per single sku.
     */
    BigDecimal getPrice();

    /**
     * Set the final price after all promotion have been applied.
     *
     * @param price single sku price.
     */
    void setPrice(BigDecimal price);

    /**
     * Get the sku sale price including all promotions.
     *
     * @return after tax price
     */
    BigDecimal getNetPrice();

    /**
     * Set net price (price before tax).
     *
     * @param netPrice price before tax
     */
    void setNetPrice(final BigDecimal netPrice);

    /**
     * Get the sku sale price including all promotions.
     *
     * @return before tax price
     */
    BigDecimal getGrossPrice();

    /**
     * Set net price (price after tax).
     *
     * @param grossPrice price after tax
     */
    void setGrossPrice(final BigDecimal grossPrice);

    /**
     * Get tax code used for this item.
     *
     * @return tax code
     */
    String getTaxCode();

    /**
     * Set tax code reference.
     *
     * @param taxCode tax code
     */
    void setTaxCode(final String taxCode);

    /**
     * Get tax rate for this item.
     *
     * @return tax rate 0-99
     */
    BigDecimal getTaxRate();

    /**
     * Set tax rate used (0-99).
     *
     * @param taxRate tax rate
     */
    void setTaxRate(final BigDecimal taxRate);

    /**
     * Tax exclusive of price flag.
     *
     * @return true if exclusive, false if inclusive
     */
    boolean isTaxExclusiveOfPrice();

    /**
     * Set whether this tax is included or excluded from price.
     *
     * @param taxExclusiveOfPrice tax flag
     */
    void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice);

    /**
     * Get sale price for this item (i.e. special sale price before promotions).
     *
     * @return price
     */
    BigDecimal getSalePrice();

    /**
     * Set sale price for this item (i.e. special sale price before promotions).
     *
     * @param salePrice sale price.
     */
    void setSalePrice(BigDecimal salePrice);


    /**
     * Get list / catalog price.
     *
     * @return list price
     */
    BigDecimal getListPrice();


    /**
     * Set list / catalog price.
     *
     * @param listPrice list price
     */
    void setListPrice(BigDecimal listPrice);


    /**
     * Returns true if this item has been added as gift as
     * a result of promotion.
     *
     * @return true if this is a promotion gift
     */
    boolean isGift();

    /**
     * @param gift set gift flag
     */
    void setGift(boolean gift);

    /**
     * Returns true if promotions have been applied to this
     * item.
     *
     * @return true if promotions have been applied
     */
    boolean isPromoApplied();

    /**
     * @param promoApplied set promotion applied flag
     */
    void setPromoApplied(boolean promoApplied);

    /**
     * Comma separated list of promotion codes that have been applied
     * for this cart item.
     *
     * @return comma separated promo codes
     */
    String getAppliedPromo();

    /**
     * @param appliedPromo comma separated promo codes
     */
    void setAppliedPromo(String appliedPromo);


    /**
     * Get SKU code for item purchased.
     *
     * @return SKU code of purchased item
     */
    String getProductSkuCode();

    /**
     * Set SKU code for item purchased.
     *
     * @param skuCode SKU code of purchased item
     */
    void setProductSkuCode(String skuCode);

    /**
     * Get product name in CustomerOrder.locale.
     *
     * @return copy of product name
     */
    String getProductName();

    /**
     * Set product name in CustomerOrder.locale.
     *
     * @param productName copy of product name
     */
    void setProductName(String productName);

    /**
     * @return order holding this detail
     */
    CustomerOrder getCustomerOrder();

    /**
     * @param customerOrder order holding this detail
     */
    void setCustomerOrder(CustomerOrder customerOrder);

}


