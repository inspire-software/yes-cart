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
import org.yes.cart.domain.entity.SkuPrice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/28/12
 * Time: 8:26 PM
 */
public class SkuPriceEntityTest {

    @Test
    public void testGetSalePriceForCalculation() throws Exception {

        SkuPrice skuPrice = new SkuPriceEntity();
        assertNull(skuPrice.getSalePriceForCalculation());

        skuPrice.setSalePrice(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, skuPrice.getSalePriceForCalculation());   //simple sale without end and start

        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 10, 10);
        skuPrice.setSalefrom(calendar.getTime());
        assertNull(skuPrice.getSalePriceForCalculation()); //sale starts in future

        calendar.set(2020, 12, 12);
        skuPrice.setSaleto(calendar.getTime());
        assertNull(skuPrice.getSalePriceForCalculation()); // sale start and end in future

        calendar.set(2010, 12, 12);
        skuPrice.setSalefrom(calendar.getTime());
        assertEquals(BigDecimal.TEN, skuPrice.getSalePriceForCalculation()); // sale in range

        calendar.set(2010, 12, 12);
        skuPrice.setSaleto(calendar.getTime());
        assertNull(skuPrice.getSalePriceForCalculation()); // sale start and end in past

        skuPrice.setSalefrom(null);
        calendar.set(2010, 12, 12);
        skuPrice.setSaleto(calendar.getTime());
        assertNull(skuPrice.getSalePriceForCalculation());  // end in past

        calendar.set(2020, 12, 12);
        skuPrice.setSaleto(calendar.getTime());
        assertEquals(BigDecimal.TEN, skuPrice.getSalePriceForCalculation()); // not yet end

    }
}
