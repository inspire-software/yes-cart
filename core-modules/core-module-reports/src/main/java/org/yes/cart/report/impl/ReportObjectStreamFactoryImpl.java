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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.yes.cart.report.ReportObjectStreamFactory;
import org.yes.cart.util.DateUtils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 * Use this factory to get configured xStream object or configured object output stream
 * to perform transformation of domain objects to xml.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/1/12
 * Time: 11:55 AM
 */
public class ReportObjectStreamFactoryImpl implements ReportObjectStreamFactory {


    private static final String ROOT_NODE = "yes-report";

    private final XStream xStream;


    public ReportObjectStreamFactoryImpl() {

        this.xStream = getXStream();

    }

    /**
     * Get configured xstream object.
     * @return {@link XStream}
     */
    private XStream getXStream() {

        final XStream xStream = new XStream(new DomDriver());
        xStream.addPermission(AnyTypePermission.ANY);

        xStream.registerConverter(new Converter() {


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
        });

        xStream.registerConverter(new Converter() {
            @Override
            public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
                if (((AbstractPersistentCollection) source).wasInitialized()) {
                    context.convertAnother(new ArrayList((Collection) source));
                }
            }

            @Override
            public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
                return null; // not needed
            }

            @Override
            public boolean canConvert(final Class type) {
                return PersistentBag.class == type || PersistentList.class == type || PersistentSet.class == type;
            }
        });

        xStream.setMode(XStream.NO_REFERENCES);

        return xStream;

    }

    /**
     * Spring IoC.
     *
     * @param aliases alias
     */
    public void setAliasesMap(final Map<String, Class> aliases) {

        for (final Map.Entry<String, Class> entry : aliases.entrySet()) {
            this.xStream.alias(entry.getKey(), entry.getValue());
        }

    }

    /**
     * Spring IoC.
     *
     * @param omit omit
     */
    public void setOmitFieldsMap(final Map<Class, String[]> omit) {

        for (final Map.Entry<Class, String[]> entry : omit.entrySet()) {
            for (final String field : entry.getValue()) {
                this.xStream.omitField(entry.getKey(), field);
            }
        }

    }


    /**
     * Get configured object output stream.
     *
     * @param writer given writer
     *
     * @return {@link ObjectOutputStream}
     */
    public ObjectOutputStream getObjectOutputStream(final Writer writer) throws IOException {

        return xStream.createObjectOutputStream(writer, ROOT_NODE);
        
    }

}
