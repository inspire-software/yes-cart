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
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.service.ContentServiceFacade;

import java.util.*;

/**
 * User: denispavlov
 * Date: 20/11/2014
 * Time: 12:04
 */
public class ContentServiceFacadeImpl implements ContentServiceFacade {

    private final ContentService contentService;
    private final ShopService shopService;

    public ContentServiceFacadeImpl(final ContentService contentService, final ShopService shopService) {
        this.contentService = contentService;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    public Category getContent(final long contentId, final long shopId) {
        if (contentId > 0L && shopService.getShopContentIds(shopId).contains(contentId)) {
            return contentService.getById(contentId);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getContentBody(final long contentId, final long shopId, final String locale) {
        if (contentId > 0L && shopService.getShopContentIds(shopId).contains(contentId)) {
            return contentService.getContentBody(contentId, locale);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentBody")
    public String getContentBody(final String contentUri, final long shopId, final String locale) {

        if (StringUtils.isBlank(contentUri)) {
            return null;
        }

        final Shop shop = shopService.getById(shopId);

        if (shop == null) {
            return null;
        }

        final String shopContentUri = shop.getCode().concat("_").concat(contentUri);

        final Long shopSpecificContentId = contentService.findContentIdBySeoUri(shopContentUri);

        if (shopSpecificContentId != null && shopService.getShopContentIds(shopId).contains(shopSpecificContentId)) {
            return contentService.getContentBody(shopSpecificContentId, locale);
        }

        final Long contentId = contentService.findContentIdBySeoUri(contentUri);
        if (contentId != null && shopService.getShopContentIds(shopId).contains(contentId)) {
            return contentService.getContentBody(contentId, locale);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDynamicContentBody(final long contentId, final long shopId, final String locale, final Map<String, Object> context) {
        if (contentId > 0L && shopService.getShopContentIds(shopId).contains(contentId)) {
            return contentService.getDynamicContentBody(contentId, locale, context);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDynamicContentBody(final String contentUri, final long shopId, final String locale, final Map<String, Object> context) {

        if (StringUtils.isBlank(contentUri)) {
            return null;
        }

        final Shop shop = shopService.getById(shopId);

        if (shop == null) {
            return null;
        }

        final String shopContentUri = shop.getCode().concat("_").concat(contentUri);

        final Long shopSpecificContentId = contentService.findContentIdBySeoUri(shopContentUri);

        if (shopSpecificContentId != null && shopService.getShopContentIds(shopId).contains(shopSpecificContentId)) {
            return contentService.getDynamicContentBody(shopSpecificContentId, locale, context);
        }

        final Long contentId = contentService.findContentIdBySeoUri(contentUri);
        if (contentId != null && shopService.getShopContentIds(shopId).contains(contentId)) {
            return contentService.getDynamicContentBody(contentId, locale, context);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getCurrentContentMenu(final long currentContentId, final long shopId) {

        if (currentContentId > 0L && shopService.getShopContentIds(shopId).contains(currentContentId)) {

            final List<Category> categories = new ArrayList<Category>(contentService.getChildContent(currentContentId));
            final Iterator<Category> itCat = categories.iterator();
            while (itCat.hasNext()) {
                final Category cat = itCat.next();
                if (CentralViewLabel.CONTENT_INCLUDE.equals(cat.getUitemplate())) {
                    itCat.remove();
                }
            }
            return categories;

        }

        return Collections.emptyList();
    }


    private static final String[] CATEGORYLIST_IMAGE_SIZE =
            new String[]{
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT
            };

    private static final Pair<String, String> DEFAULT_CATEGORYLIST_IMAGE_SIZE =
            new Pair<String, String>(Constants.DEFAULT_CATEGORYLIST_IMAGE_SIZE[0], Constants.DEFAULT_CATEGORYLIST_IMAGE_SIZE[1]);

    /**
     * {@inheritDoc}
     */
    public Pair<String, String> getContentListImageSizeConfig(final long contentId, final long shopId) {

        return getImageSizeConfig(contentId, shopId, CATEGORYLIST_IMAGE_SIZE, DEFAULT_CATEGORYLIST_IMAGE_SIZE);

    }


    /**
     * {@inheritDoc}
     */
    public List<String> getItemsPerPageOptionsConfig(final long contentId, final long shopId) {

        if (contentId > 0L && shopService.getShopContentIds(shopId).contains(contentId)) {

            final String size = contentService.getContentAttributeRecursive(
                    null, contentId, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null
            );
            if (StringUtils.isNotBlank(size)) {
                return Arrays.asList(StringUtils.split(size, ','));
            }

        }

        return Constants.DEFAULT_ITEMS_ON_PAGE;

    }



    /**
     * {@inheritDoc}
     */
    public int getContentListColumnOptionsConfig(final long contentId, final long shopId) {

        return getLimitSizeConfig(contentId, shopId, AttributeNamesKeys.Category.CATEGORY_PRODUCTS_COLUMNS, Constants.PRODUCT_COLUMNS_SIZE);

    }

    private int getLimitSizeConfig(final long categoryId,
                                   final long shopId,
                                   final String limitAttribute,
                                   final int defaultLimit) {

        if (categoryId > 0L && shopService.getShopContentIds(shopId).contains(categoryId)) {

            final String size = contentService.getContentAttributeRecursive(
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
                                                    final Pair<String, String> defaultWidthAndHeight) {

        if (categoryId > 0L && shopService.getShopContentIds(shopId).contains(categoryId)) {

            final String[] size = contentService.getContentAttributeRecursive(null, categoryId, widthAndHeightAttribute);
            if (size != null && size.length == 2) {
                return new Pair<String, String>(size[0], size[1]);
            }

        }

        return defaultWidthAndHeight;
    }


}
