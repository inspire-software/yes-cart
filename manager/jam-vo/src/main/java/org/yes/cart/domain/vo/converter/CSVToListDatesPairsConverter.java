/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo.converter;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.util.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 06/02/2017
 * Time: 09:55
 */
public class CSVToListDatesPairsConverter implements ValueConverter {

    private static final Logger LOG = LoggerFactory.getLogger(CSVToListDatesPairsConverter.class);

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final List<MutablePair<String, String>> list = new ArrayList<>();
        if (object instanceof String) {
            final String[] dateRanges = StringUtils.split((String) object, ',');
            for (final String dates : dateRanges) {
                if (dates.indexOf(':') == -1) {
                    final LocalDate date = DateUtils.ldParseSDT(dates);
                    if (date != null) {
                        list.add(MutablePair.of(date, null));
                    }
                } else {
                    final String[] dateRange = StringUtils.split(dates, ':');
                    final LocalDate from = DateUtils.ldParseSDT(dateRange[0]);
                    final LocalDate to = DateUtils.ldParseSDT(dateRange[1]);
                    if (from != null && to != null) {
                        list.add(MutablePair.of(from, to));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        if (object instanceof Collection) {
            final List<MutablePair<LocalDate, LocalDate>> dates =  (List<MutablePair<LocalDate, LocalDate>>) object;
            final StringBuilder out = new StringBuilder();
            for (final MutablePair<LocalDate, LocalDate> range : dates) {
                if (out.length() > 0) {
                    out.append(',');
                }
                if (range.getFirst() != null) {
                    out.append(DateUtils.formatSD(range.getFirst()));
                }
                if (range.getSecond() != null) {
                    out.append(':');
                    out.append(DateUtils.formatSD(range.getSecond()));
                }
            }
            if (out.length() > 0) {
                return out.toString();
            }
        }
        return null;
    }
}
