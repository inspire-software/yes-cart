package org.yes.cart.service.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: Denis Lozenko
 * Date 8/22/2016.
 */
@Controller
@Api(value = "Organisation", description = "Organisation controller", tags = "organisation")
@RequestMapping("/organisations")
public interface OrganisationEndpointController {

    @ApiOperation(value = "Managers search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/managers/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoManagerInfo> getFilteredManagers(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'email', 'firstname' and 'lastname'" +
                    "\n* shopIds - optional injected automatically according to current user access", name = "filter")
                                                      @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Create manager")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/managers", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoManager createManager(@ApiParam(value = "Manager", name = "vo", required = true) @RequestBody VoManager vo)  throws Exception;

    @ApiOperation(value = "Retrieve manager")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/managers/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoManager getManagerById(@ApiParam(value = "Manager ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Update manager")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/managers", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoManager updateManager(@ApiParam(value = "Manager", name = "vo", required = true) @RequestBody VoManager vo)  throws Exception;

    @ApiOperation(value = "Delete manager")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/managers/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeManager(@ApiParam(value = "Manager ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Reset manager password. Triggers email to manager with temporary password that they can use to login once.")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/managers/{id}/reset", method = RequestMethod.POST)
    @ResponseBody
    void resetPassword(@ApiParam(value = "Manager ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Manager account status")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/managers/{id}/account", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDisabledFlag(@ApiParam(value = "Manager ID", required = true) @PathVariable("id") long id, @ApiParam(value = "Account status", name = "vo", required = true) @RequestBody VoManagerAccountStatus disabled) throws Exception;

    @ApiOperation(value = "Retrieve all roles")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/roles", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoRole> getAllRoles() throws Exception;

    @ApiOperation(value = "Create role")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/roles", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoRole createRole(@ApiParam(value = "Role", name = "vo", required = true) @RequestBody final VoRole voRole) throws Exception;

    @ApiOperation(value = "Update role")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/roles", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoRole updateRole(@ApiParam(value = "Role", name = "vo", required = true) @RequestBody final VoRole voRole) throws Exception;

    @ApiOperation(value = "Delete role")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/roles/{code}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeRole(@ApiParam(value = "Role code", required = true) @PathVariable("code") String code) throws Exception;
}
