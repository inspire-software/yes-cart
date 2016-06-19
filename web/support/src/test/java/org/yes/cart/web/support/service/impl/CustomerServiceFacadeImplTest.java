package org.yes.cart.web.support.service.impl;

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
 * Date: 01/02/2016
 * Time: 21:13
 */
public class CustomerServiceFacadeImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetShopSupportedCustomerTypesNull() throws Exception {

        final Shop shop = context.mock(Shop.class);

        context.checking(new Expectations() {{
            one(shop).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES); will(returnValue(null));
        }});

        final List<Pair<String, I18NModel>> types = new CustomerServiceFacadeImpl(null, null, null, null).getShopSupportedCustomerTypes(shop);

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

        final List<Pair<String, I18NModel>> types = new CustomerServiceFacadeImpl(null, null, null, null).getShopSupportedCustomerTypes(shop);

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

        final List<Pair<String, I18NModel>> types = new CustomerServiceFacadeImpl(null, null, null, null).getShopSupportedCustomerTypes(shop);

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

        final List<Pair<String, I18NModel>> types = new CustomerServiceFacadeImpl(null, null, null, null).getShopSupportedCustomerTypes(shop);

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