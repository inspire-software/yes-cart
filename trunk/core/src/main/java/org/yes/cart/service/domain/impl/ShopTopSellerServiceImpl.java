package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopTopSeller;
import org.yes.cart.service.domain.ShopTopSellerService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/5/11
 * Time: 12:07 AM
 */
public class ShopTopSellerServiceImpl extends BaseGenericServiceImpl<ShopTopSeller> implements ShopTopSellerService {

    private final GenericDAO<Shop, Long> shopDao;
    private final GenericDAO<Product, Long> productDao;

    /**
     * Construct service.
     * @param shopTopSellerDao        shop top seller dao
     * @param shopDao        shop top dao
     * @param productDao        product dao
     */
    public ShopTopSellerServiceImpl(final GenericDAO<ShopTopSeller, Long> shopTopSellerDao,
                                    final GenericDAO<Shop, Long> shopDao,
                                    final GenericDAO<Product, Long> productDao) {
        super(shopTopSellerDao);
        this.shopDao = shopDao;
        this.productDao = productDao;
    }

    /** {@inheritDoc} */
    //Use stored procedures in case if it will reduce performance on big data set
    //Bad ORM fashion
    public void updateTopSellers(final int calculationPeriodInDays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1 * calculationPeriodInDays);
        List<Shop> shops = shopDao.findAll();
        for(Shop shop : shops) {
            getGenericDao().executeUpdate("TOP.SELLER.SHOP.CLEAN", shop);
            List<Object[]> list = getGenericDao().findQueryObjectsByNamedQuery("TOP.SELLER.SHOP.CALCULATE", shop, calendar.getTime());
            for(Object [] tuple : list) {
                ShopTopSeller shopTopSeller = getGenericDao().getEntityFactory().getByIface(ShopTopSeller.class);
                shopTopSeller.setCounter((BigDecimal) tuple[1]);
                shopTopSeller.setShop(shop);
                shopTopSeller.setProduct(productDao.findById((Long) tuple[0]));
                getGenericDao().create(shopTopSeller) ;
            }
        }

    }
}
