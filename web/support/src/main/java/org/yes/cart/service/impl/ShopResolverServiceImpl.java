

package org.yes.cart.service.impl;

import org.yes.cart.service.ShopResolverService;

import javax.servlet.ServletRequest;

import org.yes.cart.service.domain.ShopService;
import org.yes.cart.domain.entity.Shop;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:25:46 PM
 * {@inheritDoc}
 */
public class ShopResolverServiceImpl implements ShopResolverService {

    private final ShopService shopService;

    /** IoC.
     * @param shopService instance of ShopService to set.
     * */
    public ShopResolverServiceImpl(final ShopService shopService) {
        this.shopService = shopService;
    }

    /** {@inheritDoc} */    
    public Shop getShop(final ServletRequest request) {
        final String serverName = request.getServerName().toLowerCase();
        return shopService.getShopByDomainName(serverName);
    }

}
