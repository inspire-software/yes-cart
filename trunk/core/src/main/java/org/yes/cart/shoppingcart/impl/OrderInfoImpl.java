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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.shoppingcart.OrderInfo;

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
