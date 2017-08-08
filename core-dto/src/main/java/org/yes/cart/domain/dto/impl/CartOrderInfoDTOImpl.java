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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.shoppingcart.OrderInfo;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Dto
public class CartOrderInfoDTOImpl implements OrderInfo, Serializable {

    private static final long serialVersionUID =  20110509L;

    @DtoField(readOnly = true)
    private String paymentGatewayLabel;
    @DtoField(readOnly = true)
    private boolean multipleDelivery;
    @DtoField(readOnly = true)
    private Map<String, Boolean> multipleDeliveryAvailable;
    @DtoField(readOnly = true)
    private boolean separateBillingAddress;
    @DtoField(readOnly = true)
    private boolean separateBillingAddressEnabled;
    @DtoField(readOnly = true)
    private boolean billingAddressNotRequired;
    @DtoField(readOnly = true)
    private boolean deliveryAddressNotRequired;
    @DtoField(readOnly = true)
    private Map<String, Long> carrierSlaId;
    @DtoField(readOnly = true)
    private Long billingAddressId;
    @DtoField(readOnly = true)
    private Long deliveryAddressId;
    @DtoField(readOnly = true)
    private Map<String, String> details;
    @DtoField(readOnly = true)
    private String orderMessage;

    public String getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    public void setDetails(final Map<String, String> details) {
        getDetailsInternal().clear();
        if (details != null) {
            this.details.putAll(details);
        }
    }

    public Map<String, String> getDetails() {
        return Collections.unmodifiableMap(getDetailsInternal());
    }

    protected Map<String, String> getDetailsInternal() {
        if (this.details == null) {
            this.details = new HashMap<String, String>();
        }
        return this.details;
    }

    public String getDetailByKey(final String key) {
        return getDetailsInternal().get(key);
    }

    /** {@inheritDoc} */
    public boolean isDetailByKeyTrue(final String key) {
        final String detail = getDetailByKey(key);
        return detail != null && Boolean.valueOf(detail);
    }

    public Map<String, Long> getCarrierSlaId() {
        return Collections.unmodifiableMap(getCarrierSlaIdInternal());
    }

    protected Map<String, Long> getCarrierSlaIdInternal() {
        if (this.carrierSlaId == null) {
            this.carrierSlaId = new HashMap<String, Long>();
        }
        return this.carrierSlaId;
    }

    public void setCarrierSlaId(final Map<String, Long> carrierSlaId) {
        getCarrierSlaIdInternal().clear();
        if (carrierSlaId != null) {
            this.carrierSlaId.putAll(carrierSlaId);
        }
    }

    public Long getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(final Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(final Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public boolean isSeparateBillingAddress() {
        return separateBillingAddress;
    }

    public void setSeparateBillingAddress(final boolean separateBillingAddress) {
        this.separateBillingAddress = separateBillingAddress;
    }

    public boolean isSeparateBillingAddressEnabled() {
        return separateBillingAddressEnabled;
    }

    public void setSeparateBillingAddressEnabled(final boolean separateBillingAddressEnabled) {
        this.separateBillingAddressEnabled = separateBillingAddressEnabled;
    }

    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

    public String getPaymentGatewayLabel() {
        return paymentGatewayLabel;
    }

    public void setPaymentGatewayLabel(final String paymentGatewayLabel) {
        this.paymentGatewayLabel = paymentGatewayLabel;
    }


    public boolean isMultipleDelivery() {
        return multipleDelivery;
    }

    public void setMultipleDelivery(final boolean multipleDelivery) {
        this.multipleDelivery = multipleDelivery;
    }

    public Map<String, Boolean> getMultipleDeliveryAvailable() {
        return multipleDeliveryAvailable;
    }

    public void setMultipleDeliveryAvailable(final Map<String, Boolean> multipleDeliveryAvailable) {
        this.multipleDeliveryAvailable = multipleDeliveryAvailable;
    }
}
