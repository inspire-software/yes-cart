package org.yes.cart.domain.dto.adapter.impl;

import dp.lib.dto.geda.adapter.BeanFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductType;

/* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductTypeIdToEntityConverter  extends GenericIdToEntityConverter<ProductType> {

    /**
     * Construct converter.
     * @param productTypeLongGenericDAO dao to use.
     */
    public ProductTypeIdToEntityConverter(final GenericDAO<ProductType, Long> productTypeLongGenericDAO) {
        super(productTypeLongGenericDAO);
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {
        return ((ProductType) object).getProducttypeId();
    }
    
}
