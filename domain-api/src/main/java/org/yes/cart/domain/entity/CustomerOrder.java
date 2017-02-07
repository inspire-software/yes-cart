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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;


/**
 * Customer order.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrder extends Auditable {

    /**
     * Order in pending state.
     */
    String ORDER_STATUS_NONE = "os.none";

    /**
     * Order in pending state.
     */
    String ORDER_STATUS_PENDING = "os.pending";

    /**
     * Waiting for payment confirmation.
     */
    String ORDER_STATUS_WAITING_PAYMENT = "os.waiting.payment";

    /**
     * Waiting for approval, because of offline payment system is selected for payment.
     * It can be bank of currier payment.
     */
    String ORDER_STATUS_WAITING = "os.waiting";

    /**
     * Order in progress. In this state need to look at order shipment state.
     * Quantity reserved on warehouse.
     */
    String ORDER_STATUS_IN_PROGRESS = "os.in.progress";


    /**
     * Order canceled. Quantity returned from reservation.
     */
    String ORDER_STATUS_CANCELLED = "os.cancelled";

    /**
     * Order canceled. Quantity returned from reservation. But refund failed.
     */
    String ORDER_STATUS_CANCELLED_WAITING_PAYMENT = "os.cancelled.waiting.payment";

    /**
     * Order returned. Quantity returned from credit.
     */
    String ORDER_STATUS_RETURNED = "os.returned";

    /**
     * Order returned. Quantity returned from credit. But refund failed.
     */
    String ORDER_STATUS_RETURNED_WAITING_PAYMENT = "os.returned.waiting.payment";

    /**
     * Order can have this state in case of shipment split. So at leas one shipment is incomplete.
     */
    String ORDER_STATUS_PARTIALLY_SHIPPED = "os.partially.shipped";

    /**
     * Order completed.
     */
    String ORDER_STATUS_COMPLETED = "os.completed";

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
     * Get formatted billing address, that copied from customer profile.
     * This need to prevent situation when address can be edited by customer in his
     * profile during delivery, so need to copy billing address.
     *
     * @return formatted delivery address.
     */
    String getBillingAddress();

    /**
     * Set formatted address.
     *
     * @param billingAddress formatted address.
     */
    void setBillingAddress(String billingAddress);


    /**
     * Get formatted billing address, that copied from customer profile.
     * This need to prevent situation when address can be edited by customer in his
     * profile during delivery, so need to copy billing address.
     *
     * @return formatted delivery address.
     */
    Address getBillingAddressDetails();

    /**
     * Set formatted address.
     *
     * @param billingAddress formatted address.
     */
    void setBillingAddressDetails(Address billingAddress);



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
     * Get formatted shipping address.
     *
     * @return shipping address.
     */
    Address getShippingAddressDetails();

    /**
     * Set shipping address.
     *
     * @param shippingAddress shipping address.
     */
    void setShippingAddressDetails(Address shippingAddress);

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
     * Get order locale.
     *
     * @return locale used when this order was placed
     */
    String getLocale();

    /**
     * Set order locale.
     *
     * @param locale locale user when this order was placed
     */
    void setLocale(String locale);

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
     * Order message (e.g. a gift message)
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
    Date getLastExportDate();

    /**
     * Timestamp of last attempted export.
     *
     * @param lastExportDate timestamp
     */
    void setLastExportDate(Date lastExportDate);

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
     * B2B refernce for the chargeable department/ manager in charge of paying for this etc
     *
     * @return internal charge ID
     */
    String getB2bChargeId();

    /**
     * B2B refernce for the chargeable department/ manager in charge of paying for this etc
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
    Date getB2bApprovedDate();

    /**
     * Date of approval.
     *
     * @param b2bApprovedDate date/time
     */
    void setB2bApprovedDate(Date b2bApprovedDate);


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
     * Get Customer.
     *
     * @return customer.
     */
    Customer getCustomer();

    /**
     * Set customer.
     *
     * @param customer customer
     */
    void setCustomer(Customer customer);

    /**
     * Get shop.
     *
     * @return shop.
     */
    Shop getShop();

    /**
     * Set Shop
     *
     * @param shop shop
     */
    void setShop(Shop shop);


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
     * @return coupons used with this order
     */
    Collection<PromotionCouponUsage> getCoupons();

    /**
     * @param coupons coupons used with this order
     */
    void setCoupons(Collection<PromotionCouponUsage> coupons);

    /**
     * Get collection of order details, i.e. items
     *
     * @return order details.
     */
    Collection<CustomerOrderDet> getOrderDetail();

    /**
     * Set order details.
     *
     * @param orderDetail order details.
     */
    void setOrderDetail(Collection<CustomerOrderDet> orderDetail);

    /**
     * Get order delivery. Actually it will be one delivery in most cases
     * but delivery can be split by several reasons.
     *
     * @return Order deliveries
     */
    Collection<CustomerOrderDelivery> getDelivery();

    /**
     * Set order deliveries.
     *
     * @param delivery deliveries.
     */
    void setDelivery(Collection<CustomerOrderDelivery> delivery);


    /**
     * Get delivery by given number.
     *
     * @param deliveryNumber given delivery number
     * @return Delivery if foud, otherwise null
     */
    CustomerOrderDelivery getCustomerOrderDelivery(String deliveryNumber);

    /**
     * Get order creation timestamp.
     *
     * @return order creation timestamp.
     */
    Date getOrderTimestamp();

    /**
     * Set order creation timestamp.
     *
     * @param orderTimestamp order creation timestamp.
     */
    void setOrderTimestamp(Date orderTimestamp);

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
     * Get sum of order details prices less promotion discounts applied
     * (included delivery).
     *
     * @return order total (including tax).
     */
    BigDecimal getOrderTotal();

    /**
     * Get total tax
     * (included delivery).
     *
     * @return order total tax.
     */
    BigDecimal getOrderTotalTax();

    /**
     * Get sum of order details prices less promotion discounts applied
     * (does not include delivery).
     *
     * @return order price.
     */
    BigDecimal getPrice();

    /**
     * Set sum of order details prices less promotion discounts applied
     * (does not include delivery).
     *
     * @param price order price.
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
     * Get order list price is the sum of all list prices of oder details.
     * This price is effectively sub total (does not include delivery).
     *
     * @return order price
     */
    BigDecimal getListPrice();

    /**
     * Set order list price is the sum of all list prices of oder details.
     * This price is effectively sub total (does not include delivery).
     *
     * @param listPrice order price.
     */
    void setListPrice(BigDecimal listPrice);

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

}


