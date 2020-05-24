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

package org.yes.cart.service.domain.cache.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.TreeSet;

/**
 * User: denispavlov
 * Date: 14/12/2019
 * Time: 15:30
 */
public class CollectionToStringKeyGenerator implements KeyGenerator {

    /** {@inheritDoc} */
    @Override
    public Object generate(final Object target, final Method method, final Object... params) {
        if (params == null || params.length != 1 || !(params[0] instanceof Collection)) {
            throw new UnsupportedOperationException("This key generator only supports single collection param method");
        }
        final Collection collection = (Collection) params[0];
        final TreeSet sortedValues = new TreeSet();
        collection.forEach(obj -> {
            sortedValues.add(obj.toString());
        });
        final String key = StringUtils.join(sortedValues, ';');
        return key != null ? key : "-";
    }
}
