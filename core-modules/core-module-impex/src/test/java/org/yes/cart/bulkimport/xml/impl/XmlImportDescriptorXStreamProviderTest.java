package org.yes.cart.bulkimport.xml.impl;

import org.junit.Test;
import org.yes.cart.bulkimport.xml.XmlImportDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 04/11/2018
 * Time: 18:31
 */
public class XmlImportDescriptorXStreamProviderTest {

    @Test
    public void testProvide() throws Exception {

        testProvide("src/test/resources/import/xml/schematest001.xml");

    }

    public void testProvide(final String filename) throws Exception {
        final XStreamProvider<XmlImportDescriptor> provider = new XmlImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream(filename);
        final XmlImportDescriptor desc = provider.fromXML(inputStream);

        assertNotNull(desc);

        assertNotNull(desc.getContext());
        assertEquals("SHOP10", desc.getContext().getShopCode());
        assertEquals("XML", desc.getContext().getImpExService());

        assertEquals("org.yes.cart.domain.entity.Attribute", desc.getEntityType());
        assertNull(desc.getImportDirectory());
        assertNotNull(desc.getImportFileDescriptor());

        assertEquals("UTF-8", desc.getImportFileDescriptor().getFileEncoding());
        assertEquals("attributenames.xml", desc.getImportFileDescriptor().getFileNameMask());

        assertEquals("ATTRIBUTE", desc.getXmlHandler());

    }


}