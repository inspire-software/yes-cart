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

package org.yes.cart.domain.i18n.impl;

import org.yes.cart.domain.i18n.I18NModel;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-08-13
 * Time: 9:54 PM
 */
public class FailoverStringI18NModel implements I18NModel {

    private final I18NModel model;
    private final String failover;

    public FailoverStringI18NModel(final String localisable, final String failover) {
        this.model = new StringI18NModel(localisable);
        this.failover = failover;
    }

    public String getValue(final String locale) {
        final String val = model.getValue(locale);
        if (val == null || val.length() == 0) {
            return this.failover;
        }
        return val;
    }

    public void putValue(final String locale, final String value) {
        model.putValue(locale, value);
    }

    public Map<String, String> getAllValues() {
        return model.getAllValues();
    }
}
