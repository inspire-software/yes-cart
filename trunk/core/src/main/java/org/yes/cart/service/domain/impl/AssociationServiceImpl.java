package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Association;
import org.yes.cart.service.domain.AssociationService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AssociationServiceImpl extends BaseGenericServiceImpl<Association> implements AssociationService {

    /**
     * Construct association service.
     * @param genericDao dao to use.
     */
    public AssociationServiceImpl(final GenericDAO<Association, Long> genericDao) {
        super(genericDao);
    }
}
