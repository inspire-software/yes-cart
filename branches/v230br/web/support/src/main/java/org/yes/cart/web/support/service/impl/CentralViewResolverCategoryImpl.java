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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 08:02
 */
public class CentralViewResolverCategoryImpl implements CentralViewResolver {

    private static final Pair<String, String> DEFAULT = new Pair<String, String>(CentralViewLabel.CATEGORY, CentralViewLabel.CATEGORY);
    private static final Pair<String, String> DEFAULT_PL = new Pair<String, String>(CentralViewLabel.PRODUCTS_LIST, CentralViewLabel.PRODUCTS_LIST);
    private static final Pair<String, String> DEFAULT_SC = new Pair<String, String>(CentralViewLabel.SUBCATEGORIES_LIST, CentralViewLabel.SUBCATEGORIES_LIST);

    private final CategoryService categoryService;
    private final ProductService productService;
    private final LuceneQueryFactory luceneQueryFactory;

    public CentralViewResolverCategoryImpl(final CategoryService categoryService,
                                           final ProductService productService,
                                           final LuceneQueryFactory luceneQueryFactory) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.luceneQueryFactory = luceneQueryFactory;
    }

    /**
     * Resolve category view if applicable.
     * <p>
     * Rules:<p>
     * 1. If there is no {@link WebParametersKeys#CATEGORY_ID} then this resolver is not applicable, return null<p>
     * 2. If category has template then use template.<p>
     * 3. If category search yields at least one product hit then use category product type search template
     *    {@link org.yes.cart.domain.entity.ProductType#getUisearchtemplate()} or {@link CentralViewLabel#PRODUCTS_LIST}
     *    if not search template is set<p>
     * 4. If category has children use {@link CentralViewLabel#SUBCATEGORIES_LIST}<p>
     * 5. Otherwise use {@link CentralViewLabel#CATEGORY}<p>
     *
     * @param parameters            request parameters map
     *
     * @return category view label or null (if not applicable)
     */
    @Override
    public Pair<String, String> resolveMainPanelRendererLabel(final Map parameters) {

        if (parameters.containsKey(WebParametersKeys.CATEGORY_ID)) {
            final long categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CATEGORY_ID)));
            if (categoryId > 0L) {

                // If we have template just use it without any checks (saves us 1 FT query for each request)
                final String template = categoryService.getCategoryTemplate(categoryId);
                if (StringUtils.isNotBlank(template)) {
                    return new Pair<String, String>(template, CentralViewLabel.CATEGORY);
                }

                // If template is not set try to figure out the view
                final boolean lookInSubCats = categoryService.isSearchInSubcategory(categoryId, ShopCodeContext.getShopId());

                final List<Long> catIds;
                if (lookInSubCats) {
                    catIds = new ArrayList<Long>(categoryService.getChildCategoriesRecursiveIds(categoryId));
                } else {
                    catIds = Collections.singletonList(categoryId);
                }

                // Do not use shopId as it will bring all products
                final NavigationContext hasProducts = luceneQueryFactory.getFilteredNavigationQueryChain(0L, catIds, null);

                if (productService.getProductQty(hasProducts.getProductQuery()) > 0) {

                    final Category category = categoryService.getById(categoryId);
                    if (category != null && category.getProductType() != null) {
                        final String searchTemplate = category.getProductType().getUisearchtemplate();
                        if (StringUtils.isNotBlank(searchTemplate)) {
                            return new Pair<String, String>(searchTemplate, CentralViewLabel.PRODUCTS_LIST);
                        }
                    }

                    return DEFAULT_PL;
                } else if (categoryService.isCategoryHasChildren(categoryId)) {
                    return DEFAULT_SC;
                } else {
                    return DEFAULT;
                }
            }
        }

        return null;
    }
}
