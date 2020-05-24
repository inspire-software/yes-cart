/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.search.query.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.Query;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 17/07/2017
 * Time: 23:16
 */
public class InStockProductSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder<Query> {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Query> createQueryChain(final NavigationContext<Query> navigationContext, final String parameter, final Object value) {

        String strValue = "1";
        if (value instanceof Collection) {
            if (CollectionUtils.isNotEmpty((Collection) value)) {
                strValue = "0".equals(((Collection) value).iterator().next()) ? "0" : "1";
            }
        } else {
            strValue = "0".equals(value) ? "0" : "1";
        }

        return Collections.singletonList(createNumericQuery(PRODUCT_SHOP_INSTOCK_FLAG_FIELD + strValue, navigationContext.getCustomerShopId()));
    }
}
