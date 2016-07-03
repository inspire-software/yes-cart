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

package org.yes.cart.remote.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.remote.service.RemoteLicenseService;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.util.ShopCodeContext;

import java.io.IOException;
import java.util.List;

/**
 * User: denispavlov
 * Date: 29/04/2016
 * Time: 09:22
 */
public class RemoteLicenseServiceImpl implements RemoteLicenseService {

    private static final String LICENSE_ROLE = "ROLE_LICENSEAGREED";

    private static final String DEFAULT =
            "\n\nLicense is not found. This violates condition of use of this software.\n" +
            "Stop using this software and contact your software provider immediately.\n\n";

    private final ManagementService managementService;

    private String licenseText = DEFAULT;

    public RemoteLicenseServiceImpl(final ManagementService managementService) {
        this.managementService = managementService;
    }

    /** {@inheritDoc} */
    @Override
    public String getLicenseText() {
        return licenseText;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAgreedToLicense() {

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

    /** {@inheritDoc} */
    @Override
    public void agreeToLicense() {

        final SecurityContext sc = SecurityContextHolder.getContext();
        final String username = sc != null && sc.getAuthentication() != null ? sc.getAuthentication().getName() : null;
        if (StringUtils.isNotBlank(username)) {
            managementService.grantRole(username, LICENSE_ROLE);
        }

    }

    public void setLicenseTextResource(final Resource licenseTextResource) throws IOException {

        licenseText = IOUtils.toString(licenseTextResource.getInputStream(), "UTF-8");

    }

}
