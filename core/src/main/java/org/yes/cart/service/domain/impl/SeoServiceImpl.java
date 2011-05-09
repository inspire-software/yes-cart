package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.service.domain.SeoService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SeoServiceImpl extends BaseGenericServiceImpl<Seo> implements SeoService {


    /**
     * Consctruct service.
     * @param seoDao dao to use.
     */
    public SeoServiceImpl(final GenericDAO<Seo, Long> seoDao) {
        super(seoDao);
    }

}
