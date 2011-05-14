package org.yes.cart.service.order.impl.handler;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 14-May-2011
 * Time: 17:18:07
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CancelOrderEventHandlerImplTest.class,
        CancelOrderWithRefundOrderEventHandlerImplTest.class,
        DeliveryAllowedByInventoryOrderEventHandlerImplTest.class,
        DeliveryAllowedByTimeoutOrderEventHandlerImplTest.class,
        PackCompleteOrderEventHandlerImplTest.class,
        PaymentOfflineOrderEventHandlerImplTest.class,
        PaymentOkOrderEventHandlerImplTest.class,
        PendingOrderEventHandlerImplTest.class,
        ProcessAllocationOrderEventHandlerImplTest.class,
        ProcessInventoryWaitOrderEventHandlerImplTest.class,
        ProcessTimeWaitOrderEventHandlerImplTest.class,
        ReleaseToPackOrderEventHandlerImplTest.class,
        ReleaseToShipmentOrderEventHandlerImplTest.class,
        ShipmentCompleteOrderEventHandlerImplTest.class
        
})
public class HandlerTestSuite {
}
