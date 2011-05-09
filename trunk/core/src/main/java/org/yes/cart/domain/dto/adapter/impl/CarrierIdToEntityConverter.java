package org.yes.cart.domain.dto.adapter.impl;

import dp.lib.dto.geda.adapter.BeanFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Carrier;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CarrierIdToEntityConverter extends GenericIdToEntityConverter<Carrier> {

    /**
     * Construct converter.
     * @param carrierLongGenericDAO dao to use.
     */
    public CarrierIdToEntityConverter(final GenericDAO<Carrier, Long> carrierLongGenericDAO) {
        super(carrierLongGenericDAO);
    }

    /** {@inheritDoc} */
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        return ((Carrier) object).getCarrierId();
    }
}
