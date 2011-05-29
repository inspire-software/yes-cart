package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Order DTO interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrderDTO extends Unique {

    /**
     * Get order amount. Calculated as sum of payments.
     *
     * @return amount
     */
    public BigDecimal getAmount();

    /**
     * Set order amount
     *
     * @param amount order amount.
     */
    public void setAmount(BigDecimal amount);


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
    Date getOrderTimestamp();

    /**
     * Set order creation timestamp.
     *
     * @param orderTimestamp order creation timestamp.
     */
    void setOrderTimestamp(Date orderTimestamp);


    /**
     * Get person id.
     *
     * @return customer email.
     */
    public String getEmail();

    /**
     * Set customer email
     *
     * @param email email
     */
    public void setEmail(String email);

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


}
