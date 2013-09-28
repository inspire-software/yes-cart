
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
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.service.domain.ProductAssociationService;

import java.util.List;

/**
 * Product association services.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductAssociationServiceImpl
    extends BaseGenericServiceImpl<ProductAssociation>
        implements ProductAssociationService {

    private final GenericDAO<ProductAssociation, Long> productAssociationDao;

    /**
     * Construct product association services.
     *
     * @param productAssociationDao dao to use.
     */
    public ProductAssociationServiceImpl(final GenericDAO<ProductAssociation, Long> productAssociationDao) {
        super(productAssociationDao);
        this.productAssociationDao = productAssociationDao;
    }

    /**
     * Get all product associations.
     *
     * @param productId product primary key
     * @return list of product associations
     */
    public List<ProductAssociation> getProductAssociations(final Long productId) {
        return productAssociationDao.findByNamedQuery("PRODUCT.ASSOCIATIONS", productId);
    }

    /**
     * Get all product associations by association type.
     *
     * @param productId       product primary key
     * @param associationCode association code [up, cross, etc]
     * @return list of product associations
     */
    public List<ProductAssociation> getProductAssociations(final Long productId, final String associationCode) {
        return productAssociationDao.findByNamedQuery("PRODUCT.ASSOCIATIONS.BY.TYPE", productId, associationCode);
    }


}
