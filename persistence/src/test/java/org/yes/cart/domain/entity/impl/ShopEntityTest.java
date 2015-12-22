package org.yes.cart.domain.entity.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.ShopUrl;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

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
}