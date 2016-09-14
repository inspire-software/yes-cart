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
package org.yes.cart.service.endpoint;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.vo.VoDataGroupInfo;
import org.yes.cart.domain.vo.VoJobStatus;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 08:41
 */
@Controller
@RequestMapping("/impex")
public interface ImpexEndpointController {

    /**
     * List of import groups. Each group is represented by a map and contains the following
     * keys: "name", "label".
     *
     * @param language language
     *
     * @return configured import groups
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/export/groups/{lang}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroupInfo> getExportGroups(@PathVariable("lang") String language);

    /**
     * Perform bulk export.
     * @param descriptorGroup descriptor group marker
     * @param fileName optional override full filename to export
     * @return status object token
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/export/{group}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE }, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    String doExport(@PathVariable("group") String descriptorGroup, @RequestBody String fileName);

    /**
     * Get latest job status update for given token
     * @param token job token from #doExport
     * @return status object
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/export/status", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus getExportStatus(@RequestParam("token") String token);


    /**
     * List of import groups. Each group is represented by a map and contains the following
     * keys: "name", "label".
     *
     * @param language language
     *
     * @return configured import groups
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/import/groups/{lang}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroupInfo> getImportGroups(@PathVariable("lang") String language);

    /**
     * Perform bulk import.
     * @param descriptorGroup descriptor group marker
     * @param fileName optional full filename to import
     * @return status object token
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/import/{group}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE }, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    String doImport(@PathVariable("group") String descriptorGroup, @RequestBody String fileName);

    /**
     * Get latest job status update for given token
     * @param token job token from #doImport
     * @return status object
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/import/status", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus getImportStatus(@RequestParam("token") String token);


}
