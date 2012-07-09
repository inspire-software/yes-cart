package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.StateDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteStateService;
import org.yes.cart.service.dto.DtoStateService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteStateServiceImpl extends AbstractRemoteService<StateDTO> implements RemoteStateService {

    /**
     * Construct remote service to manage states/areas .
     *
     * @param stateDTOGenericDTOService generic service.
     */
    public RemoteStateServiceImpl(final GenericDTOService<StateDTO> stateDTOGenericDTOService) {
        super(stateDTOGenericDTOService);
    }

    public List<StateDTO> findByCountry(final String countryCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoStateService) getGenericDTOService()).findByCountry(countryCode);
    }

}
