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

package org.yes.cart.domain.entity.impl;

import org.junit.Test;
import org.yes.cart.domain.entity.ProductQuantityModel;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 25/10/2014
 * Time: 12:18
 */
public class ProductQuantityModelImplTest {

    @Test
    public void testDefaultCartEmpty() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(null, null, null, null);

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertFalse(pqm.hasMin());
        assertFalse(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertFalse(pqm.hasMinMaxStep());
        assertEquals("1", pqm.getMin().toPlainString());
        assertEquals("1", pqm.getMinOrder().toPlainString());
        assertEquals("2147483647", pqm.getMax().toPlainString());
        assertEquals("2147483647", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("0", pqm.getCartQty().toPlainString());

        assertEquals("1", pqm.getValidAddQty(null).toPlainString());
        assertEquals("1", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("0", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidSetQty(null).toPlainString());
        assertEquals("1", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartEmptyWithMin() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), null, null, null);

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertFalse(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3", pqm.getMin().toPlainString());
        assertEquals("3", pqm.getMinOrder().toPlainString());
        assertEquals("2147483647", pqm.getMax().toPlainString());
        assertEquals("2147483647", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("0", pqm.getCartQty().toPlainString());

        assertEquals("3", pqm.getValidAddQty(null).toPlainString());
        assertEquals("3", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("0", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartEmptyWithMinMax() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), new BigDecimal("10"), null, null);

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3", pqm.getMin().toPlainString());
        assertEquals("3", pqm.getMinOrder().toPlainString());
        assertEquals("10", pqm.getMax().toPlainString());
        assertEquals("10", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("0", pqm.getCartQty().toPlainString());

        assertEquals("3", pqm.getValidAddQty(null).toPlainString());
        assertEquals("3", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("0", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartEmptyWithMinMaxStepInt() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), new BigDecimal("11"), new BigDecimal("2"), null);

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertTrue(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3", pqm.getMin().toPlainString());
        assertEquals("3", pqm.getMinOrder().toPlainString());
        assertEquals("11", pqm.getMax().toPlainString());
        assertEquals("11", pqm.getMaxOrder().toPlainString());
        assertEquals("2", pqm.getStep().toPlainString());
        assertEquals("0", pqm.getCartQty().toPlainString());

        assertEquals("3", pqm.getValidAddQty(null).toPlainString());
        assertEquals("3", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("9", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("11", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("0", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("0", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("7", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("11", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("11", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartEmptyWithMinMaxStepDecimal() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), new BigDecimal("11"), new BigDecimal("0.2"), null);

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertTrue(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3.0", pqm.getMin().toPlainString());
        assertEquals("3.0", pqm.getMinOrder().toPlainString());
        assertEquals("11.0", pqm.getMax().toPlainString());
        assertEquals("11.0", pqm.getMaxOrder().toPlainString());
        assertEquals("0.2", pqm.getStep().toPlainString());
        assertEquals("0.0", pqm.getCartQty().toPlainString());

        assertEquals("3.0", pqm.getValidAddQty(null).toPlainString());
        assertEquals("3.0", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3.0", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3.0", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5.4", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("9.8", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("11.0", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("0.0", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("0.0", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("0.0", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("0.0", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("0.0", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("0.0", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("0.0", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3.0", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3.0", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3.0", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3.0", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5.6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10.0", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("11.0", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }


    @Test
    public void testDefaultCartWith1Item() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(null, null, null, new BigDecimal("1.00"));

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertFalse(pqm.hasMin());
        assertFalse(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertFalse(pqm.hasMinMaxStep());
        assertEquals("1", pqm.getMin().toPlainString());
        assertEquals("1", pqm.getMinOrder().toPlainString());
        assertEquals("2147483647", pqm.getMax().toPlainString());
        assertEquals("2147483646", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("1", pqm.getCartQty().toPlainString());

        assertEquals("1", pqm.getValidAddQty(null).toPlainString());
        assertEquals("1", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidSetQty(null).toPlainString());
        assertEquals("1", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith1ItemWithMin() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), null, null, new BigDecimal("1.00"));

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertFalse(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3", pqm.getMin().toPlainString());
        assertEquals("2", pqm.getMinOrder().toPlainString());
        assertEquals("2147483647", pqm.getMax().toPlainString());
        assertEquals("2147483646", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("1", pqm.getCartQty().toPlainString());

        assertEquals("2", pqm.getValidAddQty(null).toPlainString());
        assertEquals("2", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith1ItemWithMinMax() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), new BigDecimal("10"), null, new BigDecimal("1.00"));

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3", pqm.getMin().toPlainString());
        assertEquals("2", pqm.getMinOrder().toPlainString());
        assertEquals("10", pqm.getMax().toPlainString());
        assertEquals("9", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("1", pqm.getCartQty().toPlainString());

        assertEquals("2", pqm.getValidAddQty(null).toPlainString());
        assertEquals("2", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("9", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("9", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith1ItemWithMinMaxStepInt() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), new BigDecimal("11"), new BigDecimal("2"), new BigDecimal("1.00"));

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertTrue(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3", pqm.getMin().toPlainString());
        assertEquals("2", pqm.getMinOrder().toPlainString());
        assertEquals("11", pqm.getMax().toPlainString());
        assertEquals("10", pqm.getMaxOrder().toPlainString());
        assertEquals("2", pqm.getStep().toPlainString());
        assertEquals("1", pqm.getCartQty().toPlainString());

        assertEquals("2", pqm.getValidAddQty(null).toPlainString());
        assertEquals("2", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("7", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("11", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("11", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith1ItemWithMinMaxStepDecimal() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("3"), new BigDecimal("11"), new BigDecimal("0.2"), new BigDecimal("1.00"));

        assertFalse(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertTrue(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("3.0", pqm.getMin().toPlainString());
        assertEquals("2.0", pqm.getMinOrder().toPlainString());
        assertEquals("11.0", pqm.getMax().toPlainString());
        assertEquals("10.0", pqm.getMaxOrder().toPlainString());
        assertEquals("0.2", pqm.getStep().toPlainString());
        assertEquals("1.0", pqm.getCartQty().toPlainString());

        assertEquals("2.0", pqm.getValidAddQty(null).toPlainString());
        assertEquals("2.0", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2.0", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2.0", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5.4", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("9.8", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10.0", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1.0", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1.0", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1.0", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1.0", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("1.0", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("1.0", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1.0", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3.0", pqm.getValidSetQty(null).toPlainString());
        assertEquals("3.0", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3.0", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3.0", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5.6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10.0", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("11.0", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith3Items() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(null, null, null, new BigDecimal("3.00"));

        assertTrue(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertFalse(pqm.hasMin());
        assertFalse(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertFalse(pqm.hasMinMaxStep());
        assertEquals("1", pqm.getMin().toPlainString());
        assertEquals("1", pqm.getMinOrder().toPlainString());
        assertEquals("2147483647", pqm.getMax().toPlainString());
        assertEquals("2147483644", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("3", pqm.getCartQty().toPlainString());

        assertEquals("1", pqm.getValidAddQty(null).toPlainString());
        assertEquals("1", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidSetQty(null).toPlainString());
        assertEquals("1", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith3ItemsWithMin() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("2"), null, null, new BigDecimal("3.00"));

        assertTrue(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertFalse(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("2", pqm.getMin().toPlainString());
        assertEquals("1", pqm.getMinOrder().toPlainString());
        assertEquals("2147483647", pqm.getMax().toPlainString());
        assertEquals("2147483644", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("3", pqm.getCartQty().toPlainString());

        assertEquals("1", pqm.getValidAddQty(null).toPlainString());
        assertEquals("1", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("2", pqm.getValidSetQty(null).toPlainString());
        assertEquals("2", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("1000000", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith3ItemsWithMinMax() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("2"), new BigDecimal("10"), null, new BigDecimal("3.00"));

        assertTrue(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertFalse(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("2", pqm.getMin().toPlainString());
        assertEquals("1", pqm.getMinOrder().toPlainString());
        assertEquals("10", pqm.getMax().toPlainString());
        assertEquals("7", pqm.getMaxOrder().toPlainString());
        assertEquals("1", pqm.getStep().toPlainString());
        assertEquals("3", pqm.getCartQty().toPlainString());

        assertEquals("1", pqm.getValidAddQty(null).toPlainString());
        assertEquals("1", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("7", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("7", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("1", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("2", pqm.getValidSetQty(null).toPlainString());
        assertEquals("2", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith3ItemsWithMinMaxStepInt() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("2"), new BigDecimal("10"), new BigDecimal("2"), new BigDecimal("3.00"));

        assertTrue(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertTrue(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("2", pqm.getMin().toPlainString());
        assertEquals("2", pqm.getMinOrder().toPlainString());
        assertEquals("10", pqm.getMax().toPlainString());
        assertEquals("7", pqm.getMaxOrder().toPlainString());
        assertEquals("2", pqm.getStep().toPlainString());
        assertEquals("3", pqm.getCartQty().toPlainString());

        assertEquals("1", pqm.getValidAddQty(null).toPlainString());
        assertEquals("1", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("7", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("7", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("3", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("3", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("2", pqm.getValidSetQty(null).toPlainString());
        assertEquals("2", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }

    @Test
    public void testDefaultCartWith3ItemsWithMinMaxStepDecimal() throws Exception {

        final ProductQuantityModel pqm = new ProductQuantityModelImpl(new BigDecimal("2"), new BigDecimal("10"), new BigDecimal("0.2"), new BigDecimal("3.00"));

        assertTrue(pqm.canOrderLess());
        assertTrue(pqm.canOrderMore());
        assertTrue(pqm.hasMin());
        assertTrue(pqm.hasMax());
        assertTrue(pqm.hasStep());
        assertTrue(pqm.hasMinMaxStep());
        assertEquals("2.0", pqm.getMin().toPlainString());
        assertEquals("0.2", pqm.getMinOrder().toPlainString());
        assertEquals("10.0", pqm.getMax().toPlainString());
        assertEquals("7.0", pqm.getMaxOrder().toPlainString());
        assertEquals("0.2", pqm.getStep().toPlainString());
        assertEquals("3.0", pqm.getCartQty().toPlainString());

        assertEquals("0.2", pqm.getValidAddQty(null).toPlainString());
        assertEquals("0.2", pqm.getValidAddQty(BigDecimal.ZERO).toPlainString());
        assertEquals("0.2", pqm.getValidAddQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("1.0", pqm.getValidAddQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5.4", pqm.getValidAddQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("7.0", pqm.getValidAddQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("7.0", pqm.getValidAddQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("0.2", pqm.getValidRemoveQty(null).toPlainString());
        assertEquals("0.2", pqm.getValidRemoveQty(BigDecimal.ZERO).toPlainString());
        assertEquals("0.2", pqm.getValidRemoveQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("3.0", pqm.getValidRemoveQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("3.0", pqm.getValidRemoveQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("3.0", pqm.getValidRemoveQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("3.0", pqm.getValidRemoveQty(new BigDecimal("1000000")).toPlainString());

        assertEquals("2.0", pqm.getValidSetQty(null).toPlainString());
        assertEquals("2.0", pqm.getValidSetQty(BigDecimal.ZERO).toPlainString());
        assertEquals("2.0", pqm.getValidSetQty(new BigDecimal("0.1")).toPlainString());
        assertEquals("2.0", pqm.getValidSetQty(new BigDecimal("1.1")).toPlainString());
        assertEquals("5.6", pqm.getValidSetQty(new BigDecimal("5.5")).toPlainString());
        assertEquals("10.0", pqm.getValidSetQty(new BigDecimal("9.9")).toPlainString());
        assertEquals("10.0", pqm.getValidSetQty(new BigDecimal("1000000")).toPlainString());

    }
}
