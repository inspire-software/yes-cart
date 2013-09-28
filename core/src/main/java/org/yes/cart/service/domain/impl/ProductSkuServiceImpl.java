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

import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImpl extends BaseGenericServiceImpl<ProductSku> implements ProductSkuService {


    private final GenericDAO<Product, Long> productDao;
    private final GenericDAO<SkuPrice, Long> skuPriceDao;


    /**
     * Construct  service.
     * @param productSkuDao sku dao
     * @param productDao    product dao
     */
    public ProductSkuServiceImpl(final GenericDAO<ProductSku, Long> productSkuDao,
                                 final GenericDAO<Product, Long> productDao,
                                 final GenericDAO<SkuPrice, Long> skuPriceDao) {
        super(productSkuDao);
        this.productDao = productDao;
        this.skuPriceDao = skuPriceDao;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ProductSku> getAllProductSkus(final long productId) {
        final Product product = productDao.findById(productId);
        return product.getSku();
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku getProductSkuBySkuCodeForIndexing(final String skuCode) {
        return getGenericDao().findSingleByCriteria(
                Restrictions.eq("code", skuCode)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productSkuService-productSkuBySkuCode")
    public ProductSku getProductSkuBySkuCode(final String skuCode) {
        return getProductSkuBySkuCodeForIndexing(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    public void removeAllPrices(final long productId) {
        final List<ProductSku> skus = getGenericDao().findByCriteria(Restrictions.eq("product.productId" , productId));
        for (ProductSku sku : skus) {
            removeAllPrices( sku);
        }
    }

    /**
     * {@inheritDoc}
     */
     public void removeAllPrices(final ProductSku sku) {
         getGenericDao().executeUpdate("REMOVE.ALL.SKU.PRICES", sku);

     }

    /**
     * {@inheritDoc}
     */
    public void removeAllItems(final long productId) {
        final List<ProductSku> skus = getGenericDao().findByCriteria(Restrictions.eq("product.productId" , productId));
        for (ProductSku sku : skus) {
            removeAllItems(sku);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
        "productSkuService-productSkuBySkuCode"
    }, allEntries = true)
    public void removeAllItems(final ProductSku sku) {
            getGenericDao().executeUpdate("REMOVE.ALL.SKU.ITEMS", sku);
    }

    /**
     * {@inheritDoc}
     */
    /*public void delete(ProductSku instance) {
        getGenericDao().executeUpdate("REMOVE.ALL.SKU.PRICES", instance);
        getGenericDao().executeUpdate("REMOVE.ALL.SKU.ITEMS", instance);
        getGenericDao().evict(instance.getProduct());
        super.delete(instance);
        instance.getProduct().getSku().remove(instance);
    }*/
}
