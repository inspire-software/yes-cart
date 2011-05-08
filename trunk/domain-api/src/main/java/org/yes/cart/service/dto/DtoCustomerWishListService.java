package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.CustomerWishListDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCustomerWishListService extends GenericDTOService<CustomerWishListDTO> {

    /**
     * Get wish list items
     *
     * @param customerId customer id
     * @return wish list items
     * @throws UnmappedInterfaceException in case of config errors
     * @throws UnableToCreateInstanceException
     *                                    in case of dto creation errors
     */
    List<CustomerWishListDTO> getByCustomerId(long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
