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
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImpl extends BaseGenericServiceImpl<ProductSku> implements ProductSkuService {


    private final GenericDAO<Product, Long> productDao;


    /**
     * Construct  service.
     * @param productSkuDao sku dao
     * @param productDao    product dao
     */
    public ProductSkuServiceImpl(final GenericDAO<ProductSku, Long> productSkuDao,
                                 final GenericDAO<Product, Long> productDao) {
        super(productSkuDao);
        this.productDao = productDao;
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
    public ProductSku getProductSkuBySkuCode(final String skuCode) {
        return getGenericDao().findSingleByCriteria(
                Restrictions.eq("code", skuCode)
        );
    }


}
