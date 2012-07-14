package org.yes.cart.service.domain.impl;

import org.yes.cart.cache.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.service.domain.ProductTypeAttrService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductTypeAttrServiceImpl extends BaseGenericServiceImpl<ProductTypeAttr> implements ProductTypeAttrService {

    private final static String PRODTYPE_SERV_METHOD_CACHE = "productTypeAttrServiceImplMethodCache";

    /**
     * Construct service.
     * @param genericDao product type attribute dao to use.
     */
    public ProductTypeAttrServiceImpl(final GenericDAO<ProductTypeAttr, Long> genericDao) {
        super(genericDao);
    }


    /** {@inheritDoc} */
    @Cacheable(value = PRODTYPE_SERV_METHOD_CACHE)
    public List<ProductTypeAttr> getByProductTypeId(final long productTypeId) { 
        return getGenericDao().findByNamedQuery("PRODUCT.TYPE.ATTR.BY.PROD.TYPE.ID", productTypeId);
    }

}
