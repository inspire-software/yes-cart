package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.CountryDTO;
import org.yes.cart.remote.service.RemoteCountryService;
import org.yes.cart.service.dto.GenericDTOService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCountryServiceImpl
        extends AbstractRemoteService<CountryDTO>
        implements RemoteCountryService {

    /**
     * Construct remote service to manage countries.
     *
     * @param countryDTOGenericDTOService country service.
     */
    public RemoteCountryServiceImpl(final GenericDTOService<CountryDTO> countryDTOGenericDTOService) {
        super(countryDTOGenericDTOService);
    }
}
