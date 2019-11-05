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
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 02/01/2017
 * Time: 15:51
 */
public class SkuWarehouseEntityTest {

    @Test
    public void isAvailable() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        final LocalDateTime now0 = DateUtils.ldtParseSDT("2019-08-01");
        final LocalDateTime now1 = DateUtils.ldtParseSDT("2019-09-08");
        final LocalDateTime now2 = DateUtils.ldtParseSDT("2019-10-16");

        assertTrue(stock.isAvailable(now1));
        assertTrue(stock.isReleased(now1));

        stock.setDisabled(true);
        assertFalse(stock.isAvailable(now1));
        assertTrue(stock.isReleased(now1));

        stock.setDisabled(false);
        stock.setAvailablefrom(DateUtils.ldtParseSDT("2019-09-08"));
        assertFalse(stock.isAvailable(now0));
        assertTrue(stock.isReleased(now0));
        assertTrue(stock.isAvailable(now1));
        assertTrue(stock.isReleased(now1));
        assertTrue(stock.isAvailable(now2));
        assertTrue(stock.isReleased(now2));

        stock.setAvailableto(DateUtils.ldtParseSDT("2019-09-16"));
        assertFalse(stock.isAvailable(now0));
        assertTrue(stock.isReleased(now0));
        assertTrue(stock.isAvailable(now1));
        assertTrue(stock.isReleased(now1));
        assertFalse(stock.isAvailable(now2));
        assertTrue(stock.isReleased(now2));

    }

    @Test
    public void testIsAvailableToSellStandard() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();

        assertFalse(stock.isAvailableToSell(false));
        assertFalse(stock.isAvailableToSell(true));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(false));
        assertFalse(stock.isAvailableToSell(true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

    }

    @Test
    public void testIsAvailableToSellBackorder() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_BACKORDER);

        assertFalse(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

    }

    @Test
    public void testIsAvailableToSellAlways() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_ALWAYS);

        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

        stock.setReserved(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell(false));
        assertTrue(stock.isAvailableToSell(true));

    }

    @Test
    public void testIsAvailableToSellShowroom() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_SHOWROOM);

        assertFalse(stock.isAvailableToSell(false));
        assertFalse(stock.isAvailableToSell(true));

        stock.setQuantity(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(false));
        assertFalse(stock.isAvailableToSell(true));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(false));
        assertFalse(stock.isAvailableToSell(true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertFalse(stock.isAvailableToSell(false));
        assertFalse(stock.isAvailableToSell(true));

    }

    @Test
    public void testIsAvailableToSellXStandard() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();

        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, true));
        assertFalse(stock.isAvailableToSell(new BigDecimal("1.01"), false));
        assertFalse(stock.isAvailableToSell(new BigDecimal("1.01"), true));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.5"), false));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.5"), true));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.51"), false));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.51"), true));

    }

    @Test
    public void testIsAvailableToSellXBackorder() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_BACKORDER);

        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, true));
        assertFalse(stock.isAvailableToSell(new BigDecimal("1.01"), false));
        assertTrue(stock.isAvailableToSell(new BigDecimal("1.01"), true));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.5"), false));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.5"), true));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.51"), false));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.51"), true));

    }

    @Test
    public void testIsAvailableToSellXAlways() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_ALWAYS);

        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, true));
        assertTrue(stock.isAvailableToSell(new BigDecimal("1.01"), false));
        assertTrue(stock.isAvailableToSell(new BigDecimal("1.01"), true));

        stock.setReserved(BigDecimal.ONE);
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertTrue(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.5"), false));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.5"), true));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.51"), false));
        assertTrue(stock.isAvailableToSell(new BigDecimal("0.51"), true));

    }

    @Test
    public void testIsAvailableToSellXShowroom() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_SHOWROOM);

        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, true));
        assertFalse(stock.isAvailableToSell(new BigDecimal("1.01"), false));
        assertFalse(stock.isAvailableToSell(new BigDecimal("1.01"), true));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, false));
        assertFalse(stock.isAvailableToSell(BigDecimal.ONE, true));

        stock.setQuantity(new BigDecimal("1.5"));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.5"), false));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.5"), true));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.51"), false));
        assertFalse(stock.isAvailableToSell(new BigDecimal("0.51"), true));

    }

    @Test
    public void testIsAvailableToAllocateStandard() throws Exception {

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

    @Test
    public void testIsAvailableToAllocateBackorder() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_BACKORDER);

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

    @Test
    public void testIsAvailableToAllocateAllways() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_ALWAYS);

        assertTrue(stock.isAvailableToAllocate(BigDecimal.ONE));

        stock.setQuantity(BigDecimal.ONE);
        assertTrue(stock.isAvailableToAllocate(BigDecimal.ONE));
        assertTrue(stock.isAvailableToAllocate(new BigDecimal("1.01")));

        stock.setReserved(BigDecimal.ONE);
        assertTrue(stock.isAvailableToAllocate(BigDecimal.ONE));

        stock.setQuantity(new BigDecimal("1.5"));
        assertTrue(stock.isAvailableToAllocate(new BigDecimal("0.5")));
        assertTrue(stock.isAvailableToAllocate(new BigDecimal("1.5")));
        assertTrue(stock.isAvailableToAllocate(new BigDecimal("1.51")));

    }

    @Test
    public void testIsAvailableToAllocateShowroom() throws Exception {

        final SkuWarehouseEntity stock = new SkuWarehouseEntity();
        stock.setAvailability(SkuWarehouse.AVAILABILITY_SHOWROOM);

        assertFalse(stock.isAvailableToAllocate(BigDecimal.ONE));

        stock.setQuantity(BigDecimal.ONE);
        assertFalse(stock.isAvailableToAllocate(BigDecimal.ONE));
        assertFalse(stock.isAvailableToAllocate(new BigDecimal("1.01")));

        stock.setReserved(BigDecimal.ONE);
        assertFalse(stock.isAvailableToAllocate(BigDecimal.ONE));

        stock.setQuantity(new BigDecimal("1.5"));
        assertFalse(stock.isAvailableToAllocate(new BigDecimal("0.5")));
        assertFalse(stock.isAvailableToAllocate(new BigDecimal("1.5")));
        assertFalse(stock.isAvailableToAllocate(new BigDecimal("1.51")));

    }
}