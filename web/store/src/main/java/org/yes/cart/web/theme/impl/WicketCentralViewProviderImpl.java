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

package org.yes.cart.web.theme.impl;

import org.apache.lucene.search.BooleanQuery;
import org.slf4j.Logger;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.DomainApiUtil;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.AbstractCentralView;
import org.yes.cart.web.page.component.EmptyCentralView;
import org.yes.cart.web.theme.WicketCentralViewProvider;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 10/11/2014
 * Time: 10:36
 */
public class WicketCentralViewProviderImpl implements WicketCentralViewProvider {

    public static enum CategoryType { ANY, CATEGORY, CONTENT }

    private final Map<String, Class<? extends AbstractCentralView>> rendererPanelMap;
    private final Map<Class<? extends AbstractCentralView>, CategoryType> categoryTypeMap;

    private final ShopService shopService;
    private final CategoryService categoryService;

    public WicketCentralViewProviderImpl(final ShopService shopService,
                                         final CategoryService categoryService,
                                         final Map<String, Class<? extends AbstractCentralView>> rendererPanelMap,
                                         final Map<Class<? extends AbstractCentralView>, CategoryType> categoryTypeMap) {
        this.rendererPanelMap = rendererPanelMap;
        this.categoryTypeMap = categoryTypeMap;
        this.shopService = shopService;
        this.categoryService = categoryService;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCentralView getCentralPanel(final String rendererLabel,
                                               final String wicketComponentId,
                                               final long categoryId,
                                               final BooleanQuery booleanQuery) {

        Class<? extends AbstractCentralView> clz = rendererPanelMap.get(rendererLabel);
        try {
            if (categoryId != 0 && clz != null) {

                CategoryType type = categoryTypeMap.get(clz);
                if (type == null) {
                    type = CategoryType.ANY;
                }

                switch (type) {
                    case CATEGORY:
                        //check is this category allowed to open in this shop
                        if (!isCategoryVisibleInShop(categoryId)) {
                            final Logger log = ShopCodeContext.getLog(this);
                            if (log.isWarnEnabled()) {
                                log.warn("Can not access category {} from shop {}", categoryId, ShopCodeContext.getShopId());
                            }
                            return new EmptyCentralView(wicketComponentId, booleanQuery);
                        }
                        break;
                    case CONTENT:
                        //check is this content is allowed to open in this shop
                        if (!isContentVisibleInShop(categoryId)) {
                            final Logger log = ShopCodeContext.getLog(this);
                            if (log.isWarnEnabled()) {
                                log.warn("Can not access content {} from shop {}", categoryId, ShopCodeContext.getShopId());
                            }
                            return new EmptyCentralView(wicketComponentId, booleanQuery);
                        }
                        break;
                    case ANY:
                    default:
                        //check is this category/content allowed to open in this shop
                        if (!isCategoryVisibleInShop(categoryId) && !isContentVisibleInShop(categoryId)) {
                            final Logger log = ShopCodeContext.getLog(this);
                            if (log.isWarnEnabled()) {
                                log.warn("Can not access category {} from shop {}", categoryId, ShopCodeContext.getShopId());
                            }
                            return new EmptyCentralView(wicketComponentId, booleanQuery);
                        }
                        break;

                }

            }

            Constructor<? extends AbstractCentralView> constructor = clz.getConstructor(String.class,
                    long.class,
                    BooleanQuery.class);
            return constructor.newInstance(wicketComponentId, categoryId, booleanQuery);

        } catch (Exception e) {
            ShopCodeContext.getLog(this).error(MessageFormat.format("Can not create instance of panel for label {0}", rendererLabel), e);
            //e.printStackTrace();
            return new EmptyCentralView(wicketComponentId, booleanQuery);

        }
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

        final Set<Long> catIds = shopService.getShopCategoriesIds(ShopCodeContext.getShopId());
        Category category = categoryService.getById(categoryId);
        final Date now = new Date();

        while (category != null
                && DomainApiUtil.isObjectAvailableNow(true, category.getAvailablefrom(), category.getAvailableto(), now)
                && category.getCategoryId() != category.getParentId()) { // while enabled and not reached root

            if (catIds.contains(categoryId)) {

                return true;

            }
            category = categoryService.getById(category.getParentId());

        }
        return false;
    }

    /**
     * Check if given content is visible in current shop.
     *
     * NOTE: we are using categoryService for retrieving content since Breadcrumbs use
     * categoryService and thus cache will work better
     *
     * Criteria to satisfy:
     * 1. Content parent must be root content for given shop
     * 2. Only current content object must satisfy Availablefrom/Availableto time frame
     *
     * @param contentId content to check
     *
     * @return true if criteria is met
     */
    private boolean isContentVisibleInShop(final Long contentId) {

        final Set<Long> catIds = shopService.getShopContentIds(ShopCodeContext.getShopId());
        Category content = categoryService.getById(contentId);
        final Date now = new Date();

        if (DomainApiUtil.isObjectAvailableNow(true, content.getAvailablefrom(), content.getAvailableto(), now)) {

            while (content != null && content.getCategoryId() != content.getParentId()) {

                if (catIds.contains(content.getCategoryId())) {

                    return true;

                }

                content = categoryService.getById(content.getParentId());

            }

        }
        return false;
    }


}
