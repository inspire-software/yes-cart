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

package org.yes.cart.bulkimport.csv.impl;

import org.junit.Test;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 14:16
 */
public class CsvDataDescriptorSampleGeneratorImplTest {

    @Test
    public void testGenerateSampleSupportsSimple() throws Exception {

        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/csv/productcategorynames.xml");
        final CsvImportDescriptor desc = provider.fromXML(inputStream);

        final CsvDataDescriptorSampleGeneratorImpl generator = new CsvDataDescriptorSampleGeneratorImpl();

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(3, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("productcategorynames.csv-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Template: productcategorynames.csv\n" +
                "Mode: MERGE\n" +
                "Encoding: UTF-8\n" +
                "Select command: select p from ProductCategoryEntity p where p.product.guid = {product} and p.category.guid = {category}\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("productcategorynames.csv", template1.getFirst());

        final String content1 = new String(template1.getSecond());
        assertEquals("\"product GUID\";;\"category GUID\"\n" +
                "\"\";;\"\"\n", content1);

        final Pair<String, byte[]> template2 = templates.get(2);
        assertNotNull(template2);
        assertEquals("productcategorynames.csv-descriptor.xml", template2.getFirst());

    }

    @Test
    public void testGenerateSampleSupportsDelete() throws Exception {

        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/csv/productsattributes.delete.hbm.xml");
        final CsvImportDescriptor desc = provider.fromXML(inputStream);

        final CsvDataDescriptorSampleGeneratorImpl generator = new CsvDataDescriptorSampleGeneratorImpl();

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(3, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("productsattributes.delete.hbm.csv-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Template: productsattributes.delete.hbm.csv\n" +
                "Mode: DELETE\n" +
                "Encoding: UTF-8\n" +
                "Select command: select v from AttrValueEntityProduct v where v.attributeCode = {attributeCode} and v.product.guid = {product}\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("productsattributes.delete.hbm.csv", template1.getFirst());

        final String content1 = new String(template1.getSecond());
        assertEquals("\"product\";;;\"attributeCode\"\n" +
                "\"\";;;\"\"\n", content1);

        final Pair<String, byte[]> template2 = templates.get(2);
        assertNotNull(template2);
        assertEquals("productsattributes.delete.hbm.csv-descriptor.xml", template2.getFirst());

    }

    @Test
    public void testGenerateSampleSupportsSub() throws Exception {

        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/csv/productandcategorynames.xml");
        final CsvImportDescriptor desc = provider.fromXML(inputStream);

        final CsvDataDescriptorSampleGeneratorImpl generator = new CsvDataDescriptorSampleGeneratorImpl();

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(3, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("productandcategorynames-XXXXXXX.csv-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Template: productandcategorynames-XXXXXXX.csv\n" +
                "Mode: MERGE\n" +
                "Encoding: UTF-8\n" +
                "Select command: select p from ProductEntity p where p.guid = {guid}\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("productandcategorynames-XXXXXXX.csv", template1.getFirst());

        final String content1 = new String(template1.getSecond());
        assertEquals("\"guid\";\"code\";\"manufacturerCode\";;\"brand ID\";\"producttype ID\";\"seo.uri\";\"name\";\"displayName (en)\";\"displayName (ru)\";\"displayName (uk)\";\"description\";\"product description (en) > val\";\"product description (ru) > val\";\"product description (uk) > val\";;\"tag\";;;;;;\"category GUID > category GUID\"\n" +
                "\"\";\"\";\"\";;\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";;\"\";;;;;;\"\"\n", content1);

        final Pair<String, byte[]> template2 = templates.get(2);
        assertNotNull(template2);
        assertEquals("productandcategorynames-XXXXXXX.csv-descriptor.xml", template2.getFirst());

    }

    @Test
    public void testGenerateSampleNotSupports() throws Exception {

        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/img/productimages_simple.xml");
        final CsvImportDescriptor desc = provider.fromXML(inputStream);

        final CsvDataDescriptorSampleGeneratorImpl generator = new CsvDataDescriptorSampleGeneratorImpl();

        assertFalse(generator.supports(desc));

    }
}