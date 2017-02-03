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

package org.yes.cart.service.domain.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 13:41
 */
public class ShopCustomerCustomisationSupportImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetShopSupportedCustomerTypesNull() throws Exception {

        final Shop shop = context.mock(Shop.class);

        context.checking(new Expectations() {{
            one(shop).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES); will(returnValue(null));
        }});

        final List<Pair<String, I18NModel>> types = new ShopCustomerCustomisationSupportImpl(null).getSupportedCustomerTypes(shop);

        assertNotNull(types);
        assertTrue(types.isEmpty());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShopSupportedCustomerTypesEmpty() throws Exception {

        final Shop shop = context.mock(Shop.class);
        final AttrValueShop av = context.mock(AttrValueShop.class);

        context.checking(new Expectations() {{
            one(shop).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES); will(returnValue(av));
            allowing(av).getVal(); will(returnValue(""));
        }});

        final List<Pair<String, I18NModel>> types = new ShopCustomerCustomisationSupportImpl(null).getSupportedCustomerTypes(shop);

        assertNotNull(types);
        assertTrue(types.isEmpty());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShopSupportedCustomerTypesNoI18n() throws Exception {

        final Shop shop = context.mock(Shop.class);
        final AttrValueShop av = context.mock(AttrValueShop.class);

        context.checking(new Expectations() {{
            one(shop).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES); will(returnValue(av));
            allowing(av).getVal(); will(returnValue("B2B,B2C"));
            allowing(av).getDisplayVal(); will(returnValue(null));
        }});

        final List<Pair<String, I18NModel>> types = new ShopCustomerCustomisationSupportImpl(null).getSupportedCustomerTypes(shop);

        assertNotNull(types);
        assertEquals(2, types.size());
        assertEquals("B2B", types.get(0).getFirst());
        assertEquals("B2B", types.get(0).getSecond().getValue("en"));
        assertEquals("B2C", types.get(1).getFirst());
        assertEquals("B2C", types.get(1).getSecond().getValue("en"));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetShopSupportedCustomerTypesI18n() throws Exception {

        final Shop shop = context.mock(Shop.class);
        final AttrValueShop av = context.mock(AttrValueShop.class);

        context.checking(new Expectations() {{
            one(shop).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES); will(returnValue(av));
            allowing(av).getVal(); will(returnValue("B2B,B2C"));
            allowing(av).getDisplayVal(); will(returnValue("en#~#Private#~#de#~#Private DE,Company DE"));
        }});

        final List<Pair<String, I18NModel>> types = new ShopCustomerCustomisationSupportImpl(null).getSupportedCustomerTypes(shop);

        assertNotNull(types);
        assertEquals(2, types.size());
        assertEquals("B2B", types.get(0).getFirst());
        assertEquals("Private", types.get(0).getSecond().getValue("en"));
        assertEquals("Private DE", types.get(0).getSecond().getValue("de"));
        assertEquals("B2C", types.get(1).getFirst());
        assertEquals("B2C", types.get(1).getSecond().getValue("en"));
        assertEquals("Company DE", types.get(1).getSecond().getValue("de"));

        context.assertIsSatisfied();

    }
}