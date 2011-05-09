package org.yes.cart.service.order.impl.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54 */
public class PendingOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService = null;
    private PendingOrderEventHandlerImpl handler = null;
    private CustomerOrderPaymentService customerOrderPaymentService = null;
    private WarehouseService warehouseService;
    private ProductSkuService productSkuService;
    private SkuWarehouseService skuWarehouseService;

    @Before
    public void setUp() throws Exception {
        super.setUp(new String[]{"testApplicationContext.xml", "core-aspects.xml"});


        handler = (PendingOrderEventHandlerImpl) ctx.getBean("pendingOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx.getBean(ServiceSpringKeys.ORDER_PAYMENT_SERICE);

        productSkuService = (ProductSkuService) ctx.getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        skuWarehouseService = (SkuWarehouseService) ctx.getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        warehouseService = (WarehouseService) ctx.getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
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
     * Test with ok payment and offline payment gw
     */
    @Test
    public void testHandle0_0() throws Exception {

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());


        customerOrder.setPgLabel("courierPaymentGatewayLabel");
        orderService.update(customerOrder);

        assertTrue(handler.handle(
                new OrderEventImpl(
                        "", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP
                )
        ));

        final Warehouse warehouse = warehouseService.getById(1);

        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku
        );
        assertEquals(new BigDecimal("9.00"), qty.getFirst());
        assertEquals(new BigDecimal("2.00"), qty.getSecond());


        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku
        );
        assertEquals(new BigDecimal("1.00"), qty.getFirst());
        assertEquals(new BigDecimal("1.00"), qty.getSecond());

        for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED,
                    delivery.getDeliveryStatus());

        }

        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertEquals(0, rezList.size());
        //CustomerOrderPayment customerOrderPayment = rezList.get(0);
        //assertEquals(Payment.PAYMENT_STATUS_OK, customerOrderPayment.getPaymentProcessorResult());


    }

    /**
     * Test with ok payment and online payment gw
     */
    @Test
    public void testHandle0_1() throws Exception {

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());


        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);

        assertTrue(handler.handle(
                new OrderEventImpl(
                        "", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP
                )
        ));

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

        for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED,
                    delivery.getDeliveryStatus());

        }

        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertEquals(1, rezList.size());
        CustomerOrderPayment customerOrderPayment = rezList.get(0);
        assertEquals(Payment.PAYMENT_STATUS_OK, customerOrderPayment.getPaymentProcessorResult());


    }

    /**
     * Test with failed payment
     */
    @Test
    public void testHandle1_1() throws Exception {

        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, new PaymentGatewayParameterEntity());

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);

        assertTrue(handler.handle(
                new OrderEventImpl(
                        "", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP
                )
        ));


        final Warehouse warehouse = warehouseService.getById(1);

        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku
        );
        assertEquals(new BigDecimal("9.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());


        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku
        );
        assertEquals(new BigDecimal("1.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());

        for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION,
                    delivery.getDeliveryStatus());

        }

        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertEquals(1, rezList.size());
        CustomerOrderPayment customerOrderPayment = rezList.get(0);
        assertEquals(Payment.PAYMENT_STATUS_FAILED, customerOrderPayment.getPaymentProcessorResult());

        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);


    }

}
