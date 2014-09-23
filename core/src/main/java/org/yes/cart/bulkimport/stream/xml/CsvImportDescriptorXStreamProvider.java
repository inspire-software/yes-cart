/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.bulkimport.stream.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportFile;
import org.yes.cart.bulkimport.csv.impl.CsvImportColumnImpl;
import org.yes.cart.bulkimport.csv.impl.CsvImportDescriptorImpl;
import org.yes.cart.bulkimport.csv.impl.CsvImportFileImpl;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: denispavlov
 * Date: 12-08-03
 * Time: 8:15 AM
 */
public class CsvImportDescriptorXStreamProvider implements XStreamProvider<CsvImportDescriptor> {

    private XStream xStream;

    /** {@inheritDoc} */
    public CsvImportDescriptor fromXML(final String xml) {
        return (CsvImportDescriptor) provide().fromXML(xml);
    }

    /** {@inheritDoc} */
    public CsvImportDescriptor fromXML(final InputStream is) {
        return (CsvImportDescriptor) provide().fromXML(is);
    }

    /** {@inheritDoc} */
    public String toXML(final CsvImportDescriptor object) {
        return provide().toXML(object);
    }

    private XStream provide() {
        if (this.xStream == null) {
            XStream xStream = new XStream(new DomDriver());

            xStream.alias("import-descriptor", CsvImportDescriptorImpl.class);
            xStream.addDefaultImplementation(CsvImportDescriptorImpl.class, ImportDescriptor.class);
            xStream.aliasField("entity-type", CsvImportDescriptorImpl.class, "entityType");
            xStream.aliasField("import-directory", CsvImportDescriptorImpl.class, "importDirectory");
            xStream.aliasField("select-sql", CsvImportDescriptorImpl.class, "selectSql");
            xStream.aliasField("insert-sql", CsvImportDescriptorImpl.class, "insertSql");

            xStream.aliasField("import-file-descriptor", CsvImportDescriptorImpl.class, "importFileDescriptor");
            xStream.addDefaultImplementation(CsvImportFileImpl.class, CsvImportFile.class);
            xStream.aliasField("ignore-first-line", CsvImportFileImpl.class, "ignoreFirstLine");
            xStream.aliasField("column-delimiter", CsvImportFileImpl.class, "columnDelimiter");
            xStream.aliasField("text-qualifier", CsvImportFileImpl.class, "textQualifier");
            xStream.aliasField("file-encoding", CsvImportFileImpl.class, "fileEncoding");
            xStream.aliasField("file-name-mask", CsvImportFileImpl.class, "fileNameMask");

            xStream.aliasField("import-columns", CsvImportDescriptorImpl.class, "importColumns");
            xStream.addDefaultImplementation(ArrayList.class, Collection.class);

            xStream.alias("column-descriptor", CsvImportColumnImpl.class);
            xStream.aliasField("column-index", CsvImportColumnImpl.class, "columnIndex");
            xStream.aliasField("field-type", CsvImportColumnImpl.class, "fieldType");
            xStream.aliasField("data-type", CsvImportColumnImpl.class, "dataType");
            xStream.aliasField("language", CsvImportColumnImpl.class, "language");
            xStream.aliasField("value-regex", CsvImportColumnImpl.class, "valueRegEx");
            xStream.aliasField("value-regex-group", CsvImportColumnImpl.class, "valueRegExGroup");
            xStream.aliasField("lookup-query", CsvImportColumnImpl.class, "lookupQuery");
            xStream.aliasField("value-constant", CsvImportColumnImpl.class, "valueConstant");
            xStream.aliasField("import-descriptor", CsvImportColumnImpl.class, "importDescriptor");
            xStream.aliasField("use-master-object", CsvImportColumnImpl.class, "useMasterObject");
            xStream.aliasField("import-descriptor", CsvImportColumnImpl.class, "importDescriptor");
            xStream.aliasField("entity-type", CsvImportColumnImpl.class, "entityType");

            this.xStream = xStream;
        }

        return this.xStream;
    }
}
