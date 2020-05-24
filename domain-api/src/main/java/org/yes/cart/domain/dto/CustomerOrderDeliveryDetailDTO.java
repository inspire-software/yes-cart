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
package org.yes.cart.domain.dto;


import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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
     * @return supplier code.
     */
    String getSupplierCode();

    /**
     * Set supplier code (fulfilment centre/warehouse code).
     *
     * @param supplierCode supplier code
     */
    void setSupplierCode(String supplierCode);


    /**
     * @return delivery group.
     */
    String getDeliveryGroup();

    /**
     * Set delivery group
     *
     * @param deliveryGroup delivery group
     */
    void setDeliveryGroup(String deliveryGroup);


    /**
     * @return item group.
     */
    String getItemGroup();

    /**
     * Set item group
     *
     * @param itemGroup item group
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
     * Guaranteed delivery date. This date should be populated from the third party integrations and denoted
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
     * Delivery rejected flag.
     *
     * @param deliveryRejected rejected flag
     */
    void setDeliveryRejected(boolean deliveryRejected);


    /**
     * Helper check for {@link #getDeliveredQuantity()} == {@link #getQty()}. If no confirmed quantity this check should return false.
     *
     * @return true if delivery was different for this line.
     */
    boolean isDeliveryDifferent();

    /**
     * Delivery different flag
     *
     * @param deliveryDifferent different flag
     */
    void setDeliveryDifferent(boolean deliveryDifferent);

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
    BigDecimal getPrice() ;

    /**
     * Set deal price.
     *
     * @param price deal price.
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
     * This is a configurable product.
     *
     * @return true if this is a configurable product.
     */
    boolean isConfigurable();

    /**
     * Set configurable
     *
     * @param configurable true if this is a configurable product.
     */
    void setConfigurable(boolean configurable);

    /**
     * This product not to be sold separately.
     *
     * @return not to be sold separately product.
     */
    boolean isNotSoldSeparately();

    /**
     * Set not sold separately
     *
     * @param notSoldSeparately not to be sold separately product.
     */
    void setNotSoldSeparately(boolean notSoldSeparately);

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

    /**
     * Total amount for this line.
     *
     * @return  qty * grossPrice;
     */
    BigDecimal getLineTotal();

    /**
     * Total amount for this line.
     *
     * @param lineTotal qty * grossPrice;
     */
    void setLineTotal(BigDecimal lineTotal);

    /**
     * Total tax amount for this line.
     *
     * @return qty * (grossPrice - netPrice)
     */
    BigDecimal getLineTax();

    /**
     * Total tax amount for this line.
     *
     * @param lineTax qty * (grossPrice - netPrice)
     */
    void setLineTax(BigDecimal lineTax);

    /**
     * Total amount for this line.
     *
     * @return  qty * grossPrice;
     */
    BigDecimal getLineTotalGross();

    /**
     * Total amount for this line.
     *
     * @param lineTotalGross qty * grossPrice;
     */
    void setLineTotalGross(BigDecimal lineTotalGross);

    /**
     * Total amount for this line.
     *
     * @return  qty * netPrice;
     */
    BigDecimal getLineTotalNet();

    /**
     * Total amount for this line.
     *
     * @param lineTotalNet qty * netPrice;
     */
    void setLineTotalNet(BigDecimal lineTotalNet);


    /**
     * @return all values mapped to codes
     */
    Map<String, Pair<String, Map<String, String>>> getAllValues();

    /**
     * @param allValues all values
     */
    void setAllValues(Map<String, Pair<String, Map<String, String>>> allValues);

}
