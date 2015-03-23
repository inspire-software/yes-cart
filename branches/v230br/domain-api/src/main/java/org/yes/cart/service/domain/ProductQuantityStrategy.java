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

package org.yes.cart.service.domain;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductQuantityModel;
import org.yes.cart.domain.entity.ProductSku;

import java.math.BigDecimal;

/**
 * Quantity strategy enforces business rule onto ordered quantity of product
 * by honouring minimum, maximum and step quantity requirements.
 *
 * Primarily this is used to:
 * - limit false orders with too high quantity
 * - limit too small orders that are not feasible for business
 * - provide user with extra information about product
 *
 * User: denispavlov
 * Date: 25/10/2014
 * Time: 20:59
 */
public interface ProductQuantityStrategy {

    /**
     * Quantity model.
     *
     * @param cartQty quantity of given sku in cart
     * @param product required product
     *
     * @return quantity model
     */
    ProductQuantityModel getQuantityModel(BigDecimal cartQty, Product product);

    /**
     * Quantity model.
     *
     * @param cartQty quantity of given sku in cart
     * @param product required product
     *
     * @return quantity model
     */
    ProductQuantityModel getQuantityModel(BigDecimal cartQty, ProductSearchResultDTO product);

    /**
     * Quantity model.
     *
     * @param cartQty quantity of given sku in cart
     * @param productSku required product SKU
     *
     * @return quantity model
     */
    ProductQuantityModel getQuantityModel(BigDecimal cartQty, ProductSku productSku);

}
