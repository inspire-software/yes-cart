package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentCallBackHandlerFacadeImpl implements PaymentCallBackHandlerFacade {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentCallBackHandlerFacadeImpl.class);

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
    public void handlePaymentCallback(final Map parameters, final String paymentGatewayLabel) {

        final String orderGuid = getOrderGuid(parameters, paymentGatewayLabel);

        LOG.info("Order guid to handle at call back handler is " + orderGuid);
        System.out.println("Order guid to handle at call back handler is " + orderGuid);

        if (StringUtils.isNotBlank(orderGuid)) {

            final CustomerOrder order = customerOrderService.findByGuid(orderGuid);

            if (order == null) {

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Can not get order with guid " + orderGuid);
                    System.out.println("Can not get order with guid " + orderGuid);
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
                    System.out.println("Order state transition performed for " + orderGuid + " . Result is " + rez);

                    customerOrderService.update(order);

                } else {

                        LOG.warn("Order with guid " + orderGuid + " not in " + CustomerOrder.ORDER_STATUS_NONE
                                + " state, but " + order.getOrderStatus());
                        System.out.println("Order with guid " + orderGuid + " not in " + CustomerOrder.ORDER_STATUS_NONE
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
