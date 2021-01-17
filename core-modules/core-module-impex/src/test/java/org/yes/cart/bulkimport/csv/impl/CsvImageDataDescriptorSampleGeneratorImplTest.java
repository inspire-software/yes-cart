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
 * Time: 14:08
 */
public class CsvImageDataDescriptorSampleGeneratorImplTest {

    @Test
    public void testGenerateSampleSupports() throws Exception {

        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/img/productimages_simple.xml");
        final CsvImportDescriptor desc = provider.fromXML(inputStream);

        final CsvImageDataDescriptorSampleGeneratorImpl generator = new CsvImageDataDescriptorSampleGeneratorImpl();

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(2, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("imgvaultproduct-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Images filename mask: ^([.[^_]]*)(_[a-z])?(_[a-z]{2})?\\.(jpe?g|png)$\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("imgvaultproduct-descriptor.xml", template1.getFirst());

    }

    @Test
    public void testGenerateSampleNotSupports() throws Exception {

        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/csv/attributegroupnames.xml");
        final CsvImportDescriptor desc = provider.fromXML(inputStream);

        final CsvImageDataDescriptorSampleGeneratorImpl generator = new CsvImageDataDescriptorSampleGeneratorImpl();

        assertFalse(generator.supports(desc));
        
    }
}