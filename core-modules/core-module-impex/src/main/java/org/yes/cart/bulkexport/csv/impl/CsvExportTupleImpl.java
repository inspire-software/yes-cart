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

import org.apache.commons.beanutils.PropertyUtils;
import org.yes.cart.bulkcommon.model.FieldTypeEnum;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkexport.csv.CsvExportTuple;
import org.yes.cart.bulkexport.model.ExportColumn;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.util.ShopCodeContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 12:07
 */
public class CsvExportTupleImpl implements CsvExportTuple {

    private final Object data;

    public CsvExportTupleImpl(final Object data) {
        this.data = data;
    }

    /** {@inheritDoc} */
    public String getSourceId() {
        return data == null ? "NULL" :
                data.getClass().getSimpleName() + ":" + (data instanceof Identifiable ? ((Identifiable) data).getId() : "N/A");
    }

    /** {@inheritDoc} */
    public Object getData() {
        return data;
    }

    /** {@inheritDoc} */
    public Object getColumnValue(final ExportColumn column, final ValueAdapter adapter) {
        final Object rawValue = getObjectValue(column);
        return column.getValue(rawValue, adapter);
    }

    private Object getObjectValue(final ExportColumn column) {
        final String property = column.getName();
        Object rawValue = null;
        try {
            rawValue = PropertyUtils.getNestedProperty(getData(), property);
        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error("Unable to read property: " + property, exp);
        }
        return rawValue;
    }

    /** {@inheritDoc} */
    public <I extends ImpExTuple<String, Object, ExportDescriptor, ExportColumn>> List<I> getSubTuples(final ExportDescriptor importDescriptor, final ExportColumn column, final ValueAdapter adapter) {
        if (column.getFieldType() == FieldTypeEnum.SLAVE_TUPLE_FIELD
                || column.getFieldType() == FieldTypeEnum.SLAVE_INLINE_FIELD) {
            final Object rawValue = getObjectValue(column);

            if (rawValue instanceof Collection) {
                final List<CsvExportTuple> tuples = new ArrayList<CsvExportTuple>();
                for (final Object rawValueItem : (Collection) rawValue) {
                    tuples.add(new CsvExportTupleImpl(rawValueItem));
                }
                return (List) Collections.unmodifiableList(tuples);
            }

            return (List) Collections.singletonList(new CsvExportTupleImpl(rawValue));
        }
        return Collections.emptyList();
    }
}
