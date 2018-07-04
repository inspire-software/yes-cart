/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo.converter;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 04/07/2018
 * Time: 07:49
 */
public class StringToMappingConverter implements ValueConverter {

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final StringBuilder mapping = new StringBuilder();
        if (object instanceof Map) {
            for (final Map.Entry entry : (Set<Map.Entry>) ((Map) object).entrySet()) {
                mapping.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
            }
        }
        return mapping.toString();
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        if (object instanceof String) {
            final Properties props = new Properties();
            try {
                props.load(new StringReader((String) object));
            } catch (IOException e) {
                throw new IllegalArgumentException("Malformed mapping value: " + object);
            }
            return props;
        }
        return Collections.emptyMap();
    }
}

