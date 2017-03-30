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

package org.yes.cart.domain.entity;

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/11/2015
 * Time: 17:43
 */
public interface StoredAttributes {

    /**
     * @param code attribute code
     * @return string value and display value for that code
     */
    Pair<String, I18NModel> getValue(String code);

    /**
     * @param code attribute code
     * @param value string value for that code
     * @param displayValue display value object {@link I18NModel}
     */
    void putValue(String code, String value, Object displayValue);

    /**
     * @return all values mapped to codes
     */
    Map<String, Pair<String, I18NModel>> getAllValues();

}
