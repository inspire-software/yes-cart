package org.yes.cart.bulkimport.service.impl;

import org.yes.cart.bulkcommon.service.DataDescriptorReader;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * User: denispavlov
 * Date: 20/10/2018
 * Time: 11:31
 */
public class RawXmlImportDataXmlDescriptorReaderImpl implements DataDescriptorReader<ImportDescriptor> {

    private XStreamProvider<ImportDescriptor> importDescriptorXStreamProvider;

    /**
     * {@inheritDoc}
     */
    public boolean supports(final DataDescriptor dataDescriptor) {
        return dataDescriptor != null &&
                DataDescriptor.TYPE_RAW_XML_XML.equals(dataDescriptor.getType());
    }

    /**
     * {@inheritDoc}
     */
    public ImportDescriptor toDescriptorObject(final DataDescriptor dataDescriptor) {
        if (supports(dataDescriptor)) {

            return getImportDescriptorFromXML(new ByteArrayInputStream(dataDescriptor.getValue().getBytes(StandardCharsets.UTF_8)));

        }
        return null;
    }



    /**
     * IoC. XStream provider for import descriptor files.
     *
     * @param importDescriptorXStreamProvider xStream provider
     */
    public void setImportDescriptorXStreamProvider(final XStreamProvider importDescriptorXStreamProvider) {
        this.importDescriptorXStreamProvider = importDescriptorXStreamProvider;
    }

    protected ImportDescriptor getImportDescriptorFromXML(InputStream is) {
        return importDescriptorXStreamProvider.fromXML(is);
    }

}
