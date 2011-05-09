package org.yes.cart.domain.dto.adapter.impl;

import dp.lib.dto.geda.adapter.BeanFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Shop;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShopIdToEntityConverter  extends GenericIdToEntityConverter<Shop>{

    public ShopIdToEntityConverter(final GenericDAO<Shop, Long> shopLongGenericDAO) {
        super(shopLongGenericDAO);
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {
        return ((Shop) object).getShopId();
    }

}
