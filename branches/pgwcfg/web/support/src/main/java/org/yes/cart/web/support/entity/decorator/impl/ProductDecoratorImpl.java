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

import com.google.common.collect.MapMaker;
import org.apache.commons.lang.math.NumberUtils;
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
import org.yes.cart.web.support.service.AttributableImageService;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public class ProductDecoratorImpl extends ProductEntity implements ProductDecorator {

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

    public static final ConcurrentMap<String, String> PRODUCT_ENCODE_CACHE = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expiration(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();

    private static final ConcurrentMap<String, ProductDecoratorImpl> productDecoratorCache = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expiration(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();



    private final AttributableImageService attributableImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private final HashMap<String, String> productImageUrl;
    private final ImageService imageService;
    private final Map<String, AttrValue> attrValueMap;
    private final String defaultImageAttributeValue;

    /**
     * Construct entity decorator.
     *
     * @param attributableImageService category image service to get the image.
     * @param categoryService          to get image width and height
     * @param httpServletContextPath   servlet context path
     * @param productEntity            original product to decorate.
     * @param imageService image serice to get the image seo info
     */
    private ProductDecoratorImpl(
            final ImageService imageService,
            final AttributableImageService attributableImageService,
            final CategoryService categoryService,
            final Product productEntity,
            final String httpServletContextPath,
            final boolean withAttributes,
            final String defaultImageAttributeValue) {

        BeanUtils.copyProperties(productEntity, this);
        this.httpServletContextPath = httpServletContextPath;
        this.attributableImageService = attributableImageService;
        this.categoryService = categoryService;
        this.productImageUrl = new HashMap<String, String>();
        this.imageService = imageService;
        this.defaultImageAttributeValue = defaultImageAttributeValue;
        if (withAttributes) {
            this.attrValueMap = getAllAttibutesAsMap();
        } else {
            this.attrValueMap = Collections.emptyMap();

        }

    }




    public static ProductDecoratorImpl createProductDecoratorImpl(
            final ImageService imageService,
            final AttributableImageService attributableImageService,
            final CategoryService categoryService,
            final Product productEntity,
            final String httpServletContextPath,
            final boolean withAttributes,
            final String defaultImageAttributeValue) {

        final String key = httpServletContextPath + productEntity.getProductId() + withAttributes;

        ProductDecoratorImpl rez = productDecoratorCache.get(key);

        if (rez == null) {

            rez = new ProductDecoratorImpl(
                    imageService,
                    attributableImageService,
                    categoryService,
                    productEntity,
                    httpServletContextPath,
                    withAttributes,
                    defaultImageAttributeValue);

            productDecoratorCache.put(key, rez);
            final String seoId = "" + rez.getProductId();
            String seo = DecoratorUtil.encodeId(
                    seoId,
                    rez.getSeo()
            );
            PRODUCT_ENCODE_CACHE.put(seoId, seo);
        }
        return rez;
    }


    /**
     * Get seo uri if possible to given product id.
     * @param idValueToEncode given product id
     * @param productService product service.
     * @return seo uri if seo information found otherwise id
     */    
    public static String getSeoUrlParameterValueProduct(final String idValueToEncode, final ProductService productService) {
        String seo = ProductDecoratorImpl.PRODUCT_ENCODE_CACHE.get(idValueToEncode);
        if (seo == null) {
            final Product product = productService.getById(NumberUtils.toLong(idValueToEncode));
            if (product != null) {
                seo = DecoratorUtil.encodeId(
                        idValueToEncode,
                        product.getSeo()
                );
            }
            if (seo != null) {
                ProductDecoratorImpl.PRODUCT_ENCODE_CACHE.put(idValueToEncode, seo);
            }
        }
        return seo;
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
                category,
                defaultSize
        );
    }


    /**
     * {@inheritDoc}
     */
    public String [] getThumbnailImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                category,
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

}
