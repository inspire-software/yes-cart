package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.State;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface StateService extends GenericService<State> {

    /**
     * Find by country code.
     * @param countryCode country code.
     * @return list of states , that belong to given country.
     */
    List<State> findByCountry(String countryCode);

}
