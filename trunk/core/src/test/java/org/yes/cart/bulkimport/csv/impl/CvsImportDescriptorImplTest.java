package org.yes.cart.bulkimport.csv.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Test;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CvsImportDescriptor;
import org.yes.cart.bulkimport.csv.impl.CsvImportColumnImpl;
import org.yes.cart.bulkimport.csv.impl.CsvImportFileImpl;
import org.yes.cart.bulkimport.csv.impl.CvsImportDescriptorImpl;
import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportColumn;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 1:54 PM
 */

public class CvsImportDescriptorImplTest {

    private CvsImportDescriptorImpl getCvsImportDescriptorImplWithoutPrimaryColumn() {

        CvsImportDescriptorImpl descriptor = new CvsImportDescriptorImpl();

        ImportColumn importColumn;

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(1);
        importColumn.setFieldType(FieldTypeEnum.FK_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("productType");
        importColumn.setRegExp(null);
        descriptor.getImportColumns().add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SIMPLE_SLAVE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("attributes");
        importColumn.setRegExp(".*");
        descriptor.getImportColumns().add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(3);
        importColumn.setFieldType(FieldTypeEnum.FIELD);
        importColumn.setLookupQuery(null);
        importColumn.setName("code");
        importColumn.setRegExp(".*");
        descriptor.getImportColumns().add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SIMPLE_SLAVE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("someOtherFiled");
        importColumn.setRegExp(null);
        descriptor.getImportColumns().add(importColumn);


        return descriptor;

    }

    private CvsImportDescriptorImpl getCvsImportDescriptorImplWithPrimaryColumn() {

        CvsImportDescriptorImpl descriptor = new CvsImportDescriptorImpl();

        ImportColumn importColumn;

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(1);
        importColumn.setFieldType(FieldTypeEnum.FK_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("productType");
        importColumn.setRegExp(null);
        descriptor.getImportColumns().add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SIMPLE_SLAVE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("attributes");
        importColumn.setRegExp(".*");
        descriptor.getImportColumns().add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(3);
        importColumn.setFieldType(FieldTypeEnum.FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("code");
        importColumn.setRegExp(".*");
        descriptor.getImportColumns().add(importColumn);

        importColumn = new CsvImportColumnImpl();
        importColumn.setColumnIndex(2);
        importColumn.setFieldType(FieldTypeEnum.SIMPLE_SLAVE_FIELD);
        importColumn.setLookupQuery("not important");
        importColumn.setName("someOtherFiled");
        importColumn.setRegExp(null);
        descriptor.getImportColumns().add(importColumn);


        return descriptor;

    }

    @Test
    public void testGetPrimaryKeyColumn() {

        CvsImportDescriptorImpl descriptor = getCvsImportDescriptorImplWithPrimaryColumn();
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
        CvsImportDescriptor cvsImportDescriptor = new CvsImportDescriptorImpl();
        assertNotNull(cvsImportDescriptor.getImportFile());
    }

    @Test
    public void testGetImportColumns() {
        CvsImportDescriptor cvsImportDescriptor = new CvsImportDescriptorImpl();
        assertNotNull(cvsImportDescriptor.getImportColumns());
        assertTrue(cvsImportDescriptor.getImportColumns().isEmpty());
    }

    @Test
    public void testXmlSerialization() {

        String xmlSerializationResult;

        XStream xStream = getXStream();

        CvsImportDescriptor cvsImportDescriptor = new CvsImportDescriptorImpl();

        cvsImportDescriptor.getImportFile().setColumnDelimeter(';');
        cvsImportDescriptor.getImportFile().setFileEncoding("UFT-8");
        cvsImportDescriptor.getImportFile().setFileNameMask("*.csv");
        cvsImportDescriptor.getImportFile().setIgnoreFirstLine(true);
        cvsImportDescriptor.getImportFile().setTextQualifier('"');
        cvsImportDescriptor.setImportFolder("/npa/import");

        CsvImportColumn csvImportColumn = new CsvImportColumnImpl(1, FieldTypeEnum.FIELD, "code", null, null);
        csvImportColumn.setLookupQuery("select a from table a");
        csvImportColumn.setRegExp(".*");
        csvImportColumn.setUseMasterObject(true);
        cvsImportDescriptor.getImportColumns().add(csvImportColumn);


        csvImportColumn.setImportDescriptor(new CvsImportDescriptorImpl());
        csvImportColumn.getImportDescriptor().setEntityIntface("SomeEntityIntefrace");
        csvImportColumn.getImportDescriptor().getImportColumns().add(new CsvImportColumnImpl(0, FieldTypeEnum.FIELD, "code", "re", "lookup query"));



        xmlSerializationResult = xStream.toXML(cvsImportDescriptor);

        assertNotNull(xmlSerializationResult);
        assertTrue(xmlSerializationResult.length() > 0);

        CvsImportDescriptor cvsImportDeserializedDescriptor = (CvsImportDescriptor) xStream.fromXML(xmlSerializationResult);

        assertEquals(cvsImportDescriptor.getImportFile().getColumnDelimeter(), cvsImportDeserializedDescriptor.getImportFile().getColumnDelimeter());
        assertEquals(cvsImportDescriptor.getImportFile().getFileEncoding(), cvsImportDeserializedDescriptor.getImportFile().getFileEncoding());
        assertEquals(cvsImportDescriptor.getImportFile().getFileNameMask(), cvsImportDeserializedDescriptor.getImportFile().getFileNameMask());
        assertEquals(cvsImportDescriptor.getImportFile().isIgnoreFirstLine(), cvsImportDeserializedDescriptor.getImportFile().isIgnoreFirstLine());
        assertEquals(cvsImportDescriptor.getImportFile().getTextQualifier(), cvsImportDeserializedDescriptor.getImportFile().getTextQualifier());
        assertEquals(cvsImportDescriptor.getImportFolder(), cvsImportDeserializedDescriptor.getImportFolder());

        assertEquals(cvsImportDescriptor.getImportColumns().iterator().next().getColumnIndex(),
                     cvsImportDeserializedDescriptor.getImportColumns().iterator().next().getColumnIndex());
        assertEquals(cvsImportDescriptor.getImportColumns().iterator().next().getFieldType(),
                     cvsImportDeserializedDescriptor.getImportColumns().iterator().next().getFieldType());
        assertEquals(cvsImportDescriptor.getImportColumns().iterator().next().getLookupQuery(),
                     cvsImportDeserializedDescriptor.getImportColumns().iterator().next().getLookupQuery());
        assertEquals(cvsImportDescriptor.getImportColumns().iterator().next().getName(),
                     cvsImportDeserializedDescriptor.getImportColumns().iterator().next().getName());
        assertEquals(cvsImportDescriptor.getImportColumns().iterator().next().getRegExp(),
                     cvsImportDeserializedDescriptor.getImportColumns().iterator().next().getRegExp());

    }


    protected XStream getXStream() {
           XStream xStream = new XStream(new DomDriver());
           xStream.alias("import-descriptor", CvsImportDescriptorImpl.class);
           xStream.alias("file-descriptor", CsvImportFileImpl.class);
           xStream.alias("column-descriptor", CsvImportColumnImpl.class);
           return xStream;
       }


}
