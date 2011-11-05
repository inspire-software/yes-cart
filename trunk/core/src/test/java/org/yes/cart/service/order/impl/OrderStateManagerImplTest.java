package org.yes.cart.service.order.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.service.order.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderStateManagerImplTest {

    private Map<String, OrderEventHandler> handlersOk;
    private Map<String, OrderEventHandler> handlersFailed;
    private Map<String, List<? extends OrderStateTransitionListener>> afterListenersMapOk;
    private Map<String, List<? extends OrderStateTransitionListener>> beforeListenersMapOk;
    private boolean afterTransitionListenerWasFired;
    private boolean beforeTransitionListenerWasFired;
    private boolean afterTransitionDynamicListenerWasFired;
    private boolean beforeTransitionDynamicListenerWasFired;

    @Before
    public void setUp() throws Exception {
        handlersOk = new HashMap<String, OrderEventHandler>() {{
            put("payment.ok", new OrderEventHandler() {
                public boolean handle(OrderEvent orderEvent) {
                    return true;
                }
            });
        }};
        handlersFailed = new HashMap<String, OrderEventHandler>() {{
            put("payment.ok", new OrderEventHandler() {
                public boolean handle(OrderEvent orderEvent) {
                    return false;
                }
            });
        }};
        final List<OrderStateAfterTransitionListener> orderStateAfterTransitionListeners = new ArrayList<OrderStateAfterTransitionListener>() {{
            add(new OrderStateAfterTransitionListener() {
                public boolean onEvent(OrderEvent orderEvent) {
                    afterTransitionListenerWasFired = true;
                    return true;
                }
            });
        }};
        afterListenersMapOk = new HashMap<String, List<? extends OrderStateTransitionListener>>() {{
            put("payment.ok", orderStateAfterTransitionListeners);
        }};
        final List<OrderStateBeforeTransitionListener> orderStateBeforeTransitionListeners = new ArrayList<OrderStateBeforeTransitionListener>() {{
            add(new OrderStateBeforeTransitionListener() {
                public boolean onEvent(OrderEvent orderEvent) {
                    beforeTransitionListenerWasFired = true;
                    return true;
                }
            });
        }};
        beforeListenersMapOk = new HashMap<String, List<? extends OrderStateTransitionListener>>() {{
            put("payment.ok", orderStateBeforeTransitionListeners);
        }};
    }

    /**
     * Nothing happened with wrong event id
     *
     * @throws Exception
     */
    @Test
    public void testFireTransition0() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersOk, beforeListenersMapOk, afterListenersMapOk);
        assertFalse(orderStateManager.fireTransition(
                new OrderEventImpl("some.unhendled.cart.event", null)
        ));
        assertFalse(afterTransitionListenerWasFired);
        assertFalse(beforeTransitionListenerWasFired);
    }

    /**
     * Prove, that before and after listeners and  handler fired for event
     *
     * @throws Exception
     */
    @Test
    public void testFireTransition1() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersOk, beforeListenersMapOk, afterListenersMapOk);
        assertTrue(orderStateManager.fireTransition(
                new OrderEventImpl("payment.ok", null)
        ));
        assertTrue(afterTransitionListenerWasFired);
        assertTrue(beforeTransitionListenerWasFired);

    }

    /**
     * Prove, that before listeners will be faired, but after - not in case if handler not perform transition
     *
     * @throws Exception
     */
    @Test
    public void testFireTransition2() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersFailed, beforeListenersMapOk, afterListenersMapOk);
        assertFalse(orderStateManager.fireTransition(
                new OrderEventImpl("payment.ok", null)
        ));
        assertTrue(beforeTransitionListenerWasFired);
        assertFalse(afterTransitionListenerWasFired);
    }


    /**
     * Test to prove, that dynamic handles are operational.
     *
     * @throws Exception
     */
    @Test
    public void testAddBeforeAfterTransitionListener0() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersOk, beforeListenersMapOk, afterListenersMapOk);
        List<OrderStateTransitionListener> afterTransitionEventHandlers = (List<OrderStateTransitionListener>)
                orderStateManager.getAfterListenersMap().get("payment.ok");
        afterTransitionEventHandlers.add(new OrderStateAfterTransitionListener() {
            public boolean onEvent(OrderEvent orderEvent) {
                afterTransitionDynamicListenerWasFired = true;
                return true;
            }
        });
        List<OrderStateTransitionListener> beforeTransitionEventHandlers =
                (List<OrderStateTransitionListener>) orderStateManager.getBeforeListenersMap().get("payment.ok");
        beforeTransitionEventHandlers.add(
                new OrderStateBeforeTransitionListener() {
                    public boolean onEvent(OrderEvent orderEvent) {
                        beforeTransitionDynamicListenerWasFired = true;
                        return true;
                    }
                }
        );
        assertTrue(orderStateManager.fireTransition(
                new OrderEventImpl("payment.ok", null)
        ));
        assertTrue(afterTransitionDynamicListenerWasFired);
        assertTrue(beforeTransitionDynamicListenerWasFired);
    }
}
