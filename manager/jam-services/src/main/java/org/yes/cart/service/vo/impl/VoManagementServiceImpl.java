package org.yes.cart.service.vo.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.vo.VoLicenseAgreement;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoManagementService;
import org.yes.cart.util.ShopCodeContext;

import java.io.IOException;
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

    private static final String LICENSE_ROLE = "ROLE_LICENSEAGREED";

    private static final String DEFAULT =
            "\n\nLicense is not found. This violates condition of use of this software.\n" +
                    "Stop using this software and contact your software provider immediately.\n\n";

    private String licenseText = DEFAULT;

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


    /** {@inheritDoc} */
    @Override
    public VoLicenseAgreement getMyAgreement() throws Exception {
        final VoLicenseAgreement agreement = new VoLicenseAgreement();
        agreement.setText(this.licenseText);
        agreement.setAgreed(this.isAgreedToLicense());
        return agreement;
    }

    /** {@inheritDoc} */
    @Override
    public VoLicenseAgreement acceptMyAgreement() throws Exception {

        final SecurityContext sc = SecurityContextHolder.getContext();
        final String username = sc != null && sc.getAuthentication() != null ? sc.getAuthentication().getName() : null;
        if (StringUtils.isNotBlank(username)) {
            managementService.grantRole(username, LICENSE_ROLE);
        }
        return getMyAgreement();

    }

    private boolean isAgreedToLicense() {

        if (!DEFAULT.equals(licenseText)) {
            final SecurityContext sc = SecurityContextHolder.getContext();
            final String username = sc != null && sc.getAuthentication() != null ? sc.getAuthentication().getName() : null;
            if (StringUtils.isNotBlank(username)) {
                try {
                    final List<RoleDTO> roles = managementService.getAssignedManagerRoles(username);
                    for (final RoleDTO role : roles) {
                        if (LICENSE_ROLE.equals(role.getCode())) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    ShopCodeContext.getLog(this).error("Unable to retrieve roles for {}", username);
                }
            }
        }

        return false;
    }

    public void setLicenseTextResource(final Resource licenseTextResource) throws IOException {

        licenseText = IOUtils.toString(licenseTextResource.getInputStream(), "UTF-8");

    }


}
