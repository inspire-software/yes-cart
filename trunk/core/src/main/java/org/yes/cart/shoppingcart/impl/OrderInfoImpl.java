package org.yes.cart.shoppingcart.impl;

import org.yes.cart.domain.dto.OrderInfo;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 19-May-2011
 * Time: 17:32:44
 */
public class OrderInfoImpl implements OrderInfo {

    private String paymentGatewayLabel;
    private boolean multipleDelivery;

    /**
     * Get selected payment gateway.
     * @return selected payment gateway
     */
    public String getPaymentGatewayLabel() {
        return paymentGatewayLabel;
    }

    /**
     * Set selected payment gateway.
     * @param paymentGatewayLabel   selected payment gateway.
     */
    public void setPaymentGatewayLabel(final String paymentGatewayLabel) {
        this.paymentGatewayLabel = paymentGatewayLabel;
    }


    /**
     * Is order need multiple delivery.
     * @return true if need multiple delivery.
     */
    public boolean isMultipleDelivery() {
        return multipleDelivery;
    }

    /**
     * Set multiple delivery for order.
     * @param multipleDelivery multiple delivery for order.
     */
    public void setMultipleDelivery(final boolean multipleDelivery) {
        this.multipleDelivery = multipleDelivery;
    }

    



}
