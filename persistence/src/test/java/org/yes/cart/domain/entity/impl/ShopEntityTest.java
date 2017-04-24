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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ShopUrl;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 22/12/2015
 * Time: 19:50
 */
public class ShopEntityTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetDefaultShopUrlPrimary() throws Exception {

        final ShopEntity shopEntity = new ShopEntity();

        final ShopUrl u1 = context.mock(ShopUrl.class, "u1");
        final ShopUrl u2 = context.mock(ShopUrl.class, "u2");
        final ShopUrl u3 = context.mock(ShopUrl.class, "u3");

        context.checking(new Expectations() {{
            allowing(u1).isPrimary(); will(returnValue(false));
            allowing(u1).getUrl(); will(returnValue("www.u1.com"));
            allowing(u2).isPrimary(); will(returnValue(true));
            allowing(u2).getUrl(); will(returnValue("www.u2.com"));
            allowing(u3).isPrimary(); will(returnValue(false));
            allowing(u3).getUrl(); will(returnValue("www.u3.com"));
        }});

        shopEntity.setShopUrl(new HashSet<ShopUrl>(Arrays.asList(u1, u2, u3)));

        assertEquals("http://www.u2.com", shopEntity.getDefaultShopUrl());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetDefaultShopUrlNoPrimary() throws Exception {

        final ShopEntity shopEntity = new ShopEntity();

        final ShopUrl u1 = context.mock(ShopUrl.class, "u1");
        final ShopUrl u2 = context.mock(ShopUrl.class, "u2");
        final ShopUrl u3 = context.mock(ShopUrl.class, "u3");

        context.checking(new Expectations() {{
            allowing(u1).isPrimary(); will(returnValue(false));
            allowing(u1).getUrl(); will(returnValue("localhost"));
            allowing(u2).isPrimary(); will(returnValue(false));
            allowing(u2).getUrl(); will(returnValue("www.u2.com"));
            allowing(u3).isPrimary(); will(returnValue(false));
            allowing(u3).getUrl(); will(returnValue("127.0.0.1"));
        }});

        shopEntity.setShopUrl(new HashSet<ShopUrl>(Arrays.asList(u1, u2, u3)));

        assertEquals("http://www.u2.com", shopEntity.getDefaultShopUrl());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetAddressFormatByCountryAndLocaleNoCfg() throws Exception {

        final ShopEntity shopEntity = new ShopEntity();
        assertNull(shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", "B2B", "B"));

    }

    @Test
    public void testGetAddressFormatByCountryAndLocaleBaseCfgOnly() throws Exception {

        final ShopEntity shopEntity = new ShopEntity();

        final AttrValueShop av_def_b = context.mock(AttrValueShop.class, "av_b");
        final Attribute a_def_b = context.mock(Attribute.class, "a_b");
        final AttrValueShop av_code_b = context.mock(AttrValueShop.class, "av_code_b");
        final Attribute a_code_b = context.mock(Attribute.class, "a_code_b");
        final AttrValueShop av_lang_b = context.mock(AttrValueShop.class, "av_lang_b");
        final Attribute a_lang_b = context.mock(Attribute.class, "a_lang_b");
        final AttrValueShop av_code_lang_b = context.mock(AttrValueShop.class, "av_code_lang_b");
        final Attribute a_code_lang_b = context.mock(Attribute.class, "a_code_lang_b");
        final AttrValueShop av_type_b = context.mock(AttrValueShop.class, "av_type_b");
        final Attribute a_type_b = context.mock(Attribute.class, "a_type_b");
        final AttrValueShop av_type_lang_b = context.mock(AttrValueShop.class, "av_type_lang_b");
        final Attribute a_type_lang_b = context.mock(Attribute.class, "a_type_lang_b");
        final AttrValueShop av_code_type_b = context.mock(AttrValueShop.class, "av_code_type_b");
        final Attribute a_code_type_b = context.mock(Attribute.class, "a_code_type_b");
        final AttrValueShop av_code_lang_type_b = context.mock(AttrValueShop.class, "av_code_lang_type_b");
        final Attribute a_code_lang_type_b = context.mock(Attribute.class, "a_code_lang_type_b");

        final AttrValueShop av_def_s = context.mock(AttrValueShop.class, "av_s");
        final Attribute a_def_s = context.mock(Attribute.class, "a_s");
        final AttrValueShop av_code_s = context.mock(AttrValueShop.class, "av_code_s");
        final Attribute a_code_s = context.mock(Attribute.class, "a_code_s");
        final AttrValueShop av_lang_s = context.mock(AttrValueShop.class, "av_lang_s");
        final Attribute a_lang_s = context.mock(Attribute.class, "a_lang_s");
        final AttrValueShop av_code_lang_s = context.mock(AttrValueShop.class, "av_code_lang_s");
        final Attribute a_code_lang_s = context.mock(Attribute.class, "a_code_lang_s");
        final AttrValueShop av_type_s = context.mock(AttrValueShop.class, "av_type_s");
        final Attribute a_type_s = context.mock(Attribute.class, "a_type_s");
        final AttrValueShop av_type_lang_s = context.mock(AttrValueShop.class, "av_type_lang_s");
        final Attribute a_type_lang_s = context.mock(Attribute.class, "a_type_lang_s");
        final AttrValueShop av_code_type_s = context.mock(AttrValueShop.class, "av_code_type_s");
        final Attribute a_code_type_s = context.mock(Attribute.class, "a_code_type_s");
        final AttrValueShop av_code_lang_type_s = context.mock(AttrValueShop.class, "av_code_lang_type_s");
        final Attribute a_code_lang_type_s = context.mock(Attribute.class, "a_code_lang_type_s");

        final AttrValueShop av_def = context.mock(AttrValueShop.class, "av");
        final Attribute a_def = context.mock(Attribute.class, "a");
        final AttrValueShop av_code = context.mock(AttrValueShop.class, "av_code");
        final Attribute a_code = context.mock(Attribute.class, "a_code");
        final AttrValueShop av_lang = context.mock(AttrValueShop.class, "av_lang");
        final Attribute a_lang = context.mock(Attribute.class, "a_lang");
        final AttrValueShop av_code_lang = context.mock(AttrValueShop.class, "av_code_lang");
        final Attribute a_code_lang = context.mock(Attribute.class, "a_code_lang");
        final AttrValueShop av_type = context.mock(AttrValueShop.class, "av_type");
        final Attribute a_type = context.mock(Attribute.class, "a_type");
        final AttrValueShop av_type_lang = context.mock(AttrValueShop.class, "av_type_lang");
        final Attribute a_type_lang = context.mock(Attribute.class, "a_type_lang");
        final AttrValueShop av_code_type = context.mock(AttrValueShop.class, "av_code_type");
        final Attribute a_code_type = context.mock(Attribute.class, "a_code_type");
        final AttrValueShop av_code_lang_type = context.mock(AttrValueShop.class, "av_code_lang_type");
        final Attribute a_code_lang_type = context.mock(Attribute.class, "a_code_lang_type");


        shopEntity.setAttributes(Arrays.asList(
                av_def, av_code, av_lang, av_code_lang, av_type, av_type_lang, av_code_type, av_code_lang_type,
                av_def_b, av_code_b, av_lang_b, av_code_lang_b, av_type_b, av_type_lang_b, av_code_type_b, av_code_lang_type_b,
                av_def_s, av_code_s, av_lang_s, av_code_lang_s, av_type_s, av_type_lang_s, av_code_type_s, av_code_lang_type_s
        ));

        context.checking(new Expectations() {{

            allowing(av_def).getAttribute(); will(returnValue(a_def));
            allowing(a_def).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX));
            allowing(av_def).getVal();will(returnValue("f1"));

            allowing(av_code).getAttribute(); will(returnValue(a_code));
            allowing(a_code).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_GB"));
            allowing(av_code).getVal(); will(returnValue("f1_gb"));

            allowing(av_lang).getAttribute(); will(returnValue(a_lang));
            allowing(a_lang).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_en"));
            allowing(av_lang).getVal(); will(returnValue("f1_en"));

            allowing(av_code_lang).getAttribute(); will(returnValue(a_code_lang));
            allowing(a_code_lang).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_GB_en"));
            allowing(av_code_lang).getVal(); will(returnValue("f1_gb_en"));

            allowing(av_type).getAttribute(); will(returnValue(a_type));
            allowing(a_type).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B2B"));
            allowing(av_type).getVal(); will(returnValue("f1_B2B"));

            allowing(av_type_lang).getAttribute(); will(returnValue(a_type_lang));
            allowing(a_type_lang).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_en_B2B"));
            allowing(av_type_lang).getVal(); will(returnValue("f1_B2B_en"));

            allowing(av_code_type).getAttribute(); will(returnValue(a_code_type));
            allowing(a_code_type).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_GB_B2B"));
            allowing(av_code_type).getVal(); will(returnValue("f1_gb_B2B"));

            allowing(av_code_lang_type).getAttribute(); will(returnValue(a_code_lang_type));
            allowing(a_code_lang_type).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_GB_en_B2B"));
            allowing(av_code_lang_type).getVal(); will(returnValue("f1_gb_B2B_en"));




            allowing(av_def_s).getAttribute(); will(returnValue(a_def_s));
            allowing(a_def_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S"));
            allowing(av_def_s).getVal();will(returnValue("f1_S"));

            allowing(av_code_s).getAttribute(); will(returnValue(a_code_s));
            allowing(a_code_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S_GB"));
            allowing(av_code_s).getVal(); will(returnValue("f1_gb_S"));

            allowing(av_lang_s).getAttribute(); will(returnValue(a_lang_s));
            allowing(a_lang_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S_en"));
            allowing(av_lang_s).getVal(); will(returnValue("f1_en_S"));

            allowing(av_code_lang_s).getAttribute(); will(returnValue(a_code_lang_s));
            allowing(a_code_lang_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S_GB_en"));
            allowing(av_code_lang_s).getVal(); will(returnValue("f1_gb_en_S"));

            allowing(av_type_s).getAttribute(); will(returnValue(a_type_s));
            allowing(a_type_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S_B2B"));
            allowing(av_type_s).getVal(); will(returnValue("f1_B2B_S"));

            allowing(av_type_lang_s).getAttribute(); will(returnValue(a_type_lang_s));
            allowing(a_type_lang_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S_en_B2B"));
            allowing(av_type_lang_s).getVal(); will(returnValue("f1_B2B_en_S"));

            allowing(av_code_type_s).getAttribute(); will(returnValue(a_code_type_s));
            allowing(a_code_type_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S_GB_B2B"));
            allowing(av_code_type_s).getVal(); will(returnValue("f1_gb_B2B_S"));

            allowing(av_code_lang_type_s).getAttribute(); will(returnValue(a_code_lang_type_s));
            allowing(a_code_lang_type_s).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_S_GB_en_B2B"));
            allowing(av_code_lang_type_s).getVal(); will(returnValue("f1_gb_B2B_en_S"));




            allowing(av_def_b).getAttribute(); will(returnValue(a_def_b));
            allowing(a_def_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B"));
            allowing(av_def_b).getVal();will(returnValue("f1_B"));

            allowing(av_code_b).getAttribute(); will(returnValue(a_code_b));
            allowing(a_code_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B_GB"));
            allowing(av_code_b).getVal(); will(returnValue("f1_gb_B"));

            allowing(av_lang_b).getAttribute(); will(returnValue(a_lang_b));
            allowing(a_lang_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B_en"));
            allowing(av_lang_b).getVal(); will(returnValue("f1_en_B"));

            allowing(av_code_lang_b).getAttribute(); will(returnValue(a_code_lang_b));
            allowing(a_code_lang_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B_GB_en"));
            allowing(av_code_lang_b).getVal(); will(returnValue("f1_gb_en_B"));

            allowing(av_type_b).getAttribute(); will(returnValue(a_type_b));
            allowing(a_type_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B_B2B"));
            allowing(av_type_b).getVal(); will(returnValue("f1_B2B_B"));

            allowing(av_type_lang_b).getAttribute(); will(returnValue(a_type_lang_b));
            allowing(a_type_lang_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B_en_B2B"));
            allowing(av_type_lang_b).getVal(); will(returnValue("f1_B2B_en_B"));

            allowing(av_code_type_b).getAttribute(); will(returnValue(a_code_type_b));
            allowing(a_code_type_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B_GB_B2B"));
            allowing(av_code_type_b).getVal(); will(returnValue("f1_gb_B2B_B"));

            allowing(av_code_lang_type_b).getAttribute(); will(returnValue(a_code_lang_type_b));
            allowing(a_code_lang_type_b).getCode(); will(returnValue(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_B_GB_en_B2B"));
            allowing(av_code_lang_type_b).getVal(); will(returnValue("f1_gb_B2B_en_B"));



        }});

        assertEquals("f1_gb_en_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", null, "B"));
        assertEquals("f1_gb_en_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", null, "S"));
        assertEquals("f1_gb_en", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", null, "Z"));
        assertEquals("f1_gb_B2B_en_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", "B2B", "B"));
        assertEquals("f1_gb_B2B_en_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", "B2B", "S"));
        assertEquals("f1_gb_B2B_en", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", "B2B", "Z"));
        assertEquals("f1_gb_en_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", "B2C", "B"));
        assertEquals("f1_gb_en_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", "B2C", "S"));
        assertEquals("f1_gb_en", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "en", "B2C", "Z"));
        assertEquals("f1_gb_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", null, "B"));
        assertEquals("f1_gb_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", null, "S"));
        assertEquals("f1_gb", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", null, "Z"));
        assertEquals("f1_gb_B2B_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", "B2B", "B"));
        assertEquals("f1_gb_B2B_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", "B2B", "S"));
        assertEquals("f1_gb_B2B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", "B2B", "Z"));
        assertEquals("f1_gb_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", "B2C", "B"));
        assertEquals("f1_gb_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", "B2C", "S"));
        assertEquals("f1_gb", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("GB", "fr", "B2C", "Z"));
        assertEquals("f1_en_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", null, "B"));
        assertEquals("f1_en_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", null, "S"));
        assertEquals("f1_en", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", null, "Z"));
        assertEquals("f1_B2B_en_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", "B2B", "B"));
        assertEquals("f1_B2B_en_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", "B2B", "S"));
        assertEquals("f1_B2B_en", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", "B2B", "Z"));
        assertEquals("f1_en_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", "B2C", "B"));
        assertEquals("f1_en_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", "B2C", "S"));
        assertEquals("f1_en", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "en", "B2C", "Z"));
        assertEquals("f1_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", null, "B"));
        assertEquals("f1_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", null, "S"));
        assertEquals("f1", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", null, "Z"));
        assertEquals("f1_B2B_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", "B2B", "B"));
        assertEquals("f1_B2B_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", "B2B", "S"));
        assertEquals("f1_B2B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", "B2B", "Z"));
        assertEquals("f1_B", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", "B2C", "B"));
        assertEquals("f1_S", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", "B2C", "S"));
        assertEquals("f1", shopEntity.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType("DE", "de", "B2C", "Z"));

        context.assertIsSatisfied();

    }
}