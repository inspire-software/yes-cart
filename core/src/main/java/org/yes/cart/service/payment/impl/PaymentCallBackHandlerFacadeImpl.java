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
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderException;
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

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

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

        LOG.info("Order guid to handle at call back handler is " + orderGuid);

        if (StringUtils.isNotBlank(orderGuid)) {

            final CustomerOrder order = customerOrderService.findByGuid(orderGuid);

            if (order == null) {

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Can not get order with guid " + orderGuid);
                }

            } else {

                if (CustomerOrder.ORDER_STATUS_NONE.endsWith(order.getOrderStatus())) {

                    boolean paymentWasOk = getPaymentGateway(paymentGatewayLabel).isSuccess(parameters);

                    OrderEvent orderEvent = new OrderEventImpl(
                            paymentWasOk ? OrderStateManager.EVT_PENDING : OrderStateManager.EVT_CANCEL,
                            order,
                            null,
                            parameters
                    );

                    boolean rez = orderStateManager.fireTransition(orderEvent);

                    LOG.info("Order state transition performed for " + orderGuid + " . Result is " + rez);

                    customerOrderService.update(order);

                } else {
                        LOG.warn("Order with guid " + orderGuid + " not in " + CustomerOrder.ORDER_STATUS_NONE
                                + " state, but " + order.getOrderStatus());
                }

            }

        }
    }

    private String getOrderGuid(final Map privateCallBackParameters, final String paymentGatewayLabel) {
        final PaymentGatewayExternalForm paymentGateway = getPaymentGateway(paymentGatewayLabel);
        final String orderGuid = paymentGateway.restoreOrderGuid(privateCallBackParameters);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Get order guid " + orderGuid + "  from http request with "
                    + paymentGatewayLabel + " payment gateway.");
        }
        return orderGuid;
    }

    private PaymentGatewayExternalForm getPaymentGateway(String paymentGatewayLabel) {
        return (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(paymentGatewayLabel);
    }
}
