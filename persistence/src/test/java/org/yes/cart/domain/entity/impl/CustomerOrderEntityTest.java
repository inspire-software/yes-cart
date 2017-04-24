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

package org.yes.cart.domain.entity.impl;

import org.junit.Test;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 16/10/2015
 * Time: 16:57
 */
public class CustomerOrderEntityTest {

    @Test
    public void testGetOrderTotalAndTax() throws Exception {

        final CustomerOrderEntity order = new CustomerOrderEntity();
        order.setGrossPrice(new BigDecimal("80.00"));
        order.setNetPrice(new BigDecimal("66.67"));
        order.setDelivery(new ArrayList<CustomerOrderDelivery>());

        final CustomerOrderDeliveryEntity delivery = new CustomerOrderDeliveryEntity();
        delivery.setGrossPrice(new BigDecimal("20.00"));
        delivery.setNetPrice(new BigDecimal("16.67"));

        // no deliveries
        assertEquals("80.00", order.getOrderTotal().toPlainString());
        assertEquals("13.33", order.getOrderTotalTax().toPlainString());

        // 1 delivery
        order.getDelivery().add(delivery);
        assertEquals("100.00", order.getOrderTotal().toPlainString());
        assertEquals("16.66", order.getOrderTotalTax().toPlainString());

        // 2 deliveries
        order.getDelivery().add(delivery);
        assertEquals("120.00", order.getOrderTotal().toPlainString());
        assertEquals("19.99", order.getOrderTotalTax().toPlainString());

    }
}