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

package org.yes.cart.service.order.impl.handler;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.i18n.I18NModels;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Date: 22/07/2020
 * Time: 09:48
 */
public class AbstractOrderEventHandlerImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testIsForceProcessing() throws Exception {

        final OrderEvent processing = this.context.mock(OrderEvent.class, "processing");
        final OrderEvent forceProcessing = this.context.mock(OrderEvent.class, "forceProcessing");

        this.context.checking(new Expectations() {{
            allowing(processing).getRuntimeParams(); will(returnValue(Collections.emptyMap()));
            allowing(forceProcessing).getRuntimeParams(); will(returnValue(Collections.singletonMap("forceProcessing", Boolean.TRUE)));
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return null;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        assertFalse(handler.isForceProcessing(processing));
        assertTrue(handler.isForceProcessing(forceProcessing));

    }

    @Test
    public void testHandleInternalNoAuditMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(order).getOrderStatus(); will(returnValue(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
            allowing(order).getOrdernum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(Collections.emptyMap()));
            oneOf(order).putValue(with(any(String.class)), with(equal("os.in.progress -> os.completed")), with(I18NModels.AUDITEXPORT));
            oneOf(order).setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.handleInternal(event);

    }

    @Test
    public void testHandleInternalWithAuditMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(order).getOrderStatus(); will(returnValue(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
            allowing(order).getOrdernum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(
                    Collections.singletonMap(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "audit trail"))
            );
            oneOf(order).putValue(with(any(String.class)), with(equal("os.in.progress -> os.completed, audit trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.handleInternal(event);

    }

    @Test
    public void testHandleInternalWithClientMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(order).getOrderStatus(); will(returnValue(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
            allowing(order).getOrdernum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(
                    Collections.singletonMap(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE, "client trail"))
            );
            oneOf(order).putValue(with(any(String.class)), with(equal("os.in.progress -> os.completed, client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).putValue(with(equal("OS_COMPLETED")), with(equal("client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.handleInternal(event);

    }

    @Test
    public void testHandleInternalWithAuditAndClientMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");

        final Map<String, String> params = new HashMap<>();
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "audit trail");
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE, "client trail");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(order).getOrderStatus(); will(returnValue(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
            allowing(order).getOrdernum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(params));
            oneOf(order).putValue(with(any(String.class)), with(equal("os.in.progress -> os.completed, audit trail, client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).putValue(with(equal("OS_COMPLETED")), with(equal("client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.handleInternal(event);

    }

    @Test
    public void testHandleInternalWithAuditAndClientMessageAndUser() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");

        final Map<String, String> params = new HashMap<>();
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "audit trail");
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE, "client trail");
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_USER, "user@test.com");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(order).getOrderStatus(); will(returnValue(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
            allowing(order).getOrdernum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(params));
            oneOf(order).putValue(with(any(String.class)), with(equal("os.in.progress -> os.completed, user@test.com, audit trail, client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).putValue(with(equal("OS_COMPLETED")), with(equal("client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.handleInternal(event);

    }

    @Test
    public void testTransitionOrderNoAuditMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(order).getOrderStatus(); will(returnValue(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
            allowing(order).getOrdernum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(Collections.emptyMap()));
            oneOf(order).putValue(with(any(String.class)), with(equal("os.in.progress -> os.completed")), with(I18NModels.AUDITEXPORT));
            oneOf(order).setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.transition(event, order, CustomerOrder.ORDER_STATUS_COMPLETED);

    }

    @Test
    public void testTransitionOrderWithAuditMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(order).getOrderStatus(); will(returnValue(CustomerOrder.ORDER_STATUS_IN_PROGRESS));
            allowing(order).getOrdernum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(
                    Collections.singletonMap(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "audit trail"))
            );
            oneOf(order).putValue(with(any(String.class)), with(equal("os.in.progress -> os.completed, audit trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).putValue(with(equal("OS_COMPLETED")), with(equal("audit trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.transition(event, order, CustomerOrder.ORDER_STATUS_COMPLETED);

    }

    @Test
    public void testTransitionDeliveryNoAuditMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getDeliveryStatus(); will(returnValue(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS));
            allowing(delivery).getDeliveryNum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(Collections.emptyMap()));
            oneOf(order).putValue(with(any(String.class)), with(equal("ds.shipment.inprogress -> ds.shipped")), with(I18NModels.AUDITEXPORT));
            oneOf(delivery).setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.transition(event, order, delivery, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

    }

    @Test
    public void testTransitionDeliveryWithAuditMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getDeliveryStatus(); will(returnValue(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS));
            allowing(delivery).getDeliveryNum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(
                    Collections.singletonMap(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "audit trail"))
            );
            oneOf(order).putValue(with(any(String.class)), with(equal("ds.shipment.inprogress -> ds.shipped, audit trail")), with(I18NModels.AUDITEXPORT));
            oneOf(delivery).setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.transition(event, order, delivery, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

    }

    @Test
    public void testTransitionDeliveryWithClientMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getDeliveryStatus(); will(returnValue(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS));
            allowing(delivery).getDeliveryNum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(
                    Collections.singletonMap(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE, "client trail"))
            );
            oneOf(order).putValue(with(any(String.class)), with(equal("ds.shipment.inprogress -> ds.shipped, client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).putValue(with(equal("DS_SHIPPED_100000000000-1")), with(equal("client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(delivery).setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.transition(event, order, delivery, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

    }

    @Test
    public void testTransitionDeliveryWithAuditAndClientMessage() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");

        final Map<String, String> params = new HashMap<>();
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "audit trail");
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE, "client trail");

        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getDeliveryStatus(); will(returnValue(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS));
            allowing(delivery).getDeliveryNum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(params));
            oneOf(order).putValue(with(any(String.class)), with(equal("ds.shipment.inprogress -> ds.shipped, audit trail, client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).putValue(with(equal("DS_SHIPPED_100000000000-1")), with(equal("client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(delivery).setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.transition(event, order, delivery, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

    }

    @Test
    public void testTransitionDeliveryWithAuditAndClientMessageAndUser() throws Exception {

        final OrderEvent event = this.context.mock(OrderEvent.class, "event");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");

        final Map<String, String> params = new HashMap<>();
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_AUDIT_MESSAGE, "audit trail");
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_CLIENT_MESSAGE, "client trail");
        params.put(AttributeNamesKeys.CustomerOrder.ORDER_TRANSITION_USER, "user@test.com");


        this.context.checking(new Expectations() {{
            allowing(event).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getDeliveryStatus(); will(returnValue(CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS));
            allowing(delivery).getDeliveryNum(); will(returnValue("100000000000-1"));
            allowing(event).getParams(); will(returnValue(params));
            oneOf(order).putValue(with(any(String.class)), with(equal("ds.shipment.inprogress -> ds.shipped, user@test.com, audit trail, client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(order).putValue(with(equal("DS_SHIPPED_100000000000-1")), with(equal("client trail")), with(I18NModels.AUDITEXPORT));
            oneOf(delivery).setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        }});

        final AbstractOrderEventHandlerImpl handler = new AbstractOrderEventHandlerImpl() {
            @Override
            protected String getTransitionTarget(final OrderEvent orderEvent) {
                return CustomerOrder.ORDER_STATUS_COMPLETED;
            }

            @Override
            public boolean handle(final OrderEvent orderEvent) throws OrderException {
                return false;
            }
        };

        handler.transition(event, order, delivery, CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);

    }
}