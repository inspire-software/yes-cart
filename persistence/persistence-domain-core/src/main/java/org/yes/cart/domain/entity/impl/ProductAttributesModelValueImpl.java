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
import org.yes.cart.domain.i18n.I18NModel;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:40
 */
public class ProductAttributesModelValueImpl implements ProductAttributesModelValue {

    private final String code;
    private final String val;
    private final I18NModel displayVals;

    public ProductAttributesModelValueImpl(final String code, final String val, final I18NModel displayVals) {
        this.code = code;
        this.val = val;
        this.displayVals = displayVals;
    }

    /** {@inheritDoc} */
    @Override
    public String getCode() {
        return this.code;
    }

    /** {@inheritDoc} */
    @Override
    public String getVal() {
        return this.val;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayVals() {
        return this.displayVals.getAllValues();
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayVal(final String lang) {
        return this.displayVals.getValue(lang);
    }

}
