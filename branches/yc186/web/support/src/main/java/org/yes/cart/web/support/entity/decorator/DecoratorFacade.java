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

package org.yes.cart.web.support.entity.decorator;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.web.support.i18n.I18NWebSupport;

/**
 * Simple facae for creating entity decorators for Web.
 * User: denispavlov
 * Date: 12-08-20
 * Time: 5:23 PM
 */
public interface DecoratorFacade {

    /**
     * @param category category
     * @param servletContextPath path
     * @param i18NWebSupport i18n support
     * @return decorated category
     */
    CategoryDecorator decorate(Category category, String servletContextPath, I18NWebSupport i18NWebSupport);

    /**
     *
     * @param product product
     * @param servletContextPath path
     * @param i18NWebSupport i18n support
     * @param withAttributes true if decorator should load product attributes
     * @return decorated product
     */
    ProductDecorator decorate(Product product, String servletContextPath, I18NWebSupport i18NWebSupport, final boolean withAttributes);

    /**
     * @param sku sku
     * @param servletContextPath path
     * @param i18NWebSupport i18n support
     * @return decorated sku
     */
    ProductSkuDecorator decorate(ProductSku sku, String servletContextPath, I18NWebSupport i18NWebSupport);

}
