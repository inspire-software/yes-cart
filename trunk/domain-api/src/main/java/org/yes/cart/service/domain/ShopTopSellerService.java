package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ShopTopSeller;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/5/11
 * Time: 12:09 AM
 */
public interface ShopTopSellerService  extends GenericService<ShopTopSeller> {

    /**
     * Update top sellers.
     * @param calculationPeriodInDays recalculation period
     */
   void updateTopSellers(int calculationPeriodInDays);

}
