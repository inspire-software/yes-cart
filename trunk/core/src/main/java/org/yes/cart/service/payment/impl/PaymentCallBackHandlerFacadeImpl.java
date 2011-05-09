package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * @param paymentModulesManager Payment modules manager to get the order number from request parameters.
     * @param customerOrderService to get order
     * @param orderStateManager to perform transitions
     */
    public PaymentCallBackHandlerFacadeImpl(final PaymentModulesManager paymentModulesManager,
                                            final CustomerOrderService customerOrderService,
                                            final OrderStateManager orderStateManager) {
        this.paymentModulesManager = paymentModulesManager;
        this.customerOrderService = customerOrderService;
        this.orderStateManager = orderStateManager;
    }

    /** {@inheritDoc} */
    public void handlePaymentCallback(final Map parameters, final String paymentGatewayLabel) {
        final String orderGuid = getOrderGuid(parameters, paymentGatewayLabel);

        if (StringUtils.isNotBlank(orderGuid)) {

            final CustomerOrder order = customerOrderService.findByGuid(orderGuid);

            if (order != null) {

                if (CustomerOrder.ORDER_STATUS_NONE.endsWith(order.getOrderStatus()) ) {

                    OrderEvent orderEvent = new OrderEventImpl(
                            OrderStateManager.EVT_PENDING,
                            order,
                            null,
                            parameters
                    );

                    boolean rez = orderStateManager.fireTransition(orderEvent);

                    LOG.info("Order state transitin performed for " + orderGuid + " . Result is " + rez);

                    customerOrderService.update(order);

                } else {

                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Order with guid " + orderGuid + " not in " + CustomerOrder.ORDER_STATUS_NONE
                                + " state, but " + order.getOrderStatus());
                    }

                }

            } else {

                if (LOG.isWarnEnabled()) {
                    LOG.warn("Can not get order with guid " + orderGuid);
                }

            }

        }
    }

    private String getOrderGuid(final Map privateCallBackParameters, final String paymentGatewayLabel) {
        final PaymentGatewayExternalForm paymentGateway =
                (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(paymentGatewayLabel);
        final String orderGuid = paymentGateway.restoreOrderGuid(privateCallBackParameters);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Get order guid " + orderGuid + "  from http request with "
                    + paymentGatewayLabel + " payment gateway.");
        }
        return orderGuid;
    }
}
