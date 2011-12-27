package org.yes.cart.service.order.impl.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

import java.text.MessageFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShipmentCompleteOrderEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentCompleteOrderEventHandlerImpl.class);


    private final PaymentProcessorFactory paymentProcessorFactory;

    /**
     * Construct shipment complete transition hanler.
     *
     * @param paymentProcessorFactory to get the payment processor
     */
    public ShipmentCompleteOrderEventHandlerImpl(final PaymentProcessorFactory paymentProcessorFactory) {
        this.paymentProcessorFactory = paymentProcessorFactory;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {

            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(orderEvent.getCustomerOrder().getPgLabel());

            final boolean fundCaptured = Payment.PAYMENT_STATUS_OK.equals(
                    paymentProcessor.shipmentComplete(orderEvent.getCustomerOrder(), orderEvent.getCustomerOrderDelivery().getDevileryNum())
            );
            if (fundCaptured) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(MessageFormat.format("Funds captured for delivery {0}", orderEvent.getCustomerOrderDelivery().getDevileryNum()));
                }
                orderEvent.getCustomerOrderDelivery().setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
                for (CustomerOrderDelivery delivery : orderEvent.getCustomerOrder().getDelivery()) {
                    if (!CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED.equals(delivery.getDeliveryStatus())) {
                        orderEvent.getCustomerOrder().setOrderStatus(CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED);
                        return true;
                    }
                }
                orderEvent.getCustomerOrder().setOrderStatus(CustomerOrder.ORDER_STATUS_COMPLETED);
                if (LOG.isInfoEnabled()) {
                    LOG.info(MessageFormat.format("Order {0} completed ", orderEvent.getCustomerOrder().getOrdernum()));
                }
                return true;
            }
            if (LOG.isErrorEnabled()) {
                LOG.error(MessageFormat.format("Funds not captured for delivery {0}", orderEvent.getCustomerOrderDelivery().getDevileryNum()));
            }
            return false;
        }
    }


}
