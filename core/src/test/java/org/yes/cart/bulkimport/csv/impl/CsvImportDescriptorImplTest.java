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

package org.yes.cart.bulkimport.csv.impl;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.stream.xml.CsvImportDescriptorXStreamProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 1:54 PM
 */

public class CsvImportDescriptorImplTest {

    private CsvImportDescriptorImpl getCvsImportDescriptorImplWithoutPrimaryColumn() {

        CsvImportDescriptorImpl descriptor = new CsvImportDescriptorImpl();

        final List<CsvImportColumn> columns = new ArrayList<CsvImportColumn>();

        CsvImportColumn importColumn;

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(1);
        importColumn.setFieldType(FieldTypeEnum.FK_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("productType");
        importColumn.setValueRegEx(null);
        columns.add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SLAVE_TUPLE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("attributes");
        importColumn.setValueRegEx(".*");
        columns.add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(3);
        importColumn.setFieldType(FieldTypeEnum.FIELD);
        importColumn.setLookupQuery(null);
        importColumn.setName("code");
        importColumn.setValueRegEx(".*");
        columns.add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SLAVE_TUPLE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("someOtherFiled");
        importColumn.setValueRegEx(null);
        columns.add(importColumn);

        descriptor.setImportColumns(columns);

        return descriptor;

    }

    private CsvImportDescriptorImpl getCvsImportDescriptorImplWithPrimaryColumn() {

        CsvImportDescriptorImpl descriptor = new CsvImportDescriptorImpl();

        final List<CsvImportColumn> columns = new ArrayList<CsvImportColumn>();

        CsvImportColumn importColumn;

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(1);
        importColumn.setFieldType(FieldTypeEnum.FK_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("productType");
        importColumn.setValueRegEx(null);
        columns.add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SLAVE_TUPLE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("attributes");
        importColumn.setValueRegEx(".*");
        columns.add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(3);
        importColumn.setFieldType(FieldTypeEnum.FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("code");
        importColumn.setValueRegEx(".*");
        columns.add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SLAVE_TUPLE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("someOtherFiled");
        importColumn.setValueRegEx(null);
        columns.add(importColumn);

        descriptor.setImportColumns(columns);

        return descriptor;

    }

    @Test
    public void testGetPrimaryKeyColumn() {

        CsvImportDescriptorImpl descriptor = getCvsImportDescriptorImplWithPrimaryColumn();
        ImportColumn col = descriptor.getPrimaryKeyColumn();
        assertNotNull(col);
        assertEquals("code", col.getName());
        assertNotNull(col.getLookupQuery());

        descriptor = getCvsImportDescriptorImplWithoutPrimaryColumn();
        col = descriptor.getPrimaryKeyColumn();
        assertNull(col);

    }

    @Test
    public void testGetImportFile() {
        CsvImportDescriptor csvImportDescriptor = new CsvImportDescriptorImpl();
        assertNotNull(csvImportDescriptor.getImportFileDescriptor());
    }

    @Test
    public void testGetImportColumns() {
        CsvImportDescriptor csvImportDescriptor = new CsvImportDescriptorImpl();
        assertNotNull(csvImportDescriptor.getImportColumns());
        assertTrue(csvImportDescriptor.getImportColumns().isEmpty());
    }

    @Test
    public void testXmlSerialization() {

        String xmlSerializationResult;

        CsvImportDescriptorImpl csvImportDescriptor = new CsvImportDescriptorImpl();

        csvImportDescriptor.getImportFileDescriptor().setColumnDelimiter(';');
        csvImportDescriptor.getImportFileDescriptor().setFileEncoding("UFT-8");
        csvImportDescriptor.getImportFileDescriptor().setFileNameMask("*.csv");
        csvImportDescriptor.getImportFileDescriptor().setIgnoreFirstLine(true);
        csvImportDescriptor.getImportFileDescriptor().setTextQualifier('"');
        csvImportDescriptor.setImportDirectory("/yescart/import");

        CsvImportColumn csvImportColumn = new CsvImportColumnImpl(1, FieldTypeEnum.FIELD, "code", null, null);
        csvImportColumn.setLookupQuery("select a from table a");
        csvImportColumn.setValueRegEx(".*");
        csvImportColumn.setUseMasterObject(true);
        csvImportDescriptor.setImportColumns((List) Arrays.asList(csvImportColumn));


        csvImportColumn.setImportDescriptor(new CsvImportDescriptorImpl());
        csvImportColumn.getImportDescriptor().setEntityType("SomeEntityIntefrace");
        ((CsvImportDescriptorImpl) csvImportColumn.getImportDescriptor()).setImportColumns(
                (List) Arrays.asList(new CsvImportColumnImpl(0, FieldTypeEnum.FIELD, "code", "re", "lookup query")));



        xmlSerializationResult = new CsvImportDescriptorXStreamProvider().toXML(csvImportDescriptor);

        assertNotNull(xmlSerializationResult);
        assertTrue(xmlSerializationResult.length() > 0);

        CsvImportDescriptor csvImportDeserializedDescriptor = new CsvImportDescriptorXStreamProvider().fromXML(xmlSerializationResult);

        assertEquals(csvImportDescriptor.getImportFileDescriptor().getColumnDelimiter(), csvImportDeserializedDescriptor.getImportFileDescriptor().getColumnDelimiter());
        assertEquals(csvImportDescriptor.getImportFileDescriptor().getFileEncoding(), csvImportDeserializedDescriptor.getImportFileDescriptor().getFileEncoding());
        assertEquals(csvImportDescriptor.getImportFileDescriptor().getFileNameMask(), csvImportDeserializedDescriptor.getImportFileDescriptor().getFileNameMask());
        assertEquals(csvImportDescriptor.getImportFileDescriptor().isIgnoreFirstLine(), csvImportDeserializedDescriptor.getImportFileDescriptor().isIgnoreFirstLine());
        assertEquals(csvImportDescriptor.getImportFileDescriptor().getTextQualifier(), csvImportDeserializedDescriptor.getImportFileDescriptor().getTextQualifier());
        assertEquals(csvImportDescriptor.getImportDirectory(), csvImportDeserializedDescriptor.getImportDirectory());

        assertEquals(csvImportDescriptor.getImportColumns().iterator().next().getColumnIndex(),
                     csvImportDeserializedDescriptor.getImportColumns().iterator().next().getColumnIndex());
        assertEquals(csvImportDescriptor.getImportColumns().iterator().next().getFieldType(),
                     csvImportDeserializedDescriptor.getImportColumns().iterator().next().getFieldType());
        assertEquals(csvImportDescriptor.getImportColumns().iterator().next().getLookupQuery(),
                     csvImportDeserializedDescriptor.getImportColumns().iterator().next().getLookupQuery());
        assertEquals(csvImportDescriptor.getImportColumns().iterator().next().getName(),
                     csvImportDeserializedDescriptor.getImportColumns().iterator().next().getName());
        assertEquals(csvImportDescriptor.getImportColumns().iterator().next().getValueRegEx(),
                     csvImportDeserializedDescriptor.getImportColumns().iterator().next().getValueRegEx());

    }

}
