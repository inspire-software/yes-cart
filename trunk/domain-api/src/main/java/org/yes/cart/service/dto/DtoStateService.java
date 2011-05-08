package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.StateDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoStateService extends GenericDTOService<StateDTO> {

    /**
     * Find by country code.
     *
     * @param countryCode country code.
     * @return list of states , that belong to given country.
     * @throws UnmappedInterfaceException in case of config errors
     * @throws UnableToCreateInstanceException
     *                                    some runtime errors
     */
    List<StateDTO> findByCountry(String countryCode) throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
