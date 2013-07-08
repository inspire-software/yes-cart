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

package org.yes.cart.web.page.component.breadcrumbs.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.cache.Cacheable;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.page.component.breadcrumbs.BreadCrumbsBuilder;
import org.yes.cart.web.page.component.breadcrumbs.Crumb;
import org.yes.cart.web.page.component.breadcrumbs.CrumbNamePrefixProvider;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bread crumbs builder produce category and
 * attributive filtered navigation breadcrumbs based on
 * web query string and context.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:50:51 AM
 */
public class BreadCrumbsBuilderImpl implements BreadCrumbsBuilder {



    private final CategoryService categoryService;

    /**
     *
     * @param categoryService       category service
     */
    public BreadCrumbsBuilderImpl (final CategoryService categoryService) {

        this.categoryService = categoryService;

    }


    /**
     * We have 2 kinds of breadcrumbs:
     * 1. category path, for example electronics -> phones -> ip phones
     * 2. attributive filters, for example ip phones [price range, brands, weight, ect]
     *
     * @param categoryId            current category id
     * @param pageParameters        current query string
     * @param allowedAttributeNames allowed attribute names for filtering including price, brand, search...
     * @param shopCategoryIds       all categoryIds, that belong to shop
     * @param namePrefixProvider    name prifix provider for price, brand, search..

     * @return list of crumbs
     */
    @Cacheable(value = "breadCrumbBuilderImplMethodCache")
    public List<Crumb> getBreadCrumbs(
            final long categoryId,
            final PageParameters pageParameters,
            final List<String> allowedAttributeNames,
            final List<Long> shopCategoryIds,
            final CrumbNamePrefixProvider namePrefixProvider) {

        final List<Crumb> crumbs = new ArrayList<Crumb>();
        crumbs.addAll(getCategoriesCrumbs(categoryId, shopCategoryIds));
        crumbs.addAll(getFilteredNavigationCrumbs(allowedAttributeNames, pageParameters, namePrefixProvider));
        return crumbs;
    }

    private List<Crumb> getFilteredNavigationCrumbs(
            final List<String> allowedAttributeNames,
            final PageParameters pageParameters,
            final CrumbNamePrefixProvider namePrefixProvider) {
        final List<Crumb> navigationCrumbs = new ArrayList<Crumb>();
        fillAttributes(navigationCrumbs, allowedAttributeNames, pageParameters, namePrefixProvider);
        return navigationCrumbs;
    }

    private List<Crumb> getCategoriesCrumbs(final long categoryId, final List<Long> shopCategoryIds) {
        final List<Crumb> categoriesCrumbs = new ArrayList<Crumb>();
        if (categoryId > 0) {
            fillCategories(categoriesCrumbs, categoryId, shopCategoryIds);
            Collections.reverse(categoriesCrumbs);
        }
        return categoriesCrumbs;
    }


    /**
     * Recursive function to reverse build the breadcrumbs by categories, starting from currently selected one.
     *
     * @param categoriesCrumbs the crumbs list
     * @param categoryId       the current category id
     */
    private void fillCategories(final List<Crumb> categoriesCrumbs, final long categoryId, final List<Long> shopCategoryIds) {
        final Category category = categoryService.getById(categoryId);
        if (categoryId != category.getParentId()) {
            categoriesCrumbs.add(
                    new Crumb("category", category.getName(),
                            category.getDisplayName(), getCategoryLinkParameters(categoryId),
                            getRemoveCategoryLinkParameters(category, shopCategoryIds)
                    )
            );


            fillCategories(categoriesCrumbs, category.getParentId(), shopCategoryIds);
        }
    }

    /**
     * Get {@link PageParameters}, that point to given category.
     *
     * @param categoryId given category id
     * @return page parameters for link
     */
    private PageParameters getCategoryLinkParameters(final long categoryId) {
        return new PageParameters().add(WebParametersKeys.CATEGORY_ID, categoryId);
    }

    /**
     * Get {@link PageParameters}, that point to parent, if any, of given category.
     *
     * @param category given category
     * @return page parameter for point to parent.
     */
    private PageParameters getRemoveCategoryLinkParameters(final Category category, final List<Long> shopCategoryIds) {
        if (shopCategoryIds.contains(category.getParentId())) {
            return getCategoryLinkParameters(category.getParentId());
        }
        return new PageParameters();
    }

    private void fillAttributes(
            final List<Crumb> navigationCrumbs,
            final List<String> allowedAttributeNames,
            final PageParameters pageParameters,
            final CrumbNamePrefixProvider namePrefixProvider) {

        //This is attributive only filtered navigation from request
        final PageParameters attributesOnly = WicketUtil.getRetainedRequestParameters(
                pageParameters,
                allowedAttributeNames);

        //Base hold category path from begining and accumulate all attributive navigation
        final PageParameters base = WicketUtil.getFilteredRequestParameters(
                pageParameters,
                allowedAttributeNames);

        //If we are on display product page, we have to remove for filtering  as well as sku
        base.remove(WebParametersKeys.PRODUCT_ID);
        base.remove(WebParametersKeys.SKU_ID);

        for (PageParameters.NamedPair namedPair : attributesOnly.getAllNamed()) {
            navigationCrumbs.add(createFilteredNavigationCrumb(base, namedPair.getKey(), namedPair.getValue(), pageParameters, namePrefixProvider));
        }
    }

    /**
     * Create filtered navigation crubm with two links:
     * <p/>
     * First - current position, that include the whole path before current.
     * example category/17/subcategory/156/price/100-200/currentkey/currentvalue
     * <p/>
     * Second - the whole current path without current
     * example category/17/subcategory/156/price/100-200/currentkey/currentvalue/nextkey/nextvalue
     * ^^^^^^^^^^^^^^^^^^^^^^^ this will be removed,
     * so uri will be
     * example category/17/subcategory/156/price/100-200/nextkey/nextvalue
     *
     * @param base  initial parameter map, usually category and sub category navigation
     * @param key   current key
     * @param value current value
     * @return {@link Crumb}
     */
    private Crumb createFilteredNavigationCrumb(
            final PageParameters base,
            final String key,
            final String value,
            final PageParameters pageParameters,
            final CrumbNamePrefixProvider namePrefixProvider) {

        final PageParameters withoutCurrent = WicketUtil.getFilteredRequestParameters(
                pageParameters,
                key,
                value
        );

        String linkName = namePrefixProvider.getLinkNamePrefix(key);
        if (StringUtils.isNotBlank(linkName)) {
            linkName += "::" + namePrefixProvider.getLinkName(key, value);
        } else {
            linkName = namePrefixProvider.getLinkName(key, value);
        }

        base.add(key, value);
        return new Crumb(key, linkName, null, new PageParameters(base), withoutCurrent);
    }


}
