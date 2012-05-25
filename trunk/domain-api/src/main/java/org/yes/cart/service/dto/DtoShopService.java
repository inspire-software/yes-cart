package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoShopService extends GenericDTOService<ShopDTO> {

    /**
     * Get supported currencies by given shop.
     *
     * @param shopId given shop id.
     * @return comma separated list of supported currency codes. Example USD,EUR
     */
    String getSupportedCurrencies(long shopId);

    /**
     * Get all suported currencies by all shops.
     *
     * @return all suported currencies.
     */
    Collection<String> getAllSupportedCurrenciesByShops();

    /**
     * Set supported currencies by given shop.
     *
     * @param shopId     shop id
     * @param currensies comma separated list of supported currency codes. Example USD,EUR
     */
    void setSupportedCurrencies(long shopId, String currensies);


    /**
     * Get shop by server domain name.
     * @param serverDomainName given domain nanme.
     * @return shop dto if found otherwise null.
     */
    ShopDTO getShopDtoByDomainName(String serverDomainName);


    /**
     * Gt shope, which assigned to customer.
     * @param customerId customer id
     * @return list of shops
     */
    List<ShopDTO> getAssignedShop(long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException;
}
