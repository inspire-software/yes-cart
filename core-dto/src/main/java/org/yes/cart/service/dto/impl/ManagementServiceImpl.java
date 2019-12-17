/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ManagerDTOImpl;
import org.yes.cart.domain.dto.impl.RoleDTOImpl;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.RegExUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * User management service implementation
 * <p/>
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ManagementServiceImpl implements ManagementService {

    private final ManagerService managerService;

    private final SystemService systemService;

    private final AttributeService attributeService;

    private final ShopService shopService;

    private final CategoryService categoryService;

    private final GenericDAO<ManagerRole, Long> managerRoleDao;

    private final GenericDAO<Role, Long> roleDao;

    private final GenericDAO<Shop, Long> shopDao;

    private final DtoFactory dtoFactory;

    private final Assembler managerAssembler;

    private final Assembler roleAssembler;

    private final Assembler shopAssembler;

    private final AdaptersRepository adaptersRepository;

    private final HashHelper hashHelper;


    /**
     * Construct user management service.
     * @param managerService      manager service to use
     * @param systemService       system service
     * @param attributeService    attribute service
     * @param shopService         shop service
     * @param categoryService     category service
     * @param managerRoleDao      manager roles dao
     * @param roleDao             role dao
     * @param dtoFactory          {@link DtoFactory}
     * @param hashHelper
     */
    public ManagementServiceImpl(final ManagerService managerService,
                                 final SystemService systemService,
                                 final AttributeService attributeService,
                                 final ShopService shopService,
                                 final CategoryService categoryService,
                                 final GenericDAO<ManagerRole, Long> managerRoleDao,
                                 final GenericDAO<Role, Long> roleDao,
                                 final GenericDAO<Shop, Long> shopDao,
                                 final DtoFactory dtoFactory,
                                 final AdaptersRepository adaptersRepository,
                                 final HashHelper hashHelper) {
        this.managerService = managerService;
        this.systemService = systemService;
        this.attributeService = attributeService;
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.managerRoleDao = managerRoleDao;
        this.roleDao = roleDao;
        this.shopDao = shopDao;
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;
        this.hashHelper = hashHelper;

        managerAssembler = DTOAssembler.newAssembler(ManagerDTOImpl.class, Manager.class);
        roleAssembler = DTOAssembler.newAssembler(RoleDTOImpl.class, Role.class);
        shopAssembler = DTOAssembler.newAssembler(ShopDTOImpl.class, Shop.class);
    }

    /**
     * Get the list of managers by given filtering criteria.
     *
     * @param emailFilter     optional email filter
     * @param firstNameFilter optional first name filter
     * @param lastNameFilter  optional last name filter
     * @return list of managers dto that match given criteria
     * @throws UnmappedInterfaceException errors
     * @throws UnableToCreateInstanceException errors
     *
     */
    @Override
    public List<ManagerDTO> getManagers(final String emailFilter,
                                        final String firstNameFilter,
                                        final String lastNameFilter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Manager> managers = managerService.findByCriteria(
                " where (?1 is null or lower(e.email) like ?1) and (?2 is null or lower(e.firstname) like ?2) and (?3 is null or lower(e.lastname) like ?3) order by e.email",
                StringUtils.isNotBlank(emailFilter) ? HQLUtils.criteriaIlikeAnywhere(emailFilter) : null,
                StringUtils.isNotBlank(firstNameFilter) ? HQLUtils.criteriaIlikeAnywhere(firstNameFilter) : null,
                StringUtils.isNotBlank(lastNameFilter) ? HQLUtils.criteriaIlikeAnywhere(lastNameFilter) : null
        );

        final List<ManagerDTO> managersDTO = new ArrayList<>(managers.size());



        for (Manager manager : managers) {
            final ManagerDTO managerDTO = dtoFactory.getByIface(ManagerDTO.class);
            managerAssembler.assembleDto(managerDTO, manager, adaptersRepository.getAll(), dtoFactory);
            managersDTO.add(managerDTO);
        }

        return managersDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RoleDTO> getAssignedManagerRoles(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> result = new ArrayList<>();
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager != null) {
            List<Role> roles = roleDao.findByNamedQuery(
                    "ASSIGNED.ROLES.BY.USER.EMAIL",
                    userId);
            fillRolesDTOs(result, roles);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RoleDTO> getAvailableManagerRoles(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> result = new ArrayList<>();
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager != null) {
            List<Role> roles = roleDao.findByNamedQuery(
                    "AVAILABLE.ROLES.BY.USER.EMAIL",
                    userId);
            fillRolesDTOs(result, roles);
        }
        return result;
    }

    private void fillRolesDTOs(final List<RoleDTO> result, final List<Role> roles)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (Role role : roles) {
            final RoleDTO roleDTO = dtoFactory.getByIface(RoleDTO.class);
            roleAssembler.assembleDto(roleDTO, role, adaptersRepository.getAll(), dtoFactory);
            result.add(roleDTO);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addUser(final String userId,
                        final String firstName,
                        final String lastName,
                        final String company1,
                        final String company2,
                        final String department,
                        final String shopCode)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        final Manager manager = managerService.getGenericDao().getEntityFactory().getByIface(Manager.class);

        manager.setEmail(userId);
        manager.setFirstname(firstName);
        manager.setLastname(lastName);
        manager.setCompanyName1(company1);
        manager.setCompanyName2(company2);
        manager.setCompanyDepartment(department);
        // manager.setPassword(); No need to set password as we already generating it in the aspect

        final Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.CODE", shopCode);
        if (shop == null) {
            throw new IllegalArgumentException("No shop found");
        }

        managerService.create(manager, shop);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUser(final String userId,
                           final String firstName,
                           final String lastName,
                           final String company1,
                           final String company2,
                           final String department) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager != null) {
            manager.setFirstname(firstName);
            manager.setLastname(lastName);
            manager.setCompanyName1(company1);
            manager.setCompanyName2(company2);
            manager.setCompanyDepartment(department);
            managerService.update(manager);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDashboard(final String userId, final String dashboardWidgets) {

        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager != null) {
            manager.setDashboardWidgets(dashboardWidgets);
            managerService.update(manager);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPassword(final String userId) {

        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager != null) {
            managerService.resetPassword(manager);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePassword(final String userId, final String password, final String lang) throws BadCredentialsException {

        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager != null) {

            String regex = systemService.getAttributeValue(AttributeNamesKeys.System.MANAGER_PASSWORD_REGEX);
            if (StringUtils.isBlank(regex)) {
                regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
            }
            if (!RegExUtils.getInstance(regex).matches(password)) {

                String error = AttributeNamesKeys.System.MANAGER_PASSWORD_REGEX + " (" + lang + ")";
                final Attribute attribute = attributeService.findByAttributeCode(AttributeNamesKeys.System.MANAGER_PASSWORD_REGEX);
                if (attribute != null) {
                    error = new FailoverStringI18NModel(attribute.getValidationFailedMessage(), error).getValue(lang);
                }

                throw new BadCredentialsException(error);

            }

            try {
                manager.setPassword(hashHelper.getHash(password));
                manager.setPasswordExpiry(null); // TODO: YC-906 Create password expiry flow for customers

                managerService.update(manager);
            } catch (Exception exp) {
                throw new BadCredentialsException(exp.getMessage(), exp);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(final String userId) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager != null) {
            final List<ManagerRole> assignedRoles = managerRoleDao.findByCriteria(" where e.email = ?1 ", userId);
            for (ManagerRole managerRole : assignedRoles) {
                managerRoleDao.delete(managerRole);
            }
            managerService.delete(manager);
        }
    }

    @Override
    public List<RoleDTO> getRolesList() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> result = new ArrayList<>();
        List<Role> roles = roleDao.findAll();
        fillRolesDTOs(result, roles);
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addRole(final String role, final String description) {
        final Role roleEntity = roleDao.getEntityFactory().getByIface(Role.class);
        roleEntity.setCode(role);
        roleEntity.setDescription(description);
        roleDao.create(roleEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRole(final String role, final String description) {
        final Role roleEntity = roleDao.findSingleByCriteria(" where e.code = ?1 ", role);
        if (roleEntity != null) {
            roleEntity.setDescription(description);
            roleDao.update(roleEntity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRole(final String role) {
        final Role roleEntity = roleDao.findSingleByCriteria(" where e.code = ?1 ", role);
        if (roleEntity != null) {
            final List<ManagerRole> assignedRoles = managerRoleDao.findByCriteria(" where e.code = ?1 ", role);
            for (ManagerRole managerRole : assignedRoles) {
                managerRoleDao.delete(managerRole);
            }
            roleDao.delete(roleEntity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void grantRole(final String userId, final String role) {
        final Role roleEntity = roleDao.findSingleByCriteria(" where e.code = ?1 ", role);
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (roleEntity != null && manager != null) {
            final ManagerRole managerRole = managerRoleDao.getEntityFactory().getByIface(ManagerRole.class);
            managerRole.setCode(role);
            managerRole.setEmail(userId);
            managerRoleDao.create(managerRole);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void revokeRole(final String userId, final String role) {
        final List<ManagerRole> managerRole = managerRoleDao.findByCriteria(
                " where e.code = ?1 and e.email = ?2",
                role, userId
        );
        if (managerRole != null && !managerRole.isEmpty()) {
            for (final ManagerRole roleAssignment : managerRole) {
                managerRoleDao.delete(roleAssignment);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShopDTO> getAssignedManagerShops(final String userId, final boolean includeSubs) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager == null) {
            return Collections.emptyList();
        }
        final Collection<ManagerShop> assigned = manager.getShops();
        final List<ShopDTO> shopDTOs = new ArrayList<>(assigned.size());
        fillManagerShopsDTOs(shopDTOs, assigned, includeSubs);
        return shopDTOs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShopDTO> getAvailableManagerShops(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager == null) {
            return Collections.emptyList();
        }
        final List<Shop> all = shopDao.findAll();
        final Iterator<Shop> allIt = all.iterator();
        while (allIt.hasNext()) {
            final Shop current = allIt.next();
            for (final ManagerShop shop : manager.getShops()) {
                if (shop.getShop().getShopId() == current.getShopId()) {
                    allIt.remove();
                }
            }
        }

        final List<ShopDTO> shopDTOs = new ArrayList<>(all.size());
        fillShopsDTOs(shopDTOs, all);

        return shopDTOs;
    }

    private void fillManagerShopsDTOs(final List<ShopDTO> result, final Collection<ManagerShop> shops, final boolean includeSubs)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (ManagerShop managerShop : shops) {
            final ShopDTO shopDTO = dtoFactory.getByIface(ShopDTO.class);
            final Shop shop = this.shopService.findById(managerShop.getShop().getShopId());
            shopAssembler.assembleDto(shopDTO, shop, adaptersRepository.getAll(), dtoFactory);
            if (includeSubs && shop.isB2BProfileActive()) {
                final List<Shop> subs = this.shopService.getSubShopsByMaster(shop.getShopId());
                if (subs != null) {
                    for (final Shop subShop : subs) {
                        final ShopDTO shopDTOsub = dtoFactory.getByIface(ShopDTO.class);
                        shopAssembler.assembleDto(shopDTOsub, subShop, adaptersRepository.getAll(), dtoFactory);
                        result.add(shopDTOsub);
                    }
                }
            }
            result.add(shopDTO);
        }
    }

    private void fillShopsDTOs(final List<ShopDTO> result, final Collection<Shop> shops)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (Shop shop : shops) {
            final ShopDTO shopDTO = dtoFactory.getByIface(ShopDTO.class);
            shopAssembler.assembleDto(shopDTO, shop, adaptersRepository.getAll(), dtoFactory);
            result.add(shopDTO);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void grantShop(final String userId, final String shopCode) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        final Collection<ManagerShop> assigned = manager.getShops();
        for (final ManagerShop shop : assigned) {
            if (shop.getShop().getCode().equals(shopCode)) {
                return;
            }
        }
        final Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.CODE", shopCode);
        if (shop != null) {
            final ManagerShop managerShop = shopDao.getEntityFactory().getByIface(ManagerShop.class);
            managerShop.setManager(manager);
            managerShop.setShop(shop);
            assigned.add(managerShop);
        }
        managerService.update(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void revokeShop(final String userId, final String shopCode) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        final Iterator<ManagerShop> assigned = manager.getShops().iterator();
        while (assigned.hasNext()) {
            final ManagerShop shop = assigned.next();
            if (shop.getShop().getCode().equals(shopCode)) {
                assigned.remove();
                managerService.update(manager);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAssignedManagerSupplierCatalogs(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager == null || manager.getProductSupplierCatalogs() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(manager.getProductSupplierCatalogs());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAssignedManagerCategoryCatalogs(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager == null || manager.getCategoryCatalogs() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(manager.getCategoryCatalogs());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getAssignedManagerCatalogHierarchy(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        if (manager == null) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(manager.getCategoryCatalogs())) {

            final Set<Long> allFromShops = new TreeSet<>();
            if (CollectionUtils.isNotEmpty(manager.getShops())) {
                for (final ManagerShop ms : manager.getShops()) {
                    final Shop shop = shopService.getById(ms.getShop().getShopId());
                    if (CollectionUtils.isNotEmpty(shop.getShopCategory())) {
                        for (final ShopCategory sc : shop.getShopCategory()) {
                            allFromShops.add(sc.getCategory().getCategoryId());
                        }
                    }
                }
            }

            return new ArrayList<>(allFromShops);
        }

        final Set<Long> allFromCatalog = new TreeSet<>();
        for (final String catGuid : manager.getCategoryCatalogs()) {
            allFromCatalog.add(categoryService.findCategoryIdByGUID(catGuid));
        }

        return new ArrayList<>(allFromCatalog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void grantSupplierCatalog(final String userId, final String catalogCode) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        final Set<String> assigned = new HashSet<>(manager.getProductSupplierCatalogs());
        assigned.add(catalogCode);
        manager.setProductSupplierCatalogs(assigned);
        managerService.update(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void revokeSupplierCatalog(final String userId, final String catalogCode) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        final Set<String> assigned = new HashSet<>(manager.getProductSupplierCatalogs());
        assigned.remove(catalogCode);
        manager.setProductSupplierCatalogs(assigned);
        managerService.update(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void grantCategoryCatalog(final String userId, final String catalogCode) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        final Set<String> assigned = new HashSet<>(manager.getCategoryCatalogs());
        assigned.add(catalogCode);
        manager.setCategoryCatalogs(assigned);
        managerService.update(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "shopFederationStrategy-admin", key = "#userId")
    public void revokeCategoryCatalog(final String userId, final String catalogCode) {
        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        final Set<String> assigned = new HashSet<>(manager.getCategoryCatalogs());
        assigned.remove(catalogCode);
        manager.setCategoryCatalogs(assigned);
        managerService.update(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableAccount(final String userId) {

        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        manager.setEnabled(true);
        managerService.update(manager);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableAccount(final String userId) {

        final Manager manager = managerService.findSingleByCriteria(" where e.email = ?1", userId);
        manager.setEnabled(false);
        managerService.update(manager);

    }
}
