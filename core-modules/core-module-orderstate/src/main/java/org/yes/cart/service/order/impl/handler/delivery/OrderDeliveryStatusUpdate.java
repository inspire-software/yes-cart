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

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 16/02/2017
 * Time: 10:18
 */
public interface OrderDeliveryStatusUpdate {

    /**
     * Our order number.
     *
     * @return order number
     */
    String getOrderNumber();

    /**
     * Get supplier code for this order.
     *
     * @return supplier code
     */
    String getSupplierCode();

    /**
     * Delivery line updates.
     *
     * @return line updates
     */
    List<OrderDeliveryLineStatusUpdate> getLineStatus();

    /**
     * Additional data per order.
     *
     * @return line additional data key => { value, displayValue }
     */
    Map<String, Pair<String, String>> getAdditionalData();

}
