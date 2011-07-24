package org.yes.cart.domain.dto.adapter.impl;


import org.yes.cart.dao.EntityFactory;

/**
 * Adaptor to port Domain entity factory to GeDA bean factory
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class EntityFactoryToBeanFactoryAdaptor implements dp.lib.dto.geda.adapter.BeanFactory {

    private final EntityFactory entityFactory;

    public EntityFactoryToBeanFactoryAdaptor(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    /** {@inheritDoc} */
    public Object get(final String entityBeanKey) {
        return entityFactory.getByKey(entityBeanKey);
    }

}
