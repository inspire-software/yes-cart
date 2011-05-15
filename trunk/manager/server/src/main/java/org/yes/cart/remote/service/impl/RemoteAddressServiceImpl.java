package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteAddressService;
import org.yes.cart.service.dto.DtoAddressService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteAddressServiceImpl extends AbstractRemoteService<AddressDTO>
        implements RemoteAddressService {

    private final DtoAddressService dtoAddressService;

    /**
     * Construct service.
     *
     * @param addressDTOGenericDTOService service to delegate
     */
    public RemoteAddressServiceImpl(final GenericDTOService<AddressDTO> addressDTOGenericDTOService) {
        super(addressDTOGenericDTOService);
        this.dtoAddressService = (DtoAddressService) addressDTOGenericDTOService;
    }

    /**
     * {@inheritDoc}
     */
    public List<AddressDTO> getAddressesByCustomerId(final long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoAddressService.getAddressesByCustomerId(customerId);
    }
}
