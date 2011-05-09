package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.service.domain.ShopUrlService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShopUrlServiceImpl extends BaseGenericServiceImpl<ShopUrl> implements ShopUrlService {

    public ShopUrlServiceImpl(final GenericDAO<ShopUrl, Long> shopUrlDao) {
        super(shopUrlDao);
    }

}
