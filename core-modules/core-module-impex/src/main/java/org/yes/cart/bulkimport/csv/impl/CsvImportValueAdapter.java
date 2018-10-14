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

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.ValueAdapter;
import org.yes.cart.bulkcommon.model.impl.AbstractExtensibleValueAdapter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 1:04 PM
 */
public class CsvImportValueAdapter extends AbstractExtensibleValueAdapter implements ValueAdapter {

    private final GenericConversionService extendedConversionService;

    private static final Map<String, Class> MAPPING = new HashMap<String, Class>() {{
        put(CsvImpExColumn.STRING,    String.class);
        put(CsvImpExColumn.BOOLEAN,   Boolean.class);
        put(CsvImpExColumn.INT,       Integer.class);
        put(CsvImpExColumn.LONG,      Long.class);
        put(CsvImpExColumn.DECIMAL,   BigDecimal.class);
        put(CsvImpExColumn.DATE,      LocalDate.class);
        put(CsvImpExColumn.DATETIME,  LocalDateTime.class);
        put(CsvImpExColumn.ZONEDTIME, ZonedDateTime.class);
        put(CsvImpExColumn.INSTANT,   Instant.class);
    }};

    public CsvImportValueAdapter(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
    }

    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final CsvImpExColumn csvImpExColumn, final CsvImpExTuple tuple) {
        if (requiredType == null) {
            return rawValue;
        }

        final ValueAdapter specific = getTypeSpecific(requiredType);
        if (specific != null) {
            return specific.fromRaw(rawValue, requiredType, csvImpExColumn, tuple);
        }
        if (!MAPPING.containsKey(requiredType)) {
            return rawValue;
        }
        return this.extendedConversionService.convert(rawValue,
                TypeDescriptor.valueOf(rawValue.getClass()),
                TypeDescriptor.valueOf(MAPPING.get(requiredType)));
    }
}
