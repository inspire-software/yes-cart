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

package org.yes.cart.service.order.impl.handler.delivery;

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 17/02/2017
 * Time: 12:14
 */
public class OrderDeliveryLineStatusUpdateImpl implements OrderDeliveryLineStatusUpdate {


    private final Long orderLineRef;
    private final String sku;
    private final String orderDeliveryStatus;
    private final LocalDateTime deliveryEstimatedMin;
    private final LocalDateTime deliveryEstimatedMax;
    private final LocalDateTime deliveryGuaranteed;
    private final LocalDateTime deliveryConfirmed;
    private final BigDecimal orderedQty;
    private final BigDecimal deliveredQty;
    private final boolean rejected;
    private final String supplierInvoiceNo;
    private final LocalDate supplierInvoiceDate;
    private final Map<String, Pair<String, I18NModel>> additionalData = new HashMap<>();


    public OrderDeliveryLineStatusUpdateImpl(final Long orderLineRef,
                                             final String sku,
                                             final String orderDeliveryStatus,
                                             final LocalDateTime deliveryEstimatedMin,
                                             final LocalDateTime deliveryEstimatedMax,
                                             final LocalDateTime deliveryGuaranteed,
                                             final LocalDateTime deliveryConfirmed,
                                             final BigDecimal orderedQty,
                                             final BigDecimal deliveredQty,
                                             final boolean rejected,
                                             final String supplierInvoiceNo,
                                             final LocalDate supplierInvoiceDate) {
        this.orderLineRef = orderLineRef;
        this.sku = sku;
        this.orderDeliveryStatus = orderDeliveryStatus;
        this.deliveryEstimatedMin = deliveryEstimatedMin;
        this.deliveryEstimatedMax = deliveryEstimatedMax;
        this.deliveryGuaranteed = deliveryGuaranteed;
        this.deliveryConfirmed = deliveryConfirmed;
        this.orderedQty = orderedQty;
        this.deliveredQty = deliveredQty;
        this.rejected = rejected;
        this.supplierInvoiceNo = supplierInvoiceNo;
        this.supplierInvoiceDate = supplierInvoiceDate;
    }

    public OrderDeliveryLineStatusUpdateImpl(final Long orderLineRef,
                                             final String sku,
                                             final String orderDeliveryStatus,
                                             final LocalDateTime deliveryEstimatedMin,
                                             final LocalDateTime deliveryEstimatedMax,
                                             final LocalDateTime deliveryGuaranteed,
                                             final LocalDateTime deliveryConfirmed,
                                             final BigDecimal orderedQty,
                                             final BigDecimal deliveredQty,
                                             final boolean rejected,
                                             final String supplierInvoiceNo,
                                             final LocalDate supplierInvoiceDate,
                                             final Map<String, Pair<String, I18NModel>> additionalData) {
        this.orderLineRef = orderLineRef;
        this.sku = sku;
        this.orderDeliveryStatus = orderDeliveryStatus;
        this.deliveryEstimatedMin = deliveryEstimatedMin;
        this.deliveryEstimatedMax = deliveryEstimatedMax;
        this.deliveryGuaranteed = deliveryGuaranteed;
        this.deliveryConfirmed = deliveryConfirmed;
        this.orderedQty = orderedQty;
        this.deliveredQty = deliveredQty;
        this.rejected = rejected;
        this.supplierInvoiceNo = supplierInvoiceNo;
        this.supplierInvoiceDate = supplierInvoiceDate;
        this.additionalData.putAll(additionalData);
    }

    @Override
    public Long getOrderLineRef() {
        return orderLineRef;
    }

    @Override
    public String getSKU() {
        return sku;
    }

    @Override
    public String getOrderDeliveryStatus() {
        return orderDeliveryStatus;
    }

    @Override
    public LocalDateTime getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    @Override
    public LocalDateTime getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    @Override
    public LocalDateTime getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    @Override
    public LocalDateTime getDeliveryConfirmed() {
        return deliveryConfirmed;
    }

    @Override
    public BigDecimal getOrderedQty() {
        return orderedQty;
    }

    @Override
    public BigDecimal getDeliveredQty() {
        return deliveredQty;
    }

    @Override
    public boolean isRejected() {
        return rejected;
    }

    @Override
    public String getSupplierInvoiceNo() {
        return supplierInvoiceNo;
    }

    @Override
    public LocalDate getSupplierInvoiceDate() {
        return supplierInvoiceDate;
    }

    @Override
    public Map<String, Pair<String, I18NModel>> getAdditionalData() {
        return additionalData;
    }

    @Override
    public String toString() {
        return "OrderDeliveryStatusUpdateImpl{" +
                "orderLineRef=" + orderLineRef +
                ", sku='" + sku + '\'' +
                ", orderDeliveryStatus='" + orderDeliveryStatus + '\'' +
                ", deliveryEstimatedMin=" + deliveryEstimatedMin +
                ", deliveryEstimatedMax=" + deliveryEstimatedMax +
                ", deliveryGuaranteed=" + deliveryGuaranteed +
                ", deliveryConfirmed=" + deliveryConfirmed +
                ", orderedQty=" + orderedQty +
                ", deliveredQty=" + deliveredQty +
                ", rejected=" + rejected +
                ", supplierInvoiceNo='" + supplierInvoiceNo + '\'' +
                ", supplierInvoiceDate=" + supplierInvoiceDate +
                '}';
    }


}
