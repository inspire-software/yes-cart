package org.yes.cart.service.vo.impl;

import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;
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

    public VoManagementServiceImpl(final ManagementService managementService,
                                   final FederationFacade federationFacade) {
        this.managementService = managementService;
        this.federationFacade = federationFacade;
    }

    /** {@inheritDoc} */
    @Override
    public VoManager getMyself() throws Exception {
        final ManagerDTO me = getMyselfInternal();
        final VoManager meVo = new VoManager();
        if (me != null) {
            DTOAssembler.newAssembler(VoManager.class, ManagerDTO.class).assembleDto(meVo, me, null, null);
        }
        return meVo;
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
