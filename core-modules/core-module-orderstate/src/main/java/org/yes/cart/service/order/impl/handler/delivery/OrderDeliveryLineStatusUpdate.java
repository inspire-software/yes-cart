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
import java.util.Map;

/**
 * User: denispavlov
 * Date: 16/02/2017
 * Time: 11:28
 */
public interface OrderDeliveryLineStatusUpdate {

    /**
     * Get order line reference (optional).
     *
     * @return line reference
     */
    Long getOrderLineRef();

    /**
     * SKU code for item.
     *
     * @return SKU code
     */
    String getSKU();

    /**
     * Delivery status from the 3rd party system.
     *
     * @return delivery status
     */
    String getOrderDeliveryStatus();

    /**
     * Estimated delivery time min
     *
     * @return estimated
     */
    Date getDeliveryEstimatedMin();

    /**
     * Estimated delivery time max
     *
     * @return estimated
     */
    Date getDeliveryEstimatedMax();

    /**
     * Guaranteed delivery time
     *
     * @return guaranteed
     */
    Date getDeliveryGuaranteed();

    /**
     * Actual delivery time.
     *
     * @return guaranteed date
     */
    Date getDeliveryConfirmed();

    /**
     * Get ordered quantity.
     *
     * @return quantity that was ordered
     */
    BigDecimal getOrderedQty();

    /**
     * Get quantity that was actually delivered
     *
     * @return actual quantity
     */
    BigDecimal getDeliveredQty();

    /**
     * Flag to indicated that this items was rejected and will not be delivered.
     *
     * @return flag to indicated that item is rejected
     */
    boolean isRejected();

    /**
     * Get invoice number for this delivery.
     *
     * @return supplier invoice number
     */
    String getSupplierInvoiceNo();

    /**
     * Get supplier invoice date.
     *
     * @return supplier invoice date
     */
    Date getSupplierInvoiceDate();

    /**
     * Additional data per line.
     *
     * @return line additional data key => { value, displayValue }
     */
    Map<String, Pair<String, String>> getAdditionalData();


}
