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
import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 23/02/2020
 * Time: 14:14
 */
public class ProductOptionSkuCodesToSkuCodeNamePairConverter implements ValueConverter {

    private final GenericDAO<Object, Long> genericDAO;

    public ProductOptionSkuCodesToSkuCodeNamePairConverter(final GenericDAO<Object, Long> genericDAO) {
        this.genericDAO = genericDAO;
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final List<String> skuCodes = (List<String>) object;
        final List<Pair<String, String>> options = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(skuCodes)) {
            skuCodes.forEach(code -> {
                options.add(new Pair<>(code, getName(code, "SKU.NAME.BY.CODE")));
            });
        }
        return options;
    }

    protected String getName(final String skuCode, final String query) {
        List<Object> list = genericDAO.findQueryObjectByNamedQuery(query, skuCode);
        if (list != null && !list.isEmpty()) {
            final Object name = list.get(0);
            if (name instanceof String) {
                return (String) name;
            }
        }
        return null;
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        final List<Pair<String, String>> options = (List<Pair<String, String>>) object;
        final List<String> skuCodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(options)) {
            skuCodes.addAll(options.stream().map(Pair::getFirst).collect(Collectors.toList()));
        }
        return skuCodes;
    }
}
