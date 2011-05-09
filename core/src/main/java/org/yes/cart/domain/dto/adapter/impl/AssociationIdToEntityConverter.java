package org.yes.cart.domain.dto.adapter.impl;

import dp.lib.dto.geda.adapter.BeanFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Association;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AssociationIdToEntityConverter extends GenericIdToEntityConverter<Association> {
    /**
     * Construct convertor.
     * @param associationLongGenericDAO dao to use.
     */
    public AssociationIdToEntityConverter(final GenericDAO<Association, Long> associationLongGenericDAO) {
        super(associationLongGenericDAO);
    }

    /** {@inheritDoc}*/
    @Override
    public Long convertToDto(final Object object, final BeanFactory beanFactory) {
        return ((Association) object).getAssociationId();
    }
}
