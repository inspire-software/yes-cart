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

package org.yes.cart.domain.dto.adapter.impl;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 21/09/2019
 * Time: 10:48
 */
public class CustomValuesMapConverter implements ValueConverter {

    private I18NModelConverter i18NModelConverter = new I18NModelConverter();

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {

        final Map<String, Pair<String, Map<String, String>>> out = new LinkedHashMap<>();
        final Map<String, Pair<String, I18NModel>> allValues = (Map) object;
        if (allValues != null) {
            for (final Map.Entry<String, Pair<String, I18NModel>> value : allValues.entrySet()) {
                out.put(
                        value.getKey(),
                        new Pair<String, Map<String, String>>(
                                value.getValue().getFirst(),
                                (Map<String, String>) i18NModelConverter.convertToDto(
                                        value.getValue().getSecond(),
                                        beanFactory
                                )
                        )
                );
            }
        }
        return out;

    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {

        final Map<String, Pair<String, I18NModel>> out = new LinkedHashMap<>();
        final Map<String, Pair<String, Map<String, String>>> allValues = (Map) object;
        if (allValues != null) {
            for (final Map.Entry<String, Pair<String, Map<String, String>>> value : allValues.entrySet()) {
                out.put(
                        value.getKey(),
                        new Pair<String, I18NModel>(
                                value.getValue().getFirst(),
                                (I18NModel) i18NModelConverter.convertToEntity(
                                        value.getValue().getSecond(),
                                        null,
                                        beanFactory
                                )
                        )
                );
            }
        }
        return out;

    }
}
