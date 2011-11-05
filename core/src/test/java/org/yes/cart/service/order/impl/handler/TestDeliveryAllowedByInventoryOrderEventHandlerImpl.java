package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.order.impl.TestOrderAssemblerImpl;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestDeliveryAllowedByInventoryOrderEventHandlerImpl extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private DeliveryAllowedByInventoryOrderEventHandlerImpl handler;
    private SkuWarehouseService skuWarehouseService;

    @Before
    public void setUp() throws Exception {
        handler = (DeliveryAllowedByInventoryOrderEventHandlerImpl) ctx.getBean("deliveryAllowedByInventoryOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
        skuWarehouseService = (SkuWarehouseService) ctx.getBean("skuWarehouseService");
    }

    // TODO fix to not depend on order or running
    @Test
    public void testHandle() {
        final Customer customer = TestOrderAssemblerImpl.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        CustomerOrderDelivery delivery = customerOrder.getDelivery().iterator().next();
        //initial 15120 has 9 items on 1 warehouse without reservation - pk 30
        //initial 15121 has 1 item  on 1 warehouse without reservation - pk 31
        SkuWarehouse skuWarehouse = skuWarehouseService.getById(31);
//        skuWarehouse.setReserved(BigDecimal.ONE);
//        skuWarehouseService.update(skuWarehouse);
        assertTrue(handler.handle(new OrderEventImpl("", customerOrder, delivery)));
        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("7.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED, delivery.getDeliveryStatus());
        //The equal order can not pefrorm transition , because 1 item on CC_TEST2 sku reserved
        customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        delivery = customerOrder.getDelivery().iterator().next();
        assertFalse(handler.handle(new OrderEventImpl("", customerOrder, delivery)));
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT, delivery.getDeliveryStatus());
        // update qty
        skuWarehouse = skuWarehouseService.getById(31);
        skuWarehouse.setQuantity(new BigDecimal("2"));
        skuWarehouseService.update(skuWarehouse);
        //delivery, than not pass before, now can perform transition
        assertTrue(handler.handle(new OrderEventImpl("", customerOrder, delivery)));
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED, delivery.getDeliveryStatus());
    }
}
