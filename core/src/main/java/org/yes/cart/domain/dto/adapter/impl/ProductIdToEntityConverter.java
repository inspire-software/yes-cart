package org.yes.cart.domain.dto.adapter.impl;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductIdToEntityConverter extends GenericIdToEntityConverter<Product> {

    public ProductIdToEntityConverter(final GenericDAO genericDAO) {
        super(genericDAO);
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        return ((Product) object).getProductId();
    }

}
