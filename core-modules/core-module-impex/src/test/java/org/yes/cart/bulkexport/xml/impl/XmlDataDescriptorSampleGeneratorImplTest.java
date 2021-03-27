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

package org.yes.cart.bulkexport.xml.impl;

import org.junit.Test;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.stream.xml.XStreamProvider;
import org.yes.cart.utils.spring.LinkedHashMapBeanImpl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 17/01/2021
 * Time: 14:42
 */
public class XmlDataDescriptorSampleGeneratorImplTest {

    @Test
    public void testGenerateSampleSupportsSimple() throws Exception {

        final XStreamProvider<XmlExportDescriptor> provider = new XmlExportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/export/xml/brands.xml");
        final XmlExportDescriptor desc = provider.fromXML(inputStream);

        final XmlDataDescriptorSampleGeneratorImpl generator = new XmlDataDescriptorSampleGeneratorImpl(
                new LinkedHashMapBeanImpl<>(Collections.singletonMap("BRAND_PRETTY", new BrandXmlEntityHandler()))
        );

        assertTrue(generator.supports(desc));

        final List<Pair<String, byte[]>> templates = generator.generateSample(desc);

        assertNotNull(templates);
        assertEquals(4, templates.size());

        final Pair<String, byte[]> template0 = templates.get(0);
        assertNotNull(template0);
        assertEquals("target_brands_export-yyyy-MM-dd_HH-mm-ss.xml-readme.txt", template0.getFirst());

        final String content0 = new String(template0.getSecond());
        assertEquals("Template: target_brands_export-yyyy-MM-dd_HH-mm-ss.xml\n" +
                "Encoding: UTF-8\n" +
                "Select command: select b from BrandEntity b\n", content0);

        final Pair<String, byte[]> template1 = templates.get(1);
        assertNotNull(template1);
        assertEquals("impex.xsd", template1.getFirst());

        final Pair<String, byte[]> template2 = templates.get(2);
        assertNotNull(template2);
        assertEquals("target_brands_export-yyyy-MM-dd_HH-mm-ss.xml", template2.getFirst());

        final String content2 = new String(template2.getSecond());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<brands>\n" +
                "</brands>", content2);

        final Pair<String, byte[]> template3 = templates.get(3);
        assertNotNull(template3);
        assertEquals("target_brands_export-yyyy-MM-dd_HH-mm-ss.xml-descriptor.xml", template3.getFirst());

    }


    @Test
    public void testGenerateSampleNotSupports() throws Exception {

        final XStreamProvider<XmlExportDescriptor> provider = new XmlExportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/export/xml/brands.xml");
        final XmlExportDescriptor desc = provider.fromXML(inputStream);

        final XmlDataDescriptorSampleGeneratorImpl generator = new XmlDataDescriptorSampleGeneratorImpl(new LinkedHashMapBeanImpl<>(Collections.emptyMap()));

        assertFalse(generator.supports(desc));

    }
}