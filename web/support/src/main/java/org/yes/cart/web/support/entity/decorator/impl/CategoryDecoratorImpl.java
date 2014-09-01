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

package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.entity.impl.CategoryEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.service.AttributableImageService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 8:19 PM
 */
public class CategoryDecoratorImpl extends CategoryEntity implements CategoryDecorator {

    private static final List<String> attrNames = Collections.singletonList(AttributeNamesKeys.Category.CATEGORY_IMAGE);

    private final static String[] defaultSize =
            new String[]{
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT
            };

    private final static String[] thumbnailSize =
            new String[]{
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT
            };

    private final AttributableImageService categoryImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private Map<Integer, String> categoryImageUrl = new HashMap<Integer, String>();
    private final ImageService imageService;
    private final I18NWebSupport i18NWebSupport;

    /**
     * Construct entity decorator.
     *
     * @param imageService           image service to get the image seo info
     * @param categoryImageService   category image service to get the image.
     * @param categoryService        category service to get the images width and height
     * @param categoryEntity         entity to decorate.
     * @param httpServletContextPath servlet context path
     * @param i18NWebSupport         i18n support
     */
    public CategoryDecoratorImpl(
            final ImageService imageService,
            final AttributableImageService categoryImageService,
            final CategoryService categoryService,
            final Category categoryEntity,
            final String httpServletContextPath, final I18NWebSupport i18NWebSupport) {
        this.categoryService = categoryService;
        this.categoryImageService = categoryImageService;
        this.httpServletContextPath = httpServletContextPath;
        this.imageService = imageService;
        this.i18NWebSupport = i18NWebSupport;
        if (categoryEntity != null) {
            BeanUtils.copyProperties(categoryEntity, this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getImageAttributeNames() {
        return attrNames;
    }


    /**
     * {@inheritDoc}
     */
    public List<Pair<String, String>> getImageAttributeFileNames() {
        final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>();
        for (String attrName : attrNames) {
            if (this.getAttributeByCode(attrName) != null) {
                rez.add(new Pair<String, String>(attrName, this.getAttributeByCode(attrName).getVal()));
            }
        }
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public String getImage(final String width, final String height, final String imageAttributeName) {
        final Integer hash = getImageWithSizeHash(imageAttributeName, width, height);
        String img = categoryImageUrl.get(hash);
        if (img == null) {
            img = categoryImageService.getImage(
                    this,
                    httpServletContextPath,
                    width,
                    height,
                    imageAttributeName,
                    null);
            categoryImageUrl.put(hash, img);
        }
        return img;
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final String width, final String height) {
        return getImage(width, height, null);
    }

    private Integer getImageWithSizeHash(final String ... params) {
        final int prime = 31;
        int result = 1;
        for( String param : params) {
            if (param == null) {
                continue;
            }
            result = result * prime + param.hashCode();
        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public String[] getDefaultImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                null, category,
                defaultSize
        );
    }


    /**
     * {@inheritDoc}
     */
    public String[] getThumbnailImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                null, category,
                thumbnailSize
        );
    }


    public String getThumbnailImageWidth(final Category category) {
        return categoryService.getCategoryAttributeRecursive(null, category,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                "80");
    }

    public String getThumbnailImageHeight(final Category category) {
        return categoryService.getCategoryAttributeRecursive(null, this,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT,
                "80");
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultImageAttributeName() {
        return AttributeNamesKeys.Category.CATEGORY_IMAGE;
    }


    /**
     * {@inheritDoc}
     */
    public SeoImage getSeoImage(final String fileName) {
        return imageService.getSeoImage(Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN + fileName);
    }

    /** {@inheritDoc} */
    public String getName(final String locale) {
        return i18NWebSupport.getFailoverModel(getDisplayName(), getName()).getValue(locale);
    }

    /** {@inheritDoc} */
    public String getAttributeValue(final String attribute) {
        return categoryService.getCategoryAttributeRecursive(null, this,
                attribute,
                getDescription());
    }

    /** {@inheritDoc} */
    public String getAttributeValue(final String locale, final String attribute) {
        return categoryService.getCategoryAttributeRecursive(locale, this,
                attribute,
                getDescription());
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription(final String locale) {
        return getAttributeValue(AttributeNamesKeys.Category.CATEGORY_DESCRIPTION_PREFIX + locale);
    }
}
