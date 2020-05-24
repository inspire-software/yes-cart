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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.ProductAttributesModelValue;
import org.yes.cart.domain.entity.ProductCompareModelValue;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 11:26
 */
public class ProductCompareModelValueImpl extends ProductAttributesModelValueImpl implements ProductCompareModelValue {

    private final long productId;
    private final String productCode;
    private final long skuId;
    private final String skuCode;

    public ProductCompareModelValueImpl(final long productId, final String productCode, final long skuId, final String skuCode, final String code, final String val, final I18NModel displayVals) {
        super(code, val, displayVals);
        this.productId = productId;
        this.productCode = productCode;
        this.skuId = skuId;
        this.skuCode = skuCode;
    }

    public ProductCompareModelValueImpl(final long productId, final String productCode, final long skuId, final String skuCode, final ProductAttributesModelValue val) {
        super(val.getCode(), val.getVal(), new FailoverStringI18NModel(val.getDisplayVals(), val.getVal()));
        this.productId = productId;
        this.productCode = productCode;
        this.skuId = skuId;
        this.skuCode = skuCode;
    }

    /** {@inheritDoc} */
    @Override
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc} */
    @Override
    public String getProductCode() {
        return productCode;
    }

    /** {@inheritDoc} */
    @Override
    public long getSkuId() {
        return skuId;
    }

    /** {@inheritDoc} */
    @Override
    public String getSkuCode() {
        return skuCode;
    }

}
