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
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;

import java.util.*;

/**
 * User: denispavlov
 * Date: 05/02/2017
 * Time: 19:40
 */
public class PairMapToPairListConverter implements ValueConverter {

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final List<MutablePair<String, MutablePair<String, String>>> list = new ArrayList<MutablePair<String, MutablePair<String, String>>>();
        if (object instanceof Map) {
            final Map<String, Pair<String, String>> values = (Map<String, Pair<String, String>>) object;
            for (final Map.Entry<String, Pair<String, String>> entry : values.entrySet()) {
                list.add(MutablePair.of(
                        entry.getKey(),
                        MutablePair.of(entry.getValue().getFirst(), entry.getValue().getSecond())
                ));
            }
        }
        return list;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        if (object instanceof List) {
            final List<MutablePair<String, MutablePair<String, String>>> list = (List<MutablePair<String, MutablePair<String, String>>>) object;
            if (!list.isEmpty()) {
                final Map<String, Pair<String, String>> values = new HashMap<String, Pair<String, String>>();
                for (final MutablePair<String, MutablePair<String, String>> value : list) {
                    values.put(value.getFirst(), new Pair<String, String>(value.getSecond().getFirst(), value.getSecond().getSecond()));
                }
                return values;
            }
        }
        return null;
    }
}
