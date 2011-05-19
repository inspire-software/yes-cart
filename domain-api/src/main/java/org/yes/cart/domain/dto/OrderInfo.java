package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 *
 * Hold specific order information.
 * Just remove from shopping cart to keep it simple as posible.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 19-May-2011
 * Time: 17:27:32
 */
public interface OrderInfo  extends Serializable {

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
