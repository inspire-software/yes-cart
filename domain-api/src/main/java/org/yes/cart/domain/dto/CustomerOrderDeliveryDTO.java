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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 *
 * Represent one delivery from order with details.
 * @see org.yes.cart.domain.entity.CustomerOrderDelivery for more details.
 *
 * User: iazarny@yahoo.com
 * Date: 8/15/12
 * Time: 7:42 AM
 */
public interface CustomerOrderDeliveryDTO extends Identifiable, AuditInfoDTO {



    /**
     * Shipment pk.
     *
     * @return pk value
     */
    long getCustomerOrderDeliveryId();

    /**
     * Set pk value.
     *
     * @param customerOrderDeliveryId pk value.
     */
    void setCustomerOrderDeliveryId(long customerOrderDeliveryId);

    /**
     * Get delivery number.
     *
     * @return delivery number
     */
    String getDeliveryNum();

    /**
     * Set delivery number
     *
     * @param deliveryNum delivery number
     */
    void setDeliveryNum(String deliveryNum);

    /**
     * Get external delivery number, if any.
     *
     * @return external delivery number
     */
    String getRefNo();

    /**
     * Set external delivery number.
     *
     * @param refNo external delivery number, if any.
     */
    void setRefNo(String refNo);

    /**
     * Calculated delivery price.
     *
     * @return delivery price.
     */
    BigDecimal getPrice();

    /**
     * Set delivery price.
     *
     * @param price delivery price.
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
    BigDecimal getListPrice();

    /**
     * Set sale price.
     * @param salePrice to set.
     */
    void setListPrice(BigDecimal salePrice);

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
     * Get order delivery status
     *
     * @return order delivery status
     */
    String getDeliveryStatus();

    /**
     * Set order delivery status
     *
     * @param deliveryStatus order delivery status
     */
    void setDeliveryStatus(String deliveryStatus);


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
     * Flag for export management control.
     *
     * @return type of order export applicable for this order
     */
    String getEligibleForExport();

    /**
     * Flag for export management control.
     *
     * @param eligibleForExport type of order export applicable for this order
     */
    void setEligibleForExport(String eligibleForExport);

    /**
     * Flag for better export management control (e.g. hold off export until fraud check is performed).
     *
     * @return true if export is blocked
     */
    boolean isBlockExport();

    /**
     * Flag for better export management control (e.g. hold off export until fraud check is performed).
     *
     * @param blockExport true if export is blocked
     */
    void setBlockExport(boolean blockExport);

    /**
     * Timestamp of last attempted export.
     *
     * @return timestamp
     */
    Instant getLastExportDate();

    /**
     * Timestamp of last attempted export.
     *
     * @param lastExportDate timestamp
     */
    void setLastExportDate(Instant lastExportDate);

    /**
     * Last export status. Could be any update from third party system (e.g. validation error).
     * Empty denotes success.
     *
     * @return status
     */
    String getLastExportStatus();

    /**
     * Last export status. Could be any update from third party system (e.g. validation error).
     * Empty denotes success.
     *
     * @param lastExportStatus status
     */
    void setLastExportStatus(String lastExportStatus);

    /**
     * Delivery status that was sent with last export.
     *
     * @return status
     */
    String getLastExportDeliveryStatus();

    /**
     * Delivery status that was sent with last export.
     *
     * @param lastExportDeliveryStatus status
     */
    void setLastExportDeliveryStatus(String lastExportDeliveryStatus);


    /**
     * Get delivery items.
     *
     * @return delivery items.
     */
    Collection<CustomerOrderDeliveryDetailDTO> getDetail();

    /**
     * Set delivery items.
     *
     * @param detail delivery items.
     */
    void setDetail(Collection<CustomerOrderDeliveryDetailDTO> detail);


    /**
     * Get carrier SLA name.
     * @return Carrier's SLA name
     */
    String getCarrierSlaName();

    /**
     * Set carrier sla name.
     * @param carrierSlaName     sla name.
     */
    void setCarrierSlaName(String carrierSlaName);

    /**
     * Get carrier name.
     * @return carrier name
     */
    String getCarrierName();

    /**
     * Set carrier  name.
     * @param carrierName  name.
     */
    void setCarrierName(String carrierName);


    /**
     * Requested delivery date by customer.
     *
     * @return requested delivery date
     */
    LocalDateTime getRequestedDeliveryDate();

    /**
     * Requested delivery date by customer
     *
     * @param requestedDeliveryDate requested delivery date
     */
    void setRequestedDeliveryDate(LocalDateTime requestedDeliveryDate);


    /**
     * Get the order number. Order number not a pk value, it
     * can be slightly different and depends from active {@link org.yes.cart.service.order.OrderNumberGenerator}
     * implementation.
     *
     * @return order number
     */
    String getOrdernum();

    /**
     * Set order number.
     *
     * @param ordernum order number to set.
     */
    void setOrdernum(String ordernum);


    /**
     * Get delivery group (type).
     *
     * @return delivery group.
     */
    String getDeliveryGroup();

    /**
     * Set Delvery group.
     *
     * @param deliveryGroup delivery group.
     */
    void setDeliveryGroup(String deliveryGroup);

    /**
     * Get shipping address.
     * @return shipping address.
     */
    String getShippingAddress();

    /**
     * Set shipping address.
     * @param shippingAddress address
     */
    void setShippingAddress(String shippingAddress);

    /**
     * Get billing address.
     * @return billing address.
     */
    String getBillingAddress();

    /**
     * Set billing address.
     * @param billingAddress billing address.
     */
    void setBillingAddress(String billingAddress);

    /**
     * Order currency.
     * @return currency
     */
    String getCurrency();

    /**
     * Set currency.
     * @param currency currency
     */
    void setCurrency(String currency);

    /**
     * Get shop name.
     * @return shop name.
     */
    String getShopName();

    /**
     * Set shop name.
     * @param shopName shop name.
     */
    void setShopName(String shopName);


    /**
     * Get label of payment gateway.
     * @return payment gateway.
     */
    String getPgLabel();

    /**
     * Set payment gateway.
     * @param pgLabel  payment gateway.
     */
    void setPgLabel(String pgLabel);


    /**
     * Is pwg support capture more than authorised.
     * @return true if support
     */
    boolean isSupportCaptureMore();

    /**
     * Set capture more flag.
     * @param supportCaptureMore flag to set
     */
    void setSupportCaptureMore(boolean supportCaptureMore) ;

    /**
     * support capture less, than authorised.
     * @return true if pgw supports.
     */
    boolean isSupportCaptureLess();

    /**
     * Set support capture less flag.
     * @param supportCaptureLess capture less flag.
     */
    void setSupportCaptureLess( boolean supportCaptureLess)  ;



}
