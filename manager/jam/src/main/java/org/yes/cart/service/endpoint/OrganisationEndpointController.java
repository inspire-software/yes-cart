package org.yes.cart.service.endpoint;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
