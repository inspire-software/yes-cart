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
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.impl.ProductQueryBuilderImpl;
import org.yes.cart.domain.query.impl.ProductsInCategoryQueryBuilderImpl;
import org.yes.cart.domain.query.impl.SkuQueryBuilderImpl;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service responsible to resolve label of central view in storefront.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 8:50 PM
 */
public class CentralViewResolverImpl implements CentralViewResolver {

    private final CategoryService categoryService;
    private final AttributeService attributeService;
    private final LuceneQueryFactory luceneQueryFactory;

    /**
     * Construct central view resolver.
     *
     * @param categoryService    to product and sub categories quantity determination.
     * @param attributeService   to allowed attributes name determination
     * @param luceneQueryFactory luceneQueryFactory
     */
    public CentralViewResolverImpl(final CategoryService categoryService,
                                   final AttributeService attributeService,
                                   final LuceneQueryFactory luceneQueryFactory) {
        this.categoryService = categoryService;
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

        final List allowedAttributeNames = attributeService.getAllAttributeCodes(); //list of product attributes plus brand and price

        if (parameters.containsKey(WebParametersKeys.SKU_ID)) {
            return CentralViewLabel.SKU;
        } else if (parameters.containsKey(WebParametersKeys.PRODUCT_ID)) {
            return CentralViewLabel.PRODUCT;
        } else if (parameters.containsKey(WebParametersKeys.QUERY)) {
            return CentralViewLabel.SEARCH_LIST;
        } else if (isAttibutiveFilteredNavigation(allowedAttributeNames, parameters)) {
            return CentralViewLabel.SEARCH_LIST;
        } else if (parameters.containsKey(WebParametersKeys.CATEGORY_ID)) {
            final long categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CATEGORY_ID)));
            if (categoryId > 0) {

                final boolean lookInSubCats = parameters.containsKey(WebParametersKeys.CATEGORY_PRODUCTS_RECURSIVE)
                        && Boolean.valueOf(String.valueOf(parameters.get(WebParametersKeys.CATEGORY_PRODUCTS_RECURSIVE)));

                if (categoryService.getProductQuantity(categoryId, lookInSubCats) > 0) {
                    return CentralViewLabel.PRODUCTS_LIST;
                } else if (categoryService.getChildCategories(categoryId).size() > 0) {
                    return CentralViewLabel.SUBCATEGORIES_LIST;
                }
            }
        }
        return CentralViewLabel.DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    public BooleanQuery getBooleanQuery(
            final List<BooleanQuery> queriesChain,
            final String currentQuery,
            final long categoryId,
            final List<Long> categories,
            final String viewLabel,
            final String itemId) {


        if (CentralViewLabel.PRODUCTS_LIST.equals(viewLabel)) {
            //Products in list
            final ProductsInCategoryQueryBuilderImpl queryBuilder = new ProductsInCategoryQueryBuilderImpl();
            if (CollectionUtils.isEmpty(categories)) {
                return queryBuilder.createQuery(categoryId);
            }
            final List<Long> allCategories = new ArrayList<Long>();
            for (final Long category : categories) {
                if (category != null && category > 0l) {
                    allCategories.add(category);
                }
            }
            allCategories.add(categoryId);
            return queryBuilder.createQuery(allCategories);
        } else if (CentralViewLabel.SKU.equals(viewLabel)) {
            //single sku
            final SkuQueryBuilderImpl queryBuilder = new SkuQueryBuilderImpl();
            return queryBuilder.createQuery(itemId);
        } else if (CentralViewLabel.PRODUCT.equals(viewLabel)) {
            //Single product
            final ProductQueryBuilderImpl queryBuilder = new ProductQueryBuilderImpl();
            return queryBuilder.createQuery(itemId);
        } else if (CentralViewLabel.SEARCH_LIST.equals(viewLabel)) {
            //Product in list via filtered navigation
            return luceneQueryFactory.getSnowBallQuery(queriesChain, currentQuery);
        }
        return null;

    }

    private boolean isAttibutiveFilteredNavigation(
            final List allowedAttributeNames,
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
