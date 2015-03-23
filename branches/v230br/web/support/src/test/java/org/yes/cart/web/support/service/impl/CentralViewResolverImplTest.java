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

import org.apache.lucene.search.Query;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.domain.query.impl.ProductCategorySearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 8:36 AM
 */
public class CentralViewResolverImplTest {

    private final Mockery context = new JUnit4Mockery();

    private final Set<String> navAttrs = new HashSet<String>() {{
        add("attrWeight");
        add("attrSize");
    }};

    @Test
    public void testResolveMainPanelRendererLabelSku() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        assertEquals(CentralViewLabel.SKU,
                resolver.resolveMainPanelRendererLabel(getRequestParamsWithFilterNav(WebParametersKeys.SKU_ID, null)));

    }

    @Test
    public void testResolveMainPanelRendererLabelProduct() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        assertEquals(CentralViewLabel.PRODUCT,
                resolver.resolveMainPanelRendererLabel(getRequestParamsWithFilterNav(WebParametersKeys.PRODUCT_ID, null)));

    }

    @Test
    public void testResolveMainPanelRendererLabelFilteredNav() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        assertEquals(CentralViewLabel.SEARCH_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParamsWithFilterNav("page", null)));

    }

    @Test
    public void testResolveMainPanelRendererLabelQuery() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        assertEquals(CentralViewLabel.SEARCH_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParamsWithFilterNav(WebParametersKeys.QUERY, null)));

    }

    @Test
    public void testResolveMainPanelRendererLabelUnknownParam() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        assertEquals(CentralViewLabel.DEFAULT,
                resolver.resolveMainPanelRendererLabel(getRequestParam("someParam", null)));

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryWithTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
            one(categoryService).getCategoryTemplate(10L); will(returnValue("template_10"));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        assertEquals("template_10",
                resolver.resolveMainPanelRendererLabel(getRequestParam(WebParametersKeys.CATEGORY_ID, "10")));

    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateWithProducts() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        final NavigationContext navigationContext = context.mock(NavigationContext.class, "navigationContext");
        final Query expected = new ProductCategorySearchQueryBuilder().createStrictQuery(0L, null, 10L);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
            one(categoryService).getCategoryTemplate(10L); will(returnValue(""));
            one(categoryService).isSearchInSubcategory(10L, 12L); will(returnValue(false));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(navigationContext));
            one(navigationContext).getProductQuery(); will(returnValue(expected));
            one(productService).getProductQty(expected); will(returnValue(1));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        ShopCodeContext.setShopId(12L);

        assertEquals(CentralViewLabel.PRODUCTS_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParam(WebParametersKeys.CATEGORY_ID, "10")));

        ShopCodeContext.clear();
    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateWithProductsInSubCats() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        final NavigationContext navigationContext = context.mock(NavigationContext.class, "navigationContext");
        final Query expected = new ProductCategorySearchQueryBuilder().createStrictQuery(0L, null, Arrays.asList(10L, 11L));

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
            one(categoryService).getCategoryTemplate(10L); will(returnValue(""));
            one(categoryService).isSearchInSubcategory(10L, 12L); will(returnValue(true));
            one(categoryService).getChildCategoriesRecursiveIds(10L); will(returnValue(new HashSet<Long>(Arrays.asList(10L, 11L))));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L, 11L), null); will(returnValue(navigationContext));
            one(navigationContext).getProductQuery(); will(returnValue(expected));
            one(productService).getProductQty(expected); will(returnValue(1));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        ShopCodeContext.setShopId(12L);

        assertEquals(CentralViewLabel.PRODUCTS_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParam(WebParametersKeys.CATEGORY_ID, "10")));

        ShopCodeContext.clear();
    }


    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateWithNoProducts() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        final NavigationContext navigationContext = context.mock(NavigationContext.class, "navigationContext");
        final Query expected = new ProductCategorySearchQueryBuilder().createStrictQuery(0L, null, 10L);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
            one(categoryService).getCategoryTemplate(10L); will(returnValue(""));
            one(categoryService).isSearchInSubcategory(10L, 12L); will(returnValue(false));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(navigationContext));
            one(navigationContext).getProductQuery(); will(returnValue(expected));
            one(productService).getProductQty(expected); will(returnValue(0));
            one(categoryService).isCategoryHasChildren(10L); will(returnValue(true));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        ShopCodeContext.setShopId(12L);

        assertEquals(CentralViewLabel.SUBCATEGORIES_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParam(WebParametersKeys.CATEGORY_ID, "10")));

        ShopCodeContext.clear();
    }

    @Test
    public void testResolveMainPanelRendererLabelCategoryNoTemplateWithNoProductsNoSubcategories() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        final NavigationContext navigationContext = context.mock(NavigationContext.class, "navigationContext");
        final Query expected = new ProductCategorySearchQueryBuilder().createStrictQuery(0L, null, 10L);

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
            one(categoryService).getCategoryTemplate(10L); will(returnValue(""));
            one(categoryService).isSearchInSubcategory(10L, 12L); will(returnValue(false));
            one(luceneQueryFactory).getFilteredNavigationQueryChain(0L, Arrays.asList(10L), null); will(returnValue(navigationContext));
            one(navigationContext).getProductQuery(); will(returnValue(expected));
            one(productService).getProductQty(expected); will(returnValue(0));
            one(categoryService).isCategoryHasChildren(10L); will(returnValue(false));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        ShopCodeContext.setShopId(12L);

        assertEquals(CentralViewLabel.CATEGORY,
                resolver.resolveMainPanelRendererLabel(getRequestParam(WebParametersKeys.CATEGORY_ID, "10")));

        ShopCodeContext.clear();
    }

    @Test
    public void testResolveMainPanelRendererLabelContentWithTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
            one(contentService).getContentTemplate(10L); will(returnValue(CentralViewLabel.DYNOCONTENT));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        ShopCodeContext.setShopId(12L);

        assertEquals(CentralViewLabel.DYNOCONTENT,
                resolver.resolveMainPanelRendererLabel(getRequestParam(WebParametersKeys.CONTENT_ID, "10")));

        ShopCodeContext.clear();
    }

    @Test
    public void testResolveMainPanelRendererLabelContentWithNoTemplate() throws Exception {

        final CategoryService categoryService = context.mock(CategoryService.class, "categoryService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final AttributeService attributeService = context.mock(AttributeService.class, "attributeService");
        final LuceneQueryFactory luceneQueryFactory = context.mock(LuceneQueryFactory.class, "luceneQueryFactory");

        context.checking(new Expectations() {{
            one(attributeService).getAllNavigatableAttributeCodes(); will(returnValue(navAttrs));
            one(contentService).getContentTemplate(10L); will(returnValue(""));
        }});

        CentralViewResolverImpl resolver = new CentralViewResolverImpl(categoryService, contentService, productService, attributeService, luceneQueryFactory);

        ShopCodeContext.setShopId(12L);

        assertEquals(CentralViewLabel.CONTENT,
                resolver.resolveMainPanelRendererLabel(getRequestParam(WebParametersKeys.CONTENT_ID, "10")));

        ShopCodeContext.clear();
    }

    private Map<String, String> getRequestParamsWithFilterNav(final String param, final String val) {
        return new HashMap<String, String>() {{
            put("attrWeight", "12");
            put("attrSize", "23");
            put(param, val);
        }};
    }

    private Map<String, String> getRequestParam(final String param, final String val) {
        return new HashMap<String, String>() {{
            put(param, val);
        }};
    }
}
