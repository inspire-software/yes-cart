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

package org.yes.cart.domain.dto.impl;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 03/12/2014
 * Time: 21:56
 */
public class ProductSearchResultPageDTOImpl implements ProductSearchResultPageDTO {

    private final List<ProductSearchResultDTO> results;
    private final int first;
    private final int pageSize;
    private final int totalHits;
    private final String sortField;
    private final boolean sortDesc;

    public ProductSearchResultPageDTOImpl(final List<ProductSearchResultDTO> results,
                                          final int first,
                                          final int pageSize,
                                          final int totalHits,
                                          final String sortField,
                                          final boolean sortDesc) {
        this.results = results;
        this.first = first;
        this.pageSize = pageSize;
        this.totalHits = totalHits;
        this.sortField = sortField;
        this.sortDesc = sortDesc;
    }

    /** {@inheritDoc} */
    public List<ProductSearchResultDTO> getResults() {
        return results;
    }

    /** {@inheritDoc} */
    public int getFirst() {
        return first;
    }

    /** {@inheritDoc} */
    public int getPageSize() {
        return pageSize;
    }

    /** {@inheritDoc} */
    public int getTotalHits() {
        return totalHits;
    }

    /** {@inheritDoc} */
    public String getSortField() {
        return sortField;
    }

    /** {@inheritDoc} */
    public boolean isSortDesc() {
        return sortDesc;
    }

    /** {@inheritDoc} */
    public ProductSearchResultPageDTO copy() {
        final List<ProductSearchResultDTO> copyResults = new ArrayList<ProductSearchResultDTO>();
        for (final ProductSearchResultDTO result : results) {
            copyResults.add(result.copy());
        }
        return new ProductSearchResultPageDTOImpl(copyResults, first, pageSize, totalHits, sortField, sortDesc);
    }
}
