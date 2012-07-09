package org.yes.cart.service.order.impl.handler;

import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;

import java.util.Collection;
import java.util.Date;

/**
 * Perform transition from time  wait to inventory wait state.
 * <p/>
 * * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAllowedByTimeoutOrderEventHandlerImpl implements OrderEventHandler {

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {
            final Date now = getCurrentDate();

            final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderEvent.getCustomerOrderDelivery().getDetail();

            for (CustomerOrderDeliveryDet det : deliveryDetails) {
                final ProductSku productSku = det.getSku();
                final Date availableFrom = productSku.getProduct().getAvailablefrom();
                if ((availableFrom != null) && (availableFrom.getTime() > now.getTime())) {
                    return false; // no transition, because need to wait
                }
            }

            orderEvent.getCustomerOrderDelivery().setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT);

            return true;
        }
    }

    private Date getCurrentDate() {
        return new Date(); //TODO v2 time machine
    }


}
