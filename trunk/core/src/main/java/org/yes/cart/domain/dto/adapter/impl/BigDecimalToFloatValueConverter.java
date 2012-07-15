/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;

import java.math.BigDecimal;

/**
 * Converter for big decimal to float.
 * <p/>
 * User: dogma
 * Date: Jan 26, 2011
 * Time: 4:02:09 PM
 */
public class BigDecimalToFloatValueConverter implements ValueConverter {

    /**
     * {@inheritDoc}
     */
    public Object convertToDto(final Object object, final com.inspiresoftware.lib.dto.geda.adapter.BeanFactory beanFactory) {
        return new BigDecimal((Float) object);
    }

    /**
     * {@inheritDoc}
     */
    public Object convertToEntity(final Object object,final Object oldEntity, final com.inspiresoftware.lib.dto.geda.adapter.BeanFactory beanFactory) {
        if (object instanceof BigDecimal) {
            return ((BigDecimal) object).floatValue();
        }
        return 0f;
    }
}
