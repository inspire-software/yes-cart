/*
 * Copyright 2009 Inspire-Software.com
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
import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 23/02/2020
 * Time: 14:27
 */
public class PairListToMutablePairListConverter implements ValueConverter {

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {

        final List<Pair<String, String>> list = (List<Pair<String, String>>) object;

        final List<MutablePair<String, String>> out = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            out.addAll(list.stream().map(item -> new MutablePair<>(item.getFirst(), item.getSecond())).collect(Collectors.toList()));
        }
        return out;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {

        final List<MutablePair<String, String>> list = (List<MutablePair<String, String>>) object;

        final List<Pair<String, String>> out = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            out.addAll(list.stream().map(item -> new Pair<>(item.getFirst(), item.getSecond())).collect(Collectors.toList()));
        }
        return out;
    }
}
