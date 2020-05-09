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
import org.yes.cart.domain.vo.*;

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
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/export/groups/{lang}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroupInfo> getExportGroups(@PathVariable("lang") String language);

    /**
     * Perform bulk export.
     *
     * @param impExJob export request
     *
     * @return status object token
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/export", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus doExport(@RequestBody VoImpExJob impExJob);

    /**
     * Get latest job status update for given token
     *
     * @param token job token from #doExport
     *
     * @return status object
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
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
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/import/groups/{lang}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroupInfo> getImportGroups(@PathVariable("lang") String language);

    /**
     * Perform bulk import.
     *
     * @param impExJob import request
     *
     * @return status object token
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/import", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus doImport(@RequestBody VoImpExJob impExJob);

    /**
     * Get latest job status update for given token
     *
     * @param token job token from #doImport
     *
     * @return status object
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/import/status", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus getImportStatus(@RequestParam("token") String token);





    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroup/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroup> getAllDataGroups() throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroup/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataGroup getDataGroupById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroup", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataGroup createDataGroup(@RequestBody VoDataGroup voDataGroup)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroup", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataGroup updateDataGroup(@RequestBody VoDataGroup voDataGroup)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroup/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeDataGroup(@PathVariable("id") long id) throws Exception;



    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptor/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataDescriptor> getAllDataDescriptors() throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptor/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataDescriptor getDataDescriptorById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptor", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataDescriptor createDataDescriptor(@RequestBody VoDataDescriptor voDataDescriptor)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptor", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataDescriptor updateDataDescriptor(@RequestBody VoDataDescriptor voDataDescriptor)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptor/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeDataDescriptor(@PathVariable("id") long id) throws Exception;



}
