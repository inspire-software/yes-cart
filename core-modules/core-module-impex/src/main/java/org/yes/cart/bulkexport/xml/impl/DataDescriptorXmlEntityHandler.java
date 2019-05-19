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

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class DataDescriptorXmlEntityHandler extends AbstractXmlEntityHandler<DataDescriptor> {

    public DataDescriptorXmlEntityHandler() {
        super("data-descriptors");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, DataDescriptor> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagDataDescriptor(null, tuple.getData()), writer, entityCount);

    }

    Tag tagDataDescriptor(final Tag parent, final DataDescriptor descriptor) {

        return tag(parent, "data-descriptor")
                .attr("id", descriptor.getDatadescriptorId())
                .attr("name", descriptor.getName())
                .attr("type", descriptor.getType())
                    .tagCdata("value", descriptor.getValue())
                    .tagTime(descriptor)
                .end();

    }

}
