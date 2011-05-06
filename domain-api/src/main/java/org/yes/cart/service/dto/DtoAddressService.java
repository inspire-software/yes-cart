package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.exception.UnableToCreateInstanceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoAddressService extends GenericDTOService<AddressDTO> {

    /**
     * Get customers addresses
     * @param customerId customer id
     * @return list of addresses
     */
    List<AddressDTO> getAddressesByCustomerId(long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
