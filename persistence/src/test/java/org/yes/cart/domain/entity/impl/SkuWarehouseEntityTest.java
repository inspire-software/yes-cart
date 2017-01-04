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

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 02/01/2017
 * Time: 15:51
 */
public class SkuWarehouseEntityTest {

    @Test
    public void testIsAvailableToSell() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();

        assertFalse(stock.isAvailableToSell());

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell());

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell());

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell());

    }

    @Test
    public void testIsAvailableToSellX() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();

        assertFalse(stock.isAvailableToSell(BigDecimal.ONE));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE));
        assertFalse(stock.isAvailableToSell(new BigDecimal("1.01")));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.5")));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.51")));

    }

    @Test
    public void testIsAvailableToAllocate() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();

        assertFalse(stock.isAvailableToAllocate(BigDecimal.ONE));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToAllocate(BigDecimal.ONE));
        assertFalse(stock.isAvailableToAllocate(new BigDecimal("1.01")));

        stock.setReserved(BigDecimal.ONE);
        assertTrue(stock.isAvailableToAllocate(BigDecimal.ONE));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToAllocate(new BigDecimal("0.5")));
        assertTrue(stock.isAvailableToAllocate(new BigDecimal("1.5")));
        assertFalse(stock.isAvailableToAllocate(new BigDecimal("1.51")));

    }
}