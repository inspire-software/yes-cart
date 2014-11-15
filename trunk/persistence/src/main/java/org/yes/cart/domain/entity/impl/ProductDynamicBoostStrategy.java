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

package org.yes.cart.domain.entity.impl;

import org.hibernate.search.engine.BoostStrategy;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductCategory;

/**
 * User: denispavlov
 * Date: 13/11/2014
 * Time: 17:13
 */
public class ProductDynamicBoostStrategy implements BoostStrategy {

    /**
     * Dynamic boosting of products.
     *
     * @param value product entity
     *
     * @return boost for full text index
     */
    @Override
    public float defineBoost(final Object value) {
        final Product product = (Product) value;
        return 1f + featuredBoost(product) + categoryBoost(product);
    }

    private float featuredBoost(final Product product) {
        // Make featured products 20% more important
        return (product.getFeatured() != null && product.getFeatured()) ? 0.2f : 0f;
    }

    private float categoryBoost(final Product product) {
        float boost = 0f;
        for (final ProductCategory pcat : product.getProductCategory()) {
            // 500 is base rank, anything lower reduces boost, higher increased boost
            // 1pt == 0.001f boost
            boost += ((float) (pcat.getRank() - 500)) / 1000f;
        }
        return boost;
    }



}
