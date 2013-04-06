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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.web.support.entity.decorator.ProductAvailabilityModel;

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
     * @param product product
     * @return availability of this product
     */
    ProductAvailabilityModel getAvailabilityModel(final Product product);

    /**
     * @param sku product sku
     * @return availability of this sku
     */
    ProductAvailabilityModel getAvailabilityModel(final ProductSku sku);


}
