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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.ProductSku;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * User: Igor Azarny
 * Date: 6/6/13
 * Time: 3:26 PM
 */
public class ProductEntityTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetLocale() {
        ProductEntity pe = new ProductEntity();
        assertEquals("en", pe.getLocale("PRODUCT_DESCRIPTION_en"));
    }

    @Test
    public void testGetDefaultSkuSingle() throws Exception {

        final ProductSku sku1 = context.mock(ProductSku.class, "sku1");
        ProductEntity pe = new ProductEntity();
        pe.setSku(Collections.singleton(sku1));

        assertSame(sku1, pe.getDefaultSku());

    }

    @Test
    public void testGetDefaultSkuMultipleSameRank() throws Exception {

        final ProductSku sku1 = context.mock(ProductSku.class, "sku1");
        final ProductSku sku2 = context.mock(ProductSku.class, "sku2");
        ProductEntity pe = new ProductEntity();
        pe.setSku(Arrays.asList(sku1, sku2));

        context.checking(new Expectations() {{
            allowing(sku1).getRank(); will(returnValue(0));
            allowing(sku2).getRank(); will(returnValue(0));
        }});

        assertSame(sku1, pe.getDefaultSku());

    }

    @Test
    public void testGetDefaultSkuMultipleDifferentRankDesc() throws Exception {

        final ProductSku sku1 = context.mock(ProductSku.class, "sku1");
        final ProductSku sku2 = context.mock(ProductSku.class, "sku2");
        ProductEntity pe = new ProductEntity();
        pe.setSku(Arrays.asList(sku1, sku2));

        context.checking(new Expectations() {{
            allowing(sku1).getRank(); will(returnValue(1));
            allowing(sku2).getRank(); will(returnValue(0));
        }});

        assertSame(sku2, pe.getDefaultSku());

    }

    @Test
    public void testGetDefaultSkuMultipleDifferentRankAsc() throws Exception {

        final ProductSku sku1 = context.mock(ProductSku.class, "sku1");
        final ProductSku sku2 = context.mock(ProductSku.class, "sku2");
        ProductEntity pe = new ProductEntity();
        pe.setSku(Arrays.asList(sku1, sku2));

        context.checking(new Expectations() {{
            allowing(sku1).getRank(); will(returnValue(0));
            allowing(sku2).getRank(); will(returnValue(1));
        }});

        assertSame(sku1, pe.getDefaultSku());

    }
}
