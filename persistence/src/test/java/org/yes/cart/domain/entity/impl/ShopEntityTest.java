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
        assertNull(shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("GB", "en", "B2B"));

    }

    @Test
    public void testGetAddressFormatByCountryAndLocaleBaseCfgOnly() throws Exception {

        final ShopEntity shopEntity = new ShopEntity();

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

        shopEntity.setAttributes(Arrays.asList(av_def, av_code, av_lang, av_code_lang, av_type, av_type_lang, av_code_type, av_code_lang_type));

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

        }});

        assertEquals("f1_gb_en", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("GB", "en", null));
        assertEquals("f1_gb_B2B_en", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("GB", "en", "B2B"));
        assertEquals("f1_gb_en", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("GB", "en", "B2C"));
        assertEquals("f1_gb", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("GB", "fr", null));
        assertEquals("f1_gb_B2B", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("GB", "fr", "B2B"));
        assertEquals("f1_gb", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("GB", "fr", "B2C"));
        assertEquals("f1_en", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("DE", "en", null));
        assertEquals("f1_B2B_en", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("DE", "en", "B2B"));
        assertEquals("f1_en", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("DE", "en", "B2C"));
        assertEquals("f1", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("DE", "de", null));
        assertEquals("f1_B2B", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("DE", "de", "B2B"));
        assertEquals("f1", shopEntity.getAddressFormatByCountryAndCustomerTypeAndLocale("DE", "de", "B2C"));

        context.assertIsSatisfied();

    }
}