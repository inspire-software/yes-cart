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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
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
    private Map<String, Boolean> multipleDeliveryAvailable;
    private boolean separateBillingAddress;
    private boolean separateBillingAddressEnabled;
    private boolean billingAddressNotRequired;
    private boolean deliveryAddressNotRequired;
    private Map<String, Long> carrierSlaId;
    private Long billingAddressId;
    private Long deliveryAddressId;
    private Map<String, String> details;
    private String orderMessage;

    /** {@inheritDoc} */
    @Override
    public String getOrderMessage() {
        return orderMessage;
    }

    /** {@inheritDoc} */
    @Override
    public void setOrderMessage(final String orderMessage) {
        putDetail(ORDER_MSG_KEY, orderMessage);
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    public void putDetail(final String key, final String detail) {
        if (StringUtils.isNotBlank(key)) {
            if (StringUtils.isBlank(detail)) {
                getDetailsInternal().remove(key);
                if (ORDER_MSG_KEY.equals(key)) {
                    this.orderMessage = null;
                }
            } else {
                getDetailsInternal().put(key, detail);
                if (ORDER_MSG_KEY.equals(key)) {
                    this.orderMessage = detail;
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDetails() {
        return Collections.unmodifiableMap(getDetailsInternal());
    }

    protected Map<String, String> getDetailsInternal() {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        return this.details;
    }

    /** {@inheritDoc} */
    @Override
    public String getDetailByKey(final String key) {
        return getDetailsInternal().get(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDetailByKeyTrue(final String key) {
        final String detail = getDetailByKey(key);
        return detail != null && Boolean.valueOf(detail);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Long> getCarrierSlaId() {
        return Collections.unmodifiableMap(getCarrierSlaIdInternal());
    }

    protected Map<String, Long> getCarrierSlaIdInternal() {
        if (this.carrierSlaId == null) {
            this.carrierSlaId = new HashMap<>();
        }
        return this.carrierSlaId;
    }

    /** {@inheritDoc} */
    @Override
    public void setCarrierSlaId(final Map<String, Long> carrierSlaId) {
        getCarrierSlaIdInternal().clear();
        if (carrierSlaId != null) {
            this.carrierSlaId.putAll(carrierSlaId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void putCarrierSlaId(final String supplier, final Long carrierSlaId) {
        if (carrierSlaId == null) {
            getCarrierSlaIdInternal().remove(supplier);
        } else {
            getCarrierSlaIdInternal().put(supplier == null ? "" : supplier, carrierSlaId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Long getBillingAddressId() {
        return billingAddressId;
    }

    /** {@inheritDoc} */
    @Override
    public void setBillingAddressId(final Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    /** {@inheritDoc} */
    @Override
    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryAddressId(final Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSeparateBillingAddress() {
        return separateBillingAddress;
    }

    /** {@inheritDoc} */
    @Override
    public void setSeparateBillingAddress(final boolean separateBillingAddress) {
        this.separateBillingAddress = separateBillingAddress;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSeparateBillingAddressEnabled() {
        return separateBillingAddressEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public void setSeparateBillingAddressEnabled(final boolean separateBillingAddressEnabled) {
        this.separateBillingAddressEnabled = separateBillingAddressEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    /** {@inheritDoc} */
    @Override
    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

    /** {@inheritDoc} */
    @Override
    public String getPaymentGatewayLabel() {
        return paymentGatewayLabel;
    }

    /** {@inheritDoc} */
    @Override
    public void setPaymentGatewayLabel(final String paymentGatewayLabel) {
        this.paymentGatewayLabel = paymentGatewayLabel;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMultipleDelivery() {
        return multipleDelivery;
    }

    /** {@inheritDoc} */
    @Override
    public void setMultipleDelivery(final boolean multipleDelivery) {
        this.multipleDelivery = multipleDelivery;
    }


    protected Map<String, Boolean> getMultipleDeliveryAvailableInternal() {
        if (this.multipleDeliveryAvailable == null) {
            this.multipleDeliveryAvailable = new HashMap<>();
        }
        return this.multipleDeliveryAvailable;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Boolean> getMultipleDeliveryAvailable() {
        return Collections.unmodifiableMap(getMultipleDeliveryAvailableInternal());
    }

    /** {@inheritDoc} */
    @Override
    public void setMultipleDeliveryAvailable(final Map<String, Boolean> multipleDeliveryAvailable) {
        getMultipleDeliveryAvailableInternal().clear();
        if (multipleDeliveryAvailable != null) {
            this.multipleDeliveryAvailable.putAll(multipleDeliveryAvailable);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void putMultipleDeliveryAvailable(final String supplier, final Boolean multipleDeliveryAvailable) {
        if (multipleDeliveryAvailable == null) {
            getMultipleDeliveryAvailableInternal().remove(supplier);
        } else {
            getMultipleDeliveryAvailableInternal().put(supplier == null ? "" : supplier, multipleDeliveryAvailable);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearInfo() {
        this.paymentGatewayLabel = null;
        this.multipleDelivery = false;
        getMultipleDeliveryAvailableInternal().clear();
        this.separateBillingAddress = false;
        this.separateBillingAddressEnabled = false;
        this.billingAddressNotRequired = false;
        this.deliveryAddressNotRequired = false;
        getCarrierSlaIdInternal().clear();
        this.billingAddressId = null;
        this.deliveryAddressId = null;
        this.setOrderMessage(null);
        this.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_APPROVED_BY, null);
        this.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_APPROVED_DATE, null);
        this.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_ORDER_REMARKS_ID, null);
        getDetailsInternal().entrySet().removeIf(detail -> detail.getKey().startsWith(AttributeNamesKeys.Cart.ORDER_INFO_B2B_ORDER_LINE_REMARKS_ID) ||
                detail.getKey().startsWith(AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_PRICE_REF_ID) ||
                detail.getKey().startsWith(AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID) ||
                detail.getKey().startsWith(AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_ATTRIBUTE_ID) ||
                detail.getKey().startsWith(AttributeNamesKeys.Cart.ORDER_INFO_ORDER_ATTRIBUTE_ID));
    }
}
