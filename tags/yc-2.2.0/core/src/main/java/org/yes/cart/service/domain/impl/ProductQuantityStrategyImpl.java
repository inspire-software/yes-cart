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

package org.yes.cart.service.domain.impl;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductQuantityModel;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.impl.ProductQuantityModelImpl;
import org.yes.cart.service.domain.ProductQuantityStrategy;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 25/10/2014
 * Time: 21:05
 */
public class ProductQuantityStrategyImpl implements ProductQuantityStrategy {

    /** {@inheritDoc} */
    public ProductQuantityModel getQuantityModel(final BigDecimal cartQty, final Product product) {
        return new ProductQuantityModelImpl(
                product.getMinOrderQuantity(),
                product.getMaxOrderQuantity(),
                product.getStepOrderQuantity(),
                cartQty);
    }

    /** {@inheritDoc} */
    public ProductQuantityModel getQuantityModel(final BigDecimal cartQty, final ProductSearchResultDTO product) {
        return new ProductQuantityModelImpl(
                product.getMinOrderQuantity(),
                product.getMaxOrderQuantity(),
                product.getStepOrderQuantity(),
                cartQty);
    }

    /** {@inheritDoc} */
    public ProductQuantityModel getQuantityModel(final BigDecimal cartQty, final ProductSku productSku) {
        return getQuantityModel(cartQty, productSku.getProduct());
    }
}
