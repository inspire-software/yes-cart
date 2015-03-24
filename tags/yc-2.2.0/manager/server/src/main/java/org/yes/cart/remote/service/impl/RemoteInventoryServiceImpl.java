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

package org.yes.cart.remote.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteInventoryService;
import org.yes.cart.service.dto.DtoInventoryService;
import org.yes.cart.service.dto.support.InventoryFilter;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 12-11-29
 * Time: 9:47 PM
 */
public class RemoteInventoryServiceImpl implements RemoteInventoryService {

    private final DtoInventoryService dtoInventoryService;
    private final ReindexService reindexService;
    private final FederationFacade federationFacade;

    public RemoteInventoryServiceImpl(final DtoInventoryService dtoInventoryService,
                                      final ReindexService reindexService,
                                      final FederationFacade federationFacade) {
        this.dtoInventoryService = dtoInventoryService;
        this.reindexService = reindexService;
        this.federationFacade = federationFacade;
    }

    /** {@inheritDoc} */
    @Override
    public List<InventoryDTO> getInventoryList(final InventoryFilter filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (filter.getWarehouse() != null && federationFacade.isManageable(filter.getWarehouse().getWarehouseId(), WarehouseDTO.class)) {
            return dtoInventoryService.getInventoryList(filter);
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO createInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(inventory.getWarehouseCode(), WarehouseDTO.class)) {
            final InventoryDTO rez = dtoInventoryService.createInventory(inventory);
            reindexService.reindexProductSkuCode(inventory.getSkuCode());
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO updateInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(inventory.getWarehouseCode(), WarehouseDTO.class)) {
            final InventoryDTO rez = dtoInventoryService.updateInventory(inventory);
            reindexService.reindexProductSkuCode(inventory.getSkuCode());
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<WarehouseDTO> getWarehouses() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<WarehouseDTO> all = new ArrayList<WarehouseDTO>(dtoInventoryService.getWarehouses());
        federationFacade.applyFederationFilter(all, WarehouseDTO.class);
        return all;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO getInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final InventoryDTO inv = dtoInventoryService.getInventory(skuWarehouseId);
        if (inv != null) {
            if (federationFacade.isManageable(inv.getWarehouseCode(), WarehouseDTO.class)) {
                return inv;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void removeInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final InventoryDTO skuWarehouse = dtoInventoryService.getInventory(skuWarehouseId);
        if (skuWarehouse != null) {
            if (federationFacade.isManageable(skuWarehouse.getWarehouseCode(), WarehouseDTO.class)) {
                dtoInventoryService.removeInventory(skuWarehouseId);
                if (skuWarehouse != null) {
                    reindexService.reindexProductSkuCode(skuWarehouse.getSkuCode());
                }
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
    }

}
