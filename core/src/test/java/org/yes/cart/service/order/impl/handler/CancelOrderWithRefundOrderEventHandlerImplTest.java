package org.yes.cart.service.order.impl.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.Collections;

/* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CancelOrderWithRefundOrderEventHandlerImplTest    extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService = null;
    private ShipmentCompleteOrderEventHandlerImpl shipmentCompleteHandler = null;
    private PendingOrderEventHandlerImpl pendingHandler = null;
    private SkuWarehouseService skuWarehouseService = null;
    private CustomerOrderPaymentService customerOrderPaymentService = null;
    private CancelOrderWithRefundOrderEventHandlerImpl handler = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();


        shipmentCompleteHandler = (ShipmentCompleteOrderEventHandlerImpl) ctx.getBean("shipmentCompleteOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
        skuWarehouseService = (SkuWarehouseService) ctx.getBean("skuWarehouseService");
        customerOrderPaymentService =  (CustomerOrderPaymentService)  ctx.getBean("customerOrderPaymentService");
        pendingHandler = (PendingOrderEventHandlerImpl) ctx.getBean("pendingOrderEventHandler");
        handler = (CancelOrderWithRefundOrderEventHandlerImpl) ctx.getBean("cancelOrderWithRefundOrderEventHandler");
    }

    @After
    public void tearDown() {
        orderService = null;
        shipmentCompleteHandler = null;
        skuWarehouseService = null;
        customerOrderPaymentService = null;
        pendingHandler = null;
        handler = null;
        super.tearDown();
    }

    /**
     * Test on order , that not completed.
     */
    @Test
    public void testHandle0() throws Exception {

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);

        assertTrue(pendingHandler.handle( new OrderEventImpl( "",  customerOrder,  null,  Collections.EMPTY_MAP  ) ));


        SkuWarehouse skuWarehouse = skuWarehouseService.getById(31);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("0.00"));
        skuWarehouseService.update(skuWarehouse);

        skuWarehouse = skuWarehouseService.getById(30);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("7.00"));
        skuWarehouseService.update(skuWarehouse);


        assertTrue(handler.handle( new OrderEventImpl( "",  customerOrder,  null,  null  ) ));

        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("1.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));

        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("9.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        
        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

    /**
     * Test on order , that not completed.
     */
    @Test
    public void testHandle1()  throws Exception  {

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);

        assertTrue(pendingHandler.handle( new OrderEventImpl( "",  customerOrder,  null,   Collections.EMPTY_MAP  ) ));

        customerOrder.getDelivery().iterator().next().setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED);
        orderService.update(customerOrder);
        customerOrder = orderService.getById(customerOrder.getCustomerorderId());
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED,
                customerOrder.getDelivery().iterator().next().getDeliveryStatus());


        SkuWarehouse skuWarehouse = skuWarehouseService.getById(31);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("0.00"));
        skuWarehouseService.update(skuWarehouse);

        skuWarehouse = skuWarehouseService.getById(30);
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouse.setQuantity(new BigDecimal("7.00"));
        skuWarehouseService.update(skuWarehouse);


        assertTrue(handler.handle( new OrderEventImpl( "",  customerOrder,  null,  null  ) ));

        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("1.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));

        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("9.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));

        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());

    }

}
