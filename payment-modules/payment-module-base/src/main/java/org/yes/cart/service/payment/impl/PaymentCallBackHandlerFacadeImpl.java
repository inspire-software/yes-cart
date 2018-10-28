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
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
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
import org.yes.cart.util.MoneyUtils;

import java.util.LinkedHashMap;
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
    @Override
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
    @Override
    public void handlePaymentCallback(final PaymentGatewayCallback callback, final boolean forceProcessing) throws OrderException {

        final Map parameters = callback.getParameterMap();

        final CallbackAware.Callback pgCallback = convertToPgCallback(callback, forceProcessing);

        if (pgCallback == null
                || pgCallback.getOperation() == null
                || pgCallback.getOperation() == CallbackAware.CallbackOperation.INVALID
                || StringUtils.isBlank(pgCallback.getOrderGuid())) {
            LOG.warn("Unable to resolve payment gateway callback. " +
                     "This is abnormal behaviour and it is recommended to enable DEBUG log for " +
                     "'org.yes.cart.web.filter.payment', 'org.yes.cart.payment.impl' and 'org.yes.cart.web.page.payment.callback' and review " +
                     "allowed IPs for PG for shop. Also check that hash signatures are correctly configured. Callback: {}",
                     callback.getPaymentGatewayCallbackId());
        } else if (pgCallback.getOperation() == CallbackAware.CallbackOperation.INFO) {
            LOG.info("Receive INFO callback from payment gateway. It will be ignored. Callback: {}",
                    callback.getPaymentGatewayCallbackId());
        } else {

            LOG.info("Callback to handle is {}. Callback: {}", pgCallback, callback.getPaymentGatewayCallbackId());

            final CustomerOrder order = customerOrderService.findByReference(pgCallback.getOrderGuid());

            if (order == null) {

                LOG.warn("Cannot resolve order with {}. Callback: {}", pgCallback, callback.getPaymentGatewayCallbackId());
                return;
            }

            LOG.info("Processing callback for order {}. Callback: {}", pgCallback, callback.getPaymentGatewayCallbackId());

            if (CustomerOrder.ORDER_STATUS_NONE.endsWith(order.getOrderStatus())) {

                // New order flow (this should be AUTH or AUTH_CAPTURE)
                if (pgCallback.getOperation() == CallbackAware.CallbackOperation.PAYMENT) {
                    try {
                        handleNewOrderToPending(parameters, order);
                        finaliseCallback(callback, "New order to pending: " + pgCallback.getOrderGuid());
                    } catch (OrderItemAllocationException oiae) {
                        handleNewOrderToCancelWithRefund(parameters,
                                customerOrderService.findByReference(pgCallback.getOrderGuid())); // Need fresh order
                        finaliseCallback(callback, "New order to cancelled, because there was no stock: " + pgCallback.getOrderGuid());
                    }
                } else {
                    LOG.error("Non-payment callback invoked on temporary order {}. Callback: {}", pgCallback, callback.getPaymentGatewayCallbackId());
                }

            } else if (CustomerOrder.ORDER_STATUS_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

                if (pgCallback.getOperation() == CallbackAware.CallbackOperation.PAYMENT) {
                    handleWaitingPaymentToPending(parameters, order);
                    finaliseCallback(callback, "New order to waiting payment: " + pgCallback.getOrderGuid());
                } else {
                    LOG.warn("Non-payment callback invoked on waiting-payment order {}. Callback: {}", pgCallback, callback.getPaymentGatewayCallbackId());
                }

            } else if (CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.endsWith(order.getOrderStatus()) ||
                    CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

                if (pgCallback.getOperation() == CallbackAware.CallbackOperation.REFUND) {
                    final Map params = new LinkedHashMap(parameters);
                    params.put(CallbackAware.CALLBACK_PARAM, pgCallback);
                    handleWaitingRefundToRefunded(params, order);
                    finaliseCallback(callback, "Waiting payment to pending: " + pgCallback.getOrderGuid());
                } else {
                    LOG.warn("Non-refund callback invoked on waiting-refund order {}. Callback: {}", pgCallback, callback.getPaymentGatewayCallbackId());
                }

            } else {

                if (pgCallback.getOperation() == CallbackAware.CallbackOperation.PAYMENT) {

                    // 1. Payment callback should not get processes further because we should have captured all funds by now
                    // for AuthCapture payment gateways.

                    // 2. Authorise per shipment calls are made by specialized payment gateways that run direct calls with
                    // synchronous response, so processing those callbacks is unnecessary (in fact it may lead to capturing
                    // payments more than once)

                    LOG.warn("Cannot handle payment callback {} for order in state {}. " +
                             "This is probably follow up capture callback that was already accounted for. Callback: {}",
                            order.getOrderStatus(), pgCallback, callback.getPaymentGatewayCallbackId());

                } else if (pgCallback.getOperation() == CallbackAware.CallbackOperation.REFUND) {

                    // This is a special case which could happen at any point in time as refunds could be triggered from
                    // customer payment gateway accounts directly, thus bypassing the YC flow (e.g. PayPal refund from
                    // paypal website would generate a callback for capture but the order may well still be in processing
                    // or even complete state)

                    if (MoneyUtils.isPositive(pgCallback.getAmount())) {
                        final Map params = new LinkedHashMap(parameters);
                        params.put(CallbackAware.CALLBACK_PARAM, pgCallback);
                        handlePrematureRefund(params, order);
                        finaliseCallback(callback, "Processed refund callback: " + pgCallback.getOrderGuid());
                    } else {
                        LOG.warn("Cannot handle zero-refund callback {} for order in state {}. Callback: {}",
                                order.getOrderStatus(), pgCallback, callback.getPaymentGatewayCallbackId());
                    }

                } else {

                    // This is probably some notification from payment gateway that can be ignored.

                    LOG.warn("Cannot handle info callback {} for order in state {}. Callback: {}",
                            order.getOrderStatus(), pgCallback, callback.getPaymentGatewayCallbackId());

                }


            }

        }
    }

    private void finaliseCallback(final PaymentGatewayCallback callback, final String msg) {

        callback.setProcessed(true);
        paymentGatewayCallbackService.update(callback);

        LOG.info("Callback {} processed. {}", callback.getPaymentGatewayCallbackId(), msg);

    }

    private void handleNewOrderToCancelWithRefund(final Map parameters, final CustomerOrder order) throws OrderException {

        if (CustomerOrder.ORDER_STATUS_NONE.endsWith(order.getOrderStatus())) {

            // Cancel with refund event handler will cancel reservation and create refund record if necessary
            OrderEvent orderEvent = new OrderEventImpl(
                    OrderStateManager.EVT_CANCEL_NEW_WITH_REFUND,
                    order,
                    null,
                    parameters
            );

            // For cancellation of new orders we only need to refund potentially successful payments, no reservations were made yet
            boolean rez = getOrderStateManager().fireTransition(orderEvent);

            LOG.info("Order state transition performed for {} . Result is {}", order.getOrdernum(), rez);

            customerOrderService.update(order);

        } else {
            LOG.warn("Order with guid {} not in NONE state, but {}", order.getOrdernum(), order.getOrderStatus());
        }

    }

    private void handleNewOrderToPending(final Map parameters, final CustomerOrder order) throws OrderException {

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

            LOG.info("Order state transition performed for {} . Result is {}", order.getOrdernum(), rez);

            customerOrderService.update(order);

        } else {

            LOG.warn("Order with guid {} not in NONE state, but {}", order.getOrdernum(), order.getOrderStatus());

        }

    }


    private void handleWaitingPaymentToPending(final Map parameters, final CustomerOrder order) throws OrderException {

        if (CustomerOrder.ORDER_STATUS_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

            // Another call to confirm payment that was in processing
            OrderEvent orderEvent = new OrderEventImpl(
                    OrderStateManager.EVT_PAYMENT_PROCESSED,
                    order,
                    null,
                    parameters
            );

            boolean rez = getOrderStateManager().fireTransition(orderEvent);

            LOG.info("Order state transition performed for {} . Result is {}", order.getOrdernum(), rez);

            customerOrderService.update(order);


        } else {

            LOG.warn("Order with guid {} not in WAITING_PAYMENT state, but {}", order.getOrdernum(), order.getOrderStatus());

        }

    }


    private void handleWaitingRefundToRefunded(final Map parameters, final CustomerOrder order) throws OrderException {

        if (CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.endsWith(order.getOrderStatus()) ||
                CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.endsWith(order.getOrderStatus())) {

            // Another call to confirm refund that was in processing
            OrderEvent orderEvent = new OrderEventImpl(
                    OrderStateManager.EVT_REFUND_PROCESSED,
                    order,
                    null,
                    parameters
            );

            boolean rez = getOrderStateManager().fireTransition(orderEvent);

            LOG.info("Order state transition performed for {} . Result is {}", order.getOrdernum(), rez);

            customerOrderService.update(order);


        } else {

            LOG.warn("Order with guid {} not in CANCELLED_WAITING_PAYMENT or RETURNED_WAITING_PAYMENT state, but {}", order.getOrdernum(), order.getOrderStatus());

        }

    }


    private void handlePrematureRefund(final Map parameters, final CustomerOrder order) throws OrderException {

        // Potentially external call trigger from refund made outside of YC
        OrderEvent orderEvent = new OrderEventImpl(
                OrderStateManager.EVT_REFUND_EXTERNAL,
                order,
                null,
                parameters
        );

        boolean rez = getOrderStateManager().fireTransition(orderEvent);

        LOG.info("Order state transition performed for {} . Result is {}", order.getOrdernum(), rez);

        customerOrderService.update(order);

    }


    private CallbackAware.Callback convertToPgCallback(final PaymentGatewayCallback callback, final boolean forceProcessing) {
        final Map privateCallBackParameters = callback.getParameterMap();
        final String paymentGatewayLabel = callback.getLabel();
        final PaymentGateway paymentGateway = getPaymentGateway(paymentGatewayLabel, callback.getShopCode());
        if (paymentGateway == null) {
            LOG.error("{} payment gateway cannot be resolved. Is payment gateway enabled?\n{}",
                        paymentGatewayLabel, HttpParamsUtils.stringify("CALLBACK:\n", privateCallBackParameters));
            return null;
        } else if (!(paymentGateway instanceof CallbackAware)) {
            LOG.error("{} payment gateway is not callback-aware. Ignoring callback.\n{}",
                    paymentGatewayLabel, HttpParamsUtils.stringify("CALLBACK:\n", privateCallBackParameters));
            return null;
        }
        final CallbackAware.Callback pgCallback = ((CallbackAware) paymentGateway).convertToCallback(privateCallBackParameters, forceProcessing);
        LOG.debug("Resolved callback {} from http request with {} payment gateway.", pgCallback, paymentGatewayLabel);
        return pgCallback;
    }

    private PaymentGateway getPaymentGateway(final String paymentGatewayLabel, final String shopCode) {
        if ("DEFAULT".equals(shopCode)) {
            throw new RuntimeException("Payment gateway URL must be configured for shop specific URL's");
        }
        return paymentModulesManager.getPaymentGateway(paymentGatewayLabel, shopCode);
    }



    private OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = applicationContext.getBean("orderStateManager", OrderStateManager.class);
        }
        return orderStateManager;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
