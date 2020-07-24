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

import org.yes.cart.domain.i18n.I18NModel;

import java.util.Collections;
import java.util.Map;

/**
 * Date: 24/07/2020
 * Time: 17:28
 */
public class Immutable18NModel implements I18NModel {

    private final I18NModel i18NModel;

    public Immutable18NModel(final I18NModel i18NModel) {
        this.i18NModel = i18NModel;
    }

    @Override
    public String getValue(final String locale) {
        return this.i18NModel.getValue(locale);
    }

    @Override
    public void putValue(final String locale, final String value) {

    }

    @Override
    public Map<String, String> getAllValues() {
        return Collections.unmodifiableMap(i18NModel.getAllValues());
    }

    @Override
    public I18NModel copy() {
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Immutable18NModel)) return false;

        final Immutable18NModel that = (Immutable18NModel) o;

        return i18NModel != null ? i18NModel.equals(that.i18NModel) : that.i18NModel == null;
    }

    @Override
    public int hashCode() {
        return i18NModel != null ? i18NModel.hashCode() : 0;
    }

    @Override
    public String toString() {
        return i18NModel.toString();
    }
}
