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

package org.yes.cart.bulkexport.csv.impl;

import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.Map;

/**
 * User: IS
 * Date: 02/12/2022
 * Time: 18:39
 */
public class CsvAllValuesByCodeValueAdapterImpl implements CsvValueAdapter {


    /**
     * {@inheritDoc}
     */
    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final CsvImpExColumn csvImpExColumn, final CsvImpExTuple tuple) {
        final String code = csvImpExColumn.getContext();
        final Map<String, Pair<String, I18NModel>> values = (Map<String, Pair<String, I18NModel>>) rawValue;
        final Pair<String, I18NModel> av = values.get(code);
        if (av != null) {
            return av.getFirst();
        }
        return null;
    }

}
