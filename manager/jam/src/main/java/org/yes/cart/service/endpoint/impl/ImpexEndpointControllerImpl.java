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
import org.yes.cart.domain.vo.VoDataGroupInfo;
import org.yes.cart.domain.vo.VoJobStatus;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.endpoint.ImpexEndpointController;

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

    @Autowired
    public ImpexEndpointControllerImpl(final ImportDirectorService importDirectorService,
                                       final ExportDirectorService exportDirectorService) {
        this.importDirectorService = importDirectorService;
        this.exportDirectorService = exportDirectorService;
    }

    public @ResponseBody
    List<VoDataGroupInfo> getExportGroups(@PathVariable("lang") final String language) {
        return mapToGroups(this.exportDirectorService.getExportGroups(language));
    }

    public @ResponseBody
    String doExport(@PathVariable("group") final String descriptorGroup, @RequestBody final String fileName) {
        return this.exportDirectorService.doExport(descriptorGroup, fileName, true);
    }

    public @ResponseBody
    VoJobStatus getExportStatus(@RequestParam("token")  final String token) {
        return statusToVo(this.exportDirectorService.getExportStatus(token));
    }

    public @ResponseBody
    List<VoDataGroupInfo> getImportGroups(@PathVariable("lang") final String language) {
        return mapToGroups(this.importDirectorService.getImportGroups(language));
    }

    public @ResponseBody
    String doImport(@PathVariable("group") final String descriptorGroup, @RequestBody final String fileName) {
        return this.importDirectorService.doImport(descriptorGroup, fileName, true);
    }

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

}
