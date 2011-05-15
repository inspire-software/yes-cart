package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteShopUrlService;
import org.yes.cart.service.dto.DtoShopUrlService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteShopUrlServiceImpl
        extends AbstractRemoteService<ShopUrlDTO>
        implements RemoteShopUrlService {


    /**
     * Construct remote service.
     *
     * @param dtoShopUrlService dto service to use
     */
    public RemoteShopUrlServiceImpl(final DtoShopUrlService dtoShopUrlService) {
        super(dtoShopUrlService);
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopUrlDTO> getAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoShopUrlService) getGenericDTOService()).getAllByShopId(shopId);
    }

}
