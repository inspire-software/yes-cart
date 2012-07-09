package org.yes.cart.domain.interceptor;

import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

/**
 * User: igora Igor Azarny
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
       if (Product.AVAILABILITY_STANDARD == entity.getAvailability()) {
           return MoneyUtils.isFirstBiggerThanSecond(entity.getQtyOnWarehouse(), BigDecimal.ZERO);
       }
       return true;
    }


    /** {@inheritDoc} */
    public IndexingOverride onAdd(final ProductEntity entity) {
        return isIncludeInLuceneIndex(entity)
                ?IndexingOverride.APPLY_DEFAULT
                :IndexingOverride.SKIP;
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
