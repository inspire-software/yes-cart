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

package org.yes.cart.domain.i18n.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.i18n.I18NModel;

import java.util.Collections;
import java.util.Map;

/**
 * This is I18NModel decorator for simple string values.
 * Primary purpose is provision of default models where no localisation values are available.
 *
 * User: denispavlov
 * Date: 12-08-13
 * Time: 9:54 PM
 */
public class NonI18NModel implements I18NModel {

    private String value;

    public NonI18NModel(final String value) {
        this.value = value;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(final String locale) {
        return this.value;
    }

    /** {@inheritDoc} */
    @Override
    public void putValue(final String locale, final String value) {
        if (DEFAULT.equals(locale)) {
            this.value = value;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getAllValues() {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(DEFAULT, value);
    }

    /** {@inheritDoc} */
    @Override
    public I18NModel copy() {
        return new NonI18NModel(value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NonI18NModel)) return false;

        final NonI18NModel that = (NonI18NModel) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return value;
    }
}
