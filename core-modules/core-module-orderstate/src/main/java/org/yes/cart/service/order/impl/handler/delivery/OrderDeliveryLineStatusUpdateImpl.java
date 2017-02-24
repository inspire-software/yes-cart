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

package org.yes.cart.service.order.impl.handler.delivery;

import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.Date;
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
    private final Date deliveryEstimatedMin;
    private final Date deliveryEstimatedMax;
    private final Date deliveryGuaranteed;
    private final Date deliveryConfirmed;
    private final BigDecimal orderedQty;
    private final BigDecimal deliveredQty;
    private final boolean rejected;
    private final String supplierInvoiceNo;
    private final Date supplierInvoiceDate;
    private final Map<String, Pair<String, String>> additionalData = new HashMap<String, Pair<String, String>>();


    public OrderDeliveryLineStatusUpdateImpl(final Long orderLineRef,
                                             final String sku,
                                             final String orderDeliveryStatus,
                                             final Date deliveryEstimatedMin,
                                             final Date deliveryEstimatedMax,
                                             final Date deliveryGuaranteed,
                                             final Date deliveryConfirmed,
                                             final BigDecimal orderedQty,
                                             final BigDecimal deliveredQty,
                                             final boolean rejected,
                                             final String supplierInvoiceNo,
                                             final Date supplierInvoiceDate) {
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
                                             final Date deliveryEstimatedMin,
                                             final Date deliveryEstimatedMax,
                                             final Date deliveryGuaranteed,
                                             final Date deliveryConfirmed,
                                             final BigDecimal orderedQty,
                                             final BigDecimal deliveredQty,
                                             final boolean rejected,
                                             final String supplierInvoiceNo,
                                             final Date supplierInvoiceDate,
                                             final Map<String, Pair<String, String>> additionalData) {
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

    public Long getOrderLineRef() {
        return orderLineRef;
    }

    public String getSKU() {
        return sku;
    }

    public String getOrderDeliveryStatus() {
        return orderDeliveryStatus;
    }

    public Date getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    public Date getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    public Date getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    public Date getDeliveryConfirmed() {
        return deliveryConfirmed;
    }

    public BigDecimal getOrderedQty() {
        return orderedQty;
    }

    public BigDecimal getDeliveredQty() {
        return deliveredQty;
    }

    public boolean isRejected() {
        return rejected;
    }

    public String getSupplierInvoiceNo() {
        return supplierInvoiceNo;
    }

    public Date getSupplierInvoiceDate() {
        return supplierInvoiceDate;
    }

    public Map<String, Pair<String, String>> getAdditionalData() {
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
