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

package org.yes.cart.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 11:47:04 PM
 */
public class MoneyUtilsTest {

    @Test
    public void testMaxWithNulls() {
        assertEquals("Must return zero if bot arguments are null", BigDecimal.ZERO, MoneyUtils.max(null, null));
    }

    @Test
    public void testMaxWithFirstNull() {
        assertEquals("Must return zero if bot arguments are null", BigDecimal.ONE, MoneyUtils.max(null, BigDecimal.ONE));
    }

    @Test
    public void testMaxWithSecondNull() {
        assertEquals("Must return zero if bot arguments are null", BigDecimal.TEN, MoneyUtils.max(BigDecimal.TEN, null));
    }

    @Test
    public void testMax() {
        assertEquals("Must return zero if bot arguments are null", BigDecimal.TEN, MoneyUtils.max(BigDecimal.TEN, BigDecimal.ONE));
    }

    @Test
    public void testNotNullIsNullSafe() {
        assertEquals("Must return zero if bot arguments are null", BigDecimal.ZERO, MoneyUtils.notNull(null, null));
    }

    @Test
    public void testNotNullWithNull() {
        assertEquals("Must return ifNull (10) for null value", BigDecimal.TEN, MoneyUtils.notNull(null, BigDecimal.TEN));
    }

    @Test
    public void testNotNullWithThree() {
        assertEquals("Must return value (3) for non-null value", new BigDecimal(3), MoneyUtils.notNull(new BigDecimal(3), BigDecimal.TEN));
    }

    @Test
    public void testIsFirstNullBiggerThanSecondNulls() {
        assertFalse("Two nulls must give a false", MoneyUtils.isFirstBiggerThanSecond(null, null));
    }

    @Test
    public void testIsFirstNullBiggerThanSecondZero() {
        assertFalse("Null is not bigger than zero", MoneyUtils.isFirstBiggerThanSecond(null, BigDecimal.ZERO));
    }

    @Test
    public void testIsFirstZeroBiggerThanSecondNull() {
        assertTrue("Zero is bigger than null", MoneyUtils.isFirstBiggerThanSecond(BigDecimal.ZERO, null));
    }

    @Test
    public void testIsFirstZeroBiggerThanSecondTen() {
        assertFalse("zero is not bigger than ten", MoneyUtils.isFirstBiggerThanSecond(BigDecimal.ZERO, BigDecimal.TEN));
    }

    @Test
    public void testIsFirstTenBiggerThanSecondZero() {
        assertTrue("ten is bigger than zero", MoneyUtils.isFirstBiggerThanSecond(BigDecimal.TEN, BigDecimal.ZERO));
    }

    @Test
    public void testIsFirstTenBiggerThanSecondTen() {
        assertFalse("ten is not bigger than ten", MoneyUtils.isFirstBiggerThanSecond(BigDecimal.TEN, new BigDecimal(10.00)));
    }

    @Test
    public void testIsFirstNullBiggerThanOrEqualToSecondNulls() {
        assertFalse("Two nulls must give a false", MoneyUtils.isFirstBiggerThanOrEqualToSecond(null, null));
    }

    @Test
    public void testIsFirstNullBiggerThanOrEqualToSecondZero() {
        assertFalse("Null is not bigger than or equal to zero", MoneyUtils.isFirstBiggerThanOrEqualToSecond(null, BigDecimal.ZERO));
    }

    @Test
    public void testIsFirstZeroBiggerThanOrEqualToSecondNull() {
        assertTrue("Zero is bigger than or equal to null", MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, null));
    }

    @Test
    public void testIsFirstZeroBiggerThanOrEqualToSecondTen() {
        assertFalse("zero is not bigger than or equal to ten", MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, BigDecimal.TEN));
    }

    @Test
    public void testIsFirstTenBiggerThanOrEqualToSecondZero() {
        assertTrue("ten is bigger than or equal to zero", MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.TEN, BigDecimal.ZERO));
    }

    @Test
    public void testIsFirstTenBiggerThanOrEqualToSecondTen() {
        assertTrue("ten is bigger than or equal to ten", MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.TEN, BigDecimal.ZERO));
    }

    @Test
    public void testIsFirstNullEqualToSecondNulls() {
        assertFalse("Two nulls must give a false", MoneyUtils.isFirstEqualToSecond(null, null));
    }

    @Test
    public void testIsFirstNullEqualToSecondZero() {
        assertFalse("Null is not equal to zero", MoneyUtils.isFirstEqualToSecond(null, BigDecimal.ZERO));
    }

    @Test
    public void testIsFirstZeroEqualToSecondNull() {
        assertFalse("Zero is not equal to null", MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, null));
    }

    @Test
    public void testIsFirstZeroEqualToSecondTen() {
        assertFalse("zero is not equal to ten", MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, BigDecimal.TEN));
    }

    @Test
    public void testIsFirstTenEqualToSecondZero() {
        assertFalse("ten is not equal to zero", MoneyUtils.isFirstEqualToSecond(BigDecimal.TEN, BigDecimal.ZERO));
    }

    @Test
    public void testIsFirstTenEqualToSecondTen() {
        assertTrue("ten is equal to ten", MoneyUtils.isFirstEqualToSecond(BigDecimal.TEN, new BigDecimal("10.0000")));
    }

    @Test
    public void testMinimalPositiveIsFirstTenMoreThanSecondOne() {
        assertTrue("ten is equal to ten", MoneyUtils.minPositive(BigDecimal.TEN, BigDecimal.ONE).equals(BigDecimal.ONE));
    }

    @Test
    public void testMinimalPositiveIsFirstOneLessThanSecondTen() {
        assertTrue("ten is equal to ten", MoneyUtils.minPositive(BigDecimal.ONE, BigDecimal.TEN).equals(BigDecimal.ONE));
    }

    @Test
    public void testMinimalPositiveIsFirstMinimalFromTenAndNull() {
        assertTrue("ten is equal to ten", MoneyUtils.minPositive(BigDecimal.TEN, null).equals(BigDecimal.TEN));
    }

    @Test
    public void testMinimalPositiveIsSecondMinimalFromTenAndNull() {
        assertTrue("ten is equal to ten", MoneyUtils.minPositive(null, BigDecimal.TEN).equals(BigDecimal.TEN));
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

        assertEquals("100.00", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("0.00"), true).toPlainString());
        assertEquals("100.00", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("0.00"), false).toPlainString());
        assertEquals("83.33", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), true).toPlainString());
        assertEquals("100.00", MoneyUtils.getNetAmount(new BigDecimal("100.00"), new BigDecimal("20.00"), false).toPlainString());

    }

    @Test
    public void testGetGrossAmount() throws Exception {

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
