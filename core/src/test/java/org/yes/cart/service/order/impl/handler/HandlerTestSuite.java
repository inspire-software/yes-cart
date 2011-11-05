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
        TestCancelOrderEventHandlerImpl.class,
        TestCancelOrderWithRefundOrderEventHandlerImpl.class,
        TestDeliveryAllowedByInventoryOrderEventHandlerImpl.class,
        TestDeliveryAllowedByTimeoutOrderEventHandlerImpl.class,
        TestPackCompleteOrderEventHandlerImpl.class,
        TestPaymentOfflineOrderEventHandlerImpl.class,
        TestPaymentOkOrderEventHandlerImpl.class,
        TestPendingOrderEventHandlerImpl.class,
        TestProcessAllocationOrderEventHandlerImpl.class,
        TestProcessInventoryWaitOrderEventHandlerImpl.class,
        TestProcessTimeWaitOrderEventHandlerImpl.class,
        TestReleaseToPackOrderEventHandlerImpl.class,
        TestReleaseToShipmentOrderEventHandlerImpl.class,
        TestShipmentCompleteOrderEventHandlerImpl.class
        
})
public class HandlerTestSuite {
}
