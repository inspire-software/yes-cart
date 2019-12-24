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
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoAttrValue;
import org.yes.cart.domain.vo.VoUtils;
import org.yes.cart.service.domain.AttributeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/02/2017
 * Time: 19:40
 */
public class CustomValuesListConverter implements ValueConverter {

    private final AttributeService attributeService;

    public CustomValuesListConverter(final AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final List<VoAttrValue> list = new ArrayList<>();
        if (object instanceof Map) {

            final Map<String, I18NModel> attrNames = this.attributeService.getAllAttributeNames();

            final Map<String, Pair<String, Map<String, String>>> values = (Map<String, Pair<String, Map<String, String>>>) object;
            for (final Map.Entry<String, Pair<String, Map<String, String>>> entry : values.entrySet()) {
                final List<MutablePair<String, String>> names = new ArrayList<>();
                boolean secure = false;
                if (entry.getValue().getSecond() != null) {
                    for (final Map.Entry<String, String> nameEntry : entry.getValue().getSecond().entrySet()) {
                        names.add(MutablePair.of(nameEntry.getKey(), nameEntry.getValue()));
                    }
                    secure = "SUPPLIER".equals(entry.getValue().getSecond().get(I18NModel.DEFAULT));
                }

                final VoAttrValue av = new VoAttrValue();
                av.getAttribute().setCode(entry.getKey());
                final I18NModel attrName = attrNames.get(entry.getKey());
                if (attrName != null) {
                    final String defaultName = attrName.getValue(I18NModel.DEFAULT);
                    av.getAttribute().setName(StringUtils.isNotBlank(defaultName) ? defaultName : entry.getKey());
                    av.getAttribute().setDisplayNames(VoUtils.adaptMapToPairs(attrName.getAllValues()));
                } else {
                    av.getAttribute().setName(entry.getKey());
                }
                av.getAttribute().setSecure(secure);
                av.setVal(entry.getValue().getFirst());
                av.setDisplayVals(names);

                list.add(av);
            }
        }
        return list;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        if (object instanceof List) {
            final List<VoAttrValue> list = (List<VoAttrValue>) object;
            if (!list.isEmpty()) {
                final Map<String, Pair<String, Map<String, String>>> values = new HashMap<>();
                for (final VoAttrValue value : list) {
                    final List<MutablePair<String, String>> names = value.getDisplayVals();
                    final Map<String, String> namesI1n8 = new HashMap<>();
                    if (names != null) {
                        for (final MutablePair<String, String> name : names) {
                            namesI1n8.put(name.getFirst(), name.getSecond());
                        }
                    }

                    values.put(value.getAttribute().getCode(), new Pair<>(value.getVal(), namesI1n8));
                }
                return values;
            }
        }
        return null;
    }
}
