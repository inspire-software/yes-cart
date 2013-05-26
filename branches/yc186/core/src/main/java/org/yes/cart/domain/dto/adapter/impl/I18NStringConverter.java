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

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.util.Map;

/**
 * Converter to convert I18n strings - string that have several language text within
 * them.
 *
 * User: denispavlov
 * Date: 12-08-10
 * Time: 6:34 PM
 */
public class I18NStringConverter implements ValueConverter {

    /** {@inheritDoc} */
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final I18NModel model = new StringI18NModel((String) object);
        return model.getAllValues();
    }

    /** {@inheritDoc} */
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        final I18NModel model = new StringI18NModel((Map<String, String>) object);
        return model.toString();
    }
}
