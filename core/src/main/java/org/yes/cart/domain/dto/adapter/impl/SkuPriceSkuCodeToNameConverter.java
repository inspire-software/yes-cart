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
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SkuPrice;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/05/2015
 * Time: 19:18
 */
public class SkuPriceSkuCodeToNameConverter implements ValueConverter {

    private final GenericDAO<Object, Long> genericDAO;

    public SkuPriceSkuCodeToNameConverter(final GenericDAO<Object, Long> genericDAO) {
        this.genericDAO = genericDAO;
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final String skuCode = ((SkuPrice) object).getSkuCode();
        String name = getName(skuCode, "SKU.NAME.BY.CODE");
        if (name == null) {
            name = getName(slaCode(skuCode), "SKU.NAME.BY.SLA.CODE");
        }
        return name;
    }

    protected String slaCode(final String skuCode) {
        if (skuCode.endsWith("_KG")) {
            return skuCode.substring(0, skuCode.length() - 3);
        } else if (skuCode.endsWith("_KGMAX")) {
            return skuCode.substring(0, skuCode.length() - 6);
        }
        return skuCode;
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
        throw new UnsupportedOperationException("Not supported");
    }
}
