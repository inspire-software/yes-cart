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
package org.yes.cart.service.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 08:41
 */
@Controller
@Api(value = "ImpEx", description = "Import/Export controller", tags = "impex")
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
    @ApiOperation(value = "Retrieve available export data groups")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/export/groups", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroupImpEx> getExportGroups(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String language) throws Exception;

    /**
     * Perform bulk export.
     *
     * @param impExJob export request
     *
     * @return status object token
     */
    @ApiOperation(value = "Perform export job")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/export", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus doExport(@ApiParam(value = "Export request", name = "impExJob", required = true) @RequestBody VoImpExJob impExJob);

    /**
     * Get latest job status update for given token
     *
     * @param token job token from #doExport
     *
     * @return status object
     */
    @ApiOperation(value = "Check export job status")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/export/status", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus getExportStatus(@ApiParam(value = "Export job token", required = true) @RequestParam("token") String token);


    /**
     * List of import groups. Each group is represented by a map and contains the following
     * keys: "name", "label".
     *
     * @param language language
     *
     * @return configured import groups
     */
    @ApiOperation(value = "Retrieve available import data groups")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/import/groups", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroupImpEx> getImportGroups(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String language) throws Exception;

    /**
     * Perform bulk import.
     *
     * @param impExJob import request
     *
     * @return status object token
     */
    @ApiOperation(value = "Perform import job")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/import", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus doImport(@ApiParam(value = "Import request", name = "impExJob", required = true) @RequestBody VoImpExJob impExJob);

    /**
     * Get latest job status update for given token
     *
     * @param token job token from #doImport
     *
     * @return status object
     */
    @ApiOperation(value = "Check import job status")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/import/status", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus getImportStatus(@ApiParam(value = "Import job token", required = true) @RequestParam("token") String token);





    @ApiOperation(value = "Retrieve data groups")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroups", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataGroup> getAllDataGroups() throws Exception;

    @ApiOperation(value = "Retrieve data group")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroups/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataGroup getDataGroupById(@ApiParam(value = "Data group ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create data group")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroups", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataGroup createDataGroup(@ApiParam(value = "Data group", name = "vo", required = true) @RequestBody VoDataGroup voDataGroup)  throws Exception;

    @ApiOperation(value = "Update data group")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroups", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataGroup updateDataGroup(@ApiParam(value = "Data group", name = "vo", required = true) @RequestBody VoDataGroup voDataGroup)  throws Exception;

    @ApiOperation(value = "Delete data group")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datagroups/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeDataGroup(@ApiParam(value = "Data group ID", required = true) @PathVariable("id") long id) throws Exception;



    @ApiOperation(value = "Retrieve data descriptors")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptors", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoDataDescriptor> getAllDataDescriptors() throws Exception;

    @ApiOperation(value = "Retrieve data descriptor")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptors/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataDescriptor getDataDescriptorById(@ApiParam(value = "Data descriptor ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create data descriptor")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptors", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataDescriptor createDataDescriptor(@ApiParam(value = "Data descriptor", name = "vo", required = true) @RequestBody VoDataDescriptor voDataDescriptor)  throws Exception;

    @ApiOperation(value = "Update data descriptor")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptors", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoDataDescriptor updateDataDescriptor(@ApiParam(value = "Data descriptor", name = "vo", required = true) @RequestBody VoDataDescriptor voDataDescriptor)  throws Exception;

    @ApiOperation(value = "Delete data descriptor")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/datadescriptors/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeDataDescriptor(@ApiParam(value = "Data descriptor ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Download specified file")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/datagroups/{id}/templates", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    void downloadDataGroupDescriptorTemplates(@ApiParam(value = "Data group ID", required = true) @PathVariable("id") long id, HttpServletResponse response) throws Exception;


}
