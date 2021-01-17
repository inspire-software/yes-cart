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

package org.yes.cart.bulkexport.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.service.DataDescriptorReader;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;
import org.yes.cart.utils.log.Markers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * User: denispavlov
 * Date: 20/10/2018
 * Time: 11:27
 */
public class RawXmlExportDataCsvDescriptorReaderImpl implements DataDescriptorReader<ExportDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(RawXmlExportDataCsvDescriptorReaderImpl.class);

    private XStreamProvider<ExportDescriptor> importDescriptorXStreamProvider;


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
    public ExportDescriptor toDescriptorObject(final DataDescriptor dataDescriptor) {
        if (supports(dataDescriptor)) {

            if (DataDescriptor.TYPE_WEBINF_XML.equals(dataDescriptor.getType())) {
                LOG.warn(Markers.alert(), "Descriptor {} uses deprecated namespace", dataDescriptor.getDatadescriptorId());
            }

            final ExportDescriptor descriptor = getExportDescriptorFromXML(new ByteArrayInputStream(dataDescriptor.getValue().getBytes(StandardCharsets.UTF_8)));
            descriptor.setSource(dataDescriptor.getValue());
            return descriptor;

        }
        return null;
    }



    /**
     * IoC. XStream provider for import descriptor files.
     *
     * @param importDescriptorXStreamProvider xStream provider
     */
    public void setExportDescriptorXStreamProvider(final XStreamProvider importDescriptorXStreamProvider) {
        this.importDescriptorXStreamProvider = importDescriptorXStreamProvider;
    }

    protected ExportDescriptor getExportDescriptorFromXML(InputStream is) {
        return importDescriptorXStreamProvider.fromXML(is);
    }

}
