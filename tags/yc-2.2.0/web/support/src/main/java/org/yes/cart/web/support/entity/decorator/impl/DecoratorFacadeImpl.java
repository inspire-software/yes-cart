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


import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.*;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.service.AttributableImageService;

/**
 * User: denispavlov
 * Date: 12-08-20
 * Time: 5:27 PM
 */
public class DecoratorFacadeImpl implements DecoratorFacade {


    private final ImageService imageService;
    private final AttributableImageService categoryImageService;
    private final AttributableImageService productImageService;
    private final AttributableImageService skuImageService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final I18NWebSupport i18NWebSupport;

    public DecoratorFacadeImpl(final ImageService imageService,
                               final AttributableImageService categoryImageService,
                               final AttributableImageService productImageService,
                               final AttributableImageService skuImageService,
                               final CategoryService categoryService,
                               final ProductService productService,
                               final I18NWebSupport i18NWebSupport) {
        this.imageService = imageService;
        this.categoryImageService = categoryImageService;
        this.productImageService = productImageService;
        this.skuImageService = skuImageService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.i18NWebSupport = i18NWebSupport;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ObjectDecorator> T decorate(final Object object,
                                                  final String servletContextPath,
                                                  final boolean withAttributes) {

        if (object instanceof Product) {
            return (T) decorate((Product) object, servletContextPath, withAttributes);
        } else if (object instanceof ProductSku) {
            return (T) decorate((ProductSku) object, servletContextPath);
        } else if (object instanceof Category) {
            return (T) decorate((Category) object, servletContextPath);
        }
        throw new IllegalArgumentException("Object is not decoratable: " + object.getClass());
    }

    private CategoryDecorator decorate(final Category category,
                                       final String servletContextPath) {

        return new CategoryDecoratorImpl(
                imageService,
                categoryImageService,
                categoryService,
                category,
                servletContextPath,
                i18NWebSupport);

    }


    /**
     * {@inheritDoc}
     */
    private ProductDecorator decorate(final Product product,
                                      final String servletContextPath,
                                      final boolean withAttributes) {

        return new ProductDecoratorImpl(
                imageService,
                productImageService,
                productService,
                i18NWebSupport,
                product,
                servletContextPath,
                withAttributes,
                productService.getDefaultImage(product.getProductId()));
    }

    private ProductSkuDecorator decorate(final ProductSku sku,
                                         final String servletContextPath) {
        return new ProductSkuDecoratorImpl(
                imageService,
                skuImageService,
                sku,
                servletContextPath,
                productService,
                i18NWebSupport);
    }

}
