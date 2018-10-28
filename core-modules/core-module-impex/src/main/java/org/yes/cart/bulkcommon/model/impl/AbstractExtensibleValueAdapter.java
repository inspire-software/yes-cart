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

import java.util.Collections;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 30/11/2015
 * Time: 21:43
 */
public abstract class AbstractExtensibleValueAdapter<T> {

    private Map<String, T> extensions = Collections.emptyMap();

    /**
     * Get data type specific adapter.
     *
     * @param dataType data type
     *
     * @return value adapter or null
     */
    protected final T getTypeSpecific(final String dataType) {
        return extensions.get(dataType);
    }

    /**
     * Spring IoC.
     *
     * @param extensions extensions
     */
    public void setExtensions(final Map<String, T> extensions) {
        this.extensions = extensions;
    }
}
