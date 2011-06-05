package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopTopSeller;
import org.yes.cart.service.domain.ShopTopSellerService;

import java.util.Calendar;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/5/11
 * Time: 12:07 AM
 */
public class ShopTopSellerServiceImpl extends BaseGenericServiceImpl<ShopTopSeller> implements ShopTopSellerService {

    private final GenericDAO<Shop, Long> shopDao;

    /**
     * Construct service.
     * @param shopTopSellerDao        shop top seller dao
     * @param shopDao        shop top dao
     */
    public ShopTopSellerServiceImpl(final GenericDAO<ShopTopSeller, Long> shopTopSellerDao,
                                    final GenericDAO<Shop, Long> shopDao) {
        super(shopTopSellerDao);
        this.shopDao = shopDao;
    }

    /** {@inheritDoc} */
    public void updateTopSellers(int calculationPeriodInDays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1 * calculationPeriodInDays);
        List<Shop> shops = shopDao.findAll();
        for(Shop shop : shops) {
            getGenericDao().executeUpdate("TOP.SELLER.SHOP.CLEAN", shop);
            List<Object[]> list = getGenericDao().findQueryObjectsByNamedQuery("TOP.SELLER.SHOP.CALCULATE", shop, calendar.getTime());
            for(Object [] tuple : list) {
                System.out.println(tuple[0].getClass());
                System.out.println(tuple[0]);
                System.out.println(tuple[1].getClass());
                System.out.println(tuple[1]);
            }
        }

    }
}
