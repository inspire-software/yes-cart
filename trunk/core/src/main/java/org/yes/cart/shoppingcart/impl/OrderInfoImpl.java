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
    private boolean separateBillingAddress;
    private Integer carrierSlaId;
    private String orderMessage;

    /**
     * Get order message.
     * @return order message
     */
    public String getOrderMessage() {
        return orderMessage;
    }

    /**
     * Set order message.
     * @param orderMessage order message.
     */
    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }


    /**
     * Get carrier shipping SLA.
     * @return carries sla id.
     */
    public Integer getCarrierSlaId() {
        return carrierSlaId;
    }

    /**
     * Set carrier shipping SLA.
     * @param carrierSlaId selected sla id.
     */
    public void setCarrierSlaId(final Integer carrierSlaId) {
        this.carrierSlaId = carrierSlaId;
    }



    /**
     * Is billing address different from shipping adress.
     * @return true is billing and shipping address are different.
     */
    public boolean isSeparateBillingAddress() {
        return separateBillingAddress;
    }

    /**
     * Set billilnd address different from shipping address flag.
     * @param separateBillingAddress flag.
     */
    public void setSeparateBillingAddress(final boolean separateBillingAddress) {
        this.separateBillingAddress = separateBillingAddress;
    }

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
