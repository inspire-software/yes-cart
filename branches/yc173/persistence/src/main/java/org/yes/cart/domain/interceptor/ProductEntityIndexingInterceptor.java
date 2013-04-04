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
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

/**
 * User:  Igor Azarny
 * Date: 5/5/12
 * Time: 3:40 PM
 */
public class ProductEntityIndexingInterceptor implements EntityIndexingInterceptor<ProductEntity> {


    /**
     * Check is product need to be in index.
     * Product will be added to index:
     * in case if product available for pre/back order;
     * or always available (for example digital products);
     * or has quantity of sku more than 0  on any stock.
     *
     *
     * @param entity entity to check
     * @return true if entity need to be in lucene index.
     */
    public boolean isIncludeInLuceneIndex(final Product entity) {
       if (entity != null && Product.AVAILABILITY_STANDARD == entity.getAvailability()) {
           return MoneyUtils.isFirstBiggerThanSecond(entity.getQtyOnWarehouse(), BigDecimal.ZERO);
       }
       return true;
    }


    /** {@inheritDoc} */
    public IndexingOverride onAdd(final ProductEntity entity) {
        return isIncludeInLuceneIndex(entity)
                ?IndexingOverride.APPLY_DEFAULT
                :IndexingOverride.REMOVE;
    }

    /** {@inheritDoc} */
    public IndexingOverride onUpdate(final ProductEntity entity) {
        return isIncludeInLuceneIndex(entity)
                ?IndexingOverride.APPLY_DEFAULT
                :IndexingOverride.REMOVE;
    }

    /** {@inheritDoc} */
    public IndexingOverride onDelete(final ProductEntity entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }

    /** {@inheritDoc} */
    public IndexingOverride onCollectionUpdate(final ProductEntity entity) {
        return isIncludeInLuceneIndex(entity)
                ?IndexingOverride.APPLY_DEFAULT
                :IndexingOverride.REMOVE;
    }

}
