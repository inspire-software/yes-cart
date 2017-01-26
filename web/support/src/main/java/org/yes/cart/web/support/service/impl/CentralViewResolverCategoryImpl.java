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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ShopSearchSupportService;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;

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

    private final ShopService shopService;
    private final CategoryService categoryService;
    private final ShopSearchSupportService shopSearchSupportService;
    private final ProductService productService;
    private final LuceneQueryFactory luceneQueryFactory;

    public CentralViewResolverCategoryImpl(final ShopService shopService,
                                           final CategoryService categoryService,
                                           final ShopSearchSupportService shopSearchSupportService,
                                           final ProductService productService,
                                           final LuceneQueryFactory luceneQueryFactory) {
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.shopSearchSupportService = shopSearchSupportService;
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

                final long shopId = ApplicationDirector.getShoppingCart().getShoppingContext().getShopId();

                // If we have template just use it without any checks (saves us 1 FT query for each request)
                final String template = shopService.getShopCategoryTemplate(shopId, categoryId);
                if (StringUtils.isNotBlank(template)) {
                    if (CentralViewLabel.PRODUCTS_LIST.equals(template)) {
                        return DEFAULT_PL;
                    } else if (CentralViewLabel.SUBCATEGORIES_LIST.equals(template)) {
                        return DEFAULT_SC;
                    }
                    return new Pair<String, String>(template, CentralViewLabel.CATEGORY);
                }

                final Pair<List<Long>, Boolean> catIds = determineSearchCategories(categoryId, shopId);


                if (CollectionUtils.isEmpty(catIds.getFirst())) {
                    return DEFAULT; // Must never be empty, as we should have at least current category
                }

                // shopId will be used for inStock check, because we have category IDs will always look in those
                final NavigationContext hasProducts = luceneQueryFactory.getFilteredNavigationQueryChain(shopId, catIds.getFirst(), catIds.getSecond(), null);

                if (productService.getProductQty(hasProducts.getProductQuery()) > 0) {

                    final String searchTemplate = shopService.getShopCategorySearchTemplate(shopId, categoryId);
                    if (StringUtils.isNotBlank(searchTemplate)) {
                        return new Pair<String, String>(searchTemplate, CentralViewLabel.PRODUCTS_LIST);
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

    private Pair<List<Long>, Boolean> determineSearchCategories(final long categoryId, final long shopId) {

        return shopSearchSupportService.getSearchCategoriesIds(categoryId, shopId);

    }
}
