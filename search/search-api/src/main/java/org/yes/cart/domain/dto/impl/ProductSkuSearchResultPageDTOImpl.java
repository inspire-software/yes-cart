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

package org.yes.cart.domain.dto.impl;

import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultPageDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 03/12/2014
 * Time: 21:56
 */
public class ProductSkuSearchResultPageDTOImpl implements ProductSkuSearchResultPageDTO {

    private final List<ProductSkuSearchResultDTO> results;
    private final int totalHits;

    public ProductSkuSearchResultPageDTOImpl(final List<ProductSkuSearchResultDTO> results,
                                             final int totalHits) {
        this.results = results;
        this.totalHits = totalHits;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductSkuSearchResultDTO> getResults() {
        return results;
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalHits() {
        return totalHits;
    }

    /** {@inheritDoc} */
    @Override
    public ProductSkuSearchResultPageDTO copy() {
        final List<ProductSkuSearchResultDTO> copyResults = new ArrayList<>();
        if (this.results != null) {
            for (final ProductSkuSearchResultDTO result : results) {
                copyResults.add(result.copy());
            }
        }
        return new ProductSkuSearchResultPageDTOImpl(copyResults, totalHits);
    }
}
