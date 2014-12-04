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

package org.yes.cart.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 03/12/2014
 * Time: 21:40
 */
public interface ProductSearchResultPageDTO extends Serializable {

    /**
     * Results for a single page.
     *
     * @return list all results
     */
    List<ProductSearchResultDTO> getResults();

    /**
     * Offset for first item.
     *
     * @return offset
     */
    int getFirst();

    /**
     * Page size is the max result that can be on page.
     *
     * @return page size
     */
    int getPageSize();

    /**
     * Count of total possible results.
     *
     * @return
     */
    int getTotalHits();

    /**
     * Sort field or null if no sorting.
     *
     * @return sort field or null
     */
    String getSortField();

    /**
     * Flag for descending sorting if sort field is specified
     *
     * @return true if descending sort order
     */
    boolean isSortDesc();

    /**
     * Creates copy of this object
     *
     * @return copy
     */
    ProductSearchResultPageDTO copy();


}
