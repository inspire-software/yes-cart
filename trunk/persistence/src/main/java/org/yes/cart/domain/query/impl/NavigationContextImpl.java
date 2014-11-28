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

package org.yes.cart.domain.query.impl;

import org.apache.lucene.search.Query;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.query.NavigationContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 25/11/2014
 * Time: 22:25
 */
public class NavigationContextImpl implements NavigationContext {

    private final long shopId;
    private final List<Long> categories;
    private final Map<String, List<String>> navigationParameters;

    private final Query productQuery;

    public NavigationContextImpl(final long shopId,
                                 final List<Long> categories,
                                 final Map<String, List<String>> navigationParameters,
                                 final Query productQuery) {
        this.shopId = shopId;
        this.categories = categories;
        this.navigationParameters = navigationParameters;
        if (navigationParameters == null) {
            throw new NullPointerException();
        }
        this.productQuery = productQuery;
    }

    /**
     * {@inheritDoc}
     */
    public long getShopId() {
        return shopId;
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getCategories() {
        return categories;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGlobal() {
        return CollectionUtils.isEmpty(categories);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFilteredBy(final String attribute) {
        return navigationParameters.containsKey(attribute);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<String>> getFilterParameters() {
        return Collections.unmodifiableMap(navigationParameters);
    }

    /**
     * {@inheritDoc}
     */
    public Query getProductQuery() {
        return productQuery;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NavigationContextImpl that = (NavigationContextImpl) o;

        if (shopId != that.shopId) return false;
        if (categories != null) {
            if (that.categories == null || categories.size() != that.categories.size()) return false;
            for (int i = 0; i < categories.size(); i++) {
                if (!categories.get(i).equals(that.categories.get(i))) return false;
            }
        } else if (that.categories != null) return false;

        if (navigationParameters.size() != that.navigationParameters.size()) return false;
        for (final Map.Entry<String, List<String>> entry : navigationParameters.entrySet()) {
            final List<String> thatValue = that.navigationParameters.get(entry.getKey());
            if (entry.getValue() != null) {
                if (thatValue == null || entry.getValue().size() != thatValue.size()) return false;
                for (int i = 0; i < thatValue.size(); i++) {
                    if (!entry.getValue().get(i).equals(thatValue.get(i))) return false;
                }
            } else if (thatValue != null) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (shopId ^ (shopId >>> 32));
        if (categories != null) {
            for (final Long category : categories) {
                result = 31 * result + category.hashCode();
            }
        }
        for (final Map.Entry<String, List<String>> entry : navigationParameters.entrySet()) {
            result = 31 * result + entry.getKey().hashCode();
            if (entry.getValue() != null) {
                for (final String value : entry.getValue()) {
                    result = 31 * result + value.hashCode();
                }
            }
        }
        return result;
    }
}
