/*
 * Copyright 2009 Inspire-Software.com
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
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.yes.cart.report.ReportObjectStreamFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
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
     * Spring IoC.
     *
     * @param converters converter
     */
    public void setConverterMap(final Map<String, Converter> converters) {

        for (final Map.Entry<String, Converter> entry : converters.entrySet()) {
            this.xStream.registerConverter(entry.getValue());
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
