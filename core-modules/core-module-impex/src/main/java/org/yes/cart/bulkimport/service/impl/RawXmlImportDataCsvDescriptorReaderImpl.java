package org.yes.cart.bulkimport.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.service.DataDescriptorReader;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;
import org.yes.cart.utils.log.Markers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * User: denispavlov
 * Date: 20/10/2018
 * Time: 11:31
 */
public class RawXmlImportDataCsvDescriptorReaderImpl implements DataDescriptorReader<ImportDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(RawXmlImportDataCsvDescriptorReaderImpl.class);
    
    private XStreamProvider<ImportDescriptor> importDescriptorXStreamProvider;

    /**
     * {@inheritDoc}
     */
    public boolean supports(final DataDescriptor dataDescriptor) {
        return dataDescriptor != null &&
                (DataDescriptor.TYPE_RAWINF_XML.equals(dataDescriptor.getType())
                        || DataDescriptor.TYPE_RAW_XML_CSV.equals(dataDescriptor.getType()));
    }

    /**
     * {@inheritDoc}
     */
    public ImportDescriptor toDescriptorObject(final DataDescriptor dataDescriptor) {
        if (supports(dataDescriptor)) {

            if (DataDescriptor.TYPE_RAWINF_XML.equals(dataDescriptor.getType())) {
                LOG.warn(Markers.alert(), "Descriptor {} uses deprecated namespace", dataDescriptor.getDatadescriptorId());
            }

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
