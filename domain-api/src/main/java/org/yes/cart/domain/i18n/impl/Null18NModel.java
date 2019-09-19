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

package org.yes.cart.domain.i18n.impl;

import org.yes.cart.domain.i18n.I18NModel;

import java.util.Collections;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 17/09/2019
 * Time: 11:51
 */
public class Null18NModel implements I18NModel {

    @Override
    public String getValue(final String locale) {
        return null;
    }

    @Override
    public void putValue(final String locale, final String value) {
        // Nothing
    }

    @Override
    public Map<String, String> getAllValues() {
        return Collections.emptyMap();
    }

    @Override
    public I18NModel copy() {
        return this;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Null18NModel;
    }
}
