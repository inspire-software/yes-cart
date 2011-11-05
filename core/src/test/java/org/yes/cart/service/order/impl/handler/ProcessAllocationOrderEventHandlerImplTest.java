package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.impl.TestOrderAssemblerImpl;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProcessAllocationOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private ProcessAllocationOrderEventHandlerImpl handler;
    private WarehouseService warehouseService;
    private ProductSkuService productSkuService;
    private SkuWarehouseService skuWarehouseService;

    @Before
    public void setUp() throws Exception {
        handler = (ProcessAllocationOrderEventHandlerImpl) ctx.getBean("processAllocationOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
        productSkuService = (ProductSkuService) ctx.getBean("productSkuService");
        skuWarehouseService = (SkuWarehouseService) ctx.getBean("skuWarehouseService");
        warehouseService = (WarehouseService) ctx.getBean("warehouseService");
    }

    // TODO fix to not depend on order or running
    @Test
    public void testHandle() {
        final Customer customer = TestOrderAssemblerImpl.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());
        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);
        CustomerOrderDelivery customerOrderDelivery = customerOrder.getDelivery().iterator().next();
        assertTrue(handler.handle(new OrderEventImpl("", customerOrder, customerOrderDelivery)));
        final Warehouse warehouse = warehouseService.getById(1);
        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku
        );
        assertEquals(new BigDecimal("7.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku
        );
        assertEquals(new BigDecimal("0.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED,
                customerOrderDelivery.getDeliveryStatus());
    }
}
