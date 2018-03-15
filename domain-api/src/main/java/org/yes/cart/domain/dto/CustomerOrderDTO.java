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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Order DTO interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrderDTO extends Identifiable {

    /**
     * Get order pk value.
     *
     * @return order pk value.
     */
    long getCustomerorderId();

    /**
     * Set order pk value.
     *
     * @param customerorderId order pk value.
     */
    void setCustomerorderId(long customerorderId);

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
     * Get payment gateway label.
     *
     * @return payment gateway label.
     */
    String getPgLabel();

    /**
     * Set payment gateway label.
     *
     * @param pgLabel payment gateway label.
     */
    void setPgLabel(String pgLabel);

    /**
     * Get formated billing address, that copied from customer profile.
     * This need to prevent situation when address can be edited by customer in his
     * profile during delivery, so need to copy billing address.
     *
     * @return formated delivery address.
     */
    String getBillingAddress();

    /**
     * Set formatted address.
     *
     * @param billingAddress formatted address.
     */
    void setBillingAddress(String billingAddress);


    /**
     * Get formatted shipping address.
     *
     * @return shipping address.
     */
    String getShippingAddress();

    /**
     * Set shipping address.
     *
     * @param shippingAddress shipping address.
     */
    void setShippingAddress(String shippingAddress);

    /**
     * Get the original cart guid.
     *
     * @return cart guid
     */
    String getCartGuid();

    /**
     * Set cart guid.
     *
     * @param cartGuid cart guid.
     */
    void setCartGuid(String cartGuid);


    /**
     * Get order currency code.
     *
     * @return order currency code.
     */
    String getCurrency();

    /**
     * Set  order currency code.
     *
     * @param currency order currency code.
     */
    void setCurrency(String currency);

    /**
     * Order message, can be a gift message or what ever
     *
     * @return order message.
     */
    String getOrderMessage();

    /**
     * Set order message from shopping cart.
     *
     * @param orderMessage order message.
     */
    void setOrderMessage(String orderMessage);

    /**
     * Get order status.
     *
     * @return order status.
     */
    String getOrderStatus();

    /**
     * Set order status.
     *
     * @param orderStatus status of order
     */
    void setOrderStatus(String orderStatus);

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
     * Order status that was sent with last export.
     *
     * @return status
     */
    String getLastExportOrderStatus();

    /**
     * Order status that was sent with last export.
     *
     * @param lastExportOrderStatus status
     */
    void setLastExportOrderStatus(String lastExportOrderStatus);

    /**
     * B2B customer reference. Used by customer for their purposes.
     *
     * @return ref
     */
    String getB2bRef();

    /**
     * B2B customer reference. Used by customer for their purposes.
     *
     * @param b2bRef ref
     */
    void setB2bRef(String b2bRef);

    /**
     * B2B internal employee ID (the one who places the order).
     *
     * @return internal employee ID
     */
    String getB2bEmployeeId();

    /**
     * B2B internal employee ID (the one who places the order).
     *
     * @param b2bEmployeeId internal employee ID
     */
    void setB2bEmployeeId(String b2bEmployeeId);

    /**
     * B2B reference for the chargeable department/ manager in charge of paying for this etc
     *
     * @return internal charge ID
     */
    String getB2bChargeId();

    /**
     * B2B reference for the chargeable department/ manager in charge of paying for this etc
     *
     * @param b2bChargeId internal charge ID
     */
    void setB2bChargeId(String b2bChargeId);

    /**
     * Flag to denote that this order requires approval to be placed.
     *
     * @return approval required flag
     */
    boolean isB2bRequireApprove();


    /**
     * Flag to denote that this order requires approval to be placed.
     *
     * @param b2bRequireApprove approval required flag
     */
    void setB2bRequireApprove(boolean b2bRequireApprove);

    /**
     * If this order has been approved then this is the reference of the manager who authorised this.
     *
     * @return internal approve authority reference
     */
    String getB2bApprovedBy();

    /**
     * If this order has been approved then this is the reference of the manager who authorised this.
     *
     * @param b2bApprovedBy internal approve authority reference
     */
    void setB2bApprovedBy(String b2bApprovedBy);

    /**
     * Date of approval.
     *
     * @return date/time
     */
    LocalDateTime getB2bApprovedDate();

    /**
     * Date of approval.
     *
     * @param b2bApprovedDate date/time
     */
    void setB2bApprovedDate(LocalDateTime b2bApprovedDate);


    /**
     * B2B customer remarks. Used by customer for their purposes.
     *
     * @return remarks
     */
    String getB2bRemarks();

    /**
     * B2B customer remarks. Used by customer for their purposes.
     *
     * @param b2bRemarks remarks
     */
    void setB2bRemarks(String b2bRemarks);

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
     * Is order will be delivered in several shipments.
     *
     * @return true in case multiple deliveries, false in case of single delivery.
     */
    boolean isMultipleShipmentOption();

    /**
     * Set multiple delivery flag.
     *
     * @param multipleShipmentOption multiple delivery flag.
     */
    void setMultipleShipmentOption(boolean multipleShipmentOption);


    /**
     * Get order creation timestamp.
     *
     * @return order creation timestamp.
     */
    LocalDateTime getOrderTimestamp();

    /**
     * Set order creation timestamp.
     *
     * @param orderTimestamp order creation timestamp.
     */
    void setOrderTimestamp(LocalDateTime orderTimestamp);


    /**
     * IP address of the request that created this order.
     *
     * @return order creation IP.
     */
    String getOrderIp();

    /**
     * IP address of the request that created this order.
     *
     * @param orderIp order creation IP.
     */
    void setOrderIp(String orderIp);


    /**
     * Get person id.
     *
     * @return customer email.
     */
    String getEmail();

    /**
     * Set customer email
     *
     * @param email email
     */
    void setEmail(String email);

    /**
     * Get first name.
     *
     * @return first name
     */
    String getFirstname();

    /**
     * Set first name
     *
     * @param firstname value to set
     */
    void setFirstname(String firstname);

    /**
     * Get last name.
     *
     * @return last name
     */
    String getLastname();

    /**
     * Set last name
     *
     * @param lastname value to set
     */
    void setLastname(String lastname);

    /**
     * Get middle name
     *
     * @return middle name
     */
    String getMiddlename();

    /**
     * Set middle name
     *
     * @param middlename value to set
     */
    void setMiddlename(String middlename);



    /**
     * Get salutation
     *
     * @return salutation
     */
    String getSalutation();

    /**
     * Set salutation
     *
     * @param salutation value to set
     */
    void setSalutation(String salutation);

    /**
     * Get customer primary key.
     *
     * @return pk value.
     */
    long getCustomerId();

    /**
     * Set customer pk value.
     *
     * @param customerId pk value to set
     */
    void setCustomerId(long customerId);


    /**
     * Get shop primary key.
     *
     * @return pk value.
     */
    long getShopId();

    /**
     * Set pk value of shop.
     *
     * @param shopId pk value to set
     */
    void setShopId(long shopId);


    /**
     * Get shop code.
     *
     * @return shop code.
     */
    String getCode();

    /**
     * Set shop code.
     *
     * @param code shop code.
     */
    void setCode(String code);

    /**
     * Get order amount. Calculated as sum of payments.
     *
     * @return amount
     */
    BigDecimal getAmount();

    /**
     * Set order amount
     *
     * @param amount order amount.
     */
    void setAmount(BigDecimal amount);


    /**
     * Get sum of order details prices less promotion discounts applied
     * (included delivery).
     *
     * @return order price.
     */
    BigDecimal getOrderTotal();

    /**
     * Set order total.
     *
     * @param orderTotal order total
     */
    void setOrderTotal(BigDecimal orderTotal);

    /**
     * Get sum of tax amounts
     * (included delivery).
     *
     * @return order price.
     */
    BigDecimal getOrderTotalTax();

    /**
     * Set order total tax.
     *
     * @param orderTotalTax order total tax
     */
    void setOrderTotalTax(BigDecimal orderTotalTax);

    /**
     * Get sum of order details prices less promotion discounts applied
     * (included delivery).
     *
     * @return order total (including tax).
     */
    BigDecimal getOrderGrossTotal();

    /**
     * Set order total.
     *
     * @param orderGrossTotal order total
     */
    void setOrderGrossTotal(BigDecimal orderGrossTotal);

    /**
     * Get sum of order details prices less promotion discounts applied
     * (included delivery).
     *
     * @return order total (excluding tax).
     */
    BigDecimal getOrderNetTotal();

    /**
     * Set order total.
     *
     * @param orderNetTotal order total
     */
    void setOrderNetTotal(BigDecimal orderNetTotal);

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
     * @return all values mapped to codes
     */
    Map<String, Pair<String, String>> getAllValues();

    /**
     * @param allValues all values
     */
    void setAllValues(Map<String, Pair<String, String>> allValues);


}
