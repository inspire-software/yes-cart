/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopTopSeller;
import org.yes.cart.service.domain.ShopTopSellerService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            final Map<Long, BigDecimal> topSellersCount = new HashMap<Long, BigDecimal>();
            final Map<Long, Product> topSellersProducts = new HashMap<Long, Product>();
            for(Object [] tuple : list) {
                final String skuCode = (String) tuple[0];
                final BigDecimal qty = (BigDecimal) tuple[1];

                final Product product = productDao.findSingleByNamedQueryCached("PRODUCT.BY.SKU.CODE", skuCode);
                if (product != null) {
                    topSellersProducts.put(product.getProductId(), product);

                    final BigDecimal runningTotal = topSellersCount.get(product.getProductId());
                    if (runningTotal != null) {
                        topSellersCount.put(product.getProductId(), runningTotal.add(qty));
                    } else {
                        topSellersCount.put(product.getProductId(), qty);
                    }
                }
            }
            for(Map.Entry<Long, Product> top : topSellersProducts.entrySet()) {
                ShopTopSeller shopTopSeller = getGenericDao().getEntityFactory().getByIface(ShopTopSeller.class);
                shopTopSeller.setCounter(topSellersCount.get(top.getKey()));
                shopTopSeller.setShop(shop);
                shopTopSeller.setProduct(top.getValue());
                getGenericDao().create(shopTopSeller) ;
            }
        }

    }
}
