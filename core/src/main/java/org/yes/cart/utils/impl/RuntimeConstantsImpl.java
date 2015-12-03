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

package org.yes.cart.utils.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.utils.RuntimeConstants;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 02/12/2015
 * Time: 21:42
 */
public class RuntimeConstantsImpl implements RuntimeConstants {

    private final Map<String, String> constants;

    public RuntimeConstantsImpl(final Map<String, String> constants) {
        this.constants = constants;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasValue(final String key) {
        return StringUtils.isNotBlank(constants.get(key));
    }

    /** {@inheritDoc} */
    @Override
    public String getConstant(final String key) {
        return constants.get(key);
    }
}
