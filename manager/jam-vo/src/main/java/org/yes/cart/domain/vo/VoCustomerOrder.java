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
package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;

import java.util.List;

/**
 * User: denispavlov
 * Date: 02/09/2016
 * Time: 13:28
 */
@Dto
public class VoCustomerOrder extends VoCustomerOrderInfo {

    private List<VoCustomerOrderLine> lines;

    private List<VoCustomerOrderDeliveryInfo> deliveries;

    private List<VoPromotion> promotions;

    private List<VoPayment> payments;

    public List<VoCustomerOrderLine> getLines() {
        return lines;
    }

    public void setLines(final List<VoCustomerOrderLine> lines) {
        this.lines = lines;
    }

    public List<VoCustomerOrderDeliveryInfo> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(final List<VoCustomerOrderDeliveryInfo> deliveries) {
        this.deliveries = deliveries;
    }

    public List<VoPromotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(final List<VoPromotion> promotions) {
        this.promotions = promotions;
    }

    public List<VoPayment> getPayments() {
        return payments;
    }

    public void setPayments(final List<VoPayment> payments) {
        this.payments = payments;
    }

}
