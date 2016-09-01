package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.vo.*;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoManagementService;
import org.yes.cart.util.ShopCodeContext;

import java.io.IOException;
import java.util.*;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 09:00
 */
public class VoManagementServiceImpl implements VoManagementService {

    private final ManagementService managementService;
    private final DtoShopService shopService;
    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    private static final String LICENSE_ROLE = "ROLE_LICENSEAGREED";

    private static final String DEFAULT =
            "\n\nLicense is not found. This violates condition of use of this software.\n" +
                    "Stop using this software and contact your software provider immediately.\n\n";

    private String licenseText = DEFAULT;

    public VoManagementServiceImpl(final ManagementService managementService,
                                   final DtoShopService shopService,
                                   final FederationFacade federationFacade,
                                   final VoAssemblySupport voAssemblySupport) {
        this.managementService = managementService;
        this.shopService = shopService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    @Override
    public VoManagerInfo getMyself() throws Exception {
        final ManagerDTO me = getMyselfInternal();
        if (me != null) {
            return voAssemblySupport.assembleVo(VoManagerInfo.class, ManagerDTO.class, new VoManagerInfo(), me);
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

    /** {@inheritDoc} */
    @Override
    public List<VoManagerInfo> getManagers() throws Exception {
        final List<ManagerDTO> all = managementService.getManagers(null, null, null);
        federationFacade.applyFederationFilter(all, ManagerDTO.class);
        return voAssemblySupport.assembleVos(VoManagerInfo.class, ManagerDTO.class, all);
    }

    /** {@inheritDoc} */
    @Override
    public VoManager getByEmail(String email) throws Exception {
        if (federationFacade.isManageable(email, ManagerDTO.class)) {
            final List<ManagerDTO> all = managementService.getManagers(email, null, null);
            if (CollectionUtils.isNotEmpty(all)) {
                final ManagerDTO managerDTO = all.get(0);
                final VoManager voManager = voAssemblySupport.assembleVo(
                        VoManager.class, ManagerDTO.class, new VoManager(), managerDTO);
                final List<VoManagerShop> voManagerShops = new ArrayList<>();
                for (final ShopDTO shop : managementService.getAssignedManagerShops(voManager.getEmail())) {
                    final VoManagerShop link = new VoManagerShop();
                    link.setManagerId(voManager.getManagerId());
                    link.setShopId(shop.getShopId());
                    voManagerShops.add(link);
                }
                voManager.setManagerShops(voManagerShops);

                final List<VoManagerRole> voManagerRoles = new ArrayList<>();
                for (final RoleDTO role : managementService.getAssignedManagerRoles(voManager.getEmail())) {
                    final VoManagerRole link = new VoManagerRole();
                    link.setManagerId(voManager.getManagerId());
                    link.setRoleId(role.getRoleId());
                    voManagerRoles.add(link);
                }
                voManager.setManagerRoles(voManagerRoles);
                return voManager;
            }
        }
        throw new AccessDeniedException("Access is denied");
    }

    /** {@inheritDoc} */
    @Override
    public VoManager createManager(VoManager voManager) throws Exception {
        if (voManager != null && CollectionUtils.isNotEmpty(voManager.getManagerShops())) {
            for (final VoManagerShop shop : voManager.getManagerShops()) {
                if (!federationFacade.isShopAccessibleByCurrentManager(shop.getShopId())) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
            final ShopDTO shop = shopService.getById(voManager.getManagerShops().get(0).getShopId());
            managementService.addUser(voManager.getEmail(), voManager.getFirstName(),
                    voManager.getLastName(), shop.getCode());

            if (voManager.getManagerShops().size() > 1) {
                for (final VoManagerShop otherShop : voManager.getManagerShops().subList(1, voManager.getManagerShops().size())) {
                    final ShopDTO otherShopDTO = shopService.getById(otherShop.getShopId());
                    managementService.grantShop(voManager.getEmail(), otherShopDTO.getCode());
                }
            }

            if (CollectionUtils.isNotEmpty(voManager.getManagerRoles())) {

                final List<RoleDTO> roles = managementService.getRolesList();
                final Map<Long, String> roleMap = new HashMap<>();
                for (final RoleDTO roleDTO : roles) {
                    roleMap.put(roleDTO.getRoleId(), roleDTO.getCode());
                }
                for (final VoManagerRole managerRole : voManager.getManagerRoles()) {
                    final String roleCode = roleMap.get(managerRole.getRoleId());
                    if ("ROLE_SMADMIN".equals(roleCode) && !federationFacade.isCurrentUserSystemAdmin()) {
                        throw new AccessDeniedException("Access is denied");
                    }
                    managementService.grantRole(voManager.getEmail(), roleCode);
                }
            }

            final List<ManagerDTO> managers = managementService.getManagers(voManager.getEmail(), null, null);
            if (CollectionUtils.isNotEmpty(managers)) {
                return voAssemblySupport.assembleVo(VoManager.class, ManagerDTO.class, new VoManager(), managers.get(0));
            }
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public VoManager updateManager(VoManager voManager) throws Exception {
        if (voManager != null && CollectionUtils.isNotEmpty(voManager.getManagerShops())) {
            allowUpdateOnlyBySysAdmin(voManager.getEmail());
            for (final VoManagerShop shop : voManager.getManagerShops()) {
                if (!federationFacade.isShopAccessibleByCurrentManager(shop.getShopId())) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
            managementService.updateUser(voManager.getEmail(), voManager.getFirstName(),
                    voManager.getLastName());

            for (final ShopDTO link : managementService.getAssignedManagerShops(voManager.getEmail())) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    managementService.revokeShop(voManager.getEmail(), link.getCode());
                } // else skip updates for inaccessible shops
            }

            for (final VoManagerShop link : voManager.getManagerShops()) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    final ShopDTO shop = shopService.getById(link.getShopId());
                    managementService.grantShop(voManager.getEmail(), shop.getCode());
                } // else skip updates for inaccessible shops
            }

            final List<RoleDTO> roles = managementService.getRolesList();
            final Map<Long, String> roleMap = new HashMap<>();
            for (final RoleDTO roleDTO : roles) {
                roleMap.put(roleDTO.getRoleId(), roleDTO.getCode());
            }

            for (final RoleDTO managerRole : managementService.getAssignedManagerRoles(voManager.getEmail())) {
                if ("ROLE_SMADMIN".equals(managerRole.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                    continue;
                }
                managementService.revokeRole(voManager.getEmail(), managerRole.getCode());
            }

            for (final VoManagerRole managerRole : voManager.getManagerRoles()) {
                final String roleCode = roleMap.get(managerRole.getRoleId());
                if ("ROLE_SMADMIN".equals(roleCode) && !federationFacade.isCurrentUserSystemAdmin()) {
                    continue;
                }
                managementService.grantRole(voManager.getEmail(), roleCode);
            }

            final List<ManagerDTO> managers = managementService.getManagers(voManager.getEmail(), null, null);
            if (CollectionUtils.isNotEmpty(managers)) {
                return voAssemblySupport.assembleVo(VoManager.class, ManagerDTO.class, new VoManager(), managers.get(0));
            }
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteManager(String email) throws Exception {
        if (federationFacade.isManageable(email, ManagerDTO.class)) {
            managementService.deleteUser(email);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resetPassword(String managerId) throws Exception {

    }

    /** {@inheritDoc} */
    @Override
    public void updateDisabledFlag(String manager, boolean disabled) throws Exception {
        allowUpdateOnlyBySysAdmin(manager);
        if (federationFacade.isManageable(manager, ManagerDTO.class)) {
            if (disabled) {
                managementService.disableAccount(manager);
            } else {
                managementService.enableAccount(manager);
            }
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private void allowUpdateOnlyBySysAdmin(String manager) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> roles = managementService.getAssignedManagerRoles(manager);
        for (final RoleDTO role : roles) {
            if ("ROLE_SMADMIN".equals(role.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                throw new AccessDeniedException("Access is denied");
            }
        }
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
