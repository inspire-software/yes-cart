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

package org.yes.cart.domain.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 03/11/2017
 * Time: 10:31
 */
public interface ProductSearchResultNavDTO extends Serializable {

    /**
     * Get all facet codes in this navigation.
     *
     * @return all codes
     */
    Set<String> getNavCodes();

    /**
     * Get navigation items for specific facet code.
     *
     * @param code code
     *
     * @return list of items
     */
    List<ProductSearchResultNavItemDTO> getItems(String code);

}
