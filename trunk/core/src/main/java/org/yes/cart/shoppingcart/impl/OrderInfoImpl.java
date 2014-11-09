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

import org.yes.cart.shoppingcart.MutableOrderInfo;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 19-May-2011
 * Time: 17:32:44
 */
public class OrderInfoImpl implements MutableOrderInfo {

    private String paymentGatewayLabel;
    private boolean multipleDelivery;
    private boolean separateBillingAddress;
    private boolean billingAddressNotRequired;
    private boolean deliveryAddressNotRequired;
    private Long carrierSlaId;
    private Long billingAddressId;
    private Long deliveryAddressId;
    private String orderMessage;

    /** {@inheritDoc} */
    public String getOrderMessage() {
        return orderMessage;
    }

    /**
         * Set order message.
         *
         * @param orderMessage order message.
         */
    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }


    /** {@inheritDoc} */
    public Long getCarrierSlaId() {
        return carrierSlaId;
    }

    /**
         * Set carrier shipping SLA.
         *
         * @param carrierSlaId selected sla id.
         */
    public void setCarrierSlaId(final Long carrierSlaId) {
        this.carrierSlaId = carrierSlaId;
    }

    /** {@inheritDoc} */
    public Long getBillingAddressId() {
        return billingAddressId;
    }

    /**
         * Set billing address.
         *
         * @param billingAddressId billing address.
         */
    public void setBillingAddressId(final Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    /** {@inheritDoc} */
    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    /**
         * Set delivery address.
         *
         * @param deliveryAddressId delivery address.
         */
    public void setDeliveryAddressId(final Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    /** {@inheritDoc} */
    public boolean isSeparateBillingAddress() {
        return separateBillingAddress;
    }

    /**
         * Set billing address different from shipping address flag.
         *
         * @param separateBillingAddress flag.
         */
    public void setSeparateBillingAddress(final boolean separateBillingAddress) {
        this.separateBillingAddress = separateBillingAddress;
    }

    /** {@inheritDoc} */
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    /**
         * Set billing address not required for this order flag.
         *
         * @param billingAddressRequired flag.
         */
    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    /** {@inheritDoc} */
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    /**
         * Set delivery address not required for this order flag.
         *
         * @param deliveryAddressRequired flag.
         */
    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

    /** {@inheritDoc} */
    public String getPaymentGatewayLabel() {
        return paymentGatewayLabel;
    }

    /**
         * Set selected payment gateway.
         *
         * @param paymentGatewayLabel selected payment gateway.
         */
    public void setPaymentGatewayLabel(final String paymentGatewayLabel) {
        this.paymentGatewayLabel = paymentGatewayLabel;
    }


    /** {@inheritDoc} */
    public boolean isMultipleDelivery() {
        return multipleDelivery;
    }

    /**
         * Set multiple delivery for order.
         *
         * @param multipleDelivery multiple delivery for order.
         */
    public void setMultipleDelivery(final boolean multipleDelivery) {
        this.multipleDelivery = multipleDelivery;
    }

}
