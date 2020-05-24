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

package org.yes.cart.domain.dto.adapter.impl;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 19/09/2019
 * Time: 11:36
 */
public class I18NModelConverter implements ValueConverter {

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final Map<String, String> values = new HashMap<>();
        if (object instanceof I18NModel) {
            values.putAll(((I18NModel) object).getAllValues());
        }
        return values;
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        if (object instanceof Map) {
            return new StringI18NModel((Map<String, String>) object);
        }
        return null;
    }
}
