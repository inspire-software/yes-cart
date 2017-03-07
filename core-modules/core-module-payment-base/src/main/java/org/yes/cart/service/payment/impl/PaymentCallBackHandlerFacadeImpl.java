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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayCallbackEntity;
import org.yes.cart.payment.service.PaymentModuleGenericService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.util.HttpParamsUtils;

import java.util.Map;
import java.util.UUID;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentCallBackHandlerFacadeImpl implements PaymentCallBackHandlerFacade, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentCallBackHandlerFacadeImpl.class);

    private final PaymentModulesManager paymentModulesManager;
    private final CustomerOrderService customerOrderService;
    private final PaymentModuleGenericService<PaymentGatewayCallback> paymentGatewayCallbackService;

    private ApplicationContext applicationContext;

    private OrderStateManager orderStateManager;

    /**
     * Construct service.
     *
     * @param paymentModulesManager Payment modules manager to get the order number from request parameters.
     * @param customerOrderService  to get order
     * @param paymentGatewayCallbackService callback service
     */
    public PaymentCallBackHandlerFacadeImpl(final PaymentModulesManager paymentModulesManager,
                                            final CustomerOrderService customerOrderService,
                                            final PaymentModuleGenericService<PaymentGatewayCallback> paymentGatewayCallbackService) {
        this.paymentModulesManager = paymentModulesManager;
        this.customerOrderService = customerOrderService;
        this.paymentGatewayCallbackService = paymentGatewayCallbackService;
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayCallback registerCallback(final Map parameters,
                                                   final String paymentGatewayLabel,
                                                   final String shopCode,
                                                   final String requestDump) {

        final PaymentGatewayCallback callback = new PaymentGatewayCallbackEntity();
        callback.setLabel(paymentGatewayLabel);
        callback.setProcessed(false);
        callback.setParameterMap(parameters);
        callback.setRequestDump(requestDump);
        callback.setShopCode(shopCode);
        callback.setGuid(UUID.randomUUID().toString());

        return this.paymentGatewayCallbackService.create(callback);

    }

    /**
     * {@inheritDoc}
     */
    public void handlePaymentCallback(final PaymentGatewayCallback callback) throws OrderException {

        final Map parameters = callback.getParameterMap();

        final String orderGuid = getOrderGuid(callback);

        if (StringUtils.isNotBlank(orderGuid)) {

            LOG.info("Order guid to handle at call back handler is {}. Callback: {}", orderGuid, callback.getPaymentGatewayCallbackId());

            final CustomerOrder order = customerOrderService.findByReference(orderGuid);

            if (order == null) {

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Can not get order with guid {}. Callback: {}", orderGuid, callback.getPaymentGatewayCallbackId());
                }
                return;
            }

            if (LOG.isInfoEnabled()) {
                LOG.warn("Processing callback for order with guid {}. Callback: {}", orderGuid, callback.getPaymentGatewayCallbackId());
            }

            if (CustomerOrder.ORDER_STATUS_NONE.endsWith(order.getOrderStatus())) {

                // New order flow (this should AUTH or AUTH_CAPTURE)

                try {
                    handleNewOrderToPending(parameters, orderGuid);
                    finaliseCallback(callback, "New order to pending");
                } catch (OrderItemAllocationException oiae) {
                    handleNewOrderToCancelWithRefund(parameters, orderGuid);
                    finaliseCallback(callback, "New order to cancelled, because there was no stock");
                }

            } else if (CustomerOrder.ORDER_STATUS_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

                handleWaitingPaymentToPending(parameters, orderGuid);
                finaliseCallback(callback, "New order to waiting payment");

            } else if (CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.endsWith(order.getOrderStatus()) ||
                    CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

                handleWaitingRefundToPending(parameters, orderGuid);
                finaliseCallback(callback, "Waiting payment to pending");

            } else {

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Can not handle state {} for order with guid {}. Callback: {}",
                            new Object[] { order.getOrderStatus(), orderGuid, callback.getPaymentGatewayCallbackId() });
                }

            }

        } else {
            LOG.warn("Order guid to handle at call back handler is NULL or blank. " +
                            "This is abnormal behaviour and it is recommended to enable DEBUG log for " +
                            "SHOPXX.org.yes.cart.web.filter.payment and SHOPXX.org.yes.cart.payment.impl and review " +
                            "allowed IPs for PG for shop. Also check that hash signatures are correctly configured. Callback: {}",
                    callback.getPaymentGatewayCallbackId());
        }
    }

    private void finaliseCallback(final PaymentGatewayCallback callback, final String msg) {

        callback.setProcessed(true);
        paymentGatewayCallbackService.update(callback);
        if (LOG.isInfoEnabled()) {
            LOG.info("Callback {} processed. {}", callback.getPaymentGatewayCallbackId(), msg);
        }

    }

    private void handleNewOrderToCancelWithRefund(final Map parameters, final String orderGuid) throws OrderException {

        // Need to get fresh order instance from db so that we have a clean object
        final CustomerOrder order = customerOrderService.findByReference(orderGuid);

        if (order == null) {

            if (LOG.isWarnEnabled()) {
                LOG.warn("Can not get order with guid {}", orderGuid);
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
                boolean rez = getOrderStateManager().fireTransition(orderEvent);

                LOG.info("Order state transition performed for {} . Result is {}", orderGuid, rez);

                customerOrderService.update(order);

            } else {
                LOG.warn("Order with guid {} not in NONE state, but {}", orderGuid, order.getOrderStatus());
            }

        }
    }

    private void handleNewOrderToPending(final Map parameters, final String orderGuid) throws OrderException {

        final CustomerOrder order = customerOrderService.findByReference(orderGuid);

        if (order == null) {

            if (LOG.isWarnEnabled()) {
                LOG.warn("Can not get order with guid {}", orderGuid);
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
                boolean rez = getOrderStateManager().fireTransition(orderEvent);

                LOG.info("Order state transition performed for {} . Result is {}", orderGuid, rez);

                customerOrderService.update(order);

            } else {

                LOG.warn("Order with guid {} not in NONE state, but {}", orderGuid, order.getOrderStatus());

            }

        }
    }


    private void handleWaitingPaymentToPending(final Map parameters, final String orderGuid) throws OrderException {

        final CustomerOrder order = customerOrderService.findByReference(orderGuid);

        if (order == null) {

            if (LOG.isWarnEnabled()) {
                LOG.warn("Can not get order with guid {}", orderGuid);
            }

        } else {

            if (CustomerOrder.ORDER_STATUS_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

                // Another call possibly to confirm payment that was processing
                OrderEvent orderEvent = new OrderEventImpl(
                        OrderStateManager.EVT_PAYMENT_PROCESSED,
                        order,
                        null,
                        parameters
                );

                boolean rez = getOrderStateManager().fireTransition(orderEvent);

                LOG.info("Order state transition performed for {} . Result is {}", orderGuid, rez);

                customerOrderService.update(order);


            } else {

                LOG.warn("Order with guid {} not in WAITING_PAYMENT state, but {}", orderGuid, order.getOrderStatus());

            }

        }
    }


    private void handleWaitingRefundToPending(final Map parameters, final String orderGuid) throws OrderException {

        final CustomerOrder order = customerOrderService.findByReference(orderGuid);

        if (order == null) {

            if (LOG.isWarnEnabled()) {
                LOG.warn("Can not get order with guid {}", orderGuid);
            }

        } else {

            if (CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.endsWith(order.getOrderStatus()) ||
                    CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

                // Another call possibly to confirm payment that was processing
                OrderEvent orderEvent = new OrderEventImpl(
                        OrderStateManager.EVT_REFUND_PROCESSED,
                        order,
                        null,
                        parameters
                );

                boolean rez = getOrderStateManager().fireTransition(orderEvent);

                LOG.info("Order state transition performed for {} . Result is {}", orderGuid, rez);

                customerOrderService.update(order);


            } else {

                LOG.warn("Order with guid {} not in CANCELLED_WAITING_PAYMENT or RETURNED_WAITING_PAYMENT state, but {}", orderGuid, order.getOrderStatus());

            }

        }
    }



    private String getOrderGuid(final PaymentGatewayCallback callback) {
        final Map privateCallBackParameters = callback.getParameterMap();
        final String paymentGatewayLabel = callback.getLabel();
        final PaymentGatewayExternalForm paymentGateway = getPaymentGateway(paymentGatewayLabel, callback.getShopCode());
        if (paymentGateway == null) {
            LOG.error("Get order guid from http request with {} payment gateway cannot be resolved. Is payment gateway enabled?\n{}",
                        paymentGatewayLabel, HttpParamsUtils.stringify("CALLBACK:\n", privateCallBackParameters));
            return null;
        }
        final String orderGuid = paymentGateway.restoreOrderGuid(privateCallBackParameters);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Get order guid {}  from http request with {} payment gateway.",
                    orderGuid, paymentGatewayLabel);
        }
        return orderGuid;
    }

    private PaymentGatewayExternalForm getPaymentGateway(final String paymentGatewayLabel, final String shopCode) {
        if ("DEFAULT".equals(shopCode)) {
            throw new RuntimeException("Payment gateway URL must be configured for shop specific URL's");
        }
        return (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(paymentGatewayLabel, shopCode);
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
