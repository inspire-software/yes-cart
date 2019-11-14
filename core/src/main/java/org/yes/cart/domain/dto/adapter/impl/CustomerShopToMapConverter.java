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
import org.yes.cart.domain.entity.CustomerShop;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 14/11/2019
 * Time: 06:21
 */
public class CustomerShopToMapConverter implements ValueConverter {

    /** {@inheritDoc} */
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final Map<Long, Boolean> shops = new LinkedHashMap<>();
        if (object instanceof Collection) {
            for (final CustomerShop customerShop : (Collection<CustomerShop>) object) {
                shops.put(customerShop.getShop().getShopId(), customerShop.isDisabled());
            }
        }
        return shops;
    }

    /** {@inheritDoc} */
    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        throw new UnsupportedOperationException("read only converter");
    }
}
