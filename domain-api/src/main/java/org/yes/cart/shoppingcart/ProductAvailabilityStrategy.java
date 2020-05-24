/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.shoppingcart;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.entity.ProductSku;

/**
 * Availability strategy allows to determine if this product is eligible for
 * purchase. It is mostly used to support "AddToCart", "Preorder" buttons
 * and additional inventory related messaging.
 *
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:07 PM
 */
public interface ProductAvailabilityStrategy {


    /**
     * Get availability model for given supplier for all SKU available for product.
     *
     * @param shopId   shop PK
     * @param product  product
     * @param supplier supplier
     *
     * @return availability of this product
     */
    ProductAvailabilityModel getAvailabilityModel(long shopId,
                                                  Product product,
                                                  String supplier);

    /**
     * Get availability model for given supplier for given SKU.
     *
     * @param shopId shop PK
     * @param sku product sku
     * @param supplier supplier
     *
     * @return availability of this sku
     */
    ProductAvailabilityModel getAvailabilityModel(long shopId,
                                                  ProductSku sku,
                                                  String supplier);

    /**
     * Get availability model for given supplier for all SKU available for product.
     *
     * @param shopId   shop PK
     * @param product  product
     *
     * @return availability of this product
     */
    ProductAvailabilityModel getAvailabilityModel(long shopId,
                                                  ProductSearchResultDTO product);

    /**
     * Get availability model for given supplier for all SKU available for product.
     *
     * @param shopId   shop PK
     * @param sku      product
     *
     * @return availability of this product
     */
    ProductAvailabilityModel getAvailabilityModel(long shopId,
                                                  ProductSkuSearchResultDTO sku);

    /**
     * Get availability model for given supplier for given SKU.
     *
     * @param shopId shop PK
     * @param skuCode SKU code (not product code)
     * @param supplier supplier
     *
     * @return availability of this sku
     */
    ProductAvailabilityModel getAvailabilityModel(long shopId,
                                                  String skuCode,
                                                  String supplier);


}
