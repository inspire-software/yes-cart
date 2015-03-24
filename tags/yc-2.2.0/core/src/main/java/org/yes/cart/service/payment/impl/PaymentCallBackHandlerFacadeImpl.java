/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.util.ShopCodeContext;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentCallBackHandlerFacadeImpl implements PaymentCallBackHandlerFacade {

    private final PaymentModulesManager paymentModulesManager;
    private final CustomerOrderService customerOrderService;
    private final OrderStateManager orderStateManager;

    /**
     * Constract service.
     *
     * @param paymentModulesManager Payment modules manager to get the order number from request parameters.
     * @param customerOrderService  to get order
     * @param orderStateManager     to perform transitions
     */
    public PaymentCallBackHandlerFacadeImpl(final PaymentModulesManager paymentModulesManager,
                                            final CustomerOrderService customerOrderService,
                                            final OrderStateManager orderStateManager) {
        this.paymentModulesManager = paymentModulesManager;
        this.customerOrderService = customerOrderService;
        this.orderStateManager = orderStateManager;
    }

    /**
     * {@inheritDoc}
     */
    public void handlePaymentCallback(final Map parameters, final String paymentGatewayLabel) throws OrderException {

        final String orderGuid = getOrderGuid(parameters, paymentGatewayLabel);

        final Logger log = ShopCodeContext.getLog(this);
        log.info("Order guid to handle at call back handler is {}", orderGuid);

        if (StringUtils.isNotBlank(orderGuid)) {

            try {
                handleNewOrderToPending(parameters, orderGuid, log);
            } catch (OrderItemAllocationException oiae) {
                handleNewOrderToCancelWithRefund(parameters, orderGuid, log);
            }

        }
    }

    private void handleNewOrderToCancelWithRefund(final Map parameters, final String orderGuid, final Logger log) throws OrderException {

        final CustomerOrder order = customerOrderService.findByGuid(orderGuid);

        if (order == null) {

            if (log.isWarnEnabled()) {
                log.warn("Can not get order with guid {}", orderGuid);
            }

        } else {

            if (CustomerOrder.ORDER_STATUS_NONE.endsWith(order.getOrderStatus())) {

                // Pending event handler will check for payment and will cancel reservation if required
                OrderEvent orderEvent = new OrderEventImpl(
                        OrderStateManager.EVT_CANCEL_NEW_WITH_REFUND,
                        order,
                        null,
                        parameters
                );

                // For cancellation of new orders we only need to refund potentially successful payments, no reservations were made yet
                boolean rez = orderStateManager.fireTransition(orderEvent);

                log.info("Order state transition performed for {} . Result is {}", orderGuid, rez);

                customerOrderService.update(order);

            } else {
                log.warn("Order with guid {} not in NONE state, but {}", orderGuid, order.getOrderStatus());
            }

        }
    }

    private void handleNewOrderToPending(final Map parameters, final String orderGuid, final Logger log) throws OrderException {

        final CustomerOrder order = customerOrderService.findByGuid(orderGuid);

        if (order == null) {

            if (log.isWarnEnabled()) {
                log.warn("Can not get order with guid {}", orderGuid);
            }

        } else {

            if (CustomerOrder.ORDER_STATUS_NONE.endsWith(order.getOrderStatus())) {

                // Pending event handler will check for payment and will cancel reservation if required
                OrderEvent orderEvent = new OrderEventImpl(
                        OrderStateManager.EVT_PENDING,
                        order,
                        null,
                        parameters
                );

                // Pending may throw exception during reservation, which happens prior payment saving - need another status? and extra flow?
                boolean rez = orderStateManager.fireTransition(orderEvent);

                log.info("Order state transition performed for {} . Result is {}", orderGuid, rez);

                customerOrderService.update(order);

            } else {
                log.warn("Order with guid {} not in NONE state, but {}", orderGuid, order.getOrderStatus());
            }

        }
    }

    private String getOrderGuid(final Map privateCallBackParameters, final String paymentGatewayLabel) {
        final PaymentGatewayExternalForm paymentGateway = getPaymentGateway(paymentGatewayLabel);
        final String orderGuid = paymentGateway.restoreOrderGuid(privateCallBackParameters);
        final Logger log = ShopCodeContext.getLog(this);
        if (log.isDebugEnabled()) {
            log.debug("Get order guid {}  from http request with {} payment gateway.",
                    orderGuid, paymentGatewayLabel);
        }
        return orderGuid;
    }

    private PaymentGatewayExternalForm getPaymentGateway(String paymentGatewayLabel) {
        if ("DEFAULT".equals(ShopCodeContext.getShopCode())) {
            throw new RuntimeException("Payment gateway URL must be configured for shop specific URL's");
        }
        return (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(paymentGatewayLabel, ShopCodeContext.getShopCode());
    }
}
