package org.yes.cart.web.support.service;


import org.yes.cart.domain.entity.Shop;

import javax.servlet.ServletRequest;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:25:23 PM
 * <p/>
 * Service for resolve shop by http request.
 */
public interface ShopResolverService {

    /**
     * Resolve shop by given http request.
     *
     * @param request the <code>HttpServletRequest</code>
     * @return Instance of {@link Shop} or null if shop cannot be resolved
     */
    Shop getShop(final ServletRequest request);

}
