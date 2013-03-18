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
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.service.AttributableImageService;

import java.io.Serializable;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public class ProductDecoratorImpl extends ProductEntity implements ProductDecorator, Serializable {

    private final static List<String> attrNames = new ArrayList<String>() {{
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "1");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "2");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "3");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "4");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "5");
    }};

    final static  String [] defaultSize =
            new String [] {
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT
            };

    final static  String [] thumbnailSize =
            new String [] {
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_TUMB_WIDTH,
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_TUMB_HEIGHT
            };

    private transient ProductService productService;
    private transient AttributableImageService attributableImageService;
    private transient CategoryService categoryService;
    private transient ImageService imageService;

    private final String httpServletContextPath;
    private final HashMap<String, String> productImageUrl;
    private final Map<String, AttrValue> attrValueMap;
    private final String defaultImageAttributeValue;
    private final I18NWebSupport i18NWebSupport;


        /**
        * Construct entity decorator.
        *
        * @param imageService image serice to get the image seo info
        * @param attributableImageService category image service to get the image.
        * @param categoryService          to get image width and height
        * @param productEntity            original product to decorate.
        * @param httpServletContextPath   servlet context path
        * @param productService           product service
        * @param i18NWebSupport           i18n support
        */
    public ProductDecoratorImpl(
            final ImageService imageService,
            final AttributableImageService attributableImageService,
            final CategoryService categoryService,
            final ProductService productService,
            final I18NWebSupport i18NWebSupport  ,
            final Product productEntity,
            final String httpServletContextPath,
            final boolean withAttributes,
            final String defaultImageAttributeValue) {

        this.i18NWebSupport = i18NWebSupport;
        BeanUtils.copyProperties(productEntity, this);
        this.httpServletContextPath = httpServletContextPath;
        this.productImageUrl = new HashMap<String, String>();
        this.defaultImageAttributeValue = defaultImageAttributeValue;
        if (withAttributes) {
            this.attrValueMap = getAllAttibutesAsMap();
        } else {
            this.attrValueMap = Collections.emptyMap();
        }

        this.attributableImageService = attributableImageService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.imageService = imageService;

    }

    /**
     * Attache to context after deserialization in case of cache overflow.
     *
     * @param imageService image serice to get the image seo info
     * @param attributableImageService category image service to get the image.
     * @param categoryService          to get image width and height
     * @param productService           product service
     */
    public void attachToContext(
            final ImageService imageService,
            final AttributableImageService attributableImageService,
            final CategoryService categoryService,
            final ProductService productService) {


        this.attributableImageService = attributableImageService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.imageService = imageService;

    }

    /**
     * Construct entity decorator.
     *
     * @param productEntity            original product to decorate.
     * @param httpServletContextPath   servlet context path
     * @param i18NWebSupport           i18n support
     */
    public ProductDecoratorImpl(
            final I18NWebSupport i18NWebSupport  ,
            final Product productEntity,
            final String httpServletContextPath,
            final boolean withAttributes,
            final String defaultImageAttributeValue) {

        this.i18NWebSupport = i18NWebSupport;
        BeanUtils.copyProperties(productEntity, this);
        this.httpServletContextPath = httpServletContextPath;
        this.productImageUrl = new HashMap<String, String>();
        this.defaultImageAttributeValue = defaultImageAttributeValue;
        if (withAttributes) {
            this.attrValueMap = getAllAttibutesAsMap();
        } else {
            this.attrValueMap = Collections.emptyMap();
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
        return attributableImageService.getImage(
                this,
                httpServletContextPath,
                width,
                height,
                imageAttributeName,
                null);
    }



    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final String width, final String height) {
        final String key = width + height;
        String val = productImageUrl.get(key);
        if (val == null) {
            val = attributableImageService.getImage(
                    this,
                    httpServletContextPath,
                    width,
                    height,
                    getDefaultImageAttributeName() ,
                    defaultImageAttributeValue
            );
            productImageUrl.put(key, val);
            return val;
        }
        return val;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueProduct getAttributeByCode(final String attributeCode) {
        return (AttrValueProduct) attrValueMap.get(attributeCode);
    }


    /**
     * {@inheritDoc}
     */
    public String [] getDefaultImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                null, category,
                defaultSize
        );
    }


    /**
     * {@inheritDoc}
     */
    public String [] getThumbnailImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                null, category,
                ProductDecoratorImpl.thumbnailSize
        );
    }




    /**
     * {@inheritDoc}
     */
    public String getDefaultImageAttributeName() {
        return Constants.PRODUCT_DEFAULT_IMAGE_ATTR_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public SeoImage getSeoImage(final String fileName) {
        return imageService.getSeoImage(fileName);
    }

    /**
     * {@inheritDoc}
     */
    public String getName(final String locale) {
        return i18NWebSupport.getFailoverModel(getDisplayName(), getName()).getValue(locale);
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValue(final String attribute) {
        final AttrValueProduct val =  getAttributeByCode(attribute);
        return val != null ? val.getVal() : "";
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValue(final String locale, final String attribute) {
        final AttrValueProduct lval =  getAttributeByCode(attribute);
        return lval != null ? i18NWebSupport.getFailoverModel(lval.getDisplayVal(), lval.getVal()).getValue(locale) : "";
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription(final String locale) {
        final Pair<String, String> desc = productService.getProductAttribute(
                locale, getProductId(), 0L, AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX + locale);
        if (desc == null) {
            return getDescription();
        }
        return desc.getSecond();
    }


}
