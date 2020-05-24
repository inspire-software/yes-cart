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

package org.yes.cart.bulkexport.xml.impl;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.yes.cart.bulkcommon.model.impl.AbstractExtensibleValueAdapter;
import org.yes.cart.bulkcommon.xml.XmlImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 1:04 PM
 */
public class XmlExportValueAdapter extends AbstractExtensibleValueAdapter<XmlValueAdapter> implements XmlValueAdapter {

    private final GenericConversionService extendedConversionService;

    public XmlExportValueAdapter(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
    }

    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final XmlImpExTuple tuple) {

        if (requiredType == null) {
            return rawValue;
        }

        if (rawValue == null) {
            return null;
        }

        final XmlValueAdapter specific = getTypeSpecific(determineType(rawValue));
        if (specific != null) {
            return specific.fromRaw(rawValue, requiredType, tuple);
        }

        return this.extendedConversionService.convert(rawValue,
                TypeDescriptor.valueOf(rawValue.getClass()),
                TypeDescriptor.valueOf(String.class));
    }

    private String determineType(final Object rawValue) {

        return rawValue.getClass().getCanonicalName();

    }

}
