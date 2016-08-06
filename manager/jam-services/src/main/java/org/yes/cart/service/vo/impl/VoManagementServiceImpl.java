package org.yes.cart.service.vo.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoManagementService;
import org.yes.cart.util.ShopCodeContext;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 09:00
 */
public class VoManagementServiceImpl implements VoManagementService {

    private final ManagementService managementService;
    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoManagementServiceImpl(final ManagementService managementService,
                                   final FederationFacade federationFacade,
                                   final VoAssemblySupport voAssemblySupport) {
        this.managementService = managementService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    @Override
    public VoManager getMyself() throws Exception {
        final ManagerDTO me = getMyselfInternal();
        if (me != null) {
            return voAssemblySupport.assembleVo(VoManager.class, ManagerDTO.class, new VoManager(), me);
        }
        return null;
    }

    private ManagerDTO getMyselfInternal() {

        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        final String currentManager = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            final List<ManagerDTO> managers = this.managementService.getManagers(currentManager, null, null);
            return managers.isEmpty() ? null : managers.get(0);
        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error(exp.getMessage(), exp);
            return null;
        }

    }


}
