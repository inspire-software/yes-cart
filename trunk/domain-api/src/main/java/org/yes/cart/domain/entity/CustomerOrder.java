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
     * Order retunred. Quantity returned from credit.
     */
    String ORDER_STATUS_RETURNED = "os.returned";

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


