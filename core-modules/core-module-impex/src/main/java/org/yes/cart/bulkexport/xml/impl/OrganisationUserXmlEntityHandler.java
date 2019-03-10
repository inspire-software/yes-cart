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

package org.yes.cart.bulkexport.xml.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.ManagerRole;
import org.yes.cart.domain.entity.ManagerShop;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ManagerService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class OrganisationUserXmlEntityHandler extends AbstractXmlEntityHandler<Manager> {

    private ManagerService managerService;

    private GenericService<ManagerRole> managerRoleService;

    public OrganisationUserXmlEntityHandler() {
        super("organisation-users");
    }

    @Override
    public String handle(final JobStatusListener statusListener,
                         final XmlExportDescriptor xmlExportDescriptor,
                         final ImpExTuple<String, Manager> tuple,
                         final XmlValueAdapter xmlValueAdapter,
                         final String fileToExport) {

        return tagManager(null, tuple.getData()).toXml();
        
    }

    Tag tagManager(final Tag parent, final Manager manager) {

        final Tag mTag = tag(parent, "organisation-user")
                .attr("id", manager.getManagerId())
                .attr("guid", manager.getGuid());

            mTag.tag("credentials")
                .tagChars("email", manager.getEmail())
                .tagChars("password", manager.getPassword())
                .tagTime("password-expiry", manager.getPasswordExpiry())
                .tagBool("enabled", manager.getEnabled())
                .tagChars("auth-token", manager.getAuthToken())
                .tagTime("auth-token-expiry", manager.getAuthTokenExpiry())
            .end();

            final Tag orgTag = mTag.tag("organisation")
                    .tagChars("company-name-1", manager.getCompanyName1())
                    .tagChars("company-name-2", manager.getCompanyName2())
                    .tagChars("company-department", manager.getCompanyDepartment());

            if (CollectionUtils.isNotEmpty(manager.getShops())) {
                final Tag shopsTag = orgTag.tag("shops");
                for (final ManagerShop ms : manager.getShops()) {
                    shopsTag.tag("shop").attr("code", ms.getShop().getCode()).end();
                }
                shopsTag.end();
            }

            final List<ManagerRole> roles = managerRoleService.findByCriteria(" where e.email = ?1", manager.getEmail());

            if (CollectionUtils.isNotEmpty(roles)) {
                final Tag shopsTag = orgTag.tag("roles");
                for (final ManagerRole mr : roles) {
                    shopsTag.tag("role").attr("code", mr.getCode()).end();
                }
                shopsTag.end();
            }

            orgTag.end();

            mTag.tag("contact-details")
                .tagChars("salutation", manager.getSalutation())
                .tagChars("firstname", manager.getFirstname())
                .tagChars("middlename", manager.getMiddlename())
                .tagChars("lastname", manager.getLastname())
            .end();

            mTag.tag("preferences")
                .tagChars("dashboard-widgets", manager.getDashboardWidgets())
            .end();

        return mTag.tagTime(manager).end();

    }

    /**
     * Spring IoC.
     *
     * @param managerService Manager service
     */
    public void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * Spring IoC.
     *
     * @param managerRoleService role service
     */
    public void setManagerRoleService(final GenericService<ManagerRole> managerRoleService) {
        this.managerRoleService = managerRoleService;
    }
}
