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

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.service.CategoryImageRetrieveStrategy;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 16:22:01
 */
public class CategoryImageRetrieveStrategyRandomProductImpl implements CategoryImageRetrieveStrategy {


    private final ProductService productService;
    private final String attributeCode;

    /**
     * Construct category image retrieve strategy
     *
     * @param productService product service to get random product in category.
     * @param attributeCode attribute code.
     */
    public CategoryImageRetrieveStrategyRandomProductImpl(final ProductService productService,
                                                          final String attributeCode) {
        this.productService = productService;
        this.attributeCode = attributeCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getImageName(final Attributable category, final String attributeCodeHint, final String locale) {
        Product product = productService.getRandomProductByCategory((Category)category);
        if (product != null) {
            AttrValue attrValue = product.getAttributeByCode(attributeCode + "_" + locale);
            if (attrValue != null) {
                return attrValue.getVal();
            } else {
                attrValue = product.getAttributeByCode(attributeCode);
                if (attrValue != null) {
                    return attrValue.getVal();
                }
            }
        }
        return Constants.NO_IMAGE;
    }


    /**
     * {@inheritDoc}
     */
    public String getImageRepositoryUrlPattern() {
        return Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    public String getImageAttributePrefix() {
        return AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX;
    }
}
