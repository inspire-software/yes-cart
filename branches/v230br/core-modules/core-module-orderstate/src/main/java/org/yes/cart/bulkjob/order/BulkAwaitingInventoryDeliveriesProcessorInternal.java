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

package org.yes.cart.bulkjob.order;

import org.yes.cart.service.order.OrderException;

/**
 * User: denispavlov
 * Date: 07/05/2015
 * Time: 12:42
 */
public interface BulkAwaitingInventoryDeliveriesProcessorInternal extends Runnable {

    /**
     * This method allows to isolate transaction to single delivery update.
     *
     * @param event event to process
     * @param deliveryId delivery to update
     */
    void processDeliveryEvent(final String event, final long deliveryId) throws OrderException;

}
