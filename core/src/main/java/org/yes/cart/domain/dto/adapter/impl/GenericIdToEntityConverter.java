package org.yes.cart.domain.dto.adapter.impl;

import dp.lib.dto.geda.adapter.BeanFactory;
import dp.lib.dto.geda.adapter.ValueConverter;
import org.yes.cart.dao.GenericDAO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class GenericIdToEntityConverter<T> implements ValueConverter {

    private final GenericDAO<T, Long> genericDAO;

    public GenericIdToEntityConverter(final GenericDAO<T, Long> genericDAO) {
        this.genericDAO = genericDAO;
    }

    /** {@inheritDoc} */
    public Object convertToDto(Object object, BeanFactory beanFactory) {
        throw new RuntimeException(this.getClass().getCanonicalName() + " not support conversion from id to entity " + object.getClass());
    }

    /** {@inheritDoc} */
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {
        if (object != null) {
            return genericDAO.findById((Long) object);
        }
        return null;
    }

}
