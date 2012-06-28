package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.service.domain.ProdTypeAttributeViewGroupService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:42 PM
 */
public class ProdTypeAttributeViewGroupServiceImpl extends BaseGenericServiceImpl<ProdTypeAttributeViewGroup>

        implements ProdTypeAttributeViewGroupService {

    /**
     * Construct service.
     * @param genericDao dao to use.
     */
    public ProdTypeAttributeViewGroupServiceImpl(final GenericDAO<ProdTypeAttributeViewGroup, Long> genericDao) {
        super(genericDao);
    }
}
