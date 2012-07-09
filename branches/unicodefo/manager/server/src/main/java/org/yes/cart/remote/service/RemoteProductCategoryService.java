package org.yes.cart.remote.service;

import org.yes.cart.service.dto.DtoProductCategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public interface RemoteProductCategoryService extends DtoProductCategoryService {

    /**
     * Get the next rank for product during product assignment.
     * Default step is 50.
     *
     * @param categoryId category id
     * @return rank.
     */
    int getNextRank(long categoryId);

}
