package org.yes.cart.dao.impl;


import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Availability;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class ProductGenericDAOHibernateImpl extends GenericDAOHibernateImpl<ProductEntity, Long> {

    /**
     * Default constructor.
     *
     * @param type - entity type
     * @param entityFactory {@link EntityFactory} to create the entities 
     */
    public ProductGenericDAOHibernateImpl(final Class<ProductEntity> type,
            final EntityFactory entityFactory) {
        super(type, entityFactory);
    }

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
    @Override
    protected boolean isIncludeInLuceneIndex(ProductEntity entity) {
        if (Availability.STANDARD == entity.getAvailability().getAvailabilityId()) {
            final BigDecimal qty = (BigDecimal) findSingleByNamedQuery("SKU.QTY.BY.PRODUCT", entity);
            return MoneyUtils.isFirstBiggerThanSecond(qty, BigDecimal.ZERO);
        }
        return true;
    }

}
