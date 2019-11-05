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

package org.yes.cart.report.impl;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.yes.cart.utils.DateUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * User: denispavlov
 * Date: 05/11/2019
 * Time: 14:41
 */
public class DateTimeConverterImpl implements Converter {

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        if (source == null) {
            writer.setValue("");
        } else if (source instanceof LocalDate) {
            writer.setValue(((LocalDate) source).atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME));
        } else if (source instanceof LocalDateTime) {
            writer.setValue(((LocalDateTime) source).format(DateTimeFormatter.ISO_DATE_TIME));
        } else if (source instanceof ZonedDateTime) {
            writer.setValue(((ZonedDateTime) source).format(DateTimeFormatter.ISO_DATE_TIME));
        } else if (source instanceof Instant) {
            writer.setValue(ZonedDateTime.ofInstant((Instant) source, DateUtils.zone()).format(DateTimeFormatter.ISO_DATE_TIME));
        } else {
            writer.setValue(source.toString());
        }
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        return null; // not needed
    }

    @Override
    public boolean canConvert(final Class type) {
        return LocalDate.class == type || LocalDateTime.class == type || ZonedDateTime.class == type || Instant.class == type;
    }
    
}
