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

package org.yes.cart.web.theme.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.util.TimeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.AbstractCentralView;
import org.yes.cart.web.page.component.EmptyCentralView;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.theme.WicketCentralViewProvider;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 10/11/2014
 * Time: 10:36
 */
public class WicketCentralViewProviderImpl implements WicketCentralViewProvider {

    private static final Logger LOG = LoggerFactory.getLogger(WicketCentralViewProviderImpl.class);

    public enum CategoryType { ANY, CATEGORY, CONTENT }

    private static final String DEFAULT_PANEL = "default";

    private final Map<String, Class<? extends AbstractCentralView>> rendererPanelMap;
    private final Map<Class<? extends AbstractCentralView>, CategoryType> categoryTypeMap;

    private final ShopService shopService;
    private final CategoryService categoryService;
    private final ContentService contentService;

    public WicketCentralViewProviderImpl(final ShopService shopService,
                                         final CategoryService categoryService,
                                         final ContentService contentService,
                                         final Map<String, Class<? extends AbstractCentralView>> rendererPanelMap,
                                         final Map<Class<? extends AbstractCentralView>, CategoryType> categoryTypeMap) {
        this.rendererPanelMap = rendererPanelMap;
        this.categoryTypeMap = categoryTypeMap;
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.contentService = contentService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCentralView getCentralPanel(final Pair<String, String> rendererLabel,
                                               final String wicketComponentId,
                                               final long categoryId,
                                               final NavigationContext navigationContext) {

        Class<? extends AbstractCentralView> clz = resolveCentralPanelClass(rendererLabel);
        try {

            if (clz != null) {

                if (categoryId > 0L) {

                    CategoryType type = categoryTypeMap.get(clz);
                    if (type == null) {
                        LOG.warn("No category type is specified for panel class {}", clz.getCanonicalName());
                        type = CategoryType.ANY;
                    }

                    switch (type) {
                        case CATEGORY:
                            //check is this category allowed to open in this shop
                            if (!isCategoryVisibleInShop(categoryId)) {
                                LOG.warn("Can not access category {} from shop {}", categoryId, ShopCodeContext.getShopId());
                                clz = getDefaultPanel();
                            }
                            break;
                        case CONTENT:
                            //check is this content is allowed to open in this shop
                            if (!isContentVisibleInShop(categoryId)) {
                                LOG.warn("Can not access content {} from shop {}", categoryId, ShopCodeContext.getShopId());
                                clz = getDefaultPanel();
                            }
                            break;
                        case ANY:
                        default:
                            //check is this category/content allowed to open in this shop
                            if (!isCategoryVisibleInShop(categoryId) && !isContentVisibleInShop(categoryId)) {
                                LOG.warn("Can not access category {} from shop {}", categoryId, ShopCodeContext.getShopId());
                                clz = getDefaultPanel();
                            }
                            break;

                    }

                }

            } else {

                LOG.warn("Can not create instance of panel for label {}", rendererLabel);
                clz = getDefaultPanel();

            }

            Constructor<? extends AbstractCentralView> constructor =
                    clz.getConstructor(String.class, long.class, NavigationContext.class);
            return constructor.newInstance(wicketComponentId, categoryId, navigationContext);

        } catch (Exception e) {

            LOG.error("Can not create instance of panel for label " + rendererLabel, e);
            return new EmptyCentralView(wicketComponentId, navigationContext);

        }
    }

    private Class<? extends AbstractCentralView> resolveCentralPanelClass(final Pair<String, String> rendererLabel) {
        // Attempt to use best match label (this label could refer to custom templates)
        Class<? extends AbstractCentralView> clz = rendererPanelMap.get(rendererLabel.getFirst());
        if (clz == null) {
            // If custom template mapping is not available try default label mapping
            clz = rendererPanelMap.get(rendererLabel.getSecond());
        }
        return clz;
    }

    private Class<? extends AbstractCentralView> getDefaultPanel() {
        return rendererPanelMap.get(DEFAULT_PANEL);
    }


    /**
     * Check if given category is visible in current shop.
     *
     * Criteria to satisfy:
     * 1. Category or its parent must belong to a ShopCategory for given shop
     * 2. All categories in hierarchy leading to ShopCategory must satisfy Availablefrom/Availableto time frame
     *
     * @param categoryId category to check
     *
     * @return true if criteria is met
     */
    private boolean isCategoryVisibleInShop(final Long categoryId) {

        if (categoryId != null) {
            final long customerShopId = ApplicationDirector.getShoppingCart().getShoppingContext().getCustomerShopId();
            final Set<Long> catIds = shopService.getShopCategoriesIds(customerShopId);
            if (catIds.contains(categoryId)) {
                Category category = categoryService.getById(categoryId);
                final LocalDateTime now = now();

                while (category != null && !category.isRoot()) { // while enabled and not reached root

                    if (!DomainApiUtils.isObjectAvailableNow(!category.isDisabled(), category.getAvailablefrom(), category.getAvailableto(), now)) {
                        LOG.warn("Attempted to access category {} in shop {} but {} category is not available", categoryId, customerShopId, category.getCategoryId());
                        return false; // not available
                    }

                    category = categoryService.getById(category.getParentId());

                }

                return true; // loop finished successfully
                
            }
        }
        return false;
    }

    private LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * Check if given content is visible in current shop.
     *
     * Criteria to satisfy:
     * 1. Content parent must be root content for given shop
     * 2. All content in hierarchy leading to content object must satisfy Availablefrom/Availableto time frame
     *
     * @param contentId content to check
     *
     * @return true if criteria is met
     */
    private boolean isContentVisibleInShop(final Long contentId) {

        if (contentId != null) {
            final long shopId = ApplicationDirector.getShoppingCart().getShoppingContext().getShopId();
            final Set<Long> catIds = shopService.getShopContentIds(shopId);
            if (catIds.contains(contentId)) {
                Content content = contentService.getById(contentId);
                final LocalDateTime now = now();

                while (content != null && !content.isRoot() &&  !CentralViewLabel.INCLUDE.equals(content.getUitemplate())) {

                    if (!DomainApiUtils.isObjectAvailableNow(!content.isDisabled(), content.getAvailablefrom(), content.getAvailableto(), now)) {
                        LOG.warn("Attempted to access content {} in shop {} but {} content is not available", contentId, shopId, content.getContentId());
                        return false; // not available
                    }

                    content = contentService.getById(content.getParentId());

                }

                return true; // loop finished successfully

            }
        }
        return false;
    }


}
