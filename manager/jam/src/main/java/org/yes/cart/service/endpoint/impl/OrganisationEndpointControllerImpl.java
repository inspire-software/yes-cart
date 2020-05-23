package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.OrganisationEndpointController;
import org.yes.cart.service.vo.VoManagementService;
import org.yes.cart.service.vo.VoRoleService;

import java.util.List;

/**
 * User: Denis Lozenko
 * Date 8/22/2016.
 */
@Component
public class OrganisationEndpointControllerImpl implements OrganisationEndpointController {

    private final VoRoleService voRoleService;
    private final VoManagementService voManagementService;

    @Autowired
    public OrganisationEndpointControllerImpl(final VoRoleService voRoleService,
                                              final VoManagementService voManagementService) {
        this.voRoleService = voRoleService;
        this.voManagementService = voManagementService;
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoManagerInfo> getFilteredManagers(@RequestBody final VoSearchContext filter) throws Exception {
        return voManagementService.getFilteredManagers(filter);
    }

    @Override
    public @ResponseBody
    VoManager getManagerById(@PathVariable("id") final long id) throws Exception {
        return voManagementService.getManagerById(id);
    }

    @Override
    public @ResponseBody
    VoManager createManager(@RequestBody final VoManager vo) throws Exception {
        return voManagementService.createManager(vo);
    }

    @Override
    public @ResponseBody
    VoManager updateManager(@RequestBody final VoManager vo) throws Exception {
        return voManagementService.updateManager(vo);
    }

    @Override
    public @ResponseBody
    void removeManager(@PathVariable("id") final long id) throws Exception {
        voManagementService.deleteManager(id);
    }

    @Override
    public @ResponseBody
    void resetPassword(@PathVariable("id") final long id) throws Exception {
        voManagementService.resetPassword(id);
    }

    @Override
    public @ResponseBody
    void updateDisabledFlag(@PathVariable("id") final long id, @RequestBody VoManagerAccountStatus account) throws Exception {
        voManagementService.updateDisabledFlag(id, account.isDisabled());
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
