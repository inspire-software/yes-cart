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
import org.junit.Test;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.service.domain.impl.AttributeServiceImpl;
import org.yes.cart.service.domain.impl.CategoryServiceImpl;
import org.yes.cart.service.domain.impl.ContentServiceImpl;
import org.yes.cart.service.domain.impl.ProductServiceImpl;
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

    @Test
    public void testResolveMainPanelRendererLabel() throws Exception {
        CentralViewResolverImpl resolver = new CentralViewResolverImpl(
                new CategoryServiceImpl(null, null, null) {
                    @Override
                    public boolean isCategoryHasProducts(final long categoryId, final boolean includeChildren) {
                        if (categoryId == 10 || categoryId == 999) {
                            return false;
                        }
                        return true;
                    }
                    @Override
                    public boolean isCategoryHasChildren(final long categoryId, final boolean includeChildren) {
                        if (categoryId == 10) {
                            return true;
                        }
                        return false;
                    }
                    @Override
                    public String getCategoryTemplate(final long categoryId) {
                        if (categoryId == 999L) {
                            return "template";
                        }
                        return null;
                    }
                },
                new ContentServiceImpl(null, null, null) {
                    @Override
                    public String getContentTemplate(final long categoryId) {
                        if (categoryId == 888L) {
                            return "template";
                        }
                        return null;
                    }
                },
                new ProductServiceImpl(null, null, null, null, null, null, null) {
                    @Override
                    public List<ProductSearchResultDTO> getProductSearchResultDTOByQuery(final Query query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse) {
                        if (query.toString().indexOf("category:10") == -1 && query.toString().indexOf("category:999") == -1) {
                            return new ArrayList<ProductSearchResultDTO>() {{
                                add(new ProductSearchResultDTOImpl());
                            }};
                        }
                        return Collections.emptyList();
                    }
                }, new AttributeServiceImpl(null, null, false) {
                    @Override
                    public Set<String> getAllNavigatableAttributeCodes() {
                        return new HashSet<String>() {{
                            add("attrWeight");
                            add("attrSize");
                        }};
                    }
                },
                null

        );
        assertEquals(CentralViewLabel.SKU,
                resolver.resolveMainPanelRendererLabel(getRequestParams(WebParametersKeys.SKU_ID, null)));
        assertEquals(CentralViewLabel.PRODUCT,
                resolver.resolveMainPanelRendererLabel(getRequestParams(WebParametersKeys.PRODUCT_ID, null)));
        assertEquals(CentralViewLabel.SEARCH_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParams(WebParametersKeys.QUERY, null)));
        assertEquals(CentralViewLabel.SEARCH_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParams("mayBePagingParam", null)));
        assertEquals(CentralViewLabel.SUBCATEGORIES_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParams2(WebParametersKeys.CATEGORY_ID, "10")));
        assertEquals(CentralViewLabel.PRODUCTS_LIST,
                resolver.resolveMainPanelRendererLabel(getRequestParams2(WebParametersKeys.CATEGORY_ID, "11")));
        assertEquals("template",
                resolver.resolveMainPanelRendererLabel(getRequestParams2(WebParametersKeys.CATEGORY_ID, "999")));
        assertEquals(CentralViewLabel.CONTENT,
                resolver.resolveMainPanelRendererLabel(getRequestParams2(WebParametersKeys.CONTENT_ID, "889")));
        assertEquals("template",
                resolver.resolveMainPanelRendererLabel(getRequestParams2(WebParametersKeys.CONTENT_ID, "888")));
    }

    private Map<String, String> getRequestParams(final String param, final String val) {
        return new HashMap<String, String>() {{
            put("attrWeight", "12");
            put("attrSize", "23");
            put(param, val);
        }};
    }

    private Map<String, String> getRequestParams2(final String param, final String val) {
        return new HashMap<String, String>() {{
            put(param, val);
        }};
    }
}
