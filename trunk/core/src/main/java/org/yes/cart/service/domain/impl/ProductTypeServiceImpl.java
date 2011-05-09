package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.service.domain.ProductTypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductTypeServiceImpl extends BaseGenericServiceImpl<ProductType> implements ProductTypeService {

    /**
     * Construct service.
     * @param productTypeDao dao to use 
     */
    public ProductTypeServiceImpl(final GenericDAO<ProductType, Long> productTypeDao) {
        super(productTypeDao);
    }
}
