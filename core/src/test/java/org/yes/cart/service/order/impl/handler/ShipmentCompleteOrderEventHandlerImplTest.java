/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
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
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.AddSkuToCartEventCommandImpl;
import org.yes.cart.shoppingcart.impl.SetSkuQuantityToCartEventCommandImpl;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShipmentCompleteOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private static final String PGLABEL = "testPaymentGatewayLabel";

    private PaymentProcessorFactory paymentProcessorFactory;

    private CustomerOrderService orderService;
    private ShipmentCompleteOrderEventHandlerImpl handler;
    private PendingOrderEventHandlerImpl pendingHandler;
    private SkuWarehouseService skuWarehouseService;
    private CustomerOrderPaymentService customerOrderPaymentService;
    private WarehouseService warehouseService;
    private ProductSkuService productSkuService;

    @Before
    public void setUp() throws Exception {
        paymentProcessorFactory = (PaymentProcessorFactory) ctx().getBean(ServiceSpringKeys.PAYMENT_PROCESSOR_FACTORY);
        handler = (ShipmentCompleteOrderEventHandlerImpl) ctx().getBean("shipmentCompleteOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean("customerOrderService");
        skuWarehouseService = (SkuWarehouseService) ctx().getBean("skuWarehouseService");
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        pendingHandler = (PendingOrderEventHandlerImpl) ctx().getBean("pendingOrderEventHandler");
        warehouseService = (WarehouseService) ctx().getBean("warehouseService");
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
    }

    @Test
    public void testHandle() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx(), customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);
        SkuWarehouse skuWarehouse;
        //need to auth before capture
        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));
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
        assertFalse(handler.handle(new OrderEventImpl("", customerOrder, delivery)));
        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("7.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery.getDeliveryNum(),
                Payment.PAYMENT_STATUS_FAILED,
                PaymentGateway.CAPTURE);
        assertEquals(1, rezList.size());
        // same operation with ok fund capture
        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.CAPTURE_FAIL, null);
        assertTrue(handler.handle(new OrderEventImpl("", customerOrder, delivery)));
        skuWarehouse = skuWarehouseService.getById(30);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("7.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        skuWarehouse = skuWarehouseService.getById(31);
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getReserved().setScale(Constants.DEFAULT_SCALE));
        assertEquals(new BigDecimal("0.00"), skuWarehouse.getQuantity().setScale(Constants.DEFAULT_SCALE));
        rezList = customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery.getDeliveryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.CAPTURE);
        assertEquals(1, rezList.size());
        assertEquals(CustomerOrder.ORDER_STATUS_COMPLETED, customerOrder.getOrderStatus());
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED, delivery.getDeliveryStatus());
    }


    /**
     * Test to check non integer quantity.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentWithNonIntegerQty() throws Exception {


        final Warehouse warehouse = warehouseService.getById(1);
        final Pair<BigDecimal, BigDecimal> skuTest0 = skuWarehouseService.getQuantity(Collections.singletonList(warehouse) ,productSkuService.getProductSkuBySkuCode("CC_TEST3"));



        Customer customer = createCustomer();

        ShoppingCart cart3 = getShoppingCart3(customer.getEmail());
        CustomerOrder customerOrder = orderService.createFromCart(cart3, false); //multiple delivery enabled
        customerOrder.setPgLabel("testPaymentGatewayLabel");

        assertTrue(pendingHandler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));

        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();

        assertTrue(handler.handle(new OrderEventImpl("", customerOrder, delivery0)));




        final Pair<BigDecimal, BigDecimal> skuTest1 = skuWarehouseService.getQuantity(Collections.singletonList(warehouse) ,productSkuService.getProductSkuBySkuCode("CC_TEST3"));




        assertTrue(MoneyUtils.isFirstBiggerThanSecond(skuTest1.getFirst().remainder(BigDecimal.ONE), BigDecimal.ZERO));

        assertTrue( MoneyUtils.isFirstEqualToSecond(
                skuTest1.getFirst().add(new BigDecimal("1.5")) ,
                skuTest0.getFirst(), Constants.DEFAULT_SCALE) );




    }

    /**
     * Create simple cart to test non integer as quantity.
     *
     * @return cart
     */
    protected ShoppingCart getShoppingCart3(String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
//        new AddSkuToCartEventCommandImpl(ctx(), Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1"))
//                .execute(shoppingCart);
        new AddSkuToCartEventCommandImpl(ctx(), Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST3"))
                .execute(shoppingCart);

        final Map<String, String> param = new HashMap<String, String>() {{
            put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST3");
            put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.50");
        }};
        new SetSkuQuantityToCartEventCommandImpl(ctx(), param)
                .execute(shoppingCart);

        return shoppingCart;
    }


    private Map<String, String> createParametersMap() {
        Map<String, String> rez = new HashMap<String, String>();
        rez.put("ccHolderName", "John Dou");
        rez.put("ccNumber", "4111111111111111");
        rez.put("ccExpireMonth", "12");
        rez.put("ccExpireYear", "2020");
        rez.put("ccSecCode", "111");
        rez.put("ccType", "Visa");
        return rez;
    }




}
