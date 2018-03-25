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

package org.yes.cart.search.query.impl;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.dto.impl.NavigationContextImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 25/03/2018
 * Time: 12:16
 */
public class SearchQueryFactoryDisabled implements SearchQueryFactory<Object> {

    @Override
    public NavigationContext<Object> getProductSnowBallQuery(final NavigationContext<Object> navigationContext, final String param, final Object value) {
        return new NavigationContextImpl<>(
                navigationContext.getShopId(),
                navigationContext.getCustomerShopId(),
                navigationContext.getCustomerLanguage(),
                navigationContext.getCategories(),
                navigationContext.isIncludeSubCategories(),
                navigationContext.getMutableCopyFilterParameters(),
                navigationContext.getProductQuery(),
                navigationContext.getProductSkuQuery()
        );
    }

    @Override
    public NavigationContext<Object> getSkuSnowBallQuery(final NavigationContext<Object> navigationContext, final List<ProductSearchResultDTO> products) {
        return new NavigationContextImpl<>(
                navigationContext.getShopId(),
                navigationContext.getCustomerShopId(),
                navigationContext.getCustomerLanguage(),
                navigationContext.getCategories(),
                navigationContext.isIncludeSubCategories(),
                navigationContext.getMutableCopyFilterParameters(),
                navigationContext.getProductQuery(),
                navigationContext.getProductSkuQuery()
        );
    }

    @Override
    public NavigationContext<Object> getFilteredNavigationQueryChain(final long shopId,
                                                                     final long customerShopId,
                                                                     final String customerLanguage,
                                                                     final List<Long> categories,
                                                                     final boolean searchSubCategories,
                                                                     final Map<String, List> requestParameters) {
        return new NavigationContextImpl<>(
                shopId,
                customerShopId,
                customerLanguage,
                categories,
                searchSubCategories,
                Collections.emptyMap(),
                null,
                null
        );

    }
}
