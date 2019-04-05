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

package org.yes.cart.bulkimport.xml.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.*;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.ManagerRole;
import org.yes.cart.domain.entity.ManagerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ManagerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.DateUtils;

import java.util.List;
import java.util.Optional;

/**
 * User: denispavlov
 * Date: 10/03/2019
 * Time: 17:04
 */
public class OrganisationUserXmlEntityHandler extends AbstractXmlEntityHandler<OrganisationUserType, Manager> implements XmlEntityImportHandler<OrganisationUserType, Manager> {

    private ManagerService managerService;
    private GenericService<ManagerRole> managerRoleService;
    private ShopService shopService;


    protected OrganisationUserXmlEntityHandler() {
        super("organisation-user");
    }

    @Override
    protected void delete(final Manager domain) {
        this.managerService.delete(domain);
        this.managerService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Manager domain, final OrganisationUserType xmlType, final EntityImportModeType mode) {

        if (StringUtils.isNotBlank(xmlType.getCredentials().getPassword())) {
            domain.setPassword(xmlType.getCredentials().getPassword());
        }
        if (StringUtils.isNotBlank(xmlType.getCredentials().getPasswordExpiry())) {
            domain.setPasswordExpiry(DateUtils.iParseSDT(xmlType.getCredentials().getPasswordExpiry()));
        }
        if (xmlType.getCredentials().isEnabled() != null) {
            domain.setEnabled(xmlType.getCredentials().isEnabled());
        }
        if (StringUtils.isNotBlank(xmlType.getCredentials().getAuthToken())) {
            domain.setAuthToken(xmlType.getCredentials().getAuthToken());
        }
        if (StringUtils.isNotBlank(xmlType.getCredentials().getAuthTokenExpiry())) {
            domain.setAuthTokenExpiry(DateUtils.iParseSDT(xmlType.getCredentials().getAuthTokenExpiry()));
        }
        if (xmlType.getOrganisation() != null) {
            domain.setCompanyName1(xmlType.getOrganisation().getCompanyName1());
            domain.setCompanyName2(xmlType.getOrganisation().getCompanyName2());
            domain.setCompanyDepartment(xmlType.getOrganisation().getCompanyDepartment());
        }
        if (xmlType.getPreferences() != null) {
            domain.setDashboardWidgets(xmlType.getPreferences().getDashboardWidgets());
        }
        if (xmlType.getContactDetails() != null) {
            domain.setSalutation(xmlType.getContactDetails().getSalutation());
            domain.setFirstname(xmlType.getContactDetails().getFirstname());
            domain.setMiddlename(xmlType.getContactDetails().getMiddlename());
            domain.setLastname(xmlType.getContactDetails().getLastname());
        }

        if (domain.getManagerId() == 0L) {
            this.managerService.create(domain);
        } else {
            this.managerService.update(domain);
        }
        this.managerService.getGenericDao().flush();
        this.managerService.getGenericDao().evict(domain);

        processShops(domain, xmlType);
        processRoles(domain, xmlType);

    }

    private void processRoles(final Manager domain, final OrganisationUserType xmlType) {

        if (xmlType.getOrganisation() == null || xmlType.getOrganisation().getRoles() == null) {
            return;
        }

        final CollectionImportModeType collectionMode = xmlType.getOrganisation().getRoles().getImportMode() != null ? xmlType.getOrganisation().getRoles().getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {

            final List<ManagerRole> mrs = this.managerRoleService.findByCriteria(" where e.email = ?1", domain.getEmail());
            for (final ManagerRole mr : mrs) {
                this.managerRoleService.delete(mr);
                this.managerRoleService.getGenericDao().flush();
                this.managerRoleService.getGenericDao().evict(mr);
            }

        }

        for (final OrganisationUserRoleType role : xmlType.getOrganisation().getRoles().getRole()) {

            if (this.managerRoleService.findCountByCriteria(" where e.email = ?1 and e.code = ?2", domain.getEmail(), role.getCode()) == 0) {

                final ManagerRole mr = this.managerRoleService.getGenericDao().getEntityFactory().getByIface(ManagerRole.class);

                mr.setEmail(domain.getEmail());
                mr.setCode(role.getCode());

                this.managerRoleService.create(mr);

            }

        }


    }

    private void processShops(final Manager domain, final OrganisationUserType xmlType) {

        if (xmlType.getOrganisation() == null || xmlType.getOrganisation().getShops() == null) {
            return;
        }

        final CollectionImportModeType collectionMode = xmlType.getOrganisation().getShops().getImportMode() != null ? xmlType.getOrganisation().getShops().getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getShops().clear();
        }

        for (final OrganisationUserShopType mShop : xmlType.getOrganisation().getShops().getShop()) {

            final Shop shop = shopService.getShopByCode(mShop.getCode());
            if (shop != null) {

                final Optional<ManagerShop> ms = domain.getShops().stream().filter(managerShop -> shop.getShopId() == managerShop.getShop().getShopId()).findFirst();
                if (!ms.isPresent()) {

                    final ManagerShop msNew = this.managerService.getGenericDao().getEntityFactory().getByIface(ManagerShop.class);

                    msNew.setManager(domain);
                    msNew.setShop(shop);

                    domain.getShops().add(msNew);

                }

            }

        }

        this.managerService.update(domain);
        this.managerService.getGenericDao().flush();

    }

    @Override
    protected Manager getOrCreate(final OrganisationUserType xmlType) {
        Manager manager = this.managerService.findSingleByCriteria(" where e.email = ?1", xmlType.getCredentials().getEmail());
        if (manager != null) {
            return manager;
        }
        manager = this.managerService.getGenericDao().getEntityFactory().getByIface(Manager.class);
        manager.setEmail(xmlType.getCredentials().getEmail());
        return manager;
    }

    @Override
    protected EntityImportModeType determineImportMode(final OrganisationUserType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Manager domain) {
        return domain.getManagerId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param managerService manager service
     */
    public void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * Spring IoC.
     *
     * @param managerRoleService manager role service
     */
    public void setManagerRoleService(final GenericService<ManagerRole> managerRoleService) {
        this.managerRoleService = managerRoleService;
    }

    /**
     * Spring IoC.
     *
     * @param shopService shop service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }
}
