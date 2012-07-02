package org.yes.cart.domain.dto.adapter.impl;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Warehouse;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class WarehouseIdToEntityConverter  extends GenericIdToEntityConverter<Warehouse> {

    /**
     * Construct converter.
     * @param warehouseLongGenericDAO dao to use.
     */
    public WarehouseIdToEntityConverter(final GenericDAO<Warehouse, Long> warehouseLongGenericDAO) {
        super(warehouseLongGenericDAO);
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        return ((Warehouse) object).getWarehouseId();
    }

}
