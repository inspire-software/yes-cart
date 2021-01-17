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
package org.yes.cart.service.vo.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.service.DataDescriptorResolver;
import org.yes.cart.bulkcommon.service.DataDescriptorSampleGenerator;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoDataDescriptor;
import org.yes.cart.domain.vo.VoDataDescriptorImpEx;
import org.yes.cart.domain.vo.VoDataGroupImpEx;
import org.yes.cart.service.domain.DataDescriptorService;
import org.yes.cart.service.domain.DataGroupService;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoDataGroupServiceSupport;
import org.yes.cart.utils.impl.ZipByteArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 10:58
 */
public class VoDataGroupServiceSupportImpl implements VoDataGroupServiceSupport {

    private static final Logger LOG = LoggerFactory.getLogger(VoDataGroupServiceImpl.class);

    private final DataGroupService dataGroupService;
    private final DataDescriptorService dataDescriptorService;
    private final DataDescriptorResolver<ImportDescriptor> importDataDescriptorResolver;
    private final DataDescriptorSampleGenerator importDataDescriptorSampleGenerator;
    private final DataDescriptorResolver<ExportDescriptor> exportDataDescriptorResolver;
    private final DataDescriptorSampleGenerator exportDataDescriptorSampleGenerator;
    private final VoAssemblySupport voAssemblySupport;

    public VoDataGroupServiceSupportImpl(final DataGroupService dataGroupService,
                                         final DataDescriptorService dataDescriptorService,
                                         final DataDescriptorResolver<ImportDescriptor> importDataDescriptorResolver,
                                         final DataDescriptorSampleGenerator importDataDescriptorSampleGenerator,
                                         final DataDescriptorResolver<ExportDescriptor> exportDataDescriptorResolver,
                                         final DataDescriptorSampleGenerator exportDataDescriptorSampleGenerator,
                                         final VoAssemblySupport voAssemblySupport) {
        this.dataGroupService = dataGroupService;
        this.dataDescriptorService = dataDescriptorService;
        this.importDataDescriptorResolver = importDataDescriptorResolver;
        this.importDataDescriptorSampleGenerator = importDataDescriptorSampleGenerator;
        this.exportDataDescriptorResolver = exportDataDescriptorResolver;
        this.exportDataDescriptorSampleGenerator = exportDataDescriptorSampleGenerator;
        this.voAssemblySupport = voAssemblySupport;
    }
    @Override
    public List<VoDataGroupImpEx> getDataGroupsByNames(final List<String> names) throws Exception {

        final List<VoDataGroupImpEx> vo = new ArrayList<>();
        final List<DataGroup> all = this.dataGroupService.findAll();
        for (final String name : names) {

            final Optional<DataGroup> grpSearch = all.stream().filter(groupItem -> groupItem.getName().equals(name)).findFirst();
            if (grpSearch.isPresent()) {
                try {
                    final DataGroup grp = grpSearch.get();
                    final VoDataGroupImpEx voGroup = voAssemblySupport.assembleVo(VoDataGroupImpEx.class, DataGroup.class, new VoDataGroupImpEx(), grp);

                    final List<VoDataDescriptorImpEx> descs = new ArrayList<>();
                    voGroup.setImpexDescriptors(descs);

                    final Map<String, ImpExDescriptor> rawDescriptors = loadImpExDescriptorConfigsForGroup(voGroup.getName(), voGroup.getType());

                    final List<VoDataDescriptor> grpDescs = getDataGroupDescriptors(grp.getName());
                    for (final VoDataDescriptor grpDesc : grpDescs) {

                        final VoDataDescriptorImpEx desc = new VoDataDescriptorImpEx();
                        desc.setDatadescriptorId(grpDesc.getDatadescriptorId());
                        desc.setName(grpDesc.getName());
                        desc.setType(grpDesc.getType());
                        desc.setValue(grpDesc.getValue());
                        desc.setCreatedTimestamp(grpDesc.getCreatedTimestamp());
                        desc.setCreatedBy(grpDesc.getCreatedBy());
                        desc.setUpdatedTimestamp(grpDesc.getUpdatedTimestamp());
                        desc.setUpdatedBy(grpDesc.getUpdatedBy());

                        final ImpExDescriptor rawDescriptor = rawDescriptors.get(grpDesc.getName());

                        if (rawDescriptor != null) {
                            desc.setRawDescriptor(rawDescriptor.getSource());
                        }
                        if (DataGroup.TYPE_IMPORT.equals(voGroup.getType())) {
                            final ImportDescriptor rawImportDescriptor = (ImportDescriptor) rawDescriptor;
                            desc.setFileEncoding(rawImportDescriptor.getImportFileDescriptor().getFileEncoding());
                            desc.setFileName(rawImportDescriptor.getImportFileDescriptor().getFileNameMask());
                        } else if (DataGroup.TYPE_EXPORT.equals(voGroup.getType())) {
                            final ExportDescriptor rawExportDescriptor = (ExportDescriptor) rawDescriptor;
                            desc.setFileEncoding(rawExportDescriptor.getExportFileDescriptor().getFileEncoding());
                            desc.setFileName(rawExportDescriptor.getExportFileDescriptor().getFileName());
                        }

                        descs.add(desc);

                    }

                    vo.add(voGroup);
                } catch (Exception exp) {
                    LOG.error("Unable to load descriptors for impex group: " + name, exp);
                }
            }
        }

        return vo;

    }

    @Override
    public List<VoDataDescriptor> getDataGroupDescriptors(final String name) throws Exception {

        final DataGroup dg = this.dataGroupService.findByName(name);

        final List<DataDescriptor> all = new ArrayList<>();
        if (dg != null && StringUtils.isNotBlank(dg.getDescriptors())) {
            final String[] descriptors = StringUtils.split(dg.getDescriptors(), ',');
            if (descriptors != null) {

                for (final String descriptor : descriptors) {

                    if (StringUtils.isNotBlank(descriptor)) {

                        final DataDescriptor dataDescriptor = this.dataDescriptorService.findByName(descriptor.trim());

                        if (dataDescriptor != null) {
                            all.add(dataDescriptor);
                        }

                    }

                }

            }
        }

        return voAssemblySupport.assembleVos(VoDataDescriptor.class, DataDescriptor.class, all);
    }


    private static final Pair<String, byte[]> NO_TEMPLATE = new Pair<>("not_available.txt", "descriptor template is not available".getBytes(StandardCharsets.UTF_8));

    @Override
    public Pair<String, byte[]> generateDataGroupDescriptorTemplates(final long id) throws Exception {

        final List<Pair<String, byte[]>> templates = new ArrayList<>();
        final DataGroup group = this.dataGroupService.findById(id);
        if (group != null) {

            final Map<String, ImpExDescriptor> descriptors = loadImpExDescriptorConfigsForGroup(group.getName(), group.getType());

            final DataDescriptorSampleGenerator generator = DataGroup.TYPE_IMPORT.equals(group.getType()) ?
                    this.importDataDescriptorSampleGenerator : this.exportDataDescriptorSampleGenerator;

            final Set<String> includedFiles = new HashSet<>();

            for (final Map.Entry<String, ImpExDescriptor> descriptorEntry : descriptors.entrySet()) {

                if (generator.supports(descriptorEntry.getValue())) {

                    generator.generateSample(descriptorEntry.getValue()).forEach(template -> {
                        if (!includedFiles.contains(template.getFirst())) {
                            templates.add(template);
                            includedFiles.add(template.getFirst());
                        }
                    });

                } else {

                    final String cleanName = descriptorEntry.getKey().replace("/", "_").replace("\\", "_");
                    templates.add(new Pair<>(cleanName + "-" + NO_TEMPLATE.getFirst(), NO_TEMPLATE.getSecond()));

                }

            }

        }

        if (templates.isEmpty()) {
            templates.add(NO_TEMPLATE);
        }

        final byte[] zip = ZipByteArrayUtils.bytesToZipBytes(templates);
        return new Pair<>("templates_impex_" + id + ".zip", zip);
    }

    private Map<String, ImpExDescriptor> loadImpExDescriptorConfigsForGroup(final String group, final String mode) {
        final Map<String, ImpExDescriptor> rawDescriptors;
        if (DataGroup.TYPE_IMPORT.equals(mode)) {
            rawDescriptors = (Map) this.importDataDescriptorResolver.getByGroup(group);
        } else if (DataGroup.TYPE_EXPORT.equals(mode)) {
            rawDescriptors = (Map) this.exportDataDescriptorResolver.getByGroup(group);
        } else {
            rawDescriptors = new HashMap<>();
        }
        return rawDescriptors;
    }

}
