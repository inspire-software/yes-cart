package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoRole;
import org.yes.cart.service.endpoint.OrganisationEndpointController;
import org.yes.cart.service.vo.VoRoleService;

import java.util.List;

/**
 * User: Denis Lozenko
 * Date 8/22/2016.
 */
@Component
public class OrganisationEndpointControllerImpl implements OrganisationEndpointController {

    private final VoRoleService voRoleService;

    @Autowired
    public OrganisationEndpointControllerImpl(VoRoleService voRoleService) {
        this.voRoleService = voRoleService;
    }

    @Override
    public @ResponseBody List<VoRole> getAllRoles() throws Exception {
        return voRoleService.getAllRoles();
    }

    @Override
    public @ResponseBody VoRole createRole(@RequestBody final VoRole voRole) throws Exception {
        return voRoleService.createRole(voRole);
    }

    @Override
    public @ResponseBody VoRole updateRole(@RequestBody final VoRole voRole) throws Exception {
        return voRoleService.updateRole(voRole);
    }

    @Override
    public @ResponseBody void removeRole(@PathVariable("code") String code) throws Exception {
        voRoleService.removeRole(code);
    }
}
