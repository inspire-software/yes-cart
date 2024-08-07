/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.order.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.service.order.*;
import org.yes.cart.utils.spring.LinkedHashMapBean;
import org.yes.cart.utils.spring.LinkedHashMapBeanImpl;

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

    private LinkedHashMapBean<String, OrderEventHandler> handlersOk;
    private LinkedHashMapBean<String, OrderEventHandler> handlersFailed;
    private LinkedHashMapBean<String, List<? extends OrderStateTransitionListener>> afterListenersMapOk;
    private LinkedHashMapBean<String, List<? extends OrderStateTransitionListener>> beforeListenersMapOk;
    private boolean afterTransitionListenerWasFired;
    private boolean beforeTransitionListenerWasFired;
    private boolean afterTransitionDynamicListenerWasFired;
    private boolean beforeTransitionDynamicListenerWasFired;

    @Before
    public void setUp()  {
        afterTransitionListenerWasFired = false;
        beforeTransitionListenerWasFired = false;
        handlersOk = new LinkedHashMapBeanImpl<>(new HashMap<String, OrderEventHandler>() {{
            put("payment.ok", new OrderEventHandler() {
                public boolean handle(OrderEvent orderEvent) {
                    return true;
                }
            });
        }});
        handlersFailed = new LinkedHashMapBeanImpl<>(new HashMap<String, OrderEventHandler>() {{
            put("payment.ok", new OrderEventHandler() {
                public boolean handle(OrderEvent orderEvent) {
                    return false;
                }
            });
        }});
        final List<OrderStateAfterTransitionListener> orderStateAfterTransitionListeners = new ArrayList<OrderStateAfterTransitionListener>() {{
            add(new OrderStateAfterTransitionListener() {
                public boolean onEvent(OrderEvent orderEvent) {
                    afterTransitionListenerWasFired = true;
                    return true;
                }
            });
        }};
        afterListenersMapOk = new LinkedHashMapBeanImpl<>(new HashMap<String, List<? extends OrderStateTransitionListener>>() {{
            put("payment.ok", orderStateAfterTransitionListeners);
        }});
        final List<OrderStateBeforeTransitionListener> orderStateBeforeTransitionListeners = new ArrayList<OrderStateBeforeTransitionListener>() {{
            add(new OrderStateBeforeTransitionListener() {
                public boolean onEvent(OrderEvent orderEvent) {
                    beforeTransitionListenerWasFired = true;
                    return true;
                }
            });
        }};
        beforeListenersMapOk = new LinkedHashMapBeanImpl<>(new HashMap<String, List<? extends OrderStateTransitionListener>>() {{
            put("payment.ok", orderStateBeforeTransitionListeners);
        }});
    }

    /**
     * Nothing happened with wrong event id
     *
     * @throws Exception
     */
    @Test
    public void testFireTransitionUnmappedEvent() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersOk, beforeListenersMapOk, afterListenersMapOk);

        final OrderEvent event = new OrderEventImpl("some.unhandled.cart.event", null);

        assertFalse(orderStateManager.fireTransition(event));

        assertFalse(afterTransitionListenerWasFired);
        assertFalse(beforeTransitionListenerWasFired);
        assertFalse(event.getRuntimeParams().containsKey("handled"));
    }

    /**
     * Prove, that before and after listeners and  handler fired for event
     *
     * @throws Exception
     */
    @Test
    public void testFireTransitionHandledEvent() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersOk, beforeListenersMapOk, afterListenersMapOk);

        final OrderEvent event = new OrderEventImpl("payment.ok", null);

        assertTrue(orderStateManager.fireTransition(event));

        assertTrue(afterTransitionListenerWasFired);
        assertTrue(beforeTransitionListenerWasFired);
        assertTrue(Boolean.TRUE.equals(event.getRuntimeParams().get("handled")));
    }

    /**
     * Prove, that before listeners only before listener is fired if handler result is false
     *
     * @throws Exception
     */
    @Test
    public void testFireTransitionUnhandledEvent() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersFailed, beforeListenersMapOk, afterListenersMapOk);

        final OrderEvent event = new OrderEventImpl("payment.ok", null);

        assertFalse(orderStateManager.fireTransition(event));

        assertTrue(beforeTransitionListenerWasFired);
        assertTrue(afterTransitionListenerWasFired);
        assertTrue(Boolean.FALSE.equals(event.getRuntimeParams().get("handled")));
    }

    /**
     * Test to prove, that dynamic handles are operational.
     *
     * @throws Exception
     */
    @Test
    public void testAddBeforeAfterTransitionListenerDynamicHandlers() throws Exception {
        OrderStateManagerImpl orderStateManager = new OrderStateManagerImpl(handlersOk, beforeListenersMapOk, afterListenersMapOk);
        List<OrderStateTransitionListener> afterTransitionEventHandlers = (List<OrderStateTransitionListener>)
                orderStateManager.getAfterListenersMap().get("payment.ok");
        afterTransitionEventHandlers.add(new OrderStateAfterTransitionListener() {
            public boolean onEvent(OrderEvent orderEvent) {
                assertTrue(orderEvent.getRuntimeParams().containsKey("handled"));
                assertTrue(Boolean.TRUE.equals(orderEvent.getRuntimeParams().get("handled")));
                afterTransitionDynamicListenerWasFired = true;
                return true;
            }
        });
        List<OrderStateTransitionListener> beforeTransitionEventHandlers =
                (List<OrderStateTransitionListener>) orderStateManager.getBeforeListenersMap().get("payment.ok");
        beforeTransitionEventHandlers.add(new OrderStateBeforeTransitionListener() {
            public boolean onEvent(OrderEvent orderEvent) {
                assertFalse(orderEvent.getRuntimeParams().containsKey("handled"));
                beforeTransitionDynamicListenerWasFired = true;
                return true;
            }
        });

        final OrderEvent event = new OrderEventImpl("payment.ok", null);

        assertTrue(orderStateManager.fireTransition(event));

        assertTrue(afterTransitionDynamicListenerWasFired);
        assertTrue(beforeTransitionDynamicListenerWasFired);
        assertTrue(Boolean.TRUE.equals(event.getRuntimeParams().get("handled")));
    }
}
