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

package org.yes.cart.service.order;

import java.util.List;
import java.util.Map;

/**
 * Order state manager performs transition between order and delivery states.
 * <p/>
 * Each transition is performed as action to even. The actions are performed by event handlers
 * {@link OrderEventHandler}.
 * <p/>
 * Each event has before and after listeners.
 * <p/>
 * Success flow for event is:<p/>
 * 1. before listener onEvent<p/>
 * 2. event handler handle<p/>
 * 3. after listener onEvent<p/>
 * <p/>
 * Note: before step 3 OrderEvent.getRuntimeParams() are updated with "handled" Boolean and optionally
 * "handledException" OrderException if one occurred.<p/>
 * <p/>
 * Default implementation contain comprehensive event flow that copes with various situations.
 * If different flow is required then one should consider remapping handlers for events and implementing
 * own handlers that alter the flow as necessary.<p/>
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderStateManager {


    String EVT_PENDING                      = "evt.pending";                   // from none to pending state
    String EVT_PAYMENT_PROCESSING           = "evt.payment.processing";        // AUTH/AUTH_CAPTURE payment response was in processing state, waiting until next update
    String EVT_PAYMENT_PROCESSED            = "evt.payment.processed";         // AUTH/AUTH_CAPTURE payment response with update on processed payment
    String EVT_PAYMENT_OFFLINE              = "evt.payment.offline";           // payment via offline payment gateway, transition to wait state
    String EVT_PAYMENT_CONFIRMED            = "evt.payment.confirmed";         // from wait confirmation (offline payment) to main order flow. inherited from evt.payment.ok
    String EVT_PAYMENT_OK                   = "evt.payment.ok";                // payment ok (delivery event mediator)
    String EVT_PROCESS_ALLOCATION           = "evt.process.allocation";        // reserve quantity on warehouse
    String EVT_CANCEL                       = "evt.order.cancel";              // simply cancel event for non confirmed orders and old orders in none state
    String EVT_CANCEL_NEW_WITH_REFUND       = "evt.new.order.cancel.refund";   // cancel event with refund for not yet reserved.
    String EVT_CANCEL_WITH_REFUND           = "evt.order.cancel.refund";       // cancel event with refund and credit quantity.
    String EVT_REFUND_PROCESSED             = "evt.refund.processed";          // REFUND payment response with update on processed payment (in case refund was in processing at the time of cancellation).
    String EVT_PROCESS_TIME_WAIT            = "evt.process.date.wait";         // wait till date
    String EVT_PROCESS_INVENTORY_WAIT       = "evt.process.inventory.wait";    // wait till available quantity state
    String EVT_PROCESS_ALLOCATION_WAIT      = "evt.process.allocation.wait";   // wait till allocation job
    String EVT_DELIVERY_ALLOWED_TIMEOUT     = "evt.delivery.allowed.timeout";  // transition will be performed when all items in delivery will have availability date more than now
    String EVT_DELIVERY_ALLOWED_QUANTITY    = "evt.delivery.allowed.quantity"; // transition will be performed when inventory check will be ok for non digital products.
    String EVT_RELEASE_TO_PACK              = "evt.release.to.pack";           // all quantity was reserved, so can release to order pack
    String EVT_PACK_COMPLETE                = "evt.packing.complete";          // order packed, so just wait for shipment
    String EVT_RELEASE_TO_SHIPMENT          = "evt.release.to.shipment";       // right now delivery on the way to the customer
    String EVT_SHIPMENT_COMPLETE            = "evt.shipment.complete";         // lets go to capture funds and update qty
    String EVT_DELIVERY_UPDATE              = "evt.delivery.update";           // event to auto-progress order using delivery update object (to be used for integrations)



    /**
     * Fire transition. New order state not persisted after transition, hence
     * caller is responsible to persist order with new state.
     *
     * @param orderEvent event to fire transition
     * @return true in case if order state transition or one of the delivery state transition  was  successful
     * @throws  {@link OrderException} in case if transition can not be done
     */
    boolean fireTransition(OrderEvent orderEvent) throws OrderException;


    /**
     * Get before transition listeners map.
     *
     * @return after transition listeners map.
     */
    Map<String, List<? extends OrderStateTransitionListener>> getBeforeListenersMap();

    /**
     * Get handlers.
     *
     * @return handlers map.
     */
    Map<String, OrderEventHandler> getHandlers();

    /**
     * Get after transition listeners map.
     *
     * @return after transition listeners map.
     */
    Map<String, List<? extends OrderStateTransitionListener>> getAfterListenersMap();


}
