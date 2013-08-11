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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.ShopTopSeller;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopTopSellerService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/5/11
 * Time: 10:34 AM
 */
public class TestShopTopSellerServiceImpl extends BaseCoreDBTestCase {

    private ShopTopSellerService shopTopSellerService;
    private OrderAssembler orderAssembler;
    private CustomerOrderService customerOrderService;

    @Before
    public void setUp() {
        shopTopSellerService = (ShopTopSellerService) ctx().getBean(ServiceSpringKeys.SHOP_TOP_SELLER_SERVICE);
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService = ctx().getBean("customerOrderService", CustomerOrderService.class);
        super.setUp();
    }

    // TODO: YC-64 fix to not depend on order of running
    @Test
    public void testUpdateTopSellers() throws Exception {
        Map<Long, BigDecimal> expectation = new HashMap<Long, BigDecimal>() {{
            put(15120L, new BigDecimal("2"));
            put(15122L, new BigDecimal("4"));
            put(15123L, new BigDecimal("2"));
            put(15125L, new BigDecimal("400"));
            put(15126L, new BigDecimal("6"));
            put(15127L, new BigDecimal("2"));
            put(15128L, new BigDecimal("2"));
            put(15129L, new BigDecimal("2"));
        }};
        Customer customer = createCustomer();
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrderService.create(customerOrder);
        Customer customer2 = createCustomer2();
        ShoppingCart shoppingCart2 = getShoppingCart2(customer2.getEmail());
        CustomerOrder customerOrder2 = orderAssembler.assembleCustomerOrder(shoppingCart2);
        customerOrderService.create(customerOrder2);
        shopTopSellerService.updateTopSellers(10);
        List<ShopTopSeller> allTopSellers = shopTopSellerService.findAll();
        for (ShopTopSeller ts : allTopSellers) {
            Long key = ts.getProduct().getId();
            BigDecimal expectedCounter = expectation.remove(key);
            if (expectedCounter != null) {
                // counter can be increased by other tests
                assertThat("Unexpected counter for product with id = " + key,
                        ts.getCounter(), greaterThanOrEqualTo(expectedCounter));
            }
        }
        assertThat(expectation.keySet(), hasSize(0));
    }
}
