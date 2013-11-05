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

package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 04/11/2013
 * Time: 08:27
 */
public class TotalImplTest {

    @Test
    public void testConstructorInitialised() throws Exception {

        final TotalImpl total = new TotalImpl();

        assertEquals("0.00", total.getListSubTotal().toString());
        assertEquals("0.00", total.getSaleSubTotal().toString());
        assertEquals("0.00", total.getPriceSubTotal().toString());
        assertFalse(total.isOrderPromoApplied());
        assertNull(total.getAppliedOrderPromo());
        assertEquals("0.00", total.getSubTotal().toString());
        assertEquals("0.00", total.getSubTotalTax().toString());
        assertEquals("0.00", total.getSubTotalAmount().toString());
        assertEquals("0.00", total.getDeliveryListCost().toString());
        assertEquals("0.00", total.getDeliveryCost().toString());
        assertFalse(total.isDeliveryPromoApplied());
        assertNull(total.getAppliedDeliveryPromo());
        assertEquals("0.00", total.getDeliveryTax().toString());
        assertEquals("0.00", total.getDeliveryCostAmount().toString());
        assertEquals("0.00", total.getTotal().toString());
        assertEquals("0.00", total.getTotalTax().toString());
        assertEquals("0.00", total.getListTotalAmount().toString());
        assertEquals("0.00", total.getTotalAmount().toString());

    }

    @Test
    public void testAddOneNoPromo() throws Exception {

        final Total total = new TotalImpl().add(new TotalImpl(
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                false,
                null,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                false,
                null,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE
            ));

        assertEquals("1.00", total.getListSubTotal().toString());
        assertEquals("1.00", total.getSaleSubTotal().toString());
        assertEquals("1.00", total.getPriceSubTotal().toString());
        assertFalse(total.isOrderPromoApplied());
        assertNull(total.getAppliedOrderPromo());
        assertEquals("1.00", total.getSubTotal().toString());
        assertEquals("1.00", total.getSubTotalTax().toString());
        assertEquals("1.00", total.getSubTotalAmount().toString());
        assertEquals("1.00", total.getDeliveryListCost().toString());
        assertEquals("1.00", total.getDeliveryCost().toString());
        assertFalse(total.isDeliveryPromoApplied());
        assertNull(total.getAppliedDeliveryPromo());
        assertEquals("1.00", total.getDeliveryTax().toString());
        assertEquals("1.00", total.getDeliveryCostAmount().toString());
        assertEquals("1.00", total.getTotal().toString());
        assertEquals("1.00", total.getTotalTax().toString());
        assertEquals("1.00", total.getListTotalAmount().toString());
        assertEquals("1.00", total.getTotalAmount().toString());

    }

    @Test
    public void testAddOneWithPromo() throws Exception {

        final Total total = new TotalImpl().add(new TotalImpl(
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                true,
                "ABC",
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                true,
                "DEF",
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE
            ));

        assertEquals("1.00", total.getListSubTotal().toString());
        assertEquals("1.00", total.getSaleSubTotal().toString());
        assertEquals("1.00", total.getPriceSubTotal().toString());
        assertTrue(total.isOrderPromoApplied());
        assertEquals("ABC", total.getAppliedOrderPromo());
        assertEquals("1.00", total.getSubTotal().toString());
        assertEquals("1.00", total.getSubTotalTax().toString());
        assertEquals("1.00", total.getSubTotalAmount().toString());
        assertEquals("1.00", total.getDeliveryListCost().toString());
        assertEquals("1.00", total.getDeliveryCost().toString());
        assertTrue(total.isDeliveryPromoApplied());
        assertEquals("DEF", total.getAppliedDeliveryPromo());
        assertEquals("1.00", total.getDeliveryTax().toString());
        assertEquals("1.00", total.getDeliveryCostAmount().toString());
        assertEquals("1.00", total.getTotal().toString());
        assertEquals("1.00", total.getTotalTax().toString());
        assertEquals("1.00", total.getListTotalAmount().toString());
        assertEquals("1.00", total.getTotalAmount().toString());

    }

    @Test
    public void testAddAllWithPromo() throws Exception {

        final Total itemTotal = new TotalImpl().add(new TotalImpl(
                new BigDecimal("100"),
                new BigDecimal("90"),
                new BigDecimal("80"),
                false,
                null,
                new BigDecimal("80"),
                new BigDecimal("13.33"), // VAT 20%
                new BigDecimal("80"),
                new BigDecimal("5.00"),
                new BigDecimal("5.00"),
                false,
                null,
                new BigDecimal("0.83"), // VAT 20%
                new BigDecimal("5.00"),
                new BigDecimal("85"),
                new BigDecimal("14.17"), // VAT 20%
                new BigDecimal("105"),
                new BigDecimal("85")
        ));

        final Total orderTotal = itemTotal.add(new TotalImpl(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                true,
                "ORDER-5%,ORDER-5%,ANOTHER-5%", // duplicates
                new BigDecimal("-8"), // -10%
                new BigDecimal("-1.6"),
                new BigDecimal("-8"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                false,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("-8"),
                new BigDecimal("-1.6"),
                BigDecimal.ZERO,
                new BigDecimal("-8")
            ));

        final Total total = orderTotal.add(new TotalImpl(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                false,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("-0.75"), // -15%
                true,
                "SHIP-10%,SHIP-10%,ANOTHER-5%", // duplicates
                new BigDecimal("-0.13"),
                new BigDecimal("-0.75"),
                new BigDecimal("-0.75"),
                new BigDecimal("-0.13"),
                BigDecimal.ZERO,
                new BigDecimal("-0.75")
            ));

        assertEquals("100.00", total.getListSubTotal().toString());
        assertEquals("90.00", total.getSaleSubTotal().toString());
        assertEquals("80.00", total.getPriceSubTotal().toString());
        assertTrue(total.isOrderPromoApplied());
        assertEquals("ORDER-5%,ANOTHER-5%", total.getAppliedOrderPromo());
        assertEquals("72.00", total.getSubTotal().toString());
        assertEquals("11.73", total.getSubTotalTax().toString());
        assertEquals("72.00", total.getSubTotalAmount().toString());
        assertEquals("5.00", total.getDeliveryListCost().toString());
        assertEquals("4.25", total.getDeliveryCost().toString());
        assertTrue(total.isDeliveryPromoApplied());
        assertEquals("SHIP-10%,ANOTHER-5%", total.getAppliedDeliveryPromo());
        assertEquals("0.70", total.getDeliveryTax().toString());
        assertEquals("4.25", total.getDeliveryCostAmount().toString());
        assertEquals("76.25", total.getTotal().toString());
        assertEquals("12.44", total.getTotalTax().toString());
        assertEquals("105.00", total.getListTotalAmount().toString());
        assertEquals("76.25", total.getTotalAmount().toString());

    }
}
