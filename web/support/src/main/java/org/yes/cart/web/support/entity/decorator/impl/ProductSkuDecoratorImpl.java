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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.entity.impl.ProductSkuEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.service.AttributableImageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product sku decorator.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 1/10/11
 * Time: 14:42
 */
public class ProductSkuDecoratorImpl extends ProductSkuEntity implements ProductSkuDecorator {

    private final static List<String> attrNames = new ArrayList<String>() {{
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "1");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "2");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "3");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "4");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "5");
    }};



    private final ProductService productService;
    private final AttributableImageService attributableImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private final Map<Integer, String> productImageUrl = new HashMap<Integer, String>();
    private final ImageService imageService;
    private final I18NWebSupport i18NWebSupport;

    /**
     * Construct product sku decorator.
     *
     * @param imageService             image service to get the image seo info
     * @param attributableImageService category image service to get the image.
     * @param categoryService          to get image width and height
     * @param productSkuEntity         sku to decorate
     * @param httpServletContextPath   servlet context path
     * @param productService           product service
     * @param i18NWebSupport           i18n
     */
    public ProductSkuDecoratorImpl(final ImageService imageService,
                                   final AttributableImageService attributableImageService,
                                   final CategoryService categoryService,
                                   final ProductSku productSkuEntity,
                                   final String httpServletContextPath,
                                   final ProductService productService,
                                   final I18NWebSupport i18NWebSupport) {
        this.productService = productService;
        this.i18NWebSupport = i18NWebSupport;

        BeanUtils.copyProperties(productSkuEntity, this);
        this.httpServletContextPath = httpServletContextPath;
        this.attributableImageService = attributableImageService;
        this.categoryService = categoryService;
        this.imageService = imageService;
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
    public String getImage(final String width, final String height, final String imageAttributeName) {
        final Integer hash = getImageWithSizeHash(imageAttributeName, width, height);
        String img = productImageUrl.get(hash);
        if (img == null) {
            img = attributableImageService.getImage(
                    this,
                    httpServletContextPath,
                    width,
                    height,
                    imageAttributeName,
                    null);
            productImageUrl.put(hash, img);
        }
        return img;
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final String width, final String height) {
        final String imageAttributeName = getDefaultImageAttributeName();
        final Integer hash = getImageWithSizeHash(imageAttributeName, width, height);
        String img = productImageUrl.get(hash);
        if (img == null) {
            img = attributableImageService.getImage(
                    this,
                    httpServletContextPath,
                    width,
                    height,
                    imageAttributeName,
                    null);
            productImageUrl.put(hash, img);
        }
        return img;
    }


    /**
     * {@inheritDoc}
     */
    public String [] getDefaultImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                null, category,
                ProductDecoratorImpl.defaultSize
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
        final AttrValueProductSku val =  getAttributeByCode(attribute);
        return val != null ? val.getVal() : "";
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValue(final String locale, final String attribute) {
        final AttrValueProductSku lval =  getAttributeByCode(attribute);
        return lval != null ? i18NWebSupport.getFailoverModel(lval.getDisplayVal(), lval.getVal()).getValue(locale) : "";
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription(final String locale) {

        final String desc = getAttributeValue(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX + locale);

        if (StringUtils.isBlank(desc)) {

            if (StringUtils.isBlank(getDescription())) {
                //failover to product description
                final Pair<String, String> prodDesc = productService.getProductAttribute(
                        locale, getProduct().getProductId(), 0L, AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX + locale);
                if (prodDesc == null || StringUtils.isBlank(prodDesc.getSecond())) {
                    return getProduct().getDescription();
                }
                return prodDesc.getSecond();

            } else {

                return getDescription();

            }

        }
        return desc;
    }



}
