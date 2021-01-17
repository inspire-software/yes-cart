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

package org.yes.cart.bulkexport.csv.impl;

import org.junit.Test;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 17/01/2021
 * Time: 14:14
 */
public class CsvDataDescriptorSampleGeneratorImplTest {

    @Test
    public void testGenerateSampleSupports() throws Exception {

        final XStreamProvider<CsvExportDescriptor> provider = new CsvExportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/export/csv/attributenames.xml");
        final CsvExportDescriptor desc = provider.fromXML(inputStream);

        final CsvDataDescriptorSampleGeneratorImpl generator = new CsvDataDescriptorSampleGeneratorImpl();

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(3, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("target_attributenames_export-yyyy-MM-dd_HH-mm-ss.csv-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Template: target_attributenames_export-yyyy-MM-dd_HH-mm-ss.csv\n" +
                "Encoding: UTF-8\n" +
                "Select command: select b from AttributeEntity b where b.attributeGroup = 'PRODUCT'\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("target_attributenames_export-yyyy-MM-dd_HH-mm-ss.csv", template1.getFirst());

        final String content1 = new String(template1.getSecond());
        assertEquals("\"Group\";\"Code\";\"Name\";\"Description\";\"Display Name En (en)\";\"Display Name Ru (ru)\";\"Mandatory\";\"Allow Dup (Constant)\";\"Allow Failver (template)\";\"Rank\";\"Data Type\"\n" +
                "\"\";\"\";\"\";\"\";\"\";\"\";true;true;true;\"\";\"\"\n", content1);

        final Pair<String, byte[]> template2 = templates.get(2);
        assertNotNull(template2);
        assertEquals("target_attributenames_export-yyyy-MM-dd_HH-mm-ss.csv-descriptor.xml", template2.getFirst());

    }

    @Test
    public void testGenerateSampleSupportsSub() throws Exception {

        final XStreamProvider<CsvExportDescriptor> provider = new CsvExportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/export/csv/productnames.xml");
        final CsvExportDescriptor desc = provider.fromXML(inputStream);

        final CsvDataDescriptorSampleGeneratorImpl generator = new CsvDataDescriptorSampleGeneratorImpl();

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(3, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("target_productnames_export-yyyy-MM-dd_HH-mm-ss.csv-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Template: target_productnames_export-yyyy-MM-dd_HH-mm-ss.csv\n" +
                "Encoding: UTF-8\n" +
                "Select command: select p from ProductEntity p\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("target_productnames_export-yyyy-MM-dd_HH-mm-ss.csv", template1.getFirst());

        final String content1 = new String(template1.getSecond());
        assertEquals("\"GUID\";\"SKU code\";\"Name En (en)\";\"Name Ru (ru)\";\"Brand\";\"Attribute Slave Inline All > Code\";\"Attribute Slave Inline All > Val\";\"Attribute Slave Inline Image0 > Image Val\";\"Categories > Category GUID\";\"Categories > Category Name\";\"Attribute by Code\";\"Price by Product ID Currency (USD)\";\"Price by SKU Currency (USD)\"\n" +
                "\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";99.99;99.99\n", content1);

        final Pair<String, byte[]> template2 = templates.get(2);
        assertNotNull(template2);
        assertEquals("target_productnames_export-yyyy-MM-dd_HH-mm-ss.csv-descriptor.xml", template2.getFirst());

    }

    @Test
    public void testGenerateSampleNotSupports() throws Exception {

        final XStreamProvider<CsvExportDescriptor> provider = new CsvExportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/export/img/productimages_simple.xml");
        final CsvExportDescriptor desc = provider.fromXML(inputStream);

        final CsvDataDescriptorSampleGeneratorImpl generator = new CsvDataDescriptorSampleGeneratorImpl();

        assertFalse(generator.supports(desc));

    }
}