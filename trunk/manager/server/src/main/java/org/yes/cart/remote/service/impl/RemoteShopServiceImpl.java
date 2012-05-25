package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteShopService;
import org.yes.cart.service.dto.DtoShopService;

import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteShopServiceImpl
        extends AbstractRemoteService<ShopDTO>
        implements RemoteShopService {

    /**
     * Construct remote service.
     *
     * @param dtoShopService dto service to use.
     */
    public RemoteShopServiceImpl(final DtoShopService dtoShopService) {
        super(dtoShopService);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAllSupportedCurrenciesByShops() {
        return ((DtoShopService) getGenericDTOService()).getAllSupportedCurrenciesByShops();
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedCurrencies(final long shopId) {
        return ((DtoShopService) getGenericDTOService()).getSupportedCurrencies(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public void setSupportedCurrencies(final long shopId, final String currensies) {
        ((DtoShopService) getGenericDTOService()).setSupportedCurrencies(shopId, currensies);
    }

    /**
     * {@inheritDoc}
     */
    public ShopDTO getShopDtoByDomainName(final String serverDomainName) {
        return ((DtoShopService) getGenericDTOService()).getShopDtoByDomainName(serverDomainName);
    }


    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAssignedShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoShopService) getGenericDTOService()).getAssignedShop(customerId);
    }

}
