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
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.page.component.breadcrumbs.BreadCrumbsBuilder;
import org.yes.cart.web.page.component.breadcrumbs.Crumb;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;
import java.util.*;

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
    private final CurrencySymbolService currencySymbolService;
    private final PriceNavigation priceNavigation;
    private final AttributeService attributeService;

    /**
     *
     * @param categoryService       category service
     * @param currencySymbolService currency symbols for price crumbs
     * @param priceNavigation       price navigation
     * @param attributeService      attribute service
     */
    public BreadCrumbsBuilderImpl(final CategoryService categoryService,
                                  final CurrencySymbolService currencySymbolService,
                                  final PriceNavigation priceNavigation,
                                  final AttributeService attributeService) {

        this.categoryService = categoryService;
        this.currencySymbolService = currencySymbolService;
        this.priceNavigation = priceNavigation;
        this.attributeService = attributeService;
    }


    /** {@inheritDoc} */
    @Cacheable(value = "breadCrumbBuilder-breadCrumbs")
    public List<Crumb> getBreadCrumbs(final String locale,
                                      final long categoryId,
                                      final PageParameters pageParameters,
                                      final Set<Long> shopCategoryIds,
                                      final String brandPrefix,
                                      final String pricePrefix,
                                      final String queryPrefix,
                                      final String tagPrefix) {

        final List<Crumb> crumbs = new ArrayList<Crumb>();
        crumbs.addAll(getCategoriesCrumbs(categoryId, shopCategoryIds, pageParameters.getNamedKeys().contains(WebParametersKeys.CONTENT_ID)));
        crumbs.addAll(getFilteredNavigationCrumbs(locale, pageParameters, brandPrefix, pricePrefix, queryPrefix, tagPrefix));
        return crumbs;
    }

    private List<Crumb> getFilteredNavigationCrumbs(final String locale,
                                                    final PageParameters pageParameters,
                                                    final String brandPrefix,
                                                    final String pricePrefix,
                                                    final String queryPrefix,
                                                    final String tagPrefix) {
        final List<Crumb> navigationCrumbs = new ArrayList<Crumb>();
        fillAttributes(locale, navigationCrumbs, pageParameters, brandPrefix, pricePrefix, queryPrefix, tagPrefix);
        return navigationCrumbs;
    }

    private List<Crumb> getCategoriesCrumbs(final long categoryId, final Set<Long> shopCategoryIds, final boolean isContent) {
        final List<Crumb> categoriesCrumbs = new ArrayList<Crumb>();
        if (categoryId > 0) {
            fillCategories(categoriesCrumbs, categoryId, shopCategoryIds, isContent);
            Collections.reverse(categoriesCrumbs);
        }
        return categoriesCrumbs;
    }


    /**
     * Recursive function to reverse build the breadcrumbs by categories, starting from currently selected one.
     *
     * @param categoriesCrumbs the crumbs list
     * @param categoryId       the current category id
     * @param isContent        true if this is content hierarchy, category otherwise
     */
    private void fillCategories(final List<Crumb> categoriesCrumbs, final long categoryId, final Set<Long> shopCategoryIds, final boolean isContent) {
        if (categoryId > 0l) {
            final Category category = categoryService.getById(categoryId);
            if (!category.isRoot() && !CentralViewLabel.CONTENT_INCLUDE.equals(category.getUitemplate())) {
                categoriesCrumbs.add(
                        new Crumb("category", category.getName(),
                                category.getDisplayName(), getCategoryLinkParameters(categoryId, isContent),
                                getRemoveCategoryLinkParameters(category, shopCategoryIds, isContent)
                        )
                );


                fillCategories(categoriesCrumbs, category.getParentId(), shopCategoryIds, isContent);
            }
        }
    }

    /**
     * Get {@link PageParameters}, that point to given category.
     *
     *
     * @param categoryId given category id
     * @param isContent  true if given category is content, false if given category is category
     *
     * @return page parameters for link
     */
    private PageParameters getCategoryLinkParameters(final long categoryId, final boolean isContent) {
        return new PageParameters().add(isContent ? WebParametersKeys.CONTENT_ID : WebParametersKeys.CATEGORY_ID, categoryId);
    }

    /**
     * Get {@link PageParameters}, that point to parent, if any, of given category.
     *
     *
     * @param category given category
     * @param isContent  true if given category is content, false if given category is category
     *
     * @return page parameter for point to parent.
     */
    private PageParameters getRemoveCategoryLinkParameters(final Category category, final Set<Long> shopCategoryIds, final boolean isContent) {
        if (shopCategoryIds.contains(category.getParentId())) {
            final Category parent = categoryService.getById(category.getParentId());
            if (parent != null && !parent.isRoot() && !CentralViewLabel.CONTENT_INCLUDE.equals(parent.getUitemplate())) {
                return getCategoryLinkParameters(parent.getCategoryId(), isContent);
            }
        }
        return new PageParameters();
    }

    private void fillAttributes(final String locale,
                                final List<Crumb> navigationCrumbs,
                                final PageParameters pageParameters,
                                final String brandPrefix,
                                final String pricePrefix,
                                final String queryPrefix,
                                final String tagPrefix) {

        final Set<String> allowedAttributeNames = attributeService.getAllAttributeCodes();
        /*
           Call below creates very unproductive query for all attribute codes, so we
           use a separate method for that:
            this.attributeCodeName = attributeService.getAttributeNamesByCodes(allowedAttributeNames);
         */
        final Map<String, I18NModel> attributeCodeName = attributeService.getAllAttributeNames();


        // This is attributive only filtered navigation from request
        final PageParameters attributesOnly = WicketUtil.getRetainedRequestParameters(
                pageParameters,
                allowedAttributeNames);

        // Base hold category path from beginning and accumulate all attributive navigation
        final PageParameters base = WicketUtil.getFilteredRequestParameters(
                pageParameters,
                allowedAttributeNames);

        //If we are on display product page, we have to remove for filtering  as well as sku
        base.remove(WebParametersKeys.PRODUCT_ID);
        base.remove(WebParametersKeys.SKU_ID);

        for (PageParameters.NamedPair namedPair : attributesOnly.getAllNamed()) {
            final I18NModel displayValue = attributeService.getNavigatableAttributeDisplayValue(namedPair.getKey(), namedPair.getValue());
            navigationCrumbs.add(createFilteredNavigationCrumb(
                    base, namedPair.getKey(), namedPair.getValue(), displayValue, locale, pageParameters,
                    brandPrefix, pricePrefix, queryPrefix, tagPrefix, attributeCodeName));
        }
    }

    /**
     * Create filtered navigation crumb with two links:
     * <p/>
     * First - current position, that include the whole path before current.
     * example category/17/subcategory/156/price/100-200/currentkey/currentvalue
     * <p/>
     * Second - the whole current path without current
     * example category/17/subcategory/156/price/100-200/currentkey/currentvalue/nextkey/nextvalue
     * ^^^^^^^^^^^^^^^^^^^^^^^ this will be removed,
     * so uri will be
     * example category/17/subcategory/156/price/100-200/nextkey/nextvalue
     */
    private Crumb createFilteredNavigationCrumb(final PageParameters base,
                                                final String key,
                                                final String value,
                                                final I18NModel displayValue,
                                                final String locale,
                                                final PageParameters pageParameters,
                                                final String brandPrefix,
                                                final String pricePrefix,
                                                final String queryPrefix,
                                                final String tagPrefix,
                                                final Map<String, I18NModel> attributeCodeName) {

        final PageParameters withoutCurrent = WicketUtil.getFilteredRequestParameters(
                pageParameters,
                key,
                value
        );

        String linkName = getLinkNamePrefix(key, locale, brandPrefix, pricePrefix, queryPrefix, tagPrefix, attributeCodeName);
        if (StringUtils.isNotBlank(linkName)) {
            linkName += "::" + getLinkName(key, value, displayValue.getValue(locale));
        } else {
            linkName = getLinkName(key, value, displayValue.getValue(locale));
        }

        base.add(key, value);
        return new Crumb(key, linkName, null, new PageParameters(base), withoutCurrent);
    }

    private String getLinkNamePrefix(final String key,
                                     final String locale,
                                     final String brandPrefix,
                                     final String pricePrefix,
                                     final String queryPrefix,
                                     final String tagPrefix,
                                     final Map<String, I18NModel> attributeCodeName) {
        final String name;
        if (ProductSearchQueryBuilder.BRAND_FIELD.equals(key)) {
            name = brandPrefix;
        } else if (ProductSearchQueryBuilder.PRODUCT_PRICE.equals(key)) {
            name = pricePrefix;
        } else if (ProductSearchQueryBuilder.PRODUCT_TAG_FIELD.equals(key)) {
            name = tagPrefix;
        } else if (WebParametersKeys.QUERY.equals(key)) {
            name = queryPrefix;
        } else {
            name = attributeCodeName.get(key).getValue(locale);
        }
        return name;
    }

    private String getLinkName(final String key, final String value, final String displayValue) {
        if (ProductSearchQueryBuilder.PRODUCT_PRICE.equals(key)) {
            Pair<String, Pair<BigDecimal, BigDecimal>> pair = priceNavigation.decomposePriceRequestParams(value);
            return priceNavigation.composePriceRequestParams(
                    currencySymbolService.getCurrencySymbol(pair.getFirst()),
                    pair.getSecond().getFirst(),
                    pair.getSecond().getSecond(),
                    " ",
                    "..."
            );
        }
        return displayValue;
    }



}
