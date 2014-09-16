/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.federation.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.service.dto.ManagementService;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:32
 */
public class ShopFederationStrategyImpl implements ShopFederationStrategy {

    private final ManagementService managementService;


    public ShopFederationStrategyImpl(final ManagementService managementService) {
        this.managementService = managementService;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCurrentUserSystemAdmin() {
        for (final GrantedAuthority auth : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if ("ROLE_SMADMIN".equals(auth.getAuthority())) {
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isShopAccessibleByCurrentManager(final String shopCode) {
        if (isCurrentUserSystemAdmin()) {
            return true;
        }
        try {
            final String currentManager = SecurityContextHolder.getContext().getAuthentication().getName();
            final List<ShopDTO> currentAssigned = managementService.getAssignedManagerShops(currentManager);
            for (final ShopDTO shop : currentAssigned) {
                if (shop.getCode().equals(shopCode)) {
                    return true;
                }
            }
        } catch (Exception exp) {
            // nothing
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> getAccessibleShopIdsByCurrentManager() {
        try {
            final String currentManager = SecurityContextHolder.getContext().getAuthentication().getName();
            final List<ShopDTO> currentAssigned = managementService.getAssignedManagerShops(currentManager);
            final Set<Long> currentAssignedIds = new HashSet<Long>();
            for (final ShopDTO shop : currentAssigned) {
                currentAssignedIds.add(shop.getShopId());
            }
            return currentAssignedIds;
        } catch (Exception exp) {
            return Collections.emptySet();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAccessibleShopsByCurrentManager() {
        try {
            final String currentManager = SecurityContextHolder.getContext().getAuthentication().getName();
            return managementService.getAssignedManagerShops(currentManager);
        } catch (Exception exp) {
            return Collections.emptyList();
        }
    }


}
