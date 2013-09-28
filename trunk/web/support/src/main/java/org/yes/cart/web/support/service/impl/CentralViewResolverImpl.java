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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.impl.ProductQueryBuilderImpl;
import org.yes.cart.domain.query.impl.ProductsInCategoryQueryBuilderImpl;
import org.yes.cart.domain.query.impl.SkuQueryBuilderImpl;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service responsible to resolve label of central view in storefront.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 8:50 PM
 */
public class CentralViewResolverImpl implements CentralViewResolver {

    private final ContentService contentService;
    private final CategoryService categoryService;
    private final AttributeService attributeService;
    private final LuceneQueryFactory luceneQueryFactory;

    /**
     * Construct central view resolver.
     *
     * @param categoryService    for product, sub categories, quantity and ui template lookup.
     * @param contentService     for content templates lookup
     * @param attributeService   for allowed attributes name lookup
     * @param luceneQueryFactory luceneQueryFactory
     */
    public CentralViewResolverImpl(final CategoryService categoryService,
                                   final ContentService contentService,
                                   final AttributeService attributeService,
                                   final LuceneQueryFactory luceneQueryFactory) {
        this.categoryService = categoryService;
        this.contentService = contentService;
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

        final Set<String> allowedAttributeNames = attributeService.getAllNavigatableAttributeCodes(); //list of product attributes plus brand and price

        if (parameters.containsKey(WebParametersKeys.SKU_ID)) {
            return CentralViewLabel.SKU;
        } else if (parameters.containsKey(WebParametersKeys.PRODUCT_ID)) {
            return CentralViewLabel.PRODUCT;
        } else if (parameters.containsKey(WebParametersKeys.QUERY)) {
            return CentralViewLabel.SEARCH_LIST;
        } else if (isAttributiveFilteredNavigation(allowedAttributeNames, parameters)) {
            return CentralViewLabel.SEARCH_LIST;
        } else if (parameters.containsKey(WebParametersKeys.CATEGORY_ID)) {
            final long categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CATEGORY_ID)));
            if (categoryId > 0) {

                final boolean lookInSubCats = parameters.containsKey(WebParametersKeys.CATEGORY_PRODUCTS_RECURSIVE)
                        && Boolean.valueOf(String.valueOf(parameters.get(WebParametersKeys.CATEGORY_PRODUCTS_RECURSIVE)));

                if (categoryService.isCategoryHasProducts(categoryId, lookInSubCats)) {
                    return CentralViewLabel.PRODUCTS_LIST;
                } else {

                    final String template = categoryService.getCategoryTemplate(categoryId);
                    if (template != null) {
                        return template;
                    } else if (categoryService.isCategoryHasChildren(categoryId, false)) {
                        return CentralViewLabel.SUBCATEGORIES_LIST;
                    } else {
                        return CentralViewLabel.CATEGORY;
                    }
                }
            }
        } else if (parameters.containsKey(WebParametersKeys.CONTENT_ID)) {
            final long contentId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CONTENT_ID)));
            if (contentId > 0) {
                final String template = contentService.getContentTemplate(contentId);
                if (template != null) {
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
    @Cacheable(value = "centralViewResolver-booleanQuery")
    public BooleanQuery getBooleanQuery(
            final List<BooleanQuery> queriesChain,
            final String currentQuery,
            final long categoryId,
            final List<Long> categories,
            final String viewLabel,
            final String itemId) {

        BooleanQuery rez = null;

        if (CentralViewLabel.PRODUCTS_LIST.equals(viewLabel)) {
            //Products in list
            final ProductsInCategoryQueryBuilderImpl queryBuilder = new ProductsInCategoryQueryBuilderImpl();
            if (CollectionUtils.isEmpty(categories)) {
                rez = queryBuilder.createQuery(categoryId);
            } else {
                final List<Long> allCategories = new ArrayList<Long>();
                for (final Long category : categories) {
                    if (category != null && category > 0l) {
                        allCategories.add(category);
                    }
                }
                allCategories.add(categoryId);
                rez = queryBuilder.createQuery(allCategories);
            }
        } else if (CentralViewLabel.SKU.equals(viewLabel)) {
            //single sku
            final SkuQueryBuilderImpl queryBuilder = new SkuQueryBuilderImpl();
            rez = queryBuilder.createQuery(itemId);
        } else if (CentralViewLabel.PRODUCT.equals(viewLabel)) {
            //Single product
            final ProductQueryBuilderImpl queryBuilder = new ProductQueryBuilderImpl();
            rez = queryBuilder.createQuery(itemId);
        } else if (CentralViewLabel.SEARCH_LIST.equals(viewLabel)) {
            //Product in list via filtered navigation
            rez = luceneQueryFactory.getSnowBallQuery(queriesChain, currentQuery);

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
