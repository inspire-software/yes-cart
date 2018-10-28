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

package org.yes.cart.bulkexport.xml.impl;

import org.junit.Test;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 20/10/2018
 * Time: 13:03
 */
public class XmlExportDescriptorXStreamProviderTest {

    @Test
    public void testProvide() throws Exception {
        final XStreamProvider<XmlExportDescriptor> provider = new XmlExportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/export/xml/schematest001.xml");
        final XmlExportDescriptor desc = provider.fromXML(inputStream);

        assertNotNull(desc);

        assertNotNull(desc.getContext());
        assertEquals("SHOP10", desc.getContext().getShopCode());
        assertEquals("XML", desc.getContext().getImpExService());

        assertEquals("org.yes.cart.domain.entity.Attribute", desc.getEntityType());
        assertNotNull(desc.getExportFileDescriptor());

        assertEquals("UTF-8", desc.getExportFileDescriptor().getFileEncoding());
        assertEquals("attributenames.csv", desc.getExportFileDescriptor().getFileName());

        assertEquals("select b from AttributeEntity b where b.code = {code}", desc.getSelectSql());

        assertEquals("exportXmlAttributeHandler", desc.getXmlHandler());

    }


}