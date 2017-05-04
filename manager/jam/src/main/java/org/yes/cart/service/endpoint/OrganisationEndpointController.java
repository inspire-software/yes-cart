package org.yes.cart.service.endpoint;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerInfo;
import org.yes.cart.domain.vo.VoRole;

import java.util.List;

/**
 * User: Denis Lozenko
 * Date 8/22/2016.
 */
@Controller
@RequestMapping("/organisation")
public interface OrganisationEndpointController {

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/managers/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoManagerInfo> getMamagers() throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/manager", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoManager createManager(@RequestBody VoManager vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/manager/{email}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoManager getManagerByEmail(@PathVariable("email") String email) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/manager", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoManager updateManager(@RequestBody VoManager vo)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/manager/{email}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeManager(@PathVariable("email") String email) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/manager/reset/{email}", method = RequestMethod.POST)
    @ResponseBody
    void resetPassword(@PathVariable("email") String email) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/manager/offline/{manager}/{state}", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDisabledFlag(@PathVariable("manager") String manager, @PathVariable("state") boolean disabled) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/role/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoRole> getAllRoles() throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/role", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoRole createRole(@RequestBody final VoRole voRole) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/role", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoRole updateRole(@RequestBody final VoRole voRole) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/role/{code}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeRole(@PathVariable("code") String code) throws Exception;
}
