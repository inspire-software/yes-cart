package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.service.domain.BrandService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BrandServiceImpl extends BaseGenericServiceImpl<Brand> implements BrandService {

    /**
     * Construct brand service.
     * @param brandDao brand dao to use.
     */
    public BrandServiceImpl(final GenericDAO<Brand, Long> brandDao) {
        super(brandDao);
    }

}
