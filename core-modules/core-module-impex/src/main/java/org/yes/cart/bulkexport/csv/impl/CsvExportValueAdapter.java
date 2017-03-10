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

package org.yes.cart.bulkexport.csv.impl;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkcommon.model.impl.AbstractExtensibleValueAdapter;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 1:04 PM
 */
public class CsvExportValueAdapter extends AbstractExtensibleValueAdapter implements ValueAdapter {

    private final GenericConversionService extendedConversionService;

    public CsvExportValueAdapter(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
    }

    public Object fromRaw(final Object rawValue, final String requiredType, final ImpExColumn impExColumn, final ImpExTuple tuple) {

        if (requiredType == null) {
            return rawValue;
        }

        final ValueAdapter specific = getTypeSpecific(impExColumn.getDataType());
        if (specific != null) {
            return specific.fromRaw(rawValue, requiredType, impExColumn, tuple);
        }

        return this.extendedConversionService.convert(rawValue,
                TypeDescriptor.valueOf(rawValue.getClass()),
                TypeDescriptor.valueOf(String.class));
    }
}
