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
import java.util.List;

/**
 * User: denispavlov
 * Date: 03/12/2014
 * Time: 21:40
 */
public interface ProductSkuSearchResultPageDTO extends Serializable {

    /**
     * Results for a single page.
     *
     * @return list all results
     */
    List<ProductSkuSearchResultDTO> getResults();

    /**
     * Count of total possible results.
     *
     * @return total results count
     */
    int getTotalHits();

    /**
     * Creates copy of this object
     *
     * @return copy
     */
    ProductSkuSearchResultPageDTO copy();


}
