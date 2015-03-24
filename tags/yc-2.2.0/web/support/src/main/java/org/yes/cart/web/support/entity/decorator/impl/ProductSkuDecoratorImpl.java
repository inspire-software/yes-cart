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
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.entity.impl.ProductSkuEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.service.AttributableImageService;

import java.util.List;

/**
 * Product sku decorator.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 1/10/11
 * Time: 14:42
 */
public class ProductSkuDecoratorImpl extends ProductSkuEntity implements ProductSkuDecorator {


    private final ProductService productService;
    private final AttributableImageService productSkuImageService;
    private final String httpServletContextPath;
    private final ImageService imageService;
    private final I18NWebSupport i18NWebSupport;

    /**
     * Construct product sku decorator.
     *
     * @param imageService             image service to get the image seo info
     * @param productSkuImageService category image service to get the image.
     * @param productSkuEntity         sku to decorate
     * @param httpServletContextPath   servlet context path
     * @param productService           product service
     * @param i18NWebSupport           i18n
     */
    public ProductSkuDecoratorImpl(final ImageService imageService,
                                   final AttributableImageService productSkuImageService,
                                   final ProductSku productSkuEntity,
                                   final String httpServletContextPath,
                                   final ProductService productService,
                                   final I18NWebSupport i18NWebSupport) {
        this.productService = productService;
        this.i18NWebSupport = i18NWebSupport;
        if (productSkuEntity != null) {
            BeanUtils.copyProperties(productSkuEntity, this);
        }
        this.httpServletContextPath = httpServletContextPath;
        this.productSkuImageService = productSkuImageService;
        this.imageService = imageService;
    }


    /**
     * {@inheritDoc}
     * @param lang
     */
    public List<Pair<String, String>> getImageAttributeFileNames(final String lang) {

        return productSkuImageService.getImageAttributeFileNames(this, lang);

    }

    /**
     * {@inheritDoc}
     */
    public String getImage(final String width, final String height, final String imageAttributeName, final String lang) {
        return productSkuImageService.getImage(
                    this,
                    httpServletContextPath,
                    lang,
                    width,
                    height,
                    imageAttributeName,
                    null);
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final String width, final String height, final String lang) {
        final String imageAttributeName = getImageAttributeFileNames(lang).get(0).getFirst();
        return productSkuImageService.getImage(
                this,
                httpServletContextPath,
                lang,
                width,
                height,
                imageAttributeName,
                null);
    }


    /**
     * {@inheritDoc}
     */
    public SeoImage getSeoImage(final String fileName) {
        return imageService.getSeoImage(Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN + fileName);
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
