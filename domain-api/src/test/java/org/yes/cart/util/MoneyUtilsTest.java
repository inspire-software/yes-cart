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

package org.yes.cart.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 11:47:04 PM
 */
//TODO: YC-64 refactor to param tests
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
        assertTrue("ten is equal to ten", MoneyUtils.minPositive(null, BigDecimal.TEN ).equals(BigDecimal.TEN));
    }

    @Test
    public void testGetDiscountDisplayValueOnNullPrices() throws Exception {

        assertSame(BigDecimal.ZERO, MoneyUtils.getDiscountDisplayValue(null, null));
        assertSame(BigDecimal.ZERO, MoneyUtils.getDiscountDisplayValue(BigDecimal.TEN, null));
        assertSame(BigDecimal.ZERO, MoneyUtils.getDiscountDisplayValue(null, BigDecimal.TEN));

    }

    @Test
    public void testGetDiscountDisplayValue() throws Exception {

        assertTrue(new BigDecimal("20")
                .compareTo(MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("80.00"))) == 0);
        assertTrue(new BigDecimal("0")
                .compareTo(MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("100.00"))) == 0);
        assertTrue(new BigDecimal("-20")
                .compareTo(MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("120.00"))) == 0);
        assertTrue(new BigDecimal("19")
                .compareTo(MoneyUtils.getDiscountDisplayValue(new BigDecimal("100.00"), new BigDecimal("80.99"))) == 0);

    }
}
