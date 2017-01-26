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

package org.yes.cart.web.support.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 17:49
 */
public class CentralViewResolverSearchImplTest {


    private final Mockery context = new JUnit4Mockery();

    private static final Set<String> NAV = new HashSet<String>(Arrays.asList("nav1", "nav2"));

    private static final ShoppingCartImpl CART = new ShoppingCartImpl();
    static {
        CART.getShoppingContext().setShopId(2L);
    }

    @Test
    public void testResolveMainPanelRendererLabelNA() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes();
            will(returnValue(NAV));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put("someparam", "1");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelQueryGlobal() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.QUERY, "1");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelFilteredGlobal() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(NAV));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put("nav1", "1");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelQueryCategoryNaN() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.QUERY, "1");
            put(WebParametersKeys.CATEGORY_ID, "abc");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelFilteredCategoryNaN() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(NAV));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put("nav2", "1");
            put(WebParametersKeys.CATEGORY_ID, "abc");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());
        context.assertIsSatisfied();

    }


    @Test
    public void testResolveMainPanelRendererLabelQueryCategoryInvalid() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(shopService).getShopCategorySearchTemplate(2L, 1L); will(returnValue(null));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.QUERY, "1");
                put(WebParametersKeys.CATEGORY_ID, "1");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());

        } finally {
            ApplicationDirector.clear();
        }
        context.assertIsSatisfied();

    }


    @Test
    public void testResolveMainPanelRendererLabelFilteredCategoryInvalid() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(NAV));
            one(shopService).getShopCategorySearchTemplate(2L, 1L); will(returnValue(null));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put("nav1", "1");
                put(WebParametersKeys.CATEGORY_ID, "1");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());

        } finally {
            ApplicationDirector.clear();
        }

        context.assertIsSatisfied();

    }



    @Test
    public void testResolveMainPanelRendererLabelQueryCategoryTypeNoTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(shopService).getShopCategorySearchTemplate(2L, 1L); will(returnValue(""));
        }});


        try {
            ApplicationDirector.setShoppingCart(CART);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.QUERY, "1");
                put(WebParametersKeys.CATEGORY_ID, "1");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());

        } finally {
            ApplicationDirector.clear();
        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelFilteredCategoryTypeNoTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(NAV));
            one(shopService).getShopCategorySearchTemplate(2L, 1L); will(returnValue(""));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put("nav1", "1");
                put(WebParametersKeys.CATEGORY_ID, "1");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getFirst());
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());

        } finally {
            ApplicationDirector.clear();
        }

        context.assertIsSatisfied();

    }


    @Test
    public void testResolveMainPanelRendererLabelQueryCategoryTypeTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(shopService).getShopCategorySearchTemplate(2L, 1L); will(returnValue("prodtypetemplate"));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.QUERY, "1");
                put(WebParametersKeys.CATEGORY_ID, "1");
            }});

            assertNotNull(resolved);
            assertEquals("prodtypetemplate", resolved.getFirst());
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());

        } finally {
            ApplicationDirector.clear();
        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelFilteredCategoryTypeTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        CentralViewResolverSearchImpl resolver = new CentralViewResolverSearchImpl(shopService, attributeService, categoryService);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes();
            will(returnValue(NAV));
            one(shopService).getShopCategorySearchTemplate(2L, 1L);
            will(returnValue("prodtypetemplate"));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put("nav1", "1");
                put(WebParametersKeys.CATEGORY_ID, "1");
            }});

            assertNotNull(resolved);
            assertEquals("prodtypetemplate", resolved.getFirst());
            assertEquals(CentralViewLabel.SEARCH_LIST, resolved.getSecond());

        } finally {
            ApplicationDirector.clear();
        }

        context.assertIsSatisfied();

    }


}
