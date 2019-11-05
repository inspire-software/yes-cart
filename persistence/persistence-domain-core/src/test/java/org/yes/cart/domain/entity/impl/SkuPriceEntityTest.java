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
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/28/12
 * Time: 8:26 PM
 */
public class SkuPriceEntityTest {


    @Test
    public void testGetSalePriceForCalculationDefaultNotSellable() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNull(listAndSale.getFirst());
        assertNull(listAndSale.getSecond());
    }

    @Test
    public void testGetSalePriceForCalculationSaleOnlyRevertsToRegular() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setSalePrice(new BigDecimal("80.00"));
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNotNull(listAndSale.getFirst());
        assertEquals("80.00", listAndSale.getFirst().toPlainString());
        assertNull(listAndSale.getSecond());
    }

    @Test
    public void testGetSalePriceForCalculationListOnly() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setRegularPrice(new BigDecimal("100.00"));
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNotNull(listAndSale.getFirst());
        assertEquals("100.00", listAndSale.getFirst().toPlainString());
        assertNull(listAndSale.getSecond());
    }

    @Test
    public void testGetSalePriceForCalculationWithSale() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setRegularPrice(new BigDecimal("100.00"));
        skuPrice.setSalePrice(new BigDecimal("80.00"));
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNotNull(listAndSale.getFirst());
        assertEquals("100.00", listAndSale.getFirst().toPlainString());
        assertNotNull(listAndSale.getSecond());
        assertEquals("80.00", listAndSale.getSecond().toPlainString());
    }

    @Test
    public void testGetSalePriceForCalculationGift() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setRegularPrice(new BigDecimal("0.00"));
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNotNull(listAndSale.getFirst());
        assertEquals("0.00", listAndSale.getFirst().toPlainString());
        assertNull(listAndSale.getSecond());
    }

    @Test
    public void testGetSalePriceForCalculationSaleGift() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setRegularPrice(new BigDecimal("100.00"));
        skuPrice.setSalePrice(new BigDecimal("0.00"));
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNotNull(listAndSale.getFirst());
        assertEquals("100.00", listAndSale.getFirst().toPlainString());
        assertNotNull(listAndSale.getSecond());
        assertEquals("0.00", listAndSale.getSecond().toPlainString());
    }

    @Test
    public void testGetSalePriceForCalculationSaleGiftNoRegular() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setSalePrice(new BigDecimal("0.00"));
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNotNull(listAndSale.getFirst());
        assertEquals("0.00", listAndSale.getFirst().toPlainString());
        assertNull(listAndSale.getSecond());

    }


    @Test
    public void testGetSalePriceForCalculationOverpriced() throws Exception {

        final SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setRegularPrice(new BigDecimal("100.00"));
        skuPrice.setSalePrice(new BigDecimal("120.00"));
        final Pair<BigDecimal, BigDecimal> listAndSale = skuPrice.getSalePriceForCalculation();
        assertNotNull(listAndSale);

        assertNotNull(listAndSale.getFirst());
        assertEquals("120.00", listAndSale.getFirst().toPlainString());
        assertNull(listAndSale.getSecond());

    }


}
