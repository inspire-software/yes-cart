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

import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoWarehouseService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.*;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class WarehouseFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final DtoWarehouseService dtoWarehouseService;

    public WarehouseFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                         final DtoWarehouseService dtoWarehouseService) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.dtoWarehouseService = dtoWarehouseService;
    }

    /**
     * {@inheritDoc}
     */
    public void applyFederationFilter(final Collection list, final Class objectType) {

        final Set<Long> manageableWhIds = getManageableWarehouseIds();

        final Iterator<WarehouseDTO> whIt = list.iterator();
        while (whIt.hasNext()) {
            final WarehouseDTO wh = whIt.next();
            if (!manageableWhIds.contains(wh.getWarehouseId())) {
                whIt.remove();
            }
        }
    }

    private Set<Long> getManageableWarehouseIds() {
        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        final Set<Long> manageableWhIds = new HashSet<Long>();
        for (final Long shopId : manageableShopIds) {
            try {
                final List<WarehouseDTO> whList = dtoWarehouseService.findByShopId(shopId);
                for (final WarehouseDTO wh : whList) {
                    manageableWhIds.add(wh.getWarehouseId());
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return manageableWhIds;
    }

    private Set<String> getManageableWarehouseCodes() {
        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        final Set<String> manageableWhCodes = new HashSet<String>();
        for (final Long shopId : manageableShopIds) {
            try {
                final List<WarehouseDTO> whList = dtoWarehouseService.findByShopId(shopId);
                for (final WarehouseDTO wh : whList) {
                    manageableWhCodes.add(wh.getCode());
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return manageableWhCodes;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isManageable(final Object object, final Class objectType) {
        if (object instanceof Long) {
            final Set<Long> manageableWhIds = getManageableWarehouseIds();
            return manageableWhIds.contains(object);
        }
        final Set<String> manageableWhCodes = getManageableWarehouseCodes();
        return manageableWhCodes.contains(object);

    }

}
