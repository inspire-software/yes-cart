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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.support.service.CategoryServiceFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 20/11/2014
 * Time: 00:07
 */
public class CategoryServiceFacadeImpl implements CategoryServiceFacade {

    private final CategoryService categoryService;
    private final ShopService shopService;

    public CategoryServiceFacadeImpl(final CategoryService categoryService, final ShopService shopService) {
        this.categoryService = categoryService;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    public Category getCategory(final long categoryId, final long shopId) {
        if (categoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(categoryId)) {
            return categoryService.getById(categoryId);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getSearchCategoriesIds(final long categoryId, final long shopId) {
        if (categoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(categoryId)) {
            if (categoryService.isSearchInSubcategory(categoryId, shopId)) {
                return Collections.unmodifiableList(new ArrayList<Long>(categoryService.getChildCategoriesRecursiveIds(categoryId)));
            }
            return Collections.singletonList(categoryId);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getCurrentCategoryMenu(final long currentCategoryId, final long shopId) {

        if (currentCategoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(currentCategoryId)) {

            return categoryService.getChildCategories(currentCategoryId);

        }

        return categoryService.getTopLevelCategories(shopId);
    }

    private static final String[] CATEGORY_THUMBNAIL_SIZE =
            new String[] {
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_THUMB_WIDTH,
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_THUMB_HEIGHT
            };

    private static final String[] SHOP_THUMBNAIL_SIZE =
            new String[] {
                    AttributeNamesKeys.Shop.SHOP_PRODUCT_IMAGE_THUMB_WIDTH,
                    AttributeNamesKeys.Shop.SHOP_PRODUCT_IMAGE_THUMB_HEIGHT
            };

    private static final Pair<String, String> DEFAULT_THUMB_SIZE =
            new Pair<String, String>(Constants.DEFAULT_THUMB_SIZE[0], Constants.DEFAULT_THUMB_SIZE[1]);

    /**
     * {@inheritDoc}
     */
    public Pair<String, String> getThumbnailSizeConfig(final long categoryId, final long shopId) {

        return getImageSizeConfig(categoryId, shopId, CATEGORY_THUMBNAIL_SIZE, SHOP_THUMBNAIL_SIZE, DEFAULT_THUMB_SIZE);

    }

    private static final String[] CATEGORY_PRODUCTLIST_IMAGE_SIZE =
            new String[] {
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT
            };

    private static final String[] SHOP_PRODUCTLIST_IMAGE_SIZE =
            new String[] {
                    AttributeNamesKeys.Shop.SHOP_PRODUCT_IMAGE_WIDTH,
                    AttributeNamesKeys.Shop.SHOP_PRODUCT_IMAGE_HEIGHT
            };

    private static final Pair<String, String> DEFAULT_PRODUCTLIST_IMAGE_SIZE =
            new Pair<String, String>(Constants.DEFAULT_PRODUCTLIST_IMAGE_SIZE[0], Constants.DEFAULT_PRODUCTLIST_IMAGE_SIZE[1]);

    /**
     * {@inheritDoc}
     */
    public Pair<String, String> getProductListImageSizeConfig(final long categoryId, final long shopId) {

        return getImageSizeConfig(categoryId, shopId, CATEGORY_PRODUCTLIST_IMAGE_SIZE, SHOP_PRODUCTLIST_IMAGE_SIZE, DEFAULT_PRODUCTLIST_IMAGE_SIZE);

    }

    private static final String[] CATEGORY_CATEGORYLIST_IMAGE_SIZE =
            new String[]{
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT
            };

    private static final String[] SHOP_CATEGORYLIST_IMAGE_SIZE =
            new String[]{
                    AttributeNamesKeys.Shop.SHOP_CATEGORY_IMAGE_WIDTH,
                    AttributeNamesKeys.Shop.SHOP_CATEGORY_IMAGE_HEIGHT
            };

    private static final Pair<String, String> DEFAULT_CATEGORYLIST_IMAGE_SIZE =
            new Pair<String, String>(Constants.DEFAULT_CATEGORYLIST_IMAGE_SIZE[0], Constants.DEFAULT_CATEGORYLIST_IMAGE_SIZE[1]);

    /**
     * {@inheritDoc}
     */
    public Pair<String, String> getCategoryListImageSizeConfig(final long categoryId, final long shopId) {

        return getImageSizeConfig(categoryId, shopId, CATEGORY_CATEGORYLIST_IMAGE_SIZE, SHOP_CATEGORYLIST_IMAGE_SIZE, DEFAULT_CATEGORYLIST_IMAGE_SIZE);

    }

    /**
     * {@inheritDoc}
     */
    public int getFeaturedListSizeConfig(final long categoryId, final long shopId) {

        return getLimitSizeConfig(categoryId, shopId, AttributeNamesKeys.Category.CATEGORY_ITEMS_FEATURED, Constants.FEATURED_LIST_SIZE);

    }

    /**
     * {@inheritDoc}
     */
    public int getNewArrivalListSizeConfig(final long categoryId, final long shopId) {

        return categoryService.getCategoryNewArrivalLimit(categoryId, shopId);

    }

    /**
     * {@inheritDoc}
     */
    public List<String> getItemsPerPageOptionsConfig(final long categoryId, final long shopId) {

        if (categoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(categoryId)) {

            final String size = categoryService.getCategoryAttributeRecursive(
                    null, categoryId, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null
            );
            if (StringUtils.isNotBlank(size)) {
                return Arrays.asList(StringUtils.split(size, ','));
            }

        }

        final Shop shop = shopService.getById(shopId);
        final AttrValueShop attrValueShop = shop.getAttributeByCode(AttributeNamesKeys.Shop.SHOP_CATEGORY_ITEMS_PER_PAGE);
        if (attrValueShop != null && StringUtils.isNotBlank(attrValueShop.getVal())) {
            return Arrays.asList(StringUtils.split(attrValueShop.getVal(), ','));
        }

        return Constants.DEFAULT_ITEMS_ON_PAGE;

    }

    /**
     * {@inheritDoc}
     */
    public int getProductListColumnOptionsConfig(final long categoryId, final long shopId) {

        return getLimitSizeConfig(categoryId, shopId, AttributeNamesKeys.Category.CATEGORY_PRODUCTS_COLUMNS, Constants.PRODUCT_COLUMNS_SIZE);

    }


    /**
     * {@inheritDoc}
     */
    public int getCategoryListColumnOptionsConfig(final long categoryId, final long shopId) {

        return getLimitSizeConfig(categoryId, shopId, AttributeNamesKeys.Category.CATEGORY_SUBCATEGORIES_COLUMNS, Constants.SUBCATEGORIES_COLUMNS_SIZE);

    }

    private int getLimitSizeConfig(final long categoryId,
                                   final long shopId,
                                   final String limitAttribute,
                                   final int defaultLimit) {

        if (categoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(categoryId)) {

            final String size = categoryService.getCategoryAttributeRecursive(
                    null, categoryId, limitAttribute, null
            );
            if (size != null) {
                return NumberUtils.toInt(size, defaultLimit);
            }

        }

        return defaultLimit;
    }


    private Pair<String, String> getImageSizeConfig(final long categoryId,
                                                    final long shopId,
                                                    final String[] widthAndHeightAttribute,
                                                    final String[] shopWidthAndHeightAttribute,
                                                    final Pair<String, String> defaultWidthAndHeight) {

        if (categoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(categoryId)) {

            final String[] size = categoryService.getCategoryAttributeRecursive(null, categoryId, widthAndHeightAttribute);
            if (size != null && size.length == 2) {
                return new Pair<String, String>(size[0], size[1]);
            }

        }

        final Shop shop = shopService.getById(shopId);
        final AttrValueShop widthAttrValueShop = shop.getAttributeByCode(shopWidthAndHeightAttribute[0]);
        final AttrValueShop heightAttrValueShop = shop.getAttributeByCode(shopWidthAndHeightAttribute[1]);
        if (widthAttrValueShop != null && heightAttrValueShop != null && StringUtils.isNotBlank(widthAttrValueShop.getVal())
                && StringUtils.isNotBlank(heightAttrValueShop.getVal())) {
            return new Pair<String, String>(widthAttrValueShop.getVal(), heightAttrValueShop.getVal());
        }

        return defaultWidthAndHeight;
    }



}
