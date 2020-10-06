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

package org.yes.cart.bulkimport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.utils.DateUtils;

/**
 */
public class CsvDateValueAdapterImpl implements CsvValueAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final CsvImpExColumn csvImpExColumn, final CsvImpExTuple tuple) {

        final String pattern = csvImpExColumn.getContext();

        if (rawValue != null) {
            if (StringUtils.isNotBlank(pattern)) {

                return "DATE".equalsIgnoreCase(requiredType) ? DateUtils.ldParse((String) rawValue, pattern) : DateUtils.ldtParse((String) rawValue, pattern);

            }

            return "DATE".equalsIgnoreCase(requiredType) ? DateUtils.ldParse((String) rawValue, "yyyy-MM-dd") : DateUtils.ldtParse((String) rawValue, "yyyy-MM-dd HH:mm:ss");
        }

        return null;


    }

}
