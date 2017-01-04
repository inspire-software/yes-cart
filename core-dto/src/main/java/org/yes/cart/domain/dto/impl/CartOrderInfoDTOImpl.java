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
    private boolean multipleDeliveryAvailable;
    @DtoField(readOnly = true)
    private boolean separateBillingAddress;
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
    private String orderMessage;

    public String getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }


    public Map<String, Long> getCarrierSlaId() {
        if (this.carrierSlaId == null) {
            this.carrierSlaId = new HashMap<String, Long>();
        }
        return carrierSlaId;
    }

    public void setCarrierSlaId(final Map<String, Long> carrierSlaId) {
        this.getCarrierSlaId().clear();
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

    public boolean isMultipleDeliveryAvailable() {
        return multipleDeliveryAvailable;
    }

    public void setMultipleDeliveryAvailable(final boolean multipleDeliveryAvailable) {
        this.multipleDeliveryAvailable = multipleDeliveryAvailable;
    }
}
