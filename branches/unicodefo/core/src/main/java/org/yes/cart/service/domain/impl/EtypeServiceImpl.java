package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.service.domain.EtypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class EtypeServiceImpl extends BaseGenericServiceImpl<Etype> implements EtypeService {

    /**
     * Construct service.
     * @param etypeDao IoC dao to use.
     */
    public EtypeServiceImpl(final GenericDAO<Etype, Long> etypeDao) {
        super(etypeDao);
    }
    
}
