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

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * CustomerOrderDeliveryDet represent an sku quantity and price  in particular
 * shipment. Currency can be obtained from order.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrderDeliveryDet extends Auditable, CartItem {

    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getCustomerOrderDeliveryDetId();

    /**
     * Set pk value.
     *
     * @param customerorderdeliveryId pk value
     */
    void setCustomerOrderDeliveryDetId(long customerorderdeliveryId);


    /**
     * Get quantity of sku.
     *
     * @return quantity of sku.
     */
    @Override
    BigDecimal getQty();

    /**
     * Set  quantity of sku.
     *
     * @param qty quantity of sku.
     */
    void setQty(BigDecimal qty);

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
     * Confirmed delivery date. This date should be populated from the third party integrations and denoted
     * actual confirmed date of delivery. If this is field is null it only means that there is not confirmation
     * by the delivery company that the items is delivered, it does not mean that it has not been delivered.
     *
     * @return guaranteed date
     */
    LocalDateTime getDeliveryConfirmed();

    /**
     * Actual confirmed delivery date.
     *
     * @param delivered confirmed delivery date
     */
    void setDeliveryConfirmed(LocalDateTime delivered);

    /**
     * Actual delivered quantity. Zero indicates that item has been rejected. Must remain null if no
     * confirmation received. See {@link #getDeliveryConfirmed()}.
     *
     * @return confirmed delivered quantity
     */
    BigDecimal getDeliveredQuantity();

    /**
     * Confirmed quantity.
     *
     * @param deliveredQuantity confirmed quantity
     */
    void setDeliveredQuantity(BigDecimal deliveredQuantity);

    /**
     * Helper check for {@link #getDeliveredQuantity()} == 0. If no confirmed quantity this check should return false.
     *
     * @return true if delivery was rejected for this line.
     */
    boolean isDeliveryRejected();

    /**
     * Helper check for {@link #getDeliveredQuantity()} == {@link #getQty()}. If no confirmed quantity this check should return false.
     *
     * @return true if delivery was different for this line.
     */
    boolean isDeliveryDifferent();

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
     * Invoice number for this item. In most cases invoice matches the order, however in some cases
     * with long term recurring orders invoices are done either in bulk or per article, hence invoice
     * number is per line.
     *
     * @return supplier invoice number
     */
    String getSupplierInvoiceNo();

    /**
     * Set invoice number for this line.
     *
     * @param invoiceNo invoice number
     */
    void setSupplierInvoiceNo(String invoiceNo);

    /**
     * Invoice date, see {@link #getSupplierInvoiceNo()}.
     *
     * @return invoice date
     */
    LocalDate getSupplierInvoiceDate();

    /**
     * Set invoice date.
     *
     * @param supplierInvoiceDate invoice date
     */
    void setSupplierInvoiceDate(LocalDate supplierInvoiceDate);

    /**
     * Get order delivery.
     *
     * @return delivery
     */
    CustomerOrderDelivery getDelivery();

    /**
     * Set delivery.
     *
     * @param delivery delivery.
     */
    void setDelivery(CustomerOrderDelivery delivery);

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
    void setListPrice( BigDecimal listPrice);

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

}