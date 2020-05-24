/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Dto
public class VoCartOrderInfo {

    @DtoField(readOnly = true)
    private String paymentGatewayLabel;
    @DtoField(readOnly = true)
    private boolean multipleDelivery;
    @DtoField(readOnly = true, converter = "MapToListPairs")
    private List<MutablePair<String, Boolean>> multipleDeliveryAvailable;
    @DtoField(readOnly = true)
    private boolean separateBillingAddress;
    @DtoField(readOnly = true)
    private boolean separateBillingAddressEnabled;
    @DtoField(readOnly = true)
    private boolean billingAddressNotRequired;
    @DtoField(readOnly = true)
    private boolean deliveryAddressNotRequired;
    @DtoField(readOnly = true, converter = "MapToListPairs")
    private List<MutablePair<String, Long>> carrierSlaId;
    @DtoField(readOnly = true)
    private Long billingAddressId;
    @DtoField(readOnly = true)
    private Long deliveryAddressId;
    @DtoField(readOnly = true, converter = "MapToListPairs")
    private List<MutablePair<String, String>> details;
    @DtoField(readOnly = true)
    private String orderMessage;

    
    public String getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    public void setDetails(final List<MutablePair<String, String>> details) {
        getDetailsInternal().clear();
        if (details != null) {
            this.details.addAll(details);
        }
    }

    
    public List<MutablePair<String, String>> getDetails() {
        return Collections.unmodifiableList(getDetailsInternal());
    }

    protected List<MutablePair<String, String>> getDetailsInternal() {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        return this.details;
    }

    
    public List<MutablePair<String, Long>> getCarrierSlaId() {
        return Collections.unmodifiableList(getCarrierSlaIdInternal());
    }

    protected List<MutablePair<String, Long>> getCarrierSlaIdInternal() {
        if (this.carrierSlaId == null) {
            this.carrierSlaId = new ArrayList<>();
        }
        return this.carrierSlaId;
    }

    public void setCarrierSlaId(final List<MutablePair<String, Long>> carrierSlaId) {
        getCarrierSlaIdInternal().clear();
        if (carrierSlaId != null) {
            this.carrierSlaId.addAll(carrierSlaId);
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


    public void setMultipleDeliveryAvailable(final List<MutablePair<String, Boolean>> multipleDeliveryAvailable) {
        this.multipleDeliveryAvailable = multipleDeliveryAvailable;
    }
}
