package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoShopUrlService extends GenericDTOService<ShopUrlDTO> {

    /**
     * Get all urls, that belongs to given shop id
     *
     * @param shopId pk value of shop
     * @return list of shop's urls
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<ShopUrlDTO> getAllByShopId(long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
