/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.promotion.impl;

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.promotion.PromotionConditionSupport;

/**
 * User: denispavlov
 * Date: 06/04/2018
 * Time: 09:54
 */
public class PromotionConditionSupportCachedImpl implements PromotionConditionSupport {

    private PromotionConditionSupport conditionSupport;

    public PromotionConditionSupportCachedImpl(final PromotionConditionSupport conditionSupport) {
        this.conditionSupport = conditionSupport;
    }

    @Override
    public Product getProductBySkuCode(final String sku) {
        return conditionSupport.getProductBySkuCode(sku);
    }

    @Override
    public ProductSku getProductSkuByCode(final String sku) {
        return conditionSupport.getProductSkuByCode(sku);
    }

    @Override
    public Brand getProductBrand(final String sku) {
        return conditionSupport.getProductBrand(sku);
    }

    @Override
    @Cacheable(value = "promotionConditionSupport-hasProductAttribute")
    public boolean hasProductAttribute(final String sku, final String attribute) {
        return conditionSupport.hasProductAttribute(sku, attribute);
    }

    @Override
    @Cacheable(value = "promotionConditionSupport-productAttribute")
    public String getProductAttribute(final String sku, final String attribute) {
        return conditionSupport.getProductAttribute(sku, attribute);
    }

    @Override
    @Cacheable(value = "promotionConditionSupport-productOfBrand")
    public boolean isProductOfBrand(final String sku, final String... brandNames) {
        return conditionSupport.isProductOfBrand(sku, brandNames);
    }

    @Override
    @Cacheable(value = "promotionConditionSupport-productInCategory")
    public boolean isProductInCategory(final String sku, final Long shopId, final String... categoryGUIDs) {
        return conditionSupport.isProductInCategory(sku, shopId, categoryGUIDs);
    }
    
}
