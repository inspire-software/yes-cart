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

package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 03/11/2017
 * Time: 10:32
 */
public interface ProductSearchResultNavItemDTO extends Serializable {

    /**
     * Raw value for this facet.
     *
     * @return raw value
     */
    String getValue();

    /**
     * Display name for this facet.
     *
     * @param locale locale
     *
     * @return display name
     */
    String getDisplayValue(String locale);

    /**
     * Count of documents with given facet value.
     *
     * @return count
     */
    Integer getCount();

}
