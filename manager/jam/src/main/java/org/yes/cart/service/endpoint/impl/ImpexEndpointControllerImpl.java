/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.bulkcommon.service.ExportDirectorService;
import org.yes.cart.bulkcommon.service.ImportDirectorService;
import org.yes.cart.domain.vo.VoDataDescriptor;
import org.yes.cart.domain.vo.VoDataGroup;
import org.yes.cart.domain.vo.VoDataGroupInfo;
import org.yes.cart.domain.vo.VoJobStatus;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.endpoint.ImpexEndpointController;
import org.yes.cart.service.vo.VoDataGroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 12:33
 */
@Controller
public class ImpexEndpointControllerImpl implements ImpexEndpointController {

    private final ImportDirectorService importDirectorService;
    private final ExportDirectorService exportDirectorService;
    private final VoDataGroupService voDataGroupService;

    @Autowired
    public ImpexEndpointControllerImpl(final ImportDirectorService importDirectorService,
                                       final ExportDirectorService exportDirectorService,
                                       final VoDataGroupService voDataGroupService) {
        this.importDirectorService = importDirectorService;
        this.exportDirectorService = exportDirectorService;
        this.voDataGroupService = voDataGroupService;
    }

    @Override
    public @ResponseBody
    List<VoDataGroupInfo> getExportGroups(@PathVariable("lang") final String language) {
        return mapToGroups(this.exportDirectorService.getExportGroups(language));
    }

    @Override
    public @ResponseBody
    String doExport(@RequestParam("group") final String descriptorGroup, @RequestBody(required = false) final String fileName) {
        return this.exportDirectorService.doExport(descriptorGroup, fileName, true);
    }

    @Override
    public @ResponseBody
    VoJobStatus getExportStatus(@RequestParam("token")  final String token) {
        return statusToVo(this.exportDirectorService.getExportStatus(token));
    }

    @Override
    public @ResponseBody
    List<VoDataGroupInfo> getImportGroups(@PathVariable("lang") final String language) {
        return mapToGroups(this.importDirectorService.getImportGroups(language));
    }

    @Override
    public @ResponseBody
    String doImport(@RequestParam("group") final String descriptorGroup, @RequestBody(required = false) final String fileName) {
        return this.importDirectorService.doImport(descriptorGroup, fileName, true);
    }

    @Override
    public @ResponseBody
    VoJobStatus getImportStatus(@RequestParam("token") final String token) {
        return statusToVo(this.importDirectorService.getImportStatus(token));
    }


    private List<VoDataGroupInfo> mapToGroups(final List<Map<String, String>> groups) {

        final List<VoDataGroupInfo> vo = new ArrayList<>();
        for (final Map<String, String> group : groups) {
            final VoDataGroupInfo voGroup = new VoDataGroupInfo();
            voGroup.setName(group.get("name"));
            voGroup.setLabel(group.get("label"));
            vo.add(voGroup);
        }

        return vo;
    }

    private VoJobStatus statusToVo(final JobStatus status) {
        final VoJobStatus vo = new VoJobStatus();
        vo.setToken(status.getToken());
        vo.setState(status.getState().name());
        vo.setReport(status.getReport());
        if (status.getCompletion() != null) {
            vo.setCompletion(status.getCompletion().name());
        }
        return vo;
    }

    @Override
    public @ResponseBody List<VoDataGroup> getAllDataGroups() throws Exception {
        return voDataGroupService.getAllDataGroups();
    }

    @Override
    public @ResponseBody VoDataGroup getDataGroupById(@PathVariable("id") final long id) throws Exception {
        return voDataGroupService.getDataGroupById(id);
    }

    @Override
    public @ResponseBody VoDataGroup createDataGroup(@RequestBody final VoDataGroup vo) throws Exception {
        return voDataGroupService.createDataGroup(vo);
    }

    @Override
    public @ResponseBody VoDataGroup updateDataGroup(@RequestBody final VoDataGroup vo) throws Exception {
        return voDataGroupService.updateDataGroup(vo);
    }

    @Override
    public @ResponseBody void removeDataGroup(@PathVariable("id") final long id) throws Exception {
        voDataGroupService.removeDataGroup(id);
    }

    @Override
    public @ResponseBody List<VoDataDescriptor> getAllDataDescriptors() throws Exception {
        return voDataGroupService.getAllDataDescriptors();
    }

    @Override
    public @ResponseBody VoDataDescriptor getDataDescriptorById(@PathVariable("id") final long id) throws Exception {
        return voDataGroupService.getDataDescriptorById(id);
    }

    @Override
    public @ResponseBody VoDataDescriptor createDataDescriptor(@RequestBody final VoDataDescriptor vo) throws Exception {
        return voDataGroupService.createDataDescriptor(vo);
    }

    @Override
    public @ResponseBody VoDataDescriptor updateDataDescriptor(@RequestBody final VoDataDescriptor vo) throws Exception {
        return voDataGroupService.updateDataDescriptor(vo);
    }

    @Override
    public @ResponseBody void removeDataDescriptor(@PathVariable("id") final long id) throws Exception {
        voDataGroupService.removeDataDescriptor(id);
    }
}
