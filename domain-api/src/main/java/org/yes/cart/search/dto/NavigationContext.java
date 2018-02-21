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

package org.yes.cart.search.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Navigation context that holds all necessary information to locate products
 * with respect to users current URI location. This is an immutable object and
 * return values of its getter MUST ONLY be used in read only mode.
 *
 * User: denispavlov
 * Date: 25/11/2014
 * Time: 22:22
 */
public interface NavigationContext<T> extends Serializable {

    /**
     * Get shop PK for context.
     *
     * @return shop context
     */
    long getShopId();

    /**
     * Get shop PK for context.
     *
     * @return shop context
     */
    long getCustomerShopId();

    /**
     * Get customer language (could be null).
     *
     * @return language
     */
    String getCustomerLanguage();

    /**
     * Get category tree context.
     *
     * @return category context
     */
    List<Long> getCategories();

    /**
     * Flag to include subcategories in search of {#getCategories()} categories
     *
     * @return true to include
     */
    boolean isIncludeSubCategories();

    /**
     * Flag to determine if this context is global (i.e. across all shop or local to category branch)
     *
     * @return true if this is global context
     */
    boolean isGlobal();

    /**
     * Check if this context already is filtered by given parameter.
     *
     * @param attribute attribute name
     *
     * @return true if this context already contain criteria for given attribute
     */
    boolean isFilteredBy(String attribute);

    /**
     * Get filter parameters.
     *
     * @return get filter parameters
     */
    Set<String> getFilterParametersNames();

    /**
     * Get filter parameters.
     *
     * @param parameterName name of the parameter
     *
     * @return get filter parameters
     */
    List<String> getFilterParameterValues(String parameterName);

    /**
     * Get copy of filter parameters to be used by other classes.
     *
     * @return get filter parameters
     */
    Map<String, List<String>> getMutableCopyFilterParameters();

    /**
     * Get product navigation query.
     *
     * @return product FT query
     */
    T getProductQuery();

    /**
     * Get product SKU navigation query.
     *
     * @return product SKU FT query
     */
    T getProductSkuQuery();

}
