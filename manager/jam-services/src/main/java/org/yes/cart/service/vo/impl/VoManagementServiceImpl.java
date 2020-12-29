/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.*;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoManagementService;

import java.io.IOException;
import java.util.*;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 09:00
 */
public class VoManagementServiceImpl implements VoManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(VoManagementServiceImpl.class);

    private final ManagementService managementService;
    private final DtoShopService shopService;
    private final DtoCategoryService dtoCategoryService;
    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    private static final String LICENSE_ROLE = "ROLE_LICENSEAGREED";

    private static final String DEFAULT =
            "\n\nLicense is not found. This violates condition of use of this software.\n" +
                    "Stop using this software and contact your software provider immediately.\n\n";

    private String licenseText = DEFAULT;

    public VoManagementServiceImpl(final ManagementService managementService,
                                   final DtoShopService shopService,
                                   final DtoCategoryService dtoCategoryService,
                                   final FederationFacade federationFacade,
                                   final VoAssemblySupport voAssemblySupport) {
        this.managementService = managementService;
        this.shopService = shopService;
        this.dtoCategoryService = dtoCategoryService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    @Override
    public VoManager getMyself() throws Exception {
        return getMyselfInternal();
    }

    private VoManager getMyselfInternal() {

        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null
                || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return null;
        }
        final String currentManager = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            return getByLoginInternal(currentManager);
        } catch (Exception exp) {
            LOG.error(exp.getMessage(), exp);
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
    public VoSearchResult<VoManagerInfo> getFilteredManagers(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoManagerInfo> result = new VoSearchResult<>();
        final List<VoManagerInfo> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final Map<String, List> params = new HashMap<>();
        if (filter.getParameters() != null) {
            params.putAll(filter.getParameters());
        }
        final Map<String, List> all = filter.getParameters() != null ? new HashMap<>(filter.getParameters()) : new HashMap<>();
        if (!federationFacade.isCurrentUserSystemAdmin()) {
            final Set<Long> shopIds = federationFacade.getAccessibleShopIdsByCurrentManager();
            if (CollectionUtils.isEmpty(shopIds)) {
                return result;
            }
            all.put("shopIds", new ArrayList(shopIds));
        }

        final SearchContext searchContext = new SearchContext(
                params,
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter", "shopIds"
        );

        final SearchResult<ManagerDTO> batch = managementService.findManagers(searchContext);
        results.addAll(voAssemblySupport.assembleVos(VoManagerInfo.class, ManagerDTO.class, batch.getItems()));
        result.setTotal(batch.getTotal());

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public VoManager getManagerById(final long id) throws Exception {
        final VoManager voManager = getByIdInternal(id);
        if (voManager != null && federationFacade.isManageable(voManager.getLogin(), ManagerDTO.class)) {
            return voManager;
        }
        throw new AccessDeniedException("Access is denied");
    }

    protected VoManager getByLoginInternal(final String login) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return convertInternal(managementService.getManagerByLogin(login));
    }

    protected VoManager getByIdInternal(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return convertInternal(managementService.getManagerById(id));
    }

    protected VoManager convertInternal(final ManagerDTO managerDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (managerDTO != null) {
            final VoManager voManager = voAssemblySupport.assembleVo(
                    VoManager.class, ManagerDTO.class, new VoManager(), managerDTO);
            final List<VoManagerShop> voManagerShops = new ArrayList<>();
            for (final ShopDTO shop : managementService.getAssignedManagerShops(voManager.getLogin(), false)) {
                final VoManagerShop link = new VoManagerShop();
                link.setManagerId(voManager.getManagerId());
                link.setShopId(shop.getShopId());
                link.setCreatedBy(shop.getCreatedBy());
                link.setCreatedTimestamp(shop.getCreatedTimestamp());
                link.setUpdatedBy(shop.getUpdatedBy());
                link.setUpdatedTimestamp(shop.getUpdatedTimestamp());
                voManagerShops.add(link);
            }
            voManager.setManagerShops(voManagerShops);

            final List<VoManagerRole> voManagerRoles = new ArrayList<>();
            for (final RoleDTO role : managementService.getAssignedManagerRoles(voManager.getLogin())) {
                final VoManagerRole link = new VoManagerRole();
                link.setManagerId(voManager.getManagerId());
                link.setCode(role.getCode());
                link.setCreatedBy(role.getCreatedBy());
                link.setCreatedTimestamp(role.getCreatedTimestamp());
                link.setUpdatedBy(role.getUpdatedBy());
                link.setUpdatedTimestamp(role.getUpdatedTimestamp());
                voManagerRoles.add(link);
            }
            voManager.setManagerRoles(voManagerRoles);
            final List<VoManagerSupplierCatalog> voManagerSupplierCatalogs = new ArrayList<>();
            for (final String supplierCatalogCode : managerDTO.getProductSupplierCatalogs()) {
                final VoManagerSupplierCatalog link = new VoManagerSupplierCatalog();
                link.setManagerId(voManager.getManagerId());
                link.setCode(supplierCatalogCode);
                link.setCreatedBy(managerDTO.getCreatedBy());
                link.setCreatedTimestamp(managerDTO.getCreatedTimestamp());
                link.setUpdatedBy(managerDTO.getUpdatedBy());
                link.setUpdatedTimestamp(managerDTO.getUpdatedTimestamp());
                voManagerSupplierCatalogs.add(link);
            }
            voManager.setManagerSupplierCatalogs(voManagerSupplierCatalogs);

            final List<VoManagerCategoryCatalog> voManagerCategoryCatalogs = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(managerDTO.getCategoryCatalogs())) {
                final SearchContext filter = new SearchContext(
                        Collections.singletonMap("GUIDs", new ArrayList<>(managerDTO.getCategoryCatalogs())),
                        0,
                        managerDTO.getCategoryCatalogs().size(),
                        "name",
                        false,
                        "GUIDs"
                );
                final SearchResult<CategoryDTO> assigned = dtoCategoryService.findCategories(filter);
                if (!assigned.getItems().isEmpty()) {
                    for (final CategoryDTO category : assigned.getItems()) {
                        final VoManagerCategoryCatalog link = new VoManagerCategoryCatalog();
                        link.setManagerId(voManager.getManagerId());
                        link.setCategoryId(category.getCategoryId());
                        link.setCode(category.getGuid());
                        link.setName(category.getName());
                        link.setCreatedBy(managerDTO.getCreatedBy());
                        link.setCreatedTimestamp(managerDTO.getCreatedTimestamp());
                        link.setUpdatedBy(managerDTO.getUpdatedBy());
                        link.setUpdatedTimestamp(managerDTO.getUpdatedTimestamp());
                        voManagerCategoryCatalogs.add(link);
                    }
                }
            }
            voManager.setManagerCategoryCatalogs(voManagerCategoryCatalogs);

            return voManager;
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoManager createManager(VoManager voManager) throws Exception {

        if (voManager != null && CollectionUtils.isNotEmpty(voManager.getManagerShops())) {

            checkShopsAndRoles(voManager);

            final ShopDTO shop = shopService.getById(voManager.getManagerShops().get(0).getShopId());
            managementService.addUser(
                    voManager.getLogin(),
                    voManager.getEmail(),
                    voManager.getFirstName(),
                    voManager.getLastName(),
                    voManager.getCompanyName1(),
                    voManager.getCompanyName2(),
                    voManager.getCompanyDepartment(),
                    shop.getCode()
            );

            if (voManager.getManagerShops().size() > 1) {
                for (final VoManagerShop otherShop : voManager.getManagerShops().subList(1, voManager.getManagerShops().size())) {
                    final ShopDTO otherShopDTO = shopService.getById(otherShop.getShopId());
                    managementService.grantShop(voManager.getLogin(), otherShopDTO.getCode());
                }
            }

            if (CollectionUtils.isNotEmpty(voManager.getManagerRoles())) {

                for (final VoManagerRole managerRole : voManager.getManagerRoles()) {

                    if ("ROLE_SMADMIN".equals(managerRole.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                        throw new AccessDeniedException("Access is denied");
                    }
                    managementService.grantRole(voManager.getLogin(), managerRole.getCode());

                }
            }

            if (CollectionUtils.isNotEmpty(voManager.getManagerSupplierCatalogs())) {

                for (final VoManagerSupplierCatalog managerSupplierCatalog : voManager.getManagerSupplierCatalogs())  {
                    managementService.grantSupplierCatalog(voManager.getLogin(), managerSupplierCatalog.getCode());
                }

            }

            if (CollectionUtils.isNotEmpty(voManager.getManagerCategoryCatalogs())) {

                for (final VoManagerCategoryCatalog managerCategoryCatalog : voManager.getManagerCategoryCatalogs())  {
                    managementService.grantCategoryCatalog(voManager.getLogin(), managerCategoryCatalog.getCode());
                }

            }

            return getByLoginInternal(voManager.getLogin());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    void checkShopsAndRoles(final VoManager voManager) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        for (final VoManagerShop shop : voManager.getManagerShops()) {
            if (!federationFacade.isShopAccessibleByCurrentManager(shop.getShopId())) {
                throw new AccessDeniedException("Access is denied");
            }
        }

        if (CollectionUtils.isNotEmpty(voManager.getManagerRoles())) {
            final List<RoleDTO> roles = managementService.getRolesList();
            final Set<String> availableRole = new HashSet<>();
            for (final RoleDTO roleDTO : roles) {
                availableRole.add(roleDTO.getCode());
            }
            for (final VoManagerRole roleVo : voManager.getManagerRoles()) {
                if (roleVo.getCode() == null || !availableRole.contains(roleVo.getCode())) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
        }

        if (CollectionUtils.isNotEmpty(voManager.getManagerSupplierCatalogs())) {
            for (final VoManagerSupplierCatalog catalog : voManager.getManagerSupplierCatalogs()) {
                if (!federationFacade.isSupplierCatalogAccessibleByCurrentManager(catalog.getCode())) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
        }

        if (CollectionUtils.isNotEmpty(voManager.getManagerCategoryCatalogs())) {
            for (final VoManagerCategoryCatalog catalog : voManager.getManagerCategoryCatalogs()) {
                if (!federationFacade.isManageable(catalog.getCategoryId(), CategoryDTO.class)) {
                    throw new AccessDeniedException("Access is denied");
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoManager updateManager(VoManager voManager) throws Exception {

        if (voManager != null &&
                (CollectionUtils.isNotEmpty(voManager.getManagerShops()) ||
                        federationFacade.isCurrentUserSystemAdmin())) {

            final ManagerDTO existing = managementService.getManagerById(voManager.getManagerId());

            allowUpdateOnlyBySysAdmin(existing.getLogin());
            checkShopsAndRoles(voManager);

            if (!existing.getLogin().equals(voManager.getLogin())) {
                updateLogin(voManager.getManagerId(), voManager.getLogin());
            }

            managementService.updateUser(
                    voManager.getLogin(),
                    voManager.getEmail(),
                    voManager.getFirstName(),
                    voManager.getLastName(),
                    voManager.getCompanyName1(),
                    voManager.getCompanyName2(),
                    voManager.getCompanyDepartment()
            );

            // Shops
            final Set<String> shopsToRevoke = new HashSet<>();
            for (final ShopDTO link : managementService.getAssignedManagerShops(voManager.getLogin(), false)) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    shopsToRevoke.add(link.getCode());
                } // else skip updates for inaccessible shops
            }
            if (CollectionUtils.isNotEmpty(voManager.getManagerShops())) {
                for (final VoManagerShop link : voManager.getManagerShops()) {
                    final ShopDTO shop = shopService.getById(link.getShopId());
                    if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                        if (!shopsToRevoke.contains(shop.getCode())) {
                            managementService.grantShop(voManager.getLogin(), shop.getCode());
                        }
                    } // else skip updates for inaccessible shops
                    shopsToRevoke.remove(shop.getCode()); // Do not revoke, it is still active
                }
            }
            for (final String shopToRevoke : shopsToRevoke) {
                managementService.revokeShop(voManager.getLogin(), shopToRevoke);
            }

            // Roles
            final Set<String> rolesToRevoke = new HashSet<>();
            for (final RoleDTO managerRole : managementService.getAssignedManagerRoles(voManager.getLogin())) {
                if ("ROLE_SMADMIN".equals(managerRole.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                    continue;
                }
                rolesToRevoke.add(managerRole.getCode());
            }
            for (final VoManagerRole managerRole : voManager.getManagerRoles()) {
                if ("ROLE_SMADMIN".equals(managerRole.getCode()) && !federationFacade.isCurrentUserSystemAdmin()) {
                    continue;
                }
                if (!rolesToRevoke.contains(managerRole.getCode())) {
                    managementService.grantRole(voManager.getLogin(), managerRole.getCode());
                }
                rolesToRevoke.remove(managerRole.getCode()); // Do not revoke, it is still active
            }
            for (final String roleToRevoke : rolesToRevoke) {
                managementService.revokeRole(voManager.getLogin(), roleToRevoke);
            }

            // Suppliers
            final Set<String> suppliersToRevoke = new HashSet<>();
            for (final String managerCat : managementService.getAssignedManagerSupplierCatalogs(voManager.getLogin())) {
                if (federationFacade.isSupplierCatalogAccessibleByCurrentManager(managerCat)) {
                    suppliersToRevoke.add(managerCat);
                } // else skip updates for inaccessible suppliers
            }
            for (final VoManagerSupplierCatalog managerCat : voManager.getManagerSupplierCatalogs()) {
                if (federationFacade.isSupplierCatalogAccessibleByCurrentManager(managerCat.getCode())) {
                    if (!suppliersToRevoke.contains(managerCat.getCode())) {
                        managementService.grantSupplierCatalog(voManager.getLogin(), managerCat.getCode());
                    }
                }
                suppliersToRevoke.remove(managerCat.getCode());  // Do not revoke, it is still active
            }
            for (final String supplierToRevoke : suppliersToRevoke) {
                managementService.revokeSupplierCatalog(voManager.getLogin(), supplierToRevoke);
            }

            // Categories
            final Set<String> categoriesToRevoke = new HashSet<>();
            final List<String> codes = managementService.getAssignedManagerCategoryCatalogs(voManager.getLogin());
            if (CollectionUtils.isNotEmpty(codes)) {
                final SearchContext filter = new SearchContext(
                        Collections.singletonMap("GUIDs", new ArrayList<>(codes)),
                        0,
                        codes.size(),
                        "name",
                        false,
                        "GUIDs"
                );
                final SearchResult<CategoryDTO> assigned = dtoCategoryService.findCategories(filter);
                if (!assigned.getItems().isEmpty()) {
                    for (final CategoryDTO category : assigned.getItems()) {
                        if (federationFacade.isManageable(category.getCategoryId(), CategoryDTO.class)) {
                            categoriesToRevoke.add(category.getGuid());
                        } // else skip updates for inaccessible suppliers
                    }
                }
            }
            for (final VoManagerCategoryCatalog managerCat : voManager.getManagerCategoryCatalogs()) {
                if (federationFacade.isManageable(managerCat.getCategoryId(), CategoryDTO.class)) {
                    if (!categoriesToRevoke.contains(managerCat.getCode())) {
                        managementService.grantCategoryCatalog(voManager.getLogin(), managerCat.getCode());
                    }
                }
                categoriesToRevoke.remove(managerCat.getCode());  // Do not revoke, it is still active
            }
            for (final String supplierToRevoke : categoriesToRevoke) {
                managementService.revokeCategoryCatalog(voManager.getLogin(), supplierToRevoke);
            }

            return getByIdInternal(voManager.getManagerId());
            
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public void deleteManager(long id) throws Exception {
        final ManagerDTO managerDTO = managementService.getManagerById(id);
        if (managerDTO != null && federationFacade.isManageable(managerDTO.getLogin(), ManagerDTO.class)) {
            managementService.deleteUser(managerDTO.getLogin());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateDashboard(final long id, final String dashboardWidgets) throws Exception {
        final ManagerDTO managerDTO = managementService.getManagerById(id);
        if (managerDTO != null && federationFacade.isManageable(managerDTO.getLogin(), ManagerDTO.class)) {
            managementService.updateDashboard(managerDTO.getLogin(), dashboardWidgets);
        } else {

            final VoManager myself = getMyselfInternal();
            if (myself != null && id == myself.getManagerId()) {
                managementService.updateDashboard(myself.getLogin(), dashboardWidgets);
            } else {
                throw new AccessDeniedException("Access is denied");
            }

        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateLogin(final long id, final String login) throws Exception {
        final ManagerDTO managerDTO = managementService.getManagerById(id);
        final ManagerDTO loginCheck = managementService.getManagerByLogin(login);
        if (loginCheck == null) {
            if (managerDTO != null && federationFacade.isManageable(managerDTO.getLogin(), ManagerDTO.class)) {
                managementService.updateUserId(managerDTO.getLogin(), login);
            } else {
                final VoManager myself = getMyselfInternal();
                if (myself != null && id == myself.getManagerId()) {
                    managementService.updateUserId(myself.getLogin(), login);
                } else {
                    throw new AccessDeniedException("Access is denied");
                }

            }
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resetPassword(long id) throws Exception {
        final ManagerDTO managerDTO = managementService.getManagerById(id);
        if (managerDTO != null && federationFacade.isManageable(managerDTO.getLogin(), ManagerDTO.class)) {
            managementService.resetPassword(managerDTO.getLogin());
        } else {

            final VoManager myself = getMyselfInternal();
            if (myself != null && id == myself.getManagerId()) {
                managementService.resetPassword(myself.getLogin());
            } else {
                throw new AccessDeniedException("Access is denied");
            }

        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateDisabledFlag(long id, boolean disabled) throws Exception {
        final ManagerDTO managerDTO = managementService.getManagerById(id);
        if (managerDTO != null) {
            final String manager = managerDTO.getLogin();
            allowUpdateOnlyBySysAdmin(manager);
            if (federationFacade.isManageable(manager, ManagerDTO.class)) {
                if (disabled) {
                    managementService.disableAccount(manager);
                } else {
                    managementService.enableAccount(manager);
                }
            }
            return;
        }
        throw new AccessDeniedException("Access is denied");
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
                    LOG.error("Unable to retrieve roles for {}", username);
                }
            }
        }

        return false;
    }

    public void setLicenseTextResource(final Resource licenseTextResource) throws IOException {

        licenseText = IOUtils.toString(licenseTextResource.getInputStream(), "UTF-8");

    }



}
