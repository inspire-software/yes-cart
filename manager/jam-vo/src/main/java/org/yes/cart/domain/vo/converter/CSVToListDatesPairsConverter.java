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
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.MutablePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
        final List<MutablePair<String, String>> list = new ArrayList<MutablePair<String, String>>();
        if (object instanceof String) {
            final SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_IMPORT_DATE_FORMAT);
            final String[] dateRanges = StringUtils.split((String) object, ',');
            for (final String dates : dateRanges) {
                try {
                    if (dates.indexOf(':') == -1) {
                        list.add(
                                MutablePair.of(
                                        format.parse(dates),
                                        null
                                )
                        );
                    } else {
                        final String[] dateRange = StringUtils.split(dates, ':');
                        list.add(
                                MutablePair.of(
                                    format.parse(dateRange[0]),
                                    format.parse(dateRange[1])
                                )
                        );
                    }
                } catch (ParseException pe) {
                    LOG.error(pe.getMessage() + ": " + object, pe);
                }

            }
        }
        return list;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        if (object instanceof Collection) {
            final List<MutablePair<Date, Date>> dates =  (List<MutablePair<Date, Date>>) object;
            final SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_IMPORT_DATE_FORMAT);
            final StringBuilder out = new StringBuilder();
            for (final MutablePair<Date, Date> range : dates) {
                if (out.length() > 0) {
                    out.append(',');
                }
                if (range.getFirst() != null) {
                    out.append(format.format(range.getFirst()));
                }
                if (range.getSecond() != null) {
                    out.append(':');
                    out.append(format.format(range.getSecond()));
                }
            }
            if (out.length() > 0) {
                return out.toString();
            }
        }
        return null;
    }
}
