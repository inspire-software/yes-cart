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

package org.yes.cart.domain.interceptor;

import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;
import org.yes.cart.domain.entity.ProductSku;

/**
 * User: denispavlov
 * Date: 27/11/2014
 * Time: 13:40
 */
public class ProductSkuEntityIndexingInterceptor implements EntityIndexingInterceptor<ProductSku> {

    private final ProductEntityIndexingInterceptor productInterceptor = new ProductEntityIndexingInterceptor();

    /**
     * SKU needs to be indexed only if product is in index
     *
     *
     * @param entity entity to check
     * @return true if entity need to be in lucene index.
     */
    public boolean isIncludeInLuceneIndex(final ProductSku entity) {
        return entity != null && productInterceptor.isIncludeInLuceneIndex(entity.getProduct());
    }


    /** {@inheritDoc} */
    public IndexingOverride onAdd(final ProductSku entity) {
        return isIncludeInLuceneIndex(entity)
                ?IndexingOverride.APPLY_DEFAULT
                :IndexingOverride.REMOVE;
    }

    /** {@inheritDoc} */
    public IndexingOverride onUpdate(final ProductSku entity) {
        return isIncludeInLuceneIndex(entity)
                ?IndexingOverride.APPLY_DEFAULT
                :IndexingOverride.REMOVE;
    }

    /** {@inheritDoc} */
    public IndexingOverride onDelete(final ProductSku entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }

    /** {@inheritDoc} */
    public IndexingOverride onCollectionUpdate(final ProductSku entity) {
        return isIncludeInLuceneIndex(entity)
                ?IndexingOverride.APPLY_DEFAULT
                :IndexingOverride.REMOVE;
    }



}
