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

package org.yes.cart.bulkcommon.model.impl;

import org.yes.cart.bulkcommon.model.ExtensibleValueAdapter;
import org.yes.cart.bulkcommon.model.ValueAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 30/11/2015
 * Time: 21:43
 */
public abstract class AbstractExtensibleValueAdapter implements ExtensibleValueAdapter {

    private final Map<String, ValueAdapter> extensions = new HashMap<String, ValueAdapter>();


    /**
     * Get data type specific adapter.
     *
     * @param dataType data type
     *
     * @return value adapter or null
     */
    protected final ValueAdapter getTypeSpecific(final String dataType) {
        return extensions.get(dataType);
    }

    /** {@inheritDoc} */
    @Override
    public void extend(final ValueAdapter extension, final String customDataType) {
        extensions.put(customDataType, extension);
    }
}
