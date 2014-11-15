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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanQuery;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.impl.ProductQueryBuilderImpl;
import org.yes.cart.domain.query.impl.ProductsInCategoryQueryBuilderImpl;
import org.yes.cart.domain.query.impl.SkuQueryBuilderImpl;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.*;

/**
 * Service responsible to resolve label of central view in storefront.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 8:50 PM
 */
public class CentralViewResolverImpl implements CentralViewResolver {

    private final ContentService contentService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final AttributeService attributeService;
    private final LuceneQueryFactory luceneQueryFactory;

    private final ProductsInCategoryQueryBuilderImpl productsInCategoryQueryBuilder = new ProductsInCategoryQueryBuilderImpl();
    private final SkuQueryBuilderImpl skuIdBuilder = new SkuQueryBuilderImpl();
    private final ProductQueryBuilderImpl productIdBuilder = new ProductQueryBuilderImpl();



    /**
     * Construct central view resolver.
     *
     * @param categoryService    for product, sub categories, quantity and ui template lookup.
     * @param contentService     for content templates lookup
     * @param productService     for category template look up
     * @param attributeService   for allowed attributes name lookup
     * @param luceneQueryFactory luceneQueryFactory
     */
    public CentralViewResolverImpl(final CategoryService categoryService,
                                   final ContentService contentService,
                                   final ProductService productService,
                                   final AttributeService attributeService,
                                   final LuceneQueryFactory luceneQueryFactory) {
        this.categoryService = categoryService;
        this.contentService = contentService;
        this.productService = productService;
        this.attributeService = attributeService;
        this.luceneQueryFactory = luceneQueryFactory;
    }

    /**
     * Resolve central renderer label.
     *
     * @param parameters request parameters map
     * @return resolved main panel renderer label if resolved, otherwise null
     */
    public String resolveMainPanelRendererLabel(final Map parameters) {

        if (parameters.containsKey(WebParametersKeys.SKU_ID)) {
            return CentralViewLabel.SKU;
        } else if (parameters.containsKey(WebParametersKeys.PRODUCT_ID)) {
            return CentralViewLabel.PRODUCT;
        } else if (parameters.containsKey(WebParametersKeys.QUERY)) {
            return CentralViewLabel.SEARCH_LIST;
        } else if (isAttributiveFilteredNavigation(attributeService.getAllNavigatableAttributeCodes(), parameters)) {  //list of product attributes plus brand and price
            return CentralViewLabel.SEARCH_LIST;
        } else if (parameters.containsKey(WebParametersKeys.CATEGORY_ID)) {
            final long categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CATEGORY_ID)));
            if (categoryId > 0) {

                // If we have template just use it without any checks (saves us 1 FT query for each request)
                final String template = categoryService.getCategoryTemplate(categoryId);
                if (StringUtils.isNotBlank(template)) {
                    return template;
                }

                // If template is not set try to figure out the view
                final boolean lookInSubCats = categoryService.isSearchInSubcategory(categoryId, ShopCodeContext.getShopId());

                final List<Long> catIds;
                if (lookInSubCats) {
                    catIds = new ArrayList<Long>(categoryService.getChildCategoriesRecursiveIds(categoryId));
                } else {
                    catIds = Collections.singletonList(categoryId);
                }

                final BooleanQuery hasProducts = productsInCategoryQueryBuilder.createQuery(catIds, 0L); // Do not use shopId as it will bring all products

                if (productService.getProductQty(hasProducts) > 0) {
                    return CentralViewLabel.PRODUCTS_LIST;
                } else if (categoryService.isCategoryHasChildren(categoryId)) {
                    return CentralViewLabel.SUBCATEGORIES_LIST;
                } else {
                    return CentralViewLabel.CATEGORY;
                }
            }
        } else if (parameters.containsKey(WebParametersKeys.CONTENT_ID)) {
            final long contentId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CONTENT_ID)));
            if (contentId > 0) {
                final String template = contentService.getContentTemplate(contentId);
                if (StringUtils.isNotBlank(template)) {
                    return template;
                }
                return CentralViewLabel.CONTENT;
            }
        }
        return CentralViewLabel.DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    public BooleanQuery getBooleanQuery(final BooleanQuery queriesChain,
                                        final long shopId,
                                        final List<Long> categories,
                                        final String viewLabel,
                                        final String itemId) {

        BooleanQuery rez = null;

        if (CentralViewLabel.PRODUCTS_LIST.equals(viewLabel)) {
            // Products in list, need to add mandatory category parameters
            rez = luceneQueryFactory.getSnowBallQuery(queriesChain, productsInCategoryQueryBuilder.createQuery(categories, shopId));
        } else if (CentralViewLabel.SKU.equals(viewLabel)) {
            //single sku
            rez = skuIdBuilder.createQuery(itemId);
        } else if (CentralViewLabel.PRODUCT.equals(viewLabel)) {
            //Single product
            rez = productIdBuilder.createQuery(itemId);
        } else if (CentralViewLabel.SEARCH_LIST.equals(viewLabel)) {
            // Product in list via filtered navigation, need to add mandatory category parameters
            rez = luceneQueryFactory.getSnowBallQuery(queriesChain, productsInCategoryQueryBuilder.createQuery(categories, shopId));

        }

        return rez;

    }

    private boolean isAttributiveFilteredNavigation(
            final Set<String> allowedAttributeNames,
            final Map parameters) {
        for (Object obj : parameters.keySet()) {
            final String decodedParameterKeyName = (String) obj;
            if (allowedAttributeNames.contains(decodedParameterKeyName)) {
                return true;
            }
        }
        return false;
    }

}
