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

package org.yes.cart.service.order.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.SetSkuQuantityToCartEventCommandImpl;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAssemblerImplTest extends BaseCoreDBTestCase {

    private OrderAssembler orderAssembler;
    private DeliveryAssemblerImpl deliveryAssembler;
    //private GenericDAO<CustomerOrder, Long> customerOrderDao;
    private CustomerOrderService customerOrderService;

    @Before
    public void setUp() {
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService =  ctx().getBean("customerOrderService", CustomerOrderService.class);
        deliveryAssembler = (DeliveryAssemblerImpl) ctx().getBean(ServiceSpringKeys.DELIVERY_ASSEMBLER);
        super.setUp();
    }

    @Test
    public void testGetDeliveryGroups() {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart1(customer.getEmail());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        Map<String, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, false);
        assertEquals(1, dgroups.size());
        assertNotNull(dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        assertEquals(2, dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP).size());
        //two delivery will be planed , because of of the sku has not enough quantity
        shoppingCart = getShoppingCart2(customer.getEmail());
        customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, false);
        assertEquals(2, dgroups.size());
        assertNotNull(dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        assertNotNull(dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
        assertEquals(1, dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP).size());
        assertEquals(1, dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP).size());
        //Standard and back order with inventory. Only one delivery must be planned
        shoppingCart = getShoppingCart3(customer.getEmail());
        customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, false);
        assertEquals(1, dgroups.size());
        assertNotNull(dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        assertEquals(2, dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP).size());
        //Standard and back order with inventory. Ordered qty of back order product more that existing inventory.
        // so two delivery must be planned
        shoppingCart = getShoppingCart4(customer.getEmail());
        customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, false);
        assertEquals(2, dgroups.size());
        assertNotNull(dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        assertNotNull(dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
        assertEquals(1, dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP).size());
        assertEquals(1, dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP).size());
        //Standard and back order with inventory. Ordered qty of both products more that existing inventory.
        // so one delivery must be planned
        shoppingCart = getShoppingCart5(customer.getEmail());
        customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, false);
        assertEquals(1, dgroups.size());
        assertNotNull(dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
        assertEquals(2, dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP).size());
        //Standard, back order & pre order with inventory. Ordered qty covered by inventory.
        //Two  deliveries must be planned, because of pre order will wait 
        shoppingCart = getShoppingCart6(customer.getEmail());
        customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, false);
        assertEquals(2, dgroups.size());
        assertNotNull(dgroups.get(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
        assertNotNull(dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        assertEquals(1, dgroups.get(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP).size());
        assertEquals(2, dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP).size());
        //Standard, back order without inventory & pre order with inventory. Ordered qty not covered by inventory.
        // 4 deliveries must be planned, because of pre order will wait
        shoppingCart = getShoppingCart7(customer.getEmail());
        customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, false);
        assertEquals(4, dgroups.size());
        assertNotNull(dgroups.get(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
        assertNotNull(dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
        assertNotNull(dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        assertNotNull(dgroups.get(CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP));
        assertEquals(3, dgroups.get(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP).size());
        assertEquals(1, dgroups.get(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP).size());
        assertEquals(1, dgroups.get(CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP).size());
        assertEquals(1, dgroups.get(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP).size());
    }

    @Test
    public void testAssembleCustomerOrder() {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart7(customer.getEmail());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        assertNotNull("Customer can not be null", shoppingCart.getCustomerEmail());
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, false);
        customerOrder = customerOrderService.create(customerOrder);
        assertTrue(customerOrder.getCustomerorderId() > 0);
        for (CustomerOrderDelivery cod : customerOrder.getDelivery()) {
            assertTrue(cod.getCustomerOrderDeliveryId() > 0);
            assertNotNull(cod.getDeliveryNum());
            if (CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP.equals(cod.getDeliveryGroup())) {
                assertEquals(BigDecimal.ZERO, cod.getPrice());
            } else {
                assertEquals(new BigDecimal("16.77"), cod.getPrice());
            }
            for (CustomerOrderDeliveryDet det : cod.getDetail()) {
                assertTrue(det.getCustomerOrderDeliveryDetId() > 0);
                assertTrue(MoneyUtils.isFirstBiggerThanSecond(det.getQty(), BigDecimal.ZERO));
            }
        }
    }

    /**
     * Create simple cart with products, that have a standard availability and enough qty on warehouses.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart1(final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));

        return shoppingCart;
    }

    //Bot sku with standard availability , but one of the has not qty on warehouse
    protected ShoppingCart getShoppingCart2(final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4-M"));

        return shoppingCart;
    }

    /**
     * Get product with standard availability and back order availability both have inventory.
     * Only one delivery must be planned
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart3(String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5"));

        return shoppingCart;
    }

    /**
     * Get product with standard availability has inventory and back order has inventory , but ordered qty
     * more than inventory.
     * Two deliveries must be planned
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart4(String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4"));

        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "23.00");
        commands.execute(shoppingCart,
                (Map) param);

        return shoppingCart;
    }

    /**
     * Get product with standard availability and back order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart5(String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "34.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "23.00");
        commands.execute(shoppingCart,
                (Map) param);

        return shoppingCart;
    }

    /**
     * Get product with standard availability, back order and pre order availability both have inventory.
     * Ordered qty more than qty on warehouses, so two "wait" delivery will be planned.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart6(String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST6");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "3.00");
        commands.execute(shoppingCart,
                (Map) param);

        return shoppingCart;
    }

    /**
     * Get product with standard availability, back order and pre order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart7(String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);


        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "200.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST6");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "3.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST7");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST8");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart,
                (Map) param);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_SETQTYSKU, "CC_TEST9");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart,
                (Map) param);

        return shoppingCart;
    }

    protected ShoppingCart getEmptyCart(String customerEmail) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customerEmail);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_NAME, "John Doe");

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1");
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        commands.execute(shoppingCart,
                (Map) params);

        return shoppingCart;
    }
}
