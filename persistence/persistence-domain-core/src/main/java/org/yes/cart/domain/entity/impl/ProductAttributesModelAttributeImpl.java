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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.ProductAttributesModelAttribute;
import org.yes.cart.domain.entity.ProductAttributesModelValue;
import org.yes.cart.domain.i18n.I18NModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:44
 */
public class ProductAttributesModelAttributeImpl implements ProductAttributesModelAttribute {

    private final String code;
    private final boolean multivalue;
    private final I18NModel displayNames;
    private final List<ProductAttributesModelValue> values = new ArrayList<>();

    public ProductAttributesModelAttributeImpl(final String code, final boolean multivalue, final I18NModel displayNames) {
        this.code = code;
        this.multivalue = multivalue;
        this.displayNames = displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public String getCode() {
        return this.code;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMultivalue() {
        return multivalue;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayNames() {
        return this.displayNames.getAllValues();
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName(final String lang) {
        return this.displayNames.getValue(lang);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductAttributesModelValue> getValues() {
        return Collections.unmodifiableList(this.values);
    }

    /**
     * Append items to internal list.
     *
     * @param value value to append
     */
    public void addValue(final ProductAttributesModelValue value) {
        if (multivalue || this.values.isEmpty()) {
            this.values.add(value);
        } else {
            this.values.set(0, value);
        }
    }
}
