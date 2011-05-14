package org.yes.cart.service.order.impl.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.impl.TestPaymentGatewayImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShipmentCompleteOrderEventHandlerImplTest   extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService = null;
    private ShipmentCompleteOrderEventHandlerImpl handler = null;
    private PendingOrderEventHandlerImpl pendingHandler = null;
    private SkuWarehouseService skuWarehouseService = null;
    private CustomerOrderPaymentService customerOrderPaymentService = null;

    private WarehouseService warehouseService;
    private ProductSkuService productSkuService;


    @Before
    public void setUp() throws Exception {
        super.setUp();


        handler = (ShipmentCompleteOrderEventHandlerImpl) ctx.getBean("shipmentCompleteOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
        skuWarehouseService = (SkuWarehouseService) ctx.getBean("skuWarehouseService");
        customerOrderPaymentService =  (CustomerOrderPaymentService)  ctx.getBean("customerOrderPaymentService");
        pendingHandler = (PendingOrderEventHandlerImpl) ctx.getBean("pendingOrderEventHandler");
        productSkuService = (ProductSkuService)  ctx.getBean("productSkuService");
        warehouseService = (WarehouseService)  ctx.getBean("warehouseService");

    }

    @After
    public void tearDown() {
        orderService = null;
        handler = null;
        skuWarehouseService = null;
        customerOrderPaymentService = null;
        pendingHandler = null;
        warehouseService = null;
        productSkuService = null;

        super.tearDown();
    }

    @Test
    public void testHandle() throws Exception {

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);



        SkuWarehouse skuWarehouse ;

        //need to auth before capture
        assertTrue(pendingHandler.handle(
                new OrderEventImpl(
                        "", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP
                )
        ));


        final Warehouse warehouse = warehouseService.getById(1);


       /* //adjust reserved quanity
        SkuWarehouse sw = skuWarehouseService.findByWarehouseSku(warehouse,  productSkuService.getProductSkuBySkuCode("CC_TEST1"));
        sw.setReserved(new BigDecimal("2.00"));
        skuWarehouseService.update(sw);

        sw = skuWarehouseService.findByWarehouseSku(warehouse,  productSkuService.getProductSkuBySkuCode("CC_TEST2"));
        sw.setReserved(new BigDecimal("1.00"));
        skuWarehouseService.update(sw);
         */




        CustomerOrderDelivery delivery = customerOrder.getDelivery().iterator().next();

        // funds not captured and quantity not changed
        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.CAPTURE_FAIL, new PaymentGatewayParameterEntity());

        assertFalse(handler.handle( new OrderEventImpl( "",  customerOrder,   delivery  ) ));

        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));

        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("7.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));


        List<CustomerOrderPayment> rezList =  customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery.getDevileryNum(),
                Payment.PAYMENT_STATUS_FAILED,
                PaymentGateway.CAPTURE);
        assertEquals(1, rezList.size());



        // same operation with ok fund capture

        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.CAPTURE_FAIL, null);
        assertTrue(handler.handle( new OrderEventImpl( "",  customerOrder,   delivery  ) ));
        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("7.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));

        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));

        rezList =  customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery.getDevileryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.CAPTURE);
        assertEquals(1, rezList.size());

        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED, delivery.getDeliveryStatus());






    }

}
