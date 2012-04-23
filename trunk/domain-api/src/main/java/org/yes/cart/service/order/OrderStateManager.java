package org.yes.cart.service.order;

import java.util.List;
import java.util.Map;

/**
 * Order state manager perform transition between order and delivery states.
 * Nothing complex, but all transition in one place. Allow to add event listeners before and
 * after transitions.
 * <p/>
 * Transition event are flexible, hence easy to customize state flow.
 * Transition event descriptions:
 * <p/>
 * <table border=1>
 * <tr><td>Trigger</td><td>From</td><td>To</td><td>Description</td></tr>
 * <tr><td>OrderStateManager.EVT_PAYMENT_OFFLINE</td><td>ORDER_STATUS_PENDING</td><td>ORDER_STATUS_WAITING</td><td>Waiting for approvment, because of offline payment system</td></tr>
 * <tr><td>OrderStateManager.EVT_PAYMENT_OK     </td><td>ORDER_STATUS_PENDING</td><td>ORDER_STATUS_IN_PROGRESS, CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT</td><td>Payment was ok, so lets process oder deliveries on fullfilment center. Initial CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT will be changed to different statuses acourding to delivery group</td></tr>
 * <tr><td>OrderStateManager.EVT_PAYMENT_CONFIRMED</td><td>ORDER_STATUS_WAITING</td><td>ORDER_STATUS_IN_PROGRESS, CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT</td><td>Payment on delivery confirmed or via bank collected</td></tr>
 * <tr><td>OrderStateManager.EVT_CANCEL</td><td>ORDER_STATUS_WAITING or ORDER_STATUS_PENDING</td><td>ORDER_STATUS_CANCELLED</td><td>Because of offline payment, pending timeout, order not confirmed  </td></tr>
 * <tr><td>OrderStateManager.EVT_CANCEL_WITH_REFUND</td><td>ORDER_STATUS_IN_PROGRESS or ORDER_STATUS_PARTIALLY_SHIPPED or ORDER_STATUS_COMPLETED</td><td>ORDER_STATUS_CANCELLED</td><td>Operator can cancel th order with captured funds. If delivery was completed, the qty will be updated, if delivery in progress - reservetion will be updated</td></tr>
 * <tr><td>OrderStateManager.EVT_PROCESS_TIME_WAIT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT</td><td>Wait for particular date, when all ites will be available by date. This state used for mixed delivery. Used for pre order</td></tr>
 * <tr><td>OrderStateManager.EVT_PROCESS_INVENTORY_WAIT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT</td><td>Wait for quantity to fill delivery. Used for back orders</td></tr>
 * <tr><td>OrderStateManager.EVT_PROCESS_ALLOCATION</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED</td><td>Perform inventory allocation. Adjust reserved and on warehouse quantity</td></tr>
 * <tr><td>OrderStateManager.EVT_SHIPMENT_COMPLETE</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED</td><td>Perform funds capture. Before handler for electronic delivery send url for download. After handler for e delivery send serial & etc. After handler perform order state to ORDER_STATUS_COMPLETED or ORDER_STATUS_PARTIALLY_SHIPPED</td></tr>
 * <tr><td>OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT</td><td>Check available inventory for preorders and mixed delivery orders</td></tr>
 * <tr><td>OrderStateManager.EVT_DELIVERY_ALLOWED_QUANTITY</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED</td><td>Inventory allocation</td></tr>
 * <tr><td>OrderStateManager.EVT_RELEASE_TO_PACK</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_PACKING</td><td>Delivery is packing</td></tr>
 * <tr><td>OrderStateManager.EVT_PACK_COMPLETE</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_PACKING</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY</td><td>Wait for shipment</td></tr>
 * <tr><td>OrderStateManager.EVT_RELEASE_TO_SHIPMENT</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS</td><td>On the way. After handlers send email notifications with / without tracking numbers</td></tr>
 * <tr><td>OrderStateManager.EVT_SHIPMENT_COMPLETE</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY</td><td>*, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED</td><td>Perform funds capture. Before handler for electronic delivery send url for download. After handler for e delivery send serial & etc. After handler perform order state to ORDER_STATUS_COMPLETED or ORDER_STATUS_PARTIALLY_SHIPPED</td></tr>
 * </table>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderStateManager {


    String EVT_PENDING = "evt.pending";                   // from none to pending state
    String EVT_PAYMENT_OFFLINE = "evt.payment.offline";           // payment via offline payment gateway, transition to wait state
    String EVT_PAYMENT_CONFIRMED = "evt.payment.confirmed";         // from wait confirmation to main order flow. inherited from evt.payment.ok
    String EVT_PAYMENT_OK = "evt.payment.ok";                // online payment ok
    String EVT_PROCESS_ALLOCATION = "evt.process.allocation";        // reserve qunatity on warehouse
    String EVT_CANCEL = "evt.order.cancel";              // simply cancel event for non confirmed orders and old orders in none state
    String EVT_CANCEL_WITH_REFUND = "evt.order.cancel.refund";       // cancel event with refund and credit quantity.
    String EVT_PROCESS_TIME_WAIT = "evt.process.date.wait";         // wait till date
    String EVT_PROCESS_INVENTORY_WAIT = "evt.process.inventory.wait";    // wait till available quantity state
    String EVT_DELIVERY_ALLOWED_TIMEOUT = "evt.delivery.allowed.timeout";  //transition will be performed when all items in delivery will have availability date more than now
    String EVT_DELIVERY_ALLOWED_QUANTITY = "evt.delivery.allowed.quantity"; //transition will be performed when inventory check will be ok for non digital products.
    String EVT_RELEASE_TO_PACK = "evt.release.to.pack";           // all quantity was reserved, so can release to order pack
    String EVT_PACK_COMPLETE = "evt.packing.complete";          // order packed, so just wait for shipment
    String EVT_RELEASE_TO_SHIPMENT = "evt.release.to.shipment";       // right now delivery on the way to the customer
    String EVT_SHIPMENT_COMPLETE = "evt.shipment.complete";         // lets go to capture funds and update qty




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
