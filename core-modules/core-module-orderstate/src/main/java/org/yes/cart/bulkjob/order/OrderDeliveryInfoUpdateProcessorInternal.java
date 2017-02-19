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
import org.yes.cart.service.order.impl.handler.delivery.OrderDeliveryStatusUpdate;

import java.util.Iterator;

/**
 * User: denispavlov
 * Date: 17/02/2017
 * Time: 18:01
 */
public interface OrderDeliveryInfoUpdateProcessorInternal extends Runnable {

    /**
     * This method allows to isolate transaction to single delivery update.
     *
     * @param update update to process
     */
    void processDeliveryUpdate(final OrderDeliveryStatusUpdate update) throws OrderException;

    /**
     * Extension hook to plug in data feeds to delivery status update processor.
     *
     * @param dataFeed data feed
     */
    void registerDataFeed(final Iterator<OrderDeliveryStatusUpdate> dataFeed);


}
