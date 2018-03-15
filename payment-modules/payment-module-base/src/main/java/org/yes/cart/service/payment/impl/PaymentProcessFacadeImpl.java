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

package org.yes.cart.service.payment.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentProcessFacade;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Map;

/**
 * Payment process facade implementation.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 09/11/11
 * Time: 10:18
 */
public class PaymentProcessFacadeImpl implements PaymentProcessFacade, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentProcessFacadeImpl.class);

    private final CustomerOrderService customerOrderService;

    private ApplicationContext applicationContext;

    private OrderStateManager orderStateManager;

    /**
     * Construct payment process facade.
     * @param customerOrderService order service
     */
    public PaymentProcessFacadeImpl(final CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    /**
     * {@inheritDoc}
     */
    public boolean pay(final ShoppingCart shoppingCart, final Map paymentParameter) throws OrderException {


        final OrderEvent orderEvent = new OrderEventImpl(
                OrderStateManager.EVT_PENDING,
                customerOrderService.findByReference(shoppingCart.getGuid()),
                null,
                paymentParameter
        );
        final CustomerOrder order = orderEvent.getCustomerOrder();
        final String orderNumber = order.getOrdernum();

        if (getOrderStateManager().fireTransition(orderEvent)
                && !CustomerOrder.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {

            LOG.info("PaymentProcessFacadeImpl#pay payment(s) for order {} was successful", orderNumber);
            customerOrderService.update(order);
            return true;
        } else {
            LOG.info("PaymentProcessFacadeImpl#pay payment(s) for order {} has failed", orderNumber);
            customerOrderService.update(order);
            return false;

        }

    }


    private OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = applicationContext.getBean("orderStateManager", OrderStateManager.class);
        }
        return orderStateManager;
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
