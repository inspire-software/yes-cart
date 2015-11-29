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

package org.yes.cart.bulkcommon.service.support.impl;

import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategyValueProvider;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 12:22
 */
public class ColumnValueLookUpQueryParameterStrategyValueProviderImpl implements LookUpQueryParameterStrategyValueProvider {

    /**
     * {@inheritDoc}
     */
    public Object getPlaceholderValue(final String placeholder,
                                      final ImpExDescriptor descriptor,
                                      final Object masterObject,
                                      final ImpExTuple tuple,
                                      final ValueAdapter adapter,
                                      final String queryTemplate) {

        if (tuple != null) {
            final String realColumnName = placeholder.substring(1, placeholder.length() - 1);
            final ImpExColumn column = descriptor.getColumn(realColumnName);

            if (column != null) {
                return tuple.getColumnValue(column, adapter);
            }
        }
        return null;
    }
}
