/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LoginCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {

        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop10 = shopService.getById(10L);
        Shop shop20 = shopService.getById(20L);

        final Customer customer = createCustomer();

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        shoppingCart.getShoppingContext().setShopCode(shop20.getCode());
        shoppingCart.getShoppingContext().setShopId(shop20.getShopId());
        commands.execute(shoppingCart, (Map) params);
        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());

        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customer.getEmail());
        params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "rawpassword");
        params.put(ShoppingCartCommand.CMD_LOGIN, "1");
        commands.execute(shoppingCart, (Map) params);
        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());

        shoppingCart.getShoppingContext().setShopCode(shop10.getCode());
        shoppingCart.getShoppingContext().setShopId(shop10.getShopId());
        commands.execute(shoppingCart, (Map) params);
        assertEquals(ShoppingCart.LOGGED_IN, shoppingCart.getLogonState());
        assertNull(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_REF));
        assertNull(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_CHARGE_ID));
        assertNull(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_EMPLOYEE_ID));
        assertEquals("false", shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER));
        assertEquals("false", shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT));

    }


    @Test
    public void testExecuteB2B() {

        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop10 = shopService.getById(10L);
        Shop shop20 = shopService.getById(20L);

        final Customer customer = createCustomerB2B();

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        shoppingCart.getShoppingContext().setShopCode(shop20.getCode());
        shoppingCart.getShoppingContext().setShopId(shop20.getShopId());
        commands.execute(shoppingCart, (Map) params);
        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());

        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customer.getEmail());
        params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "rawpassword");
        params.put(ShoppingCartCommand.CMD_LOGIN, "1");
        commands.execute(shoppingCart, (Map) params);
        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());

        shoppingCart.getShoppingContext().setShopCode(shop10.getCode());
        shoppingCart.getShoppingContext().setShopId(shop10.getShopId());
        commands.execute(shoppingCart, (Map) params);
        assertEquals(ShoppingCart.LOGGED_IN, shoppingCart.getLogonState());
        assertEquals("REF-001", shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_REF));
        assertEquals("CHARGE-001", shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_CHARGE_ID));
        assertEquals("EMP-001", shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_EMPLOYEE_ID));
        assertEquals("true", shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER));
        assertEquals("false", shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT));

    }

}
