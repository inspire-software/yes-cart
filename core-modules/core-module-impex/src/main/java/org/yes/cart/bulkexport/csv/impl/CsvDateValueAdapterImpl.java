/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.utils.DateUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * User: denispavlov
 * Date: 10/11/2017
 * Time: 22:52
 */
public class CsvDateValueAdapterImpl implements CsvValueAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(CsvDateValueAdapterImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final CsvImpExColumn csvImpExColumn, final CsvImpExTuple tuple) {

        final String pattern = csvImpExColumn.getContext();

        if (rawValue != null) {
            if (StringUtils.isNotBlank(pattern)) {

                if (rawValue instanceof Instant) {
                    return DateUtils.format((Instant) rawValue, pattern);
                } else if (rawValue instanceof LocalDateTime) {
                    return DateUtils.format((LocalDateTime) rawValue, pattern);
                } else if (rawValue instanceof LocalDate) {
                    return DateUtils.format((LocalDate) rawValue, pattern);
                } else if (rawValue instanceof ZonedDateTime) {
                    return DateUtils.format((ZonedDateTime) rawValue, pattern);
                }
                LOG.warn("Unsupported value {} of type {} is set for DATE converter", rawValue, rawValue.getClass());
            }
            return rawValue.toString(); // default format for this type
        }

        return null;


    }

}
