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

package org.yes.cart.bulkimport.xml.impl;

import org.junit.Test;
import org.yes.cart.bulkimport.xml.XmlImportDescriptor;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 16:02
 */
public class XmlDataDescriptorSampleGeneratorImplTest {

    @Test
    public void testGenerateSampleSupportsSimple() throws Exception {

        final XStreamProvider<XmlImportDescriptor> provider = new XmlImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/xml/brands.xml");
        final XmlImportDescriptor desc = provider.fromXML(inputStream);

        final XmlDataDescriptorSampleGeneratorImpl generator = new XmlDataDescriptorSampleGeneratorImpl(
                Collections.singletonMap("BRAND", new BrandXmlEntityHandler())
        );

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(4, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("brands-data.xml-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Template: brands-data.xml\n" +
                "Encoding: UTF-8\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("impex.xsd", template1.getFirst());

        final Pair<String, byte[]> template2 = templates.get(2);
        assertNotNull(template2);
        assertEquals("brands-data.xml", template2.getFirst());

        final String content2 = new String(template2.getSecond());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<brands xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "        xsi:noNamespaceSchemaLocation=\"impex.xsd\">\n" +
                "<brand>\n" +
                "    <!-- refer to impex.xsd -->\n" +
                "</brand>\n" +
                "</brands>", content2);

        final Pair<String, byte[]> template3 = templates.get(3);
        assertNotNull(template3);
        assertEquals("brands-data.xml-descriptor.xml", template3.getFirst());

    }


    @Test
    public void testGenerateSampleNotSupports() throws Exception {

        final XStreamProvider<XmlImportDescriptor> provider = new XmlImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/xml/brands.xml");
        final XmlImportDescriptor desc = provider.fromXML(inputStream);

        final XmlDataDescriptorSampleGeneratorImpl generator = new XmlDataDescriptorSampleGeneratorImpl(Collections.emptyMap());

        assertFalse(generator.supports(desc));

    }
}