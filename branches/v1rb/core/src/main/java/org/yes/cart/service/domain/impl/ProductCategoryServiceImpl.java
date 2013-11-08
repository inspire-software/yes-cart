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
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.service.domain.ProductCategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductCategoryServiceImpl extends BaseGenericServiceImpl<ProductCategory> implements ProductCategoryService {

    private final static int RANK_STEP = 50;


    /**
     * Construct product category service.
     *
     * @param productCategoryDao product category dao to use.
     */
    public ProductCategoryServiceImpl(final GenericDAO<ProductCategory, Long> productCategoryDao) {
        super(productCategoryDao);
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategory findByCategoryIdProductId(final long categoryId, final long productId) {
        return getGenericDao().findSingleByNamedQuery("PRODUCT.IN.CATEGORY", categoryId, productId);
    }

    /**
     * {@inheritDoc}
     */
    public void removeByCategoryProductIds(final long categoryId, final long productId) {
        getGenericDao().executeUpdate("REMOVE.PRODUCTCATEGORY.BY.CATEGORYID.PRODUCTID", categoryId, productId);
    }

    /**
     * {@inheritDoc}
     */
    public void removeByProductIds(final long productId) {
        getGenericDao().executeUpdate("REMOVE.PRODUCTCATEGORIES.BY.PRODUCTID", productId);
    }

    /**
     * {@inheritDoc}
     */
    public int getNextRank(final long categoryId) {
        Integer maxRank = (Integer) getGenericDao().getScalarResultByNamedQuery("GET.MAX.RANK", categoryId);
        if (maxRank == null) {
            return RANK_STEP;
        } else {
            return maxRank + RANK_STEP;
        }
    }


}
