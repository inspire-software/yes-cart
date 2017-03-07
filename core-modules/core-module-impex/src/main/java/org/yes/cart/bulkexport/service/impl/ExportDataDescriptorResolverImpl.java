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

package org.yes.cart.bulkexport.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.service.DataDescriptorResolver;
import org.yes.cart.bulkcommon.service.DataDescriptorTuplizer;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.service.domain.DataDescriptorService;
import org.yes.cart.service.domain.DataGroupService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 02/06/2015
 * Time: 13:58
 */
public class ExportDataDescriptorResolverImpl implements DataDescriptorResolver<ExportDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(ExportDataDescriptorResolverImpl.class);

    private final DataGroupService dataGroupService;
    private final DataDescriptorService dataDescriptorService;

    private final List<DataDescriptorTuplizer<ExportDescriptor>> tuplizers = new ArrayList<DataDescriptorTuplizer<ExportDescriptor>>();

    public ExportDataDescriptorResolverImpl(final DataGroupService dataGroupService,
                                            final DataDescriptorService dataDescriptorService) {
        this.dataGroupService = dataGroupService;
        this.dataDescriptorService = dataDescriptorService;
    }

    /**
     * {@inheritDoc}
     */
    public ExportDescriptor getByName(final String name) {

        final DataDescriptor dataDescriptor = dataDescriptorService.findByName(name);

        if (dataDescriptor != null) {
            for (final DataDescriptorTuplizer<ExportDescriptor> tuplizer : this.tuplizers) {

                if (tuplizer.supports(dataDescriptor)) {

                    return tuplizer.toDescriptorObject(dataDescriptor);

                }

            }

            throw new RuntimeException("Unable to resolve descriptor for " + dataDescriptor.getName() + " of type " + dataDescriptor.getType());
        }

        throw new RuntimeException("Descriptor with name " + name + " cannot be resolved");
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ExportDescriptor> getByGroup(final String group) {

        final DataGroup dataGroup = dataGroupService.findByName(group);
        if (dataGroup != null) {

            if (DataGroup.TYPE_EXPORT.equals(dataGroup.getType())) {

                final Map<String, ExportDescriptor> out = new LinkedHashMap<String, ExportDescriptor>();

                final String descriptors = dataGroup.getDescriptors();
                if (StringUtils.isNotBlank(descriptors)) {

                    for (final String descriptor : StringUtils.split(descriptors, ',')) {

                        final String name = descriptor.trim();
                        out.put(name, getByName(name));
                    }

                }

                return out;
            }

            throw new RuntimeException("Group with name " + group + " is not of type: " + DataGroup.TYPE_EXPORT);

        }

        throw new RuntimeException("Group with name " + group + " cannot be resolved");
    }

    /**
     * {@inheritDoc}
     */
    public List<DataGroup> getGroups() {
        return dataGroupService.findByType(DataGroup.TYPE_EXPORT);
    }

    /**
     * {@inheritDoc}
     */
    public void register(final DataDescriptorTuplizer<ExportDescriptor> exportDescriptorDataDescriptorTuplizer) {
        for (final DataDescriptorTuplizer<ExportDescriptor> tuplizer : this.tuplizers) {
            if (exportDescriptorDataDescriptorTuplizer.equals(tuplizer)) {
                return; // already registered
            }
        }

        this.tuplizers.add(exportDescriptorDataDescriptorTuplizer);

        LOG.info("Registered data export descriptor tuplizer: {}", exportDescriptorDataDescriptorTuplizer);
    }
}
