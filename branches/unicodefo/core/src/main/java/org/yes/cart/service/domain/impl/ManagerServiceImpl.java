package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ManagerService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ManagerServiceImpl extends BaseGenericServiceImpl<Manager> implements ManagerService {

    /**
     * Create service to magane the administrative of web shop
     * @param genericDao dao to use
     */
    public ManagerServiceImpl(final GenericDAO<Manager, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc } */
    public void resetPassword(final Manager manager) {
        getGenericDao().update(manager);        
    }

    /** {@inheritDoc } */
    public Manager create(final Manager manager, final Shop shop) {
        //TODO assign to manage particular shop
        return super.create(manager);
    }

    /** {@inheritDoc } */
    public List<Manager> findByEmail(final String email) {
        return findByCriteria(Restrictions.like("email", email, MatchMode.ANYWHERE));

    }

}
