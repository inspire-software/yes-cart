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

import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.domain.vo.VoDataDescriptor;
import org.yes.cart.domain.vo.VoDataGroup;
import org.yes.cart.service.domain.DataDescriptorService;
import org.yes.cart.service.domain.DataGroupService;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoDataGroupService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 30/03/2019
 * Time: 12:30
 */
public class VoDataGroupServiceImpl implements VoDataGroupService {

    private final DataGroupService dataGroupService;
    private final DataDescriptorService dataDescriptorService;
    private final VoAssemblySupport voAssemblySupport;

    public VoDataGroupServiceImpl(final DataGroupService dataGroupService,
                                  final DataDescriptorService dataDescriptorService,
                                  final VoAssemblySupport voAssemblySupport) {
        this.dataGroupService = dataGroupService;
        this.dataDescriptorService = dataDescriptorService;
        this.voAssemblySupport = voAssemblySupport;
    }

    @Override
    public List<VoDataGroup> getAllDataGroups() throws Exception {
        final List<DataGroup> all = this.dataGroupService.findAll();
        return voAssemblySupport.assembleVos(VoDataGroup.class, DataGroup.class, all);
    }

    @Override
    public VoDataGroup getDataGroupById(final long id) throws Exception {
        final DataGroup group = this.dataGroupService.findById(id);
        if (group != null) {
            return voAssemblySupport.assembleVo(VoDataGroup.class, DataGroup.class, new VoDataGroup(), group);
        }
        return null;
    }

    @Override
    public VoDataGroup createDataGroup(final VoDataGroup vo) throws Exception {

        DataGroup group = this.dataGroupService.getGenericDao().getEntityFactory().getByIface(DataGroup.class);
        group = this.dataGroupService.create(
                voAssemblySupport.assembleDto(DataGroup.class, VoDataGroup.class, group, vo)
        );
        return getDataGroupById(group.getDatagroupId());

    }

    @Override
    public VoDataGroup updateDataGroup(final VoDataGroup vo) throws Exception {

        final DataGroup group = this.dataGroupService.findById(vo.getDatagroupId());
        if (group != null) {
            this.dataGroupService.update(
                    voAssemblySupport.assembleDto(DataGroup.class, VoDataGroup.class, group, vo)
            );
        }
        return getDataGroupById(vo.getDatagroupId());

    }

    @Override
    public void removeDataGroup(final long id) throws Exception {

        final DataGroup group = this.dataGroupService.findById(id);
        if (group != null) {
            this.dataGroupService.delete(group);
        }

    }

    @Override
    public List<VoDataDescriptor> getAllDataDescriptors() throws Exception {
        final List<DataDescriptor> all = this.dataDescriptorService.findAll();
        return voAssemblySupport.assembleVos(VoDataDescriptor.class, DataDescriptor.class, all);
    }

    @Override
    public VoDataDescriptor getDataDescriptorById(final long id) throws Exception {
        final DataDescriptor descriptor = this.dataDescriptorService.findById(id);
        if (descriptor != null) {
            return voAssemblySupport.assembleVo(VoDataDescriptor.class, DataDescriptor.class, new VoDataDescriptor(), descriptor);
        }
        return null;
    }

    @Override
    public VoDataDescriptor createDataDescriptor(final VoDataDescriptor vo) throws Exception {

        DataDescriptor descriptor = this.dataDescriptorService.getGenericDao().getEntityFactory().getByIface(DataDescriptor.class);
        descriptor = this.dataDescriptorService.create(
                voAssemblySupport.assembleDto(DataDescriptor.class, VoDataDescriptor.class, descriptor, vo)
        );
        return getDataDescriptorById(descriptor.getDatadescriptorId());

    }

    @Override
    public VoDataDescriptor updateDataDescriptor(final VoDataDescriptor vo) throws Exception {

        final DataDescriptor descriptor = this.dataDescriptorService.findById(vo.getDatadescriptorId());
        if (descriptor != null) {
            this.dataDescriptorService.update(
                    voAssemblySupport.assembleDto(DataDescriptor.class, VoDataDescriptor.class, descriptor, vo)
            );
        }
        return getDataDescriptorById(vo.getDatadescriptorId());

    }

    @Override
    public void removeDataDescriptor(final long id) throws Exception {

        final DataDescriptor descriptor = this.dataDescriptorService.findById(id);
        if (descriptor != null) {
            this.dataDescriptorService.delete(descriptor);
        }


    }
}
