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

package org.yes.cart.bulkimport.xml.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.xml.XmlImportContext;
import org.yes.cart.bulkimport.xml.XmlImportDescriptor;
import org.yes.cart.bulkimport.xml.XmlImportFile;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.InputStream;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public class XmlImportDescriptorXStreamProvider implements XStreamProvider<XmlImportDescriptor> {

    private XStream xStream;

    /** {@inheritDoc} */
    @Override
    public XmlImportDescriptor fromXML(final String xml) {
        return (XmlImportDescriptor) provide().fromXML(xml);
    }

    /** {@inheritDoc} */
    @Override
    public XmlImportDescriptor fromXML(final InputStream is) {
        return (XmlImportDescriptor) provide().fromXML(is);
    }

    /** {@inheritDoc} */
    @Override
    public String toXML(final XmlImportDescriptor object) {
        return provide().toXML(object);
    }

    private XStream provide() {
        if (this.xStream == null) {
            XStream xStream = new XStream(new DomDriver());
            xStream.addPermission(AnyTypePermission.ANY);

            xStream.alias("import-descriptor", XmlImportDescriptorImpl.class);
            xStream.addDefaultImplementation(XmlImportDescriptorImpl.class, ImportDescriptor.class);
            xStream.addDefaultImplementation(XmlImportDescriptorImpl.class, XmlImportDescriptor.class);
            xStream.aliasField("entity-type", XmlImportDescriptorImpl.class, "entityType");

            xStream.aliasField("context", XmlImportDescriptorImpl.class, "context");
            xStream.addDefaultImplementation(XmlImportContextImpl.class, XmlImportContext.class);
            xStream.aliasField("shop-code", XmlImportContextImpl.class, "shopCode");

            xStream.aliasField("xml-handler", XmlImportDescriptorImpl.class, "xmlHandler");

            xStream.aliasField("import-file-descriptor", XmlImportDescriptorImpl.class, "importFileDescriptor");
            xStream.addDefaultImplementation(XmlImportFileImpl.class, XmlImportFile.class);
            xStream.aliasField("file-encoding", XmlImportFileImpl.class, "fileEncoding");
            xStream.aliasField("file-name-mask", XmlImportFileImpl.class, "fileNameMask");

            this.xStream = xStream;
        }

        return this.xStream;
    }
}
