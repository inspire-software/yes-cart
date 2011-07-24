package org.yes.cart.domain.dto.adapter.impl;

import dp.lib.dto.geda.adapter.BeanFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductSku;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuIdToEntityConverter extends GenericIdToEntityConverter<ProductSku> {


    public ProductSkuIdToEntityConverter(final GenericDAO<ProductSku, Long> productSkuLongGenericDAO) {
        super(productSkuLongGenericDAO);
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        return ((ProductSku) object).getSkuId();
    }

}
