package org.yes.cart.service.order.impl.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProcessAllocationOrderEventHandlerImplTest  extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService = null;
    private ProcessAllocationOrderEventHandlerImpl handler = null;
    private CustomerOrderPaymentService customerOrderPaymentService = null;
    private WarehouseService warehouseService;
    private ProductSkuService productSkuService;
    private SkuWarehouseService skuWarehouseService;


    @Before
    public void setUp() throws Exception {
        super.setUp(new String[]{"testApplicationContext.xml", "core-aspects.xml"});


        handler = (ProcessAllocationOrderEventHandlerImpl) ctx.getBean("processAllocationOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx.getBean("customerOrderPaymentService");


        productSkuService = (ProductSkuService)  ctx.getBean("productSkuService");
        skuWarehouseService = (SkuWarehouseService) ctx.getBean("skuWarehouseService");
        warehouseService = (WarehouseService)  ctx.getBean("warehouseService");
    }

    @After
    public void tearDown() {
        orderService = null;
        handler = null;
        customerOrderPaymentService = null;

        warehouseService = null;
        productSkuService = null;
        skuWarehouseService = null;

        super.tearDown();
    }

    /**
     * Test with ok payment
     */
    @Test
    public void testHandle() {

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);

        CustomerOrderDelivery customerOrderDelivery = customerOrder.getDelivery().iterator().next();

        assertTrue(handler.handle( new OrderEventImpl("",  customerOrder,  customerOrderDelivery  ) ));

        final Warehouse warehouse = warehouseService.getById(1);

        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{ add(warehouse); }},
                sku
        );
        assertEquals(new BigDecimal("7.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());


        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{ add(warehouse); }},
                sku
        );
        assertEquals(new BigDecimal("0.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());



        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED,
                customerOrderDelivery.getDeliveryStatus());

    }
}
