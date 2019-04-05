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

package org.yes.cart.bulkexport.xml.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.bulkexport.xml.XmlExportContext;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.bulkexport.xml.XmlExportFile;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.InputStream;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public class XmlExportDescriptorXStreamProvider implements XStreamProvider<XmlExportDescriptor> {

    private XStream xStream;

    /** {@inheritDoc} */
    @Override
    public XmlExportDescriptor fromXML(final String xml) {
        return (XmlExportDescriptor) provide().fromXML(xml);
    }

    /** {@inheritDoc} */
    @Override
    public XmlExportDescriptor fromXML(final InputStream is) {
        return (XmlExportDescriptor) provide().fromXML(is);
    }

    /** {@inheritDoc} */
    @Override
    public String toXML(final XmlExportDescriptor object) {
        return provide().toXML(object);
    }

    private XStream provide() {
        if (this.xStream == null) {
            XStream xStream = new XStream(new DomDriver());

            xStream.alias("export-descriptor", XmlExportDescriptorImpl.class);
            xStream.addDefaultImplementation(XmlExportDescriptorImpl.class, ExportDescriptor.class);
            xStream.addDefaultImplementation(XmlExportDescriptorImpl.class, XmlExportDescriptor.class);
            xStream.aliasField("entity-type", XmlExportDescriptorImpl.class, "entityType");

            xStream.aliasField("context", XmlExportDescriptorImpl.class, "context");
            xStream.addDefaultImplementation(XmlExportContextImpl.class, XmlExportContext.class);
            xStream.aliasField("shop-code", XmlExportContextImpl.class, "shopCode");

            xStream.aliasField("select-sql", XmlExportDescriptorImpl.class, "selectSql");
            xStream.aliasField("xml-handler", XmlExportDescriptorImpl.class, "xmlHandler");

            xStream.aliasField("export-file-descriptor", XmlExportDescriptorImpl.class, "exportFileDescriptor");
            xStream.addDefaultImplementation(XmlExportFileImpl.class, XmlExportFile.class);
            xStream.aliasField("file-encoding", XmlExportFileImpl.class, "fileEncoding");
            xStream.aliasField("file-name", XmlExportFileImpl.class, "fileName");

            this.xStream = xStream;
        }

        return this.xStream;
    }
}
