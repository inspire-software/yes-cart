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

package org.yes.cart.bulkexport.stream.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.bulkexport.csv.CsvExportFile;
import org.yes.cart.bulkexport.csv.impl.CsvExportColumnImpl;
import org.yes.cart.bulkexport.csv.impl.CsvExportContextImpl;
import org.yes.cart.bulkexport.csv.impl.CsvExportDescriptorImpl;
import org.yes.cart.bulkexport.csv.impl.CsvExportFileImpl;
import org.yes.cart.bulkexport.model.ExportContext;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public class CsvExportDescriptorXStreamProvider implements XStreamProvider<CsvExportDescriptor> {

    private XStream xStream;

    /** {@inheritDoc} */
    public CsvExportDescriptor fromXML(final String xml) {
        return (CsvExportDescriptor) provide().fromXML(xml);
    }

    /** {@inheritDoc} */
    public CsvExportDescriptor fromXML(final InputStream is) {
        return (CsvExportDescriptor) provide().fromXML(is);
    }

    /** {@inheritDoc} */
    public String toXML(final CsvExportDescriptor object) {
        return provide().toXML(object);
    }

    private XStream provide() {
        if (this.xStream == null) {
            XStream xStream = new XStream(new DomDriver());

            xStream.alias("export-descriptor", CsvExportDescriptorImpl.class);
            xStream.addDefaultImplementation(CsvExportDescriptorImpl.class, ExportDescriptor.class);
            xStream.addDefaultImplementation(CsvExportDescriptorImpl.class, CsvExportDescriptor.class);
            xStream.aliasField("entity-type", CsvExportDescriptorImpl.class, "entityType");

            xStream.aliasField("context", CsvExportDescriptorImpl.class, "context");
            xStream.addDefaultImplementation(CsvExportContextImpl.class, ExportContext.class);
            xStream.aliasField("shop-code", CsvExportContextImpl.class, "shopCode");
            xStream.aliasField("shop-code-column", CsvExportContextImpl.class, "shopCodeColumn");

            xStream.aliasField("select-sql", CsvExportDescriptorImpl.class, "selectSql");

            xStream.aliasField("export-file-descriptor", CsvExportDescriptorImpl.class, "exportFileDescriptor");
            xStream.addDefaultImplementation(CsvExportFileImpl.class, CsvExportFile.class);
            xStream.aliasField("print-header", CsvExportFileImpl.class, "printHeader");
            xStream.aliasField("column-delimiter", CsvExportFileImpl.class, "columnDelimiter");
            xStream.aliasField("text-qualifier", CsvExportFileImpl.class, "textQualifier");
            xStream.aliasField("file-encoding", CsvExportFileImpl.class, "fileEncoding");
            xStream.aliasField("file-name", CsvExportFileImpl.class, "fileName");

            xStream.aliasField("export-columns", CsvExportDescriptorImpl.class, "columns");
            xStream.addDefaultImplementation(ArrayList.class, Collection.class);

            xStream.alias("column-descriptor", CsvExportColumnImpl.class);
            xStream.aliasField("column-header", CsvExportColumnImpl.class, "columnHeader");
            xStream.aliasField("field-type", CsvExportColumnImpl.class, "fieldType");
            xStream.aliasField("data-type", CsvExportColumnImpl.class, "dataType");
            xStream.aliasField("language", CsvExportColumnImpl.class, "language");
            xStream.aliasField("context", CsvExportColumnImpl.class, "context");
            xStream.aliasField("value-regex", CsvExportColumnImpl.class, "valueRegEx");
            xStream.aliasField("value-regex-group", CsvExportColumnImpl.class, "valueRegExGroup");
            xStream.aliasField("value-regex-template", CsvExportColumnImpl.class, "valueRegExTemplate");
            xStream.aliasField("lookup-query", CsvExportColumnImpl.class, "lookupQuery");
            xStream.aliasField("value-constant", CsvExportColumnImpl.class, "valueConstant");
            xStream.aliasField("use-master-object", CsvExportColumnImpl.class, "useMasterObject");
            xStream.aliasField("export-descriptor", CsvExportColumnImpl.class, "descriptor");
            xStream.aliasField("entity-type", CsvExportColumnImpl.class, "entityType");

            this.xStream = xStream;
        }

        return this.xStream;
    }
}
