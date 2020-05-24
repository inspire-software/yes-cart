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

package org.yes.cart.domain.entity;

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * Represent sku in order with quantity and price in order currency.
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    BigDecimal getListPrice();


    /**
     * Set list / catalog price.
     *
     * @param listPrice list price
     */
    void setListPrice(BigDecimal listPrice);


    /**
     * This is a configurable product.
     *
     * @return true if this is a configurable product.
     */
    boolean isConfigurable();

    /**
     * Set configurable flag.
     *
     * @param configurable true if this is a configurable
     */
    void setConfigurable(final boolean configurable);

    /**
     * This product not to be sold separately.
     *
     * @return not to be sold separately product.
     */
    boolean isNotSoldSeparately();

    /**
     * Set notSoldSeparately flag.
     *
     * @param notSoldSeparately true if this is a notSoldSeparately
     */
    void setNotSoldSeparately(boolean notSoldSeparately);

    /**
     * Returns true if this item has been added as gift as
     * a result of promotion.
     *
     * @return true if this is a promotion gift
     */
    @Override
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
    @Override
    boolean isPromoApplied();

    /**
     * @param promoApplied set promotion applied flag
     */
    void setPromoApplied(boolean promoApplied);

    /**
     * Returns true if this item has been added as fixed price offer.
     * {@link #getAppliedPromo()} will contain authorisation code
     *
     * @return true if this is a fixed price offer
     */
    @Override
    boolean isFixedPrice();

    /**
     * Fixed price flag.
     *
     * @param fixedPrice fixed price
     */
    void setFixedPrice(boolean fixedPrice);

    /**
     * Comma separated list of promotion codes that have been applied
     * for this cart item.
     *
     * @return comma separated promo codes
     */
    @Override
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
    @Override
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
    @Override
    String getProductName();

    /**
     * Set product name in CustomerOrder.locale.
     *
     * @param productName copy of product name
     */
    void setProductName(String productName);

    /**
     * @return supplier code.
     */
    @Override
    String getSupplierCode();

    /**
     * Set supplier code (fulfilment centre/warehouse code).
     *
     * @param supplierCode supplier code
     */
    void setSupplierCode(String supplierCode);

    /**
     * @return item group.
     */
    String getItemGroup();

    /**
     * Set item group (indicates logical grouping of SKU, e.g. bundle or product with options).
     *
     * @param itemGroup group
     */
    void setItemGroup(String itemGroup);


    /**
     * Delivery remarks (could be used for third party integrations or in JAM)
     *
     * @return remarks
     */
    String getDeliveryRemarks();

    /**
     * Delivery remarks (could be used for third party integrations or in JAM)
     *
     * @param deliveryRemarks remarks
     */
    void setDeliveryRemarks(String deliveryRemarks);

    /**
     * Earliest date the delivery is estimated for.
     *
     * @return estimated date
     */
    LocalDateTime getDeliveryEstimatedMin();

    /**
     * Earliest date the delivery is estimated for.
     *
     * @param deliveryEstimatedMin estimated date
     */
    void setDeliveryEstimatedMin(LocalDateTime deliveryEstimatedMin);

    /**
     * Latest date the delivery is estimated for.
     *
     * @return estimated date
     */
    LocalDateTime getDeliveryEstimatedMax();

    /**
     * Latest date the delivery is estimated for.
     *
     * @param deliveryEstimatedMax estimated date
     */
    void setDeliveryEstimatedMax(LocalDateTime deliveryEstimatedMax);

    /**
     * Guaranteed delivery date.
     *
     * @return guaranteed date
     */
    LocalDateTime getDeliveryGuaranteed();

    /**
     * Guaranteed delivery date.
     *
     * @param deliveryGuaranteed guaranteed date
     */
    void setDeliveryGuaranteed(LocalDateTime deliveryGuaranteed);

    /**
     * B2B remarks.
     *
     * @return remarks
     */
    String getB2bRemarks();

    /**
     * B2B remarks.
     *
     * @param b2bRemarks remarks
     */
    void setB2bRemarks(String b2bRemarks);


    /**
     * @param code attribute code
     *
     * @return string value and display value for that code
     */
    Pair<String, I18NModel> getValue(String code);

    /**
     * @param code attribute code
     * @param value string value for that code
     * @param displayValue display value object
     */
    void putValue(String code, String value, I18NModel displayValue);

    /**
     * @return all values mapped to codes
     */
    Map<String, Pair<String, I18NModel>> getAllValues();

    /**
     * @param allValues all values
     */
    void setAllValues(Map<String, Pair<String, I18NModel>> allValues);

    /**
     * @return order holding this detail
     */
    CustomerOrder getCustomerOrder();

    /**
     * @param customerOrder order holding this detail
     */
    void setCustomerOrder(CustomerOrder customerOrder);



}


