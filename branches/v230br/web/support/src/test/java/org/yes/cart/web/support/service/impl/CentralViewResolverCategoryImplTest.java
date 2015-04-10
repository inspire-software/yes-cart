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

package org.yes.cart.web.support.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 18:24
 */
public class CentralViewResolverCategoryImplTest {


    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testResolveMainPanelRendererLabelCategoryNA() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put("someparam", "1");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNaN() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "abc");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        context.checking(new Expectations() {{
            one(categoryService).getCategoryTemplate(10L); will(returnValue("cattemplate"));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "10");
        }});

        assertNotNull(resolved);
        assertEquals("cattemplate", resolved.getFirst());
        assertEquals(CentralViewLabel.CATEGORY, resolved.getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateNoProductsNoSubCats() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        context.checking(new Expectations() {{
            one(categoryService).getCategoryTemplate(10L); will(returnValue(" "));
            one(categoryService).isSearchInSubcategory(10L, 11L); will(returnValue(true));
            one(categoryService).getChildCategoriesRecursiveIds(10L); will(returnValue(new HashSet<Long>(Arrays.asList(10L))));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(hasProducts));
            one(hasProducts).getProductQuery();
            one(productService).getProductQty(null); will(returnValue(0));
            one(categoryService).isCategoryHasChildren(10L); will(returnValue(false));
        }});

        ShopCodeContext.setShopId(11L);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "10");
        }});

        ShopCodeContext.clear();

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.CATEGORY, resolved.getFirst());
        assertEquals(CentralViewLabel.CATEGORY, resolved.getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateNoProductsSubCats() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        context.checking(new Expectations() {{
            one(categoryService).getCategoryTemplate(10L); will(returnValue(" "));
            one(categoryService).isSearchInSubcategory(10L, 11L); will(returnValue(true));
            one(categoryService).getChildCategoriesRecursiveIds(10L); will(returnValue(new HashSet<Long>(Arrays.asList(10L))));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(hasProducts));
            one(hasProducts).getProductQuery();
            one(productService).getProductQty(null); will(returnValue(0));
            one(categoryService).isCategoryHasChildren(10L); will(returnValue(true));
        }});

        ShopCodeContext.setShopId(11L);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "10");
        }});

        ShopCodeContext.clear();

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.SUBCATEGORIES_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.SUBCATEGORIES_LIST, resolved.getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateProductsNoCat() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        context.checking(new Expectations() {{
            one(categoryService).getCategoryTemplate(10L); will(returnValue(" "));
            one(categoryService).isSearchInSubcategory(10L, 11L); will(returnValue(true));
            one(categoryService).getChildCategoriesRecursiveIds(10L); will(returnValue(new HashSet<Long>(Arrays.asList(10L))));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(hasProducts));
            one(hasProducts).getProductQuery();
            one(productService).getProductQty(null); will(returnValue(1));
            one(categoryService).getById(10L); will(returnValue(null));
        }});

        ShopCodeContext.setShopId(11L);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "10");
        }});

        ShopCodeContext.clear();

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateProductsCatNoType() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");
        final Category category = context.mock(Category.class, "category");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        context.checking(new Expectations() {{
            one(categoryService).getCategoryTemplate(10L); will(returnValue(" "));
            one(categoryService).isSearchInSubcategory(10L, 11L); will(returnValue(true));
            one(categoryService).getChildCategoriesRecursiveIds(10L); will(returnValue(new HashSet<Long>(Arrays.asList(10L))));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(hasProducts));
            one(hasProducts).getProductQuery();
            one(productService).getProductQty(null); will(returnValue(1));
            one(categoryService).getById(10L); will(returnValue(category));
            one(category).getProductType(); will(returnValue(null));
        }});

        ShopCodeContext.setShopId(11L);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "10");
        }});

        ShopCodeContext.clear();

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateProductsCatTypeNoTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");
        final Category category = context.mock(Category.class, "category");
        final ProductType type = context.mock(ProductType.class, "type");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        context.checking(new Expectations() {{
            one(categoryService).getCategoryTemplate(10L); will(returnValue(" "));
            one(categoryService).isSearchInSubcategory(10L, 11L); will(returnValue(true));
            one(categoryService).getChildCategoriesRecursiveIds(10L); will(returnValue(new HashSet<Long>(Arrays.asList(10L))));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(hasProducts));
            one(hasProducts).getProductQuery();
            one(productService).getProductQty(null); will(returnValue(1));
            one(categoryService).getById(10L); will(returnValue(category));
            one(category).getProductType(); will(returnValue(type));
            one(category).getProductType(); will(returnValue(type));
            one(type).getUisearchtemplate(); will(returnValue("  "));
        }});

        ShopCodeContext.setShopId(11L);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "10");
        }});

        ShopCodeContext.clear();

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getFirst());
        assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateProductsCatTypeTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");
        final NavigationContext hasProducts = context.mock(NavigationContext.class, "hasProducts");
        final Category category = context.mock(Category.class, "category");
        final ProductType type = context.mock(ProductType.class, "type");

        CentralViewResolverCategoryImpl resolver = new CentralViewResolverCategoryImpl(categoryService, productService, luceneQueryFactory);

        context.checking(new Expectations() {{
            one(categoryService).getCategoryTemplate(10L); will(returnValue(" "));
            one(categoryService).isSearchInSubcategory(10L, 11L); will(returnValue(true));
            one(categoryService).getChildCategoriesRecursiveIds(10L); will(returnValue(new HashSet<Long>(Arrays.asList(10L))));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(hasProducts));
            one(hasProducts).getProductQuery();
            one(productService).getProductQty(null); will(returnValue(1));
            one(categoryService).getById(10L); will(returnValue(category));
            one(category).getProductType(); will(returnValue(type));
            one(category).getProductType(); will(returnValue(type));
            one(type).getUisearchtemplate(); will(returnValue("prodtypesearch"));
        }});

        ShopCodeContext.setShopId(11L);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CATEGORY_ID, "10");
        }});

        ShopCodeContext.clear();

        assertNotNull(resolved);
        assertEquals("prodtypesearch", resolved.getFirst());
        assertEquals(CentralViewLabel.PRODUCTS_LIST, resolved.getSecond());

        context.assertIsSatisfied();

    }


}
