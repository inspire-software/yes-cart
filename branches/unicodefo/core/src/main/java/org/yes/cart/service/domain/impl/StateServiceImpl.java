package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.State;
import org.yes.cart.service.domain.StateService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class StateServiceImpl extends BaseGenericServiceImpl<State> implements StateService {
    /**
     * Construct Service.
     * @param genericDao dao to use.
     */
    public StateServiceImpl(final GenericDAO<State, Long> genericDao) {
        super(genericDao);
    }

    public List<State> findByCountry(final String countryCode) {

        return getGenericDao().findByCriteria(
                Restrictions.eq("countryCode", countryCode));

    }

}
