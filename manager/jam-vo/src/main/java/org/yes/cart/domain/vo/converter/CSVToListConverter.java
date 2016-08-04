package org.yes.cart.domain.vo.converter;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/08/2016
 * Time: 18:11
 */
public class CSVToListConverter implements ValueConverter {

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final List<String> list = new ArrayList<>();
        if (object instanceof String) {
            list.addAll(Arrays.asList(StringUtils.split((String) object, ',')));
        }
        return list;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        if (object instanceof Collection) {
            return StringUtils.join((Collection) object, ',');
        }
        return null;
    }
}
