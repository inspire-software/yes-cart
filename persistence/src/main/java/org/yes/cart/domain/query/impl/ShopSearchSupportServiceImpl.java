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

package org.yes.cart.domain.query.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ShopSearchSupportService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 05/03/2016
 * Time: 16:29
 */
public class ShopSearchSupportServiceImpl implements ShopSearchSupportService {

    private final ShopService shopService;
    private final CategoryService categoryService;

    public ShopSearchSupportServiceImpl(final ShopService shopService,
                                        final CategoryService categoryService) {
        this.shopService = shopService;
        this.categoryService = categoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-searchCategoriesIds")
    public Pair<List<Long>, Boolean> getSearchCategoriesIds(final long categoryId, final long shopId) {
        // If template is not set try to figure out the view

        Boolean lookInSubCats = null;

        Category category = categoryService.getById(categoryId);

        while (category != null) {
            final String searchInSub = category.getAttributeValueByCode(AttributeNamesKeys.Category.CATEGORY_INCLUDE_SUBCATEGORIES_IN_SEARCH);
            if (StringUtils.isBlank(searchInSub)) {
                final Long parentId = shopService.getShopCategoryParentId(shopId, category.getCategoryId());
                if (parentId != null) {
                    category = categoryService.getById(parentId);
                } else {
                    break;
                }
            } else {
                lookInSubCats = Boolean.valueOf(searchInSub);
                break;
            }
        }

        if (lookInSubCats == null) {
            lookInSubCats = shopService.getById(shopId).isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_INCLUDE_SUBCATEGORIES_IN_SEARCH);
        }

        final List<Long> catIds = categoryService.getCategoryIdsWithLinks(categoryId);

        return new Pair<List<Long>, Boolean>(catIds, lookInSubCats);
    }



    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryNewArrivalDate")
    public Date getCategoryNewArrivalDate(final long categoryId, final long shopId) {

        final Shop shop = shopService.getById(shopId);
        final String value = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_NEW_ARRIVAL_DAYS_OFFSET);
        int beforeDays = NumberUtils.toInt(value, 15);

        Category category = categoryService.getById(categoryId);
        while (category != null) {
            String catValue = category.getAttributeValueByCode(AttributeNamesKeys.Category.CATEGORY_NEW_ARRIVAL_DAYS_OFFSET);
            if (StringUtils.isBlank(catValue)) {
                final Long parentId = shopService.getShopCategoryParentId(shopId, category.getCategoryId());
                if (parentId != null) {
                    category = categoryService.getById(parentId);
                } else {
                    break;
                }
            } else {
                int catBeforeDays = NumberUtils.toInt(value, 0);
                if (catBeforeDays > beforeDays) { // get the earliest
                    beforeDays = catBeforeDays;
                }
                break;
            }
        }

        final Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR, -beforeDays);
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }


}
