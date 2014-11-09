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
package org.yes.cart.domain.dto;


import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;

/**
 * Customer order detail DTO interface.
 * Represent single order line in order with delivery details.
 *
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/12/12
 * Time: 7:21 AM
 */

public interface CustomerOrderDeliveryDetailDTO extends Identifiable {


    /**
     * Get pk value of order detail record.
     * @return pk value
     */
    long getCustomerOrderDeliveryDetId();

    /**
     * Set pk value of related order detail record.
     * @param customerorderdeliveryId pk value
     */
    void setCustomerOrderDeliveryDetId(long customerorderdeliveryId) ;

    /**
     * Get sku code .
     * @return sku code .
     */
    String getSkuCode();

    /**
     * Set sku code .
     * @param skuCode sku code .
     */
    void setSkuCode(String skuCode);

    /**
     * Get sku name.
     * @return  sku name.
     */
    String getSkuName();

    /**
     * Set  sku name.
     * @param skuName sku name.
     */
    void setSkuName(String skuName);

    /**
     * Get quantity.
     * @return  quantity.
     */
    BigDecimal getQty();

    /**
     * Set product qty.
     * @param qty       quantity.
     */
    void setQty(BigDecimal qty) ;

    /**
     * Get price of product, which is in delivery.
     * @return deal price.
     */
    BigDecimal getInvoicePrice() ;

    /**
     * Set deal price.
     * @param invoicePrice deal price.
     */
    void setInvoicePrice(BigDecimal invoicePrice);



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
     * Get sale price.
     * @return price
     */
    BigDecimal getSalePrice();

    /**
     * Set sale price.
     * @param salePrice to set.
     */
    void setSalePrice(BigDecimal salePrice);

    /**
     * Get price in catalog.
     * @return price in catalog.
     */
    BigDecimal getListPrice();

    /**
     * Set price in catalog.
     * @param listPrice price in catalog.
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
     * Get delivery number.
     * @return delivery number.
     */
    String getDeliveryNum();

    /**
     * Set delivery number.
     * @param deliveryNum  delivery num.
     */
    void setDeliveryNum(String deliveryNum) ;

    /**
     * Get delivery status label for more details look at {@link org.yes.cart.domain.entity.CustomerOrderDelivery}
     * @return delivery detail .
     */
    String getDeliveryStatusLabel();

    /**
     * Set delivery status label.
     * @param deliveryStatusLabel delivery status label.
     */
    void setDeliveryStatusLabel(String deliveryStatusLabel) ;



}
