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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.shoppingcart.MutableOrderInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 19-May-2011
 * Time: 17:32:44
 */
public class OrderInfoImpl implements MutableOrderInfo {

    private static final String ORDER_MSG_KEY = "orderMessage";

    private String paymentGatewayLabel;
    private boolean multipleDelivery;
    private boolean multipleDeliveryAvailable;
    private boolean separateBillingAddress;
    private boolean billingAddressNotRequired;
    private boolean deliveryAddressNotRequired;
    private Map<String, Long> carrierSlaId;
    private Long billingAddressId;
    private Long deliveryAddressId;
    private Map<String, String> details;
    private String orderMessage;

    /** {@inheritDoc} */
    public String getOrderMessage() {
        return orderMessage;
    }

    /** {@inheritDoc} */
    public void setOrderMessage(final String orderMessage) {
        putDetail(ORDER_MSG_KEY, orderMessage);
    }

    /** {@inheritDoc} */
    public void setDetails(final Map<String, String> details) {
        getDetailsInternal().clear();
        if (details != null) {
            this.details.putAll(details);
            if (details.containsKey(ORDER_MSG_KEY)) {
                this.orderMessage = details.get(ORDER_MSG_KEY);
            }
        }
    }

    /** {@inheritDoc} */
    public void putDetail(final String key, final String detail) {
        if (detail == null) {
            getDetailsInternal().remove(key);
            if (ORDER_MSG_KEY.equals(key)) {
                this.orderMessage = null;
            }
        } else {
            getDetailsInternal().put(key == null ? "" : key, detail);
            if (ORDER_MSG_KEY.equals(key)) {
                this.orderMessage = detail;
            }
        }
    }

    /** {@inheritDoc} */
    public Map<String, String> getDetails() {
        return Collections.unmodifiableMap(getDetailsInternal());
    }

    protected Map<String, String> getDetailsInternal() {
        if (this.details == null) {
            this.details = new HashMap<String, String>();
        }
        return this.details;
    }

    /** {@inheritDoc} */
    public String getDetailByKey(final String key) {
        return getDetailsInternal().get(key);
    }

    /** {@inheritDoc} */
    public boolean isDetailByKeyTrue(final String key) {
        final String detail = getDetailByKey(key);
        return detail != null && Boolean.valueOf(detail);
    }

    /** {@inheritDoc} */
    public Map<String, Long> getCarrierSlaId() {
        return Collections.unmodifiableMap(getCarrierSlaIdInternal());
    }

    protected Map<String, Long> getCarrierSlaIdInternal() {
        if (this.carrierSlaId == null) {
            this.carrierSlaId = new HashMap<String, Long>();
        }
        return this.carrierSlaId;
    }

    /** {@inheritDoc} */
    public void setCarrierSlaId(final Map<String, Long> carrierSlaId) {
        getCarrierSlaIdInternal().clear();
        if (carrierSlaId != null) {
            this.carrierSlaId.putAll(carrierSlaId);
        }
    }

    /** {@inheritDoc} */
    public void putCarrierSlaId(final String supplier, final Long carrierSlaId) {
        if (carrierSlaId == null) {
            getCarrierSlaIdInternal().remove(supplier);
        } else {
            getCarrierSlaIdInternal().put(supplier == null ? "" : supplier, carrierSlaId);
        }
    }

    /** {@inheritDoc} */
    public Long getBillingAddressId() {
        return billingAddressId;
    }

    /** {@inheritDoc} */
    public void setBillingAddressId(final Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    /** {@inheritDoc} */
    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    /** {@inheritDoc} */
    public void setDeliveryAddressId(final Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    /** {@inheritDoc} */
    public boolean isSeparateBillingAddress() {
        return separateBillingAddress;
    }

    /** {@inheritDoc} */
    public void setSeparateBillingAddress(final boolean separateBillingAddress) {
        this.separateBillingAddress = separateBillingAddress;
    }

    /** {@inheritDoc} */
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    /** {@inheritDoc} */
    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    /** {@inheritDoc} */
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    /** {@inheritDoc} */
    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

    /** {@inheritDoc} */
    public String getPaymentGatewayLabel() {
        return paymentGatewayLabel;
    }

    /** {@inheritDoc} */
    public void setPaymentGatewayLabel(final String paymentGatewayLabel) {
        this.paymentGatewayLabel = paymentGatewayLabel;
    }

    /** {@inheritDoc} */
    public boolean isMultipleDelivery() {
        return multipleDelivery;
    }

    /** {@inheritDoc} */
    public void setMultipleDelivery(final boolean multipleDelivery) {
        this.multipleDelivery = multipleDelivery;
    }

    /** {@inheritDoc} */
    public boolean isMultipleDeliveryAvailable() {
        return multipleDeliveryAvailable;
    }

    /** {@inheritDoc} */
    public void setMultipleDeliveryAvailable(final boolean multipleDeliveryAvailable) {
        this.multipleDeliveryAvailable = multipleDeliveryAvailable;
    }

    /** {@inheritDoc} */
    public void clearInfo() {
        this.paymentGatewayLabel = null;
        this.multipleDelivery = false;
        this.multipleDeliveryAvailable = false;
        this.separateBillingAddress = false;
        this.billingAddressNotRequired = false;
        this.deliveryAddressNotRequired = false;
        getCarrierSlaIdInternal().clear();
        this.billingAddressId = null;
        this.deliveryAddressId = null;
        this.setOrderMessage(null);
    }
}
