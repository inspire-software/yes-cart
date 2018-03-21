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
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.ShopSearchSupportService;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 18:24
 */
public class CentralViewResolverCategoryImplTest {


    private final Mockery context = new JUnit4Mockery();

    private static final ShoppingCartImpl CART_2 = new ShoppingCartImpl();
    private static final ShoppingCartImpl CART_11 = new ShoppingCartImpl();
    static {
        CART_2.getShoppingContext().setShopId(2L);
        CART_2.setCurrentLocale("en");
        CART_11.getShoppingContext().setShopId(11L);
        CART_11.setCurrentLocale("en");
    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNA() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put("someparam", "1");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNaN() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "abc");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryTemplate() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(2L, 10L);
            will(returnValue("cattemplate"));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART_2);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals("cattemplate", resolved.getFirst());
            assertEquals(CentralViewLabel.CATEGORY, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelProductsTemplate() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(2L, 10L);
            will(returnValue("products"));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART_2);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals("products", resolved.getFirst());
            assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelSubCategoriesTemplate() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(2L, 10L);
            will(returnValue("subcats"));
        }});

        try {
            ApplicationDirector.setShoppingCart(CART_2);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals("subcats", resolved.getFirst());
            assertEquals(CentralViewLabel.SUBCATEGORIES_LIST, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateNoProductsNoSubCats() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(11L, 10L); will(returnValue(" "));
            oneOf(shopSearchSupportService).getSearchCategoriesIds(10L, 11L); will(returnValue(new Pair<>(Collections.singletonList(10L), true)));
            oneOf(searchQueryFactory).getFilteredNavigationQueryChain(11L, 11L, "en", Collections.singletonList(10L), true, null); will(returnValue(hasProducts));
            oneOf(productService).getProductQty(hasProducts); will(returnValue(0));
            oneOf(categoryService).isCategoryHasChildren(10L); will(returnValue(false));
        }});

        try {

            ApplicationDirector.setShoppingCart(CART_11);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.CATEGORY, resolved.getFirst());
            assertEquals(CentralViewLabel.CATEGORY, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateNoProductsSubCats() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(11L, 10L); will(returnValue(" "));
            oneOf(shopSearchSupportService).getSearchCategoriesIds(10L, 11L); will(returnValue(new Pair<>(Collections.singletonList(10L), false)));
            oneOf(searchQueryFactory).getFilteredNavigationQueryChain(11L, 11L, "en", Collections.singletonList(10L), false, null); will(returnValue(hasProducts));
            oneOf(productService).getProductQty(hasProducts); will(returnValue(0));
            oneOf(categoryService).isCategoryHasChildren(10L); will(returnValue(true));
        }});

        try {

            ApplicationDirector.setShoppingCart(CART_11);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.SUBCATEGORIES_LIST, resolved.getFirst());
            assertEquals(CentralViewLabel.SUBCATEGORIES_LIST, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateProductsNoCat() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(11L, 10L); will(returnValue(" "));
            oneOf(shopSearchSupportService).getSearchCategoriesIds(10L, 11L); will(returnValue(new Pair<>(Collections.singletonList(10L), true)));
            oneOf(searchQueryFactory).getFilteredNavigationQueryChain(11L, 11L, "en", Collections.singletonList(10L), true, null); will(returnValue(hasProducts));
            oneOf(productService).getProductQty(hasProducts); will(returnValue(1));
            oneOf(shopService).getShopCategorySearchTemplate(11L, 10L); will(returnValue(null));
        }});

        try {

            ApplicationDirector.setShoppingCart(CART_11);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getFirst());
            assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateProductsCatTypeNoTemplate() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(11L, 10L); will(returnValue(" "));
            oneOf(shopSearchSupportService).getSearchCategoriesIds(10L, 11L); will(returnValue(new Pair<>(Collections.singletonList(10L), false)));
            oneOf(searchQueryFactory).getFilteredNavigationQueryChain(11L, 11L, "en", Collections.singletonList(10L), false, null); will(returnValue(hasProducts));
            oneOf(productService).getProductQty(hasProducts); will(returnValue(1));
            oneOf(shopService).getShopCategorySearchTemplate(11L, 10L); will(returnValue(" "));
        }});

        try {

            ApplicationDirector.setShoppingCart(CART_11);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getFirst());
            assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateProductsCatTypeTemplate() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ShopSearchSupportService shopSearchSupportService = context.mock(ShopSearchSupportService.class, "shopSearchSupportService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final SearchQueryFactory searchQueryFactory = context.mock(SearchQueryFactory.class, "ftQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(shopService, categoryService, shopSearchSupportService, productService, searchQueryFactory);

        context.checking(new Expectations() {{
            oneOf(shopService).getShopCategoryTemplate(11L, 10L); will(returnValue(" "));
            oneOf(shopSearchSupportService).getSearchCategoriesIds(10L, 11L); will(returnValue(new Pair<>(Collections.singletonList(10L), true)));
            oneOf(searchQueryFactory).getFilteredNavigationQueryChain(11L, 11L, "en", Collections.singletonList(10L), true, null); will(returnValue(hasProducts));
            oneOf(productService).getProductQty(hasProducts); will(returnValue(1));
            oneOf(shopService).getShopCategorySearchTemplate(11L, 10L); will(returnValue("prodtypesearch"));
        }});

        try {

            ApplicationDirector.setShoppingCart(CART_11);

            final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
                put(WebParametersKeys.CATEGORY_ID, "10");
            }});

            assertNotNull(resolved);
            assertEquals("prodtypesearch", resolved.getFirst());
            assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        } finally {

            ApplicationDirector.clear();

        }

        context.assertIsSatisfied();

    }


}
