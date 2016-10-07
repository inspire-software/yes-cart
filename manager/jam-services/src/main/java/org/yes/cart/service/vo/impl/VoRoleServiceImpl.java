package org.yes.cart.service.vo.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.vo.VoRole;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoRoleService;

import java.util.Iterator;
import java.util.List;

/**
 * User: Denis Lozenko
 * Date 8/22/2016.
 */
public class VoRoleServiceImpl implements VoRoleService {

    private final ManagementService managementService;
    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoRoleServiceImpl(final ManagementService managementService,
                             final FederationFacade federationFacade,
                             final VoAssemblySupport voAssemblySupport) {
        this.managementService = managementService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    @Override
    public List<VoRole> getAllRoles() throws Exception {
        final List<RoleDTO> all = managementService.getRolesList();
        if (!federationFacade.isCurrentUserSystemAdmin()) {
            final Iterator<RoleDTO> allIt = all.iterator();
            while (allIt.hasNext()) {
                // do not show system admin role to regular managers
                if ("ROLE_SMADMIN".equals(allIt.next().getCode())) {
                    allIt.remove();
                    break;
                }
            }
        }
        return voAssemblySupport.assembleVos(VoRole.class, RoleDTO.class, all);
    }

    /** {@inheritDoc} */
    @Override
    public VoRole createRole(VoRole vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            managementService.addRole(vo.getCode(), vo.getDescription());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getVoRole(vo);
    }

    /** {@inheritDoc} */
    @Override
    public VoRole updateRole(VoRole vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            managementService.updateRole(vo.getCode(), vo.getDescription());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getVoRole(vo);
    }

    /** {@inheritDoc} */
    @Override
    public void removeRole(String role) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            managementService.deleteRole(role);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private VoRole getVoRole(VoRole vo) throws Exception {
        final List<VoRole> all = getAllRoles();
        for (final VoRole role : all) {
            if (role.getCode().equals(vo.getCode())) {
                return role;
            }
        }
        return null;
    }
}
