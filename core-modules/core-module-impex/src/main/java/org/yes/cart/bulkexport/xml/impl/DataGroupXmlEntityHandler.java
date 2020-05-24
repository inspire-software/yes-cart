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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.DataDescriptorService;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class DataGroupXmlEntityHandler extends AbstractXmlEntityHandler<DataGroup> {

    private final DataDescriptorXmlEntityHandler dataDescriptorHandler = new DataDescriptorXmlEntityHandler();

    private DataDescriptorService dataDescriptorService;

    public DataGroupXmlEntityHandler() {
        super("data-groups");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, DataGroup> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagDataGroup(null, tuple.getData()), writer, statusListener);

    }

    Tag tagDataGroup(final Tag parent, final DataGroup group) {

        final Tag groupTag = tag(parent, "data-group")
                .attr("id", group.getDatagroupId())
                .attr("qualifier", group.getQualifier())
                .attr("name", group.getName())
                .attr("type", group.getType())
                    .tagI18n("display-name", group.getDisplayName());

            if (StringUtils.isNotBlank(group.getDescriptors())) {

                final String[] descriptors = StringUtils.split(group.getDescriptors(), ',');
                if (descriptors != null) {

                    final Tag descTag = groupTag.tag("descriptors");

                    for (final String descriptor : descriptors) {

                        if (StringUtils.isNotBlank(descriptor)) {

                            final DataDescriptor dataDescriptor = this.dataDescriptorService.findByName(descriptor.trim());

                            if (dataDescriptor != null) {
                                dataDescriptorHandler.tagDataDescriptor(descTag, dataDescriptor);
                            }

                        }

                    }

                    descTag.end();

                }

            }

        return groupTag
                    .tagTime(group)
                .end();

    }

    /**
     * Spring IoC.
     *
     * @param dataDescriptorService data descriptor service
     */
    public void setDataDescriptorService(final DataDescriptorService dataDescriptorService) {
        this.dataDescriptorService = dataDescriptorService;
    }

    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        this.dataDescriptorHandler.setPrettyPrint(prettyPrint);
        super.setPrettyPrint(prettyPrint);
    }
}
