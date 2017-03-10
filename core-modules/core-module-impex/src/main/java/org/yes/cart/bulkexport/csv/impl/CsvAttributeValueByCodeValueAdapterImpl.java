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

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.bulkcommon.model.ExtensibleValueAdapter;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.domain.entity.AttrValue;

import java.util.Collection;

/**
 * User: denispavlov
 * Date: 30/11/2015
 * Time: 22:16
 */
public class CsvAttributeValueByCodeValueAdapterImpl implements ValueAdapter {


    /**
     * {@inheritDoc}
     */
    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final ImpExColumn impExColumn, final ImpExTuple tuple) {
        final String code = impExColumn.getContext();
        final Collection<AttrValue> values = (Collection<AttrValue>) rawValue;
        if (CollectionUtils.isNotEmpty(values)) {
            for (final AttrValue av : values) {
                if (code.equals(av.getAttribute().getCode())) {
                    return av.getVal();
                }
            }
        }
        return null;
    }

    /**
     * Spring IoC.
     *
     * @param extensibleValueAdapter extend
     */
    public void setExtensibleValueAdapter(ExtensibleValueAdapter extensibleValueAdapter) {
        extensibleValueAdapter.extend(this, "AVC");
    }

}
