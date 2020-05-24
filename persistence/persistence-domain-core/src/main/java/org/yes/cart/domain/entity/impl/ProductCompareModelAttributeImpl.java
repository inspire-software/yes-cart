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

import org.yes.cart.domain.entity.ProductCompareModelAttribute;
import org.yes.cart.domain.entity.ProductCompareModelValue;
import org.yes.cart.domain.i18n.I18NModel;

import java.util.*;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:44
 */
public class ProductCompareModelAttributeImpl implements ProductCompareModelAttribute {

    private final String code;
    private final boolean multivalue;
    private final I18NModel displayNames;
    private final Map<String, List<ProductCompareModelValue>> values = new HashMap<>();

    public ProductCompareModelAttributeImpl(final String code, final boolean multivalue, final I18NModel displayNames) {
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
    public Map<String, List<ProductCompareModelValue>> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductCompareModelValue> getValues(final String code) {
        if (this.values.containsKey(code)) {
            return Collections.unmodifiableList(this.values.get(code));
        }
        return Collections.emptyList();
    }

    /**
     * Append items to internal list.
     *
     * @param value value to append
     */
    public void addValue(final ProductCompareModelValue value) {

        final String key = value.getSkuId() > 0L ? "s_" + value.getSkuId() : "p_" + value.getProductId();

        final List<ProductCompareModelValue> vals = this.values.computeIfAbsent(key, k -> new ArrayList<>());

        if (multivalue || vals.isEmpty()) {
            vals.add(value);
        } else {
            vals.set(0, value);
        }
    }
}
