package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.service.domain.CountryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CountryServiceImpl extends BaseGenericServiceImpl<Country> implements CountryService {

    /**
     * Construct service.
     * @param genericDao dao to use.
     */
    public CountryServiceImpl(final GenericDAO<Country, Long> genericDao) {
        super(genericDao);
    }
}
