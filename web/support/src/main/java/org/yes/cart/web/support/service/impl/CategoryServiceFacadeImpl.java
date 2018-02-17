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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.ShopSearchSupportService;
import org.yes.cart.service.domain.CategoryRankDisplayNameComparator;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.TimeContext;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.service.CategoryServiceFacade;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 20/11/2014
 * Time: 00:07
 */
public class CategoryServiceFacadeImpl implements CategoryServiceFacade {

    private final CategoryService categoryService;
    private final ShopService shopService;
    private final ShopSearchSupportService shopSearchSupportService;

    public CategoryServiceFacadeImpl(final CategoryService categoryService,
                                     final ShopService shopService,
                                     final ShopSearchSupportService shopSearchSupportService) {
        this.categoryService = categoryService;
        this.shopService = shopService;
        this.shopSearchSupportService = shopSearchSupportService;
    }

    /**
     * {@inheritDoc}
     */
    public Category getCategory(final long categoryId, final long customerShopId) {
        if (categoryId > 0L && shopService.getShopCategoriesIds(customerShopId).contains(categoryId)) {
            final Category category = categoryService.getById(categoryId);
            if (DomainApiUtils.isObjectAvailableNow(true, category.getAvailablefrom(), category.getAvailableto(), now())) {
                return category;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Category getDefaultNavigationCategory(final long customerShopId) {
        return shopService.getDefaultNavigationCategory(customerShopId);
    }

    /**
     * {@inheritDoc}
     */
    public Pair<List<Long>, Boolean> getSearchCategoriesIds(final long categoryId, final long customerShopId) {

        return shopSearchSupportService.getSearchCategoriesIds(categoryId, customerShopId);

    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-currentCategoryMenu")
    public List<Category> getCurrentCategoryMenu(final long currentCategoryId, final long customerShopId, final String locale) {

        final List<Category> categories;

        if (currentCategoryId > 0L && shopService.getShopCategoriesIds(customerShopId).contains(currentCategoryId)) {

            categories = new ArrayList<Category>(categoryService.getChildCategories(currentCategoryId));

        } else {

            categories = new ArrayList<Category>(shopService.getTopLevelCategories(customerShopId));

        }

        final Iterator<Category> itCat = categories.iterator();
        while (itCat.hasNext()) {
            final Category cat = itCat.next();
            if (CentralViewLabel.INCLUDE.equals(cat.getUitemplate())) {
                itCat.remove();
            }
        }

        Collections.sort(categories, new CategoryRankDisplayNameComparator(locale));

        return categories;


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
    public Pair<String, String> getThumbnailSizeConfig(final long categoryId, final long customerShopId) {

        return getImageSizeConfig(categoryId, customerShopId, CATEGORY_THUMBNAIL_SIZE, SHOP_THUMBNAIL_SIZE, DEFAULT_THUMB_SIZE);

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
    public Pair<String, String> getProductListImageSizeConfig(final long categoryId, final long customerShopId) {

        return getImageSizeConfig(categoryId, customerShopId, CATEGORY_PRODUCTLIST_IMAGE_SIZE, SHOP_PRODUCTLIST_IMAGE_SIZE, DEFAULT_PRODUCTLIST_IMAGE_SIZE);

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
    public Pair<String, String> getCategoryListImageSizeConfig(final long categoryId, final long customerShopId) {

        return getImageSizeConfig(categoryId, customerShopId, CATEGORY_CATEGORYLIST_IMAGE_SIZE, SHOP_CATEGORYLIST_IMAGE_SIZE, DEFAULT_CATEGORYLIST_IMAGE_SIZE);

    }

    /**
     * {@inheritDoc}
     */
    public int getFeaturedListSizeConfig(final long categoryId, final long customerShopId) {

        return getLimitSizeConfig(categoryId, customerShopId,
                AttributeNamesKeys.Category.CATEGORY_ITEMS_FEATURED,
                AttributeNamesKeys.Shop.SHOP_CATEGORY_ITEMS_FEATURED,
                Constants.FEATURED_LIST_SIZE);

    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryNewArrivalLimit")
    public int getNewArrivalListSizeConfig(final long categoryId, final long customerShopId) {


        if (categoryId > 0L) {

            Category category = categoryService.getById(categoryId);

            while (category != null) {
                final String value = category.getAttributeValueByCode(AttributeNamesKeys.Category.CATEGORY_ITEMS_NEW_ARRIVAL);
                if (StringUtils.isBlank(value)) {
                    final Long parentId = getCategoryParentId(category.getCategoryId(), customerShopId);
                    if (parentId != null) {
                        category = categoryService.getById(parentId);
                    } else {
                        break;
                    }
                } else {
                    final int limit = NumberUtils.toInt(value, 0);
                    if (limit > 1) {
                        return limit;
                    }
                }
            }

        }

        final Shop shop = shopService.getById(customerShopId);
        if (shop != null) {

            final String value = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CATEGORY_ITEMS_NEW_ARRIVAL);
            if (StringUtils.isNotBlank(value)) {
                final int limit = NumberUtils.toInt(value, 0);
                if (limit > 1) {
                    return limit;
                }
            }

        }

        return Constants.RECOMMENDATION_SIZE;

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryNewArrivalDate")
    public int getCategoryNewArrivalOffsetDays(final long categoryId, final long customerShopId) {

        return shopSearchSupportService.getCategoryNewArrivalOffsetDays(categoryId, customerShopId);

    }


    /**
     * {@inheritDoc}
     */
    public List<String> getItemsPerPageOptionsConfig(final long categoryId, final long customerShopId) {

        return getCSVConfig(categoryId,
                customerShopId,
                AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE,
                AttributeNamesKeys.Shop.SHOP_CATEGORY_ITEMS_PER_PAGE,
                Constants.DEFAULT_ITEMS_ON_PAGE);

    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPageSortingOptionsConfig(final long categoryId, final long customerShopId) {


        return getCSVConfig(categoryId,
                customerShopId,
                AttributeNamesKeys.Category.CATEGORY_SORT_OPTIONS,
                AttributeNamesKeys.Shop.SHOP_CATEGORY_SORT_OPTIONS,
                Constants.DEFAULT_PAGE_SORT);

    }

    /**
     * {@inheritDoc}
     */
    public int getProductListColumnOptionsConfig(final long categoryId, final long customerShopId) {

        return getLimitSizeConfig(categoryId, customerShopId,
                AttributeNamesKeys.Category.CATEGORY_PRODUCTS_COLUMNS,
                AttributeNamesKeys.Shop.SHOP_CATEGORY_PRODUCTS_COLUMNS,
                Constants.PRODUCT_COLUMNS_SIZE);

    }


    /**
     * {@inheritDoc}
     */
    public int getCategoryListColumnOptionsConfig(final long categoryId, final long customerShopId) {

        return getLimitSizeConfig(categoryId, customerShopId,
                AttributeNamesKeys.Category.CATEGORY_SUBCATEGORIES_COLUMNS,
                AttributeNamesKeys.Shop.SHOP_CATEGORY_SUBCATEGORIES_COLUMNS,
                Constants.SUBCATEGORIES_COLUMNS_SIZE);

    }


    private List<String> getCSVConfig(final long categoryId,
                                      final long shopId,
                                      final String categoryCsvAttribute,
                                      final String shopCsvAttribute,
                                      final List<String> defaultCsv) {

        if (categoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(categoryId)) {

            Category category = categoryService.getById(categoryId);

            while (category != null) {
                final String csv = category.getAttributeValueByCode(categoryCsvAttribute);
                if (StringUtils.isBlank(csv)) {
                    final Long parentId = getCategoryParentId(category.getCategoryId(), shopId);
                    if (parentId != null) {
                        category = categoryService.getById(parentId);
                    } else {
                        break;
                    }
                } else {
                    return Arrays.asList(StringUtils.split(csv, ','));
                }
            }

        }

        final Shop shop = shopService.getById(shopId);
        if (shop != null) {
            final String attrValueShop = shop.getAttributeValueByCode(shopCsvAttribute);
            if (StringUtils.isNotBlank(attrValueShop)) {
                return Arrays.asList(StringUtils.split(attrValueShop, ','));
            }
            if (shop.getMaster() != null) {
                final Shop master = shopService.getById(shop.getMaster().getShopId());
                final String attrValueShopMaster = master.getAttributeValueByCode(shopCsvAttribute);
                if (StringUtils.isNotBlank(attrValueShopMaster)) {
                    return Arrays.asList(StringUtils.split(attrValueShopMaster, ','));
                }
            }
        }
        return defaultCsv;

    }


    private int getLimitSizeConfig(final long categoryId,
                                   final long shopId,
                                   final String categoryLimitAttribute,
                                   final String shopLimitAttribute,
                                   final int defaultLimit) {

        if (categoryId > 0L && shopService.getShopCategoriesIds(shopId).contains(categoryId)) {

            Category category = categoryService.getById(categoryId);

            while (category != null) {
                final String size = category.getAttributeValueByCode(categoryLimitAttribute);
                if (StringUtils.isBlank(size)) {
                    final Long parentId = getCategoryParentId(category.getCategoryId(), shopId);
                    if (parentId != null) {
                        category = categoryService.getById(parentId);
                    } else {
                        break;
                    }
                } else {
                    final int limit = NumberUtils.toInt(size, 0);
                    if (limit > 1) {
                        return limit;
                    }
                }
            }

        }

        final Shop shop = shopService.getById(shopId);
        if (shop != null) {
            final String val = shop.getAttributeValueByCode(shopLimitAttribute);
            if (StringUtils.isNotBlank(val)) {
                return NumberUtils.toInt(val, defaultLimit);
            }
            if (shop.getMaster() != null) {
                final Shop master = shopService.getById(shop.getMaster().getShopId());
                final String valMaster = master.getAttributeValueByCode(shopLimitAttribute);
                if (StringUtils.isNotBlank(valMaster)) {
                    return NumberUtils.toInt(valMaster, defaultLimit);
                }
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

            Category category = categoryService.getById(categoryId);

            while (category != null) {
                final String size1 = category.getAttributeValueByCode(widthAndHeightAttribute[0]);
                final String size2 = category.getAttributeValueByCode(widthAndHeightAttribute[1]);
                if (StringUtils.isBlank(size1) || StringUtils.isBlank(size2)) {
                    final Long parentId = getCategoryParentId(category.getCategoryId(), shopId);
                    if (parentId != null) {
                        category = categoryService.getById(parentId);
                    } else {
                        break;
                    }
                } else {
                    return new Pair<String, String>(size1, size2);
                }
            }

        }

        final Shop shop = shopService.getById(shopId);
        if (shop != null) {
            final String widthAttrValueShop = shop.getAttributeValueByCode(shopWidthAndHeightAttribute[0]);
            final String heightAttrValueShop = shop.getAttributeValueByCode(shopWidthAndHeightAttribute[1]);
            if (StringUtils.isNotBlank(widthAttrValueShop) && StringUtils.isNotBlank(heightAttrValueShop)) {
                return new Pair<String, String>(widthAttrValueShop, heightAttrValueShop);
            }
            if (shop.getMaster() != null) {
                final Shop master = shopService.getById(shop.getMaster().getShopId());
                final String widthAttrValueShopMaster = master.getAttributeValueByCode(shopWidthAndHeightAttribute[0]);
                final String heightAttrValueShopMaster = master.getAttributeValueByCode(shopWidthAndHeightAttribute[1]);
                if (StringUtils.isNotBlank(widthAttrValueShopMaster) && StringUtils.isNotBlank(heightAttrValueShopMaster)) {
                    return new Pair<String, String>(widthAttrValueShopMaster, heightAttrValueShopMaster);
                }
            }
        }

        return defaultWidthAndHeight;
    }


    /**
     * {@inheritDoc}
     */
    public Long getCategoryProductTypeId(final long categoryId, final long customerShopId) {
        if (categoryId > 0L && shopService.getShopCategoriesIds(customerShopId).contains(categoryId)) {
            return shopService.getShopCategoryProductTypeId(customerShopId, categoryId);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getCategoryParentId(final long categoryId, final long customerShopId) {
        return shopService.getShopCategoryParentId(customerShopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public int getCategoryFilterLimitConfig(final long categoryId, final long customerShopId) {

        return getLimitSizeConfig(categoryId, customerShopId,
                AttributeNamesKeys.Category.CATEGORY_FILTERNAV_LIMIT,
                AttributeNamesKeys.Shop.SHOP_CATEGORY_FILTERNAV_LIMIT,
                Constants.CATEGORY_FILTERNAV_LIMIT);
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

}
