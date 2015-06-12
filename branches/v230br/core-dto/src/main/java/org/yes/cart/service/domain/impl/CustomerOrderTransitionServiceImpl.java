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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerOrderTransitionService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/06/2015
 * Time: 13:48
 */
public class CustomerOrderTransitionServiceImpl implements CustomerOrderTransitionService, ApplicationContextAware {

    private final CustomerOrderService customerOrderService;

    private ApplicationContext applicationContext;

    private OrderStateManager orderStateManager;

    public CustomerOrderTransitionServiceImpl(final CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }


    /**
     * {@inheritDoc}
     */
    public boolean transitionOrder(final String event,
                                   final String orderNumber,
                                   final String deliveryNumber,
                                   final Map params) throws OrderException {

        final CustomerOrder order = customerOrderService.findSingleByCriteria(Restrictions.eq("ordernum", orderNumber));
        if (order == null) {
            return false;
        }

        final CustomerOrderDelivery delivery;
        if (StringUtils.isNotBlank(deliveryNumber)) {
            delivery = order.getCustomerOrderDelivery(deliveryNumber);
        } else {
            delivery = null;
        }

        final Map safe = new HashMap();
        if (params != null) {
            safe.putAll(params);
        }

        if (getOrderStateManager().fireTransition(
                new OrderEventImpl(event, order, delivery, safe))) {
            customerOrderService.update(order);
            return true;
        }

        return false;
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
