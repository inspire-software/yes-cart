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

import org.junit.Test;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkexport.csv.CsvExportColumn;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.bulkexport.model.ExportColumn;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 13:44
 */
public class CsvExportDescriptorXStreamProviderTest {

    @Test
    public void testProvide() throws Exception {
        final XStreamProvider<CsvExportDescriptor> provider = new CsvExportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/export/schematest001.xml");
        final CsvExportDescriptor desc = provider.fromXML(inputStream);

        assertNotNull(desc);

        assertNotNull(desc.getContext());
        assertEquals("SHOP10", desc.getContext().getShopCode());

        assertEquals("org.yes.cart.domain.entity.Attribute", desc.getEntityType());
        assertNotNull(desc.getExportFileDescriptor());

        assertEquals("UTF-8", desc.getExportFileDescriptor().getFileEncoding());
        assertEquals("attributenames.csv", desc.getExportFileDescriptor().getFileName());
        assertTrue(desc.getExportFileDescriptor().isPrintHeader());
        assertEquals(';', desc.getExportFileDescriptor().getColumnDelimiter());
        assertEquals('"', desc.getExportFileDescriptor().getTextQualifier());

        assertEquals("select b from AttributeEntity b where b.code = {code}", desc.getSelectSql());

        assertNotNull(desc.getColumns());
        assertEquals(11, desc.getColumns().size());

        final List<ExportColumn> colums = new ArrayList<ExportColumn>(desc.getColumns());

        final CsvExportColumn col0 = (CsvExportColumn) colums.get(0);
        assertNotNull(col0);
        assertEquals("Group", col0.getColumnHeader());
        assertEquals(ImpExColumn.FK_FIELD, col0.getFieldType());
        assertEquals("attributeGroup", col0.getName());
        assertNull(col0.getValueRegEx());
        assertEquals(Integer.valueOf(1), col0.getValueRegExGroup());
        assertEquals("select b from AttributeGroupEntity b where b.code = {attributeGroup}", col0.getLookupQuery());
        assertEquals("CTX001", col0.getContext());

        final CsvExportColumn col5 = (CsvExportColumn) colums.get(5);
        assertNotNull(col5);
        assertEquals("Display Name Ru", col5.getColumnHeader());
        assertEquals(ImpExColumn.FIELD, col5.getFieldType());
        assertEquals("displayName", col5.getName());
        assertEquals("(.{0,255})(.*)", col5.getValueRegEx());
        assertEquals(Integer.valueOf(1), col5.getValueRegExGroup());
        assertEquals("Rus: $1", col5.getValueRegExTemplate());
        assertEquals("ru", col5.getLanguage());
        assertNull(col5.getLookupQuery());

    }

}
