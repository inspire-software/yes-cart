package org.yes.cart.shoppingcart;

import java.io.Serializable;

/**
 * Hold specific order information.
 * Just remove from shopping cart to keep it simple as posible.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 19-May-2011
 * Time: 17:27:32
 */
public interface OrderInfo extends Serializable {

    /**
     * Get order message.
     *
     * @return order message
     */
    String getOrderMessage();

    /**
     * Set order message.
     *
     * @param orderMessage order message.
     */
    void setOrderMessage(String orderMessage);

    /**
     * Set carrier shipping SLA.
     *
     * @param carrierSlaId selected sla id.
     */
    void setCarrierSlaId(Integer carrierSlaId);

    /**
     * Get carrier shipping SLA.
     *
     * @return carries sla id.
     */
    Integer getCarrierSlaId();


    /**
     * Is billing address different from shipping adress.
     *
     * @return true is billing and shipping address are different.
     */
    boolean isSeparateBillingAddress();

    /**
     * Set billilnd address different from shipping address flag.
     *
     * @param separateBillingAddress flag.
     */
    void setSeparateBillingAddress(boolean separateBillingAddress);

    /**
     * Get selected payment gateway.
     *
     * @return selected payment gateway
     */
    String getPaymentGatewayLabel();

    /**
     * Set selected payment gateway.
     *
     * @param paymentGatewayLabel selected payment gateway.
     */
    void setPaymentGatewayLabel(String paymentGatewayLabel);

    /**
     * Is order need multiple delivery.
     *
     * @return true if need multiple delivery.
     */
    boolean isMultipleDelivery();

    /**
     * Set multiple delivery for order.
     *
     * @param multipleDelivery multiple delivery for order.
     */
    void setMultipleDelivery(boolean multipleDelivery);


}
