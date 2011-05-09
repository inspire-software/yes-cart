package org.yes.cart.domain.dto.adapter.impl;

import dp.lib.dto.geda.adapter.ValueConverter;

import java.math.BigDecimal;

/**
 * Converter for big decimal to float.
 * <p/>
 * User: dogma
 * Date: Jan 26, 2011
 * Time: 4:02:09 PM
 */
public class BigDecimalToFloatValueConverter implements ValueConverter {

    /**
     * {@inheritDoc}
     */
    public Object convertToDto(Object object, dp.lib.dto.geda.adapter.BeanFactory beanFactory) {
        return new BigDecimal((Float) object);
    }

    /**
     * {@inheritDoc}
     */
    public Object convertToEntity(Object object, Object oldEntity, dp.lib.dto.geda.adapter.BeanFactory beanFactory) {
        if (object instanceof BigDecimal) {
            return ((BigDecimal) object).floatValue();
        }
        return 0f;
    }
}
