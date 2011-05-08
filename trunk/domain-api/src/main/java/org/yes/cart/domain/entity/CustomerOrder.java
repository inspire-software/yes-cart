package org.yes.cart.domain.entity;

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
    public static String ORDER_STATUS_NONE = "none";

    /**
     * Order in pending state.
     */
    public static String ORDER_STATUS_PENDING = "pending";

    /**
     * Waiting for approvment, because of offline payment system is selected for payment.
     * It can be bank of currier payment.
     */
    public static String ORDER_STATUS_WAITING = "waiting";

    /**
     * Order in progress. In this state need to look at order shipment state.
     * Quantity reserved on warehouse.
     */
    public static String ORDER_STATUS_IN_PROGRESS = "inprogress";

    /**
     * Order canceled. Quantity returned from revervation.
     */
    public static String ORDER_STATUS_CANCELLED = "cancelled";

    /**
     * Order can have this state in case of shipment split. So at leas one shipment is incomplete.
     */
    public static String ORDER_STATUS_PARTIALLY_SHIPPED = "partially.shipped";

    /**
     * Order completed.
     */
    public static String ORDER_STATUS_COMPLETED = "completed";

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
     * can be slighty different and depends from active {@link org.yes.cart.service.order.OrderNumberGenerator}
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
    public String getBillingAddress();

    /**
     * Set formated address.
     *
     * @param billingAddress formated address.
     */
    public void setBillingAddress(String billingAddress);


    /**
     * Get formated shipping address.
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
     * @param orderMessage order messge.
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
     * Get gollection of order datails, i.e. items
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
     * but delivery can be splited by several reasons.
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


}


