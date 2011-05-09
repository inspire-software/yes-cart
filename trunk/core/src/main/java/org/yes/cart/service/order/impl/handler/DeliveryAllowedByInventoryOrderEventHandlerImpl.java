package org.yes.cart.service.order.impl.handler;

import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAllowedByInventoryOrderEventHandlerImpl
        extends ProcessAllocationOrderEventHandlerImpl 
        implements OrderEventHandler {

    /**
     * Construct transition
     * @param warehouseService    warehouse service
     * @param skuWarehouseService sku on warehouse service to change quantity
     */
    public DeliveryAllowedByInventoryOrderEventHandlerImpl(final WarehouseService warehouseService,
                                                           final SkuWarehouseService skuWarehouseService) {
        super(warehouseService, skuWarehouseService);
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        final List<Warehouse> warehouses = getWarehouseService().findByShopId(orderEvent.getCustomerOrder().getShop().getShopId());
        final CustomerOrderDelivery orderDelivery =  orderEvent.getCustomerOrderDelivery();
        for (CustomerOrderDeliveryDet det : orderDelivery.getDetail()) {

            if (!det.getSku().getProduct().getProducttype().isDigital()) {
                
                final Pair<BigDecimal, BigDecimal> qtyPair = getSkuWarehouseService().getQuantity(
                        warehouses,
                        det.getSku()
                );
                if (MoneyUtils.isFirstBiggerThanSecond(
                        qtyPair.getSecond().add(det.getQty()),
                        qtyPair.getFirst())) {
                    return false; //reserved plus to reserve biger, than on warehouses
                }

            }

        }
        return super.handle(orderEvent);
    }




}
