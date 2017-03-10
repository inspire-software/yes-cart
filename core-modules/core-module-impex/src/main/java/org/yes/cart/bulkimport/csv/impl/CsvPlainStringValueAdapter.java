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

package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 1:28 PM
 */
public class CsvPlainStringValueAdapter implements ValueAdapter {

    /**
     * Simple string value pass through.
     *
     * @param rawValue raw value
     * @param requiredType required data type
     * @param requiredType required data type
     * @param impExColumn impex column
     * @param tuple current tuple
     *
     * @return string value
     */
    public Object fromRaw(final Object rawValue, final String requiredType, final ImpExColumn impExColumn, final ImpExTuple tuple) {
        if (rawValue != null) {
            return String.valueOf(rawValue);
        }
        return null;
    }
}
