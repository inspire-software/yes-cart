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

package org.yes.cart.utils;

import org.junit.Test;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 11:47:04 PM
 */
public class MoneyUtilsTest {

    @Test
    public void testMax() {

        assertEquals(BigDecimal.ZERO, MoneyUtils.max(null, null));

        assertEquals(BigDecimal.ONE, MoneyUtils.max(null, BigDecimal.ONE));

        assertEquals(BigDecimal.TEN, MoneyUtils.max(BigDecimal.TEN, null));

        assertEquals(BigDecimal.TEN, MoneyUtils.max(BigDecimal.TEN, BigDecimal.ONE));

        assertEquals(BigDecimal.TEN, MoneyUtils.max(BigDecimal.ONE, BigDecimal.TEN));

    }

    @Test
    public void testSecondOrFirst() throws Exception {

        assertNull(MoneyUtils.secondOrFirst(new Pair<>(null, null)));

        assertEquals(BigDecimal.TEN, MoneyUtils.secondOrFirst(new Pair<>(BigDecimal.TEN, null)));

        assertEquals(BigDecimal.ONE, MoneyUtils.secondOrFirst(new Pair<>(BigDecimal.TEN, BigDecimal.ONE)));

        assertEquals(BigDecimal.TEN, MoneyUtils.secondOrFirst(new Pair<>(BigDecimal.ONE, BigDecimal.TEN)));

        assertEquals(BigDecimal.ONE, MoneyUtils.secondOrFirst(new Pair<>(null, BigDecimal.ONE)));

    }

    @Test
    public void testNotNull() {

        assertEquals(BigDecimal.ZERO, MoneyUtils.notNull(null));

        assertEquals(BigDecimal.ZERO, MoneyUtils.notNull(null, null));

        assertEquals(BigDecimal.TEN, MoneyUtils.notNull(null, BigDecimal.TEN));

        assertEquals(new BigDecimal(3), MoneyUtils.notNull(new BigDecimal(3)));
        
        assertEquals(new BigDecimal(3), MoneyUtils.notNull(new BigDecimal(3), BigDecimal.TEN));

    }

    @Test
    public void testIsPositive() {

        assertFalse(MoneyUtils.isPositive(null));

        assertFalse(MoneyUtils.isPositive(BigDecimal.ZERO));

        assertFalse(MoneyUtils.isPositive(new BigDecimal("-1")));

        assertTrue(MoneyUtils.isPositive(BigDecimal.ONE));

    }

    @Test
    public void testIsFirstBiggerThanSecond() {

        assertFalse(MoneyUtils.isFirstBiggerThanSecond(null, null));

        assertFalse(MoneyUtils.isFirstBiggerThanSecond(null, BigDecimal.ZERO));

        assertTrue(MoneyUtils.isFirstBiggerThanSecond(BigDecimal.ZERO, null));

        assertFalse(MoneyUtils.isFirstBiggerThanSecond(null, BigDecimal.ONE));

        assertFalse(MoneyUtils.isFirstBiggerThanSecond(BigDecimal.ZERO, BigDecimal.ONE));

        assertFalse(MoneyUtils.isFirstBiggerThanSecond(BigDecimal.ONE, BigDecimal.ONE));

        assertTrue(MoneyUtils.isFirstBiggerThanSecond(BigDecimal.TEN, BigDecimal.ONE));

        assertTrue(MoneyUtils.isFirstBiggerThanSecond(BigDecimal.TEN, null));

        assertFalse(MoneyUtils.isFirstBiggerThanSecond(BigDecimal.TEN, new BigDecimal(10.00)));
    }

    @Test
    public void testIsFirstBiggerThanOrEqualToSecond() {

        assertFalse(MoneyUtils.isFirstBiggerThanOrEqualToSecond(null, null));

        assertFalse(MoneyUtils.isFirstBiggerThanOrEqualToSecond(null, BigDecimal.ZERO));

        assertTrue(MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, null));

        assertFalse(MoneyUtils.isFirstBiggerThanOrEqualToSecond(null, BigDecimal.ONE));

        assertFalse(MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, BigDecimal.ONE));

        assertTrue(MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ONE, BigDecimal.ONE));

        assertTrue(MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.TEN, BigDecimal.ONE));

        assertTrue(MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.TEN, null));

        assertTrue(MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.TEN, new BigDecimal(10.00)));

        assertTrue(MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.TEN, BigDecimal.ZERO));

    }

    @Test
    public void testIsFirstEqualToSecond() {

        assertFalse(MoneyUtils.isFirstEqualToSecond(null, null));

        assertFalse(MoneyUtils.isFirstEqualToSecond(null, BigDecimal.ZERO));

        assertFalse(MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, null));

        assertFalse(MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, BigDecimal.TEN));

        assertFalse(MoneyUtils.isFirstEqualToSecond(BigDecimal.TEN, BigDecimal.ZERO));

        assertTrue(MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, BigDecimal.ZERO));

        assertTrue(MoneyUtils.isFirstEqualToSecond(BigDecimal.TEN, new BigDecimal("10.0000")));
    }

    @Test
    public void testIsFirstEqualToSecondScaled() {

        assertFalse(MoneyUtils.isFirstEqualToSecond(null, null, 2));

        assertFalse(MoneyUtils.isFirstEqualToSecond(null, BigDecimal.ZERO, 2));

        assertFalse(MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, null, 2));

        assertFalse(MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, BigDecimal.TEN, 2));

        assertFalse(MoneyUtils.isFirstEqualToSecond(BigDecimal.TEN, BigDecimal.ZERO, 2));

        assertTrue(MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, BigDecimal.ZERO, 2));

        assertTrue(MoneyUtils.isFirstEqualToSecond(BigDecimal.TEN, new BigDecimal("10.0000"), 2));

        assertFalse(MoneyUtils.isFirstEqualToSecond(new BigDecimal("10.333"), new BigDecimal("10.336"), 3));

        assertFalse(MoneyUtils.isFirstEqualToSecond(new BigDecimal("10.333"), new BigDecimal("10.336"), 2));

        assertTrue(MoneyUtils.isFirstEqualToSecond(new BigDecimal("10.333"), new BigDecimal("10.336"), 1));

        assertTrue(MoneyUtils.isFirstEqualToSecond(new BigDecimal("10.333"), new BigDecimal("10.336"), 0));

        assertTrue(MoneyUtils.isFirstEqualToSecond(new BigDecimal("1.1"), new BigDecimal("0.99"), 0));
    }

    @Test
    public void testMinimalPositive() {

        assertEquals(BigDecimal.ZERO, MoneyUtils.minPositive(null, null));

        assertEquals(BigDecimal.ZERO, MoneyUtils.minPositive(BigDecimal.ZERO, BigDecimal.ZERO));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(BigDecimal.ONE, null));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(BigDecimal.ONE, BigDecimal.ZERO));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(null, BigDecimal.ONE));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(BigDecimal.ZERO, BigDecimal.ONE));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(BigDecimal.TEN, BigDecimal.ONE));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(BigDecimal.ONE, BigDecimal.TEN));


        assertEquals(BigDecimal.ZERO, MoneyUtils.minPositive(new Pair<>(null, null)));

        assertEquals(BigDecimal.ZERO, MoneyUtils.minPositive(new Pair<>(BigDecimal.ZERO, BigDecimal.ZERO)));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(new Pair<>(BigDecimal.ONE, null)));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(new Pair<>(BigDecimal.ONE, BigDecimal.ZERO)));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(new Pair<>(null, BigDecimal.ONE)));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(new Pair<>(BigDecimal.ZERO, BigDecimal.ONE)));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(new Pair<>(BigDecimal.TEN, BigDecimal.ONE)));

        assertEquals(BigDecimal.ONE, MoneyUtils.minPositive(new Pair<>(BigDecimal.ONE, BigDecimal.TEN)));


    }

    @Test
    public void testGetDiscountDisplayValueOnNullPrices() throws Exception {

        assertSame(BigDecimal.ZERO, MoneyUtils.getDiscountDisplayValue(null, null));
        assertSame(BigDecimal.ZERO, MoneyUtils.getDiscountDisplayValue(BigDecimal.TEN, null));
        assertSame(BigDecimal.ZERO, MoneyUtils.getDiscountDisplayValue(null, BigDecimal.TEN));

    }

    @Test
    public void testGetDiscountDisplayValue() throws Exception {

        assertEquals("20", MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("80.00")).toPlainString());
        assertEquals("0", MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("100.00")).toPlainString());
        assertEquals("-20", MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("120.00")).toPlainString());
        assertEquals("19", MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("80.99")).toPlainString());

    }

    @Test
    public void testGetTaxAmount() throws Exception {

        assertEquals("0.00", MoneyUtils.getTaxAmount(null, null, true).toPlainString());
        assertEquals("0.00", MoneyUtils.getTaxAmount(null, new BigDecimal("20.00"), true).toPlainString());
        assertEquals("0.00", MoneyUtils.getTaxAmount(new BigDecimal("100.00"), null, true).toPlainString());
        assertEquals("0.00", MoneyUtils.getTaxAmount(new BigDecimal("0.00"), new BigDecimal("20.00"), true).toPlainString());
        assertEquals("0.00", MoneyUtils.getTaxAmount(new BigDecimal("100.00"), new BigDecimal("0.00"), true).toPlainString());
        assertEquals("16.67", MoneyUtils.getTaxAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), true).toPlainString());
        assertEquals("20.00", MoneyUtils.getTaxAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), false).toPlainString());

    }

    @Test
    public void testGetNetAmount() throws Exception {

        assertEquals("0.00", MoneyUtils.getNetAmount(null, new BigDecimal("0.00"), true).toPlainString());
        assertEquals("100.00", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("0.00"), true).toPlainString());
        assertEquals("100.00", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("0.00"), false).toPlainString());
        assertEquals("83.33", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), true).toPlainString());
        assertEquals("100.00", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), false).toPlainString());

    }

    @Test
    public void testGetGrossAmount() throws Exception {

        assertEquals("0.00", MoneyUtils.getGrossAmount(null, new BigDecimal("0.00"), true).toPlainString());
        assertEquals("100.00", MoneyUtils.getGrossAmount(new BigDecimal("100.00"), new BigDecimal("0.00"), true).toPlainString());
        assertEquals("100.00", MoneyUtils.getGrossAmount(new BigDecimal("100.00"), new BigDecimal("0.00"), false).toPlainString());
        assertEquals("100.00", MoneyUtils.getGrossAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), true).toPlainString());
        assertEquals("120.00", MoneyUtils.getGrossAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), false).toPlainString());

    }

    @Test
    public void testGetMoney() throws Exception {

        final MoneyUtils.Money moneyIncl = MoneyUtils.getMoney(new BigDecimal("100.00"), new BigDecimal("20.00"), true);
        assertEquals("100.00", moneyIncl.getMoney().toPlainString());
        assertEquals("83.33", moneyIncl.getNet().toPlainString());
        assertEquals("100.00", moneyIncl.getGross().toPlainString());
        assertEquals("16.67", moneyIncl.getTax().toPlainString());
        assertEquals("20.00", moneyIncl.getRate().toPlainString());
        assertTrue(moneyIncl.isIncluded());

        final MoneyUtils.Money moneyExcl = MoneyUtils.getMoney(new BigDecimal("100.00"), new BigDecimal("20.00"), false);
        assertEquals("100.00", moneyExcl.getMoney().toPlainString());
        assertEquals("100.00", moneyExcl.getNet().toPlainString());
        assertEquals("120.00", moneyExcl.getGross().toPlainString());
        assertEquals("20.00", moneyExcl.getTax().toPlainString());
        assertEquals("20.00", moneyExcl.getRate().toPlainString());
        assertFalse(moneyExcl.isIncluded());


        final MoneyUtils.Money moneyNullIncl = MoneyUtils.getMoney(null, BigDecimal.ZERO, true);
        assertEquals("0.00", moneyNullIncl.getMoney().toPlainString());
        assertEquals("0.00", moneyNullIncl.getNet().toPlainString());
        assertEquals("0.00", moneyNullIncl.getGross().toPlainString());
        assertEquals("0.00", moneyNullIncl.getTax().toPlainString());
        assertEquals("0", moneyNullIncl.getRate().toPlainString());
        assertTrue(moneyNullIncl.isIncluded());

        final MoneyUtils.Money moneyNullExcl = MoneyUtils.getMoney(null, BigDecimal.ZERO, false);
        assertEquals("0.00", moneyNullExcl.getMoney().toPlainString());
        assertEquals("0.00", moneyNullExcl.getNet().toPlainString());
        assertEquals("0.00", moneyNullExcl.getGross().toPlainString());
        assertEquals("0.00", moneyNullExcl.getTax().toPlainString());
        assertEquals("0", moneyNullExcl.getRate().toPlainString());
        assertFalse(moneyNullExcl.isIncluded());

        final MoneyUtils.Money moneyZeroIncl = MoneyUtils.getMoney(new BigDecimal("100.00"), BigDecimal.ZERO, true);
        assertEquals("100.00", moneyZeroIncl.getMoney().toPlainString());
        assertEquals("100.00", moneyZeroIncl.getNet().toPlainString());
        assertEquals("100.00", moneyZeroIncl.getGross().toPlainString());
        assertEquals("0.00", moneyZeroIncl.getTax().toPlainString());
        assertEquals("0", moneyZeroIncl.getRate().toPlainString());
        assertTrue(moneyZeroIncl.isIncluded());

        final MoneyUtils.Money moneyZeroExcl = MoneyUtils.getMoney(new BigDecimal("100.00"), BigDecimal.ZERO, false);
        assertEquals("100.00", moneyZeroExcl.getMoney().toPlainString());
        assertEquals("100.00", moneyZeroExcl.getNet().toPlainString());
        assertEquals("100.00", moneyZeroExcl.getGross().toPlainString());
        assertEquals("0.00", moneyZeroExcl.getTax().toPlainString());
        assertEquals("0", moneyZeroExcl.getRate().toPlainString());
        assertFalse(moneyZeroExcl.isIncluded());


    }

}
