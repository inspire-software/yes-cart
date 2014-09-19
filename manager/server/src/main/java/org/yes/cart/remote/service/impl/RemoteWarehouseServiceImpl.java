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
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteWarehouseService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.dto.DtoWarehouseService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteWarehouseServiceImpl
        extends AbstractRemoteService<WarehouseDTO>
        implements RemoteWarehouseService {

    private final DtoWarehouseService dtoWarehouseService;

    private final ReindexService reindexService;

    private final FederationFacade federationFacade;


    /**
     * Construct service.
     *
     * @param service dto service to use
     * @param reindexService product reindex service
     * @param federationFacade federation service
     */
    public RemoteWarehouseServiceImpl(final DtoWarehouseService service,
                                      final ReindexService reindexService,
                                      final FederationFacade federationFacade) {
        super(service);
        this.dtoWarehouseService = service;
        this.reindexService = reindexService;
        this.federationFacade = federationFacade;
    }

    /**
     * {@inheritDoc}
     */
    public List<WarehouseDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<WarehouseDTO> all = new ArrayList<WarehouseDTO>(super.getAll());
        federationFacade.applyFederationFilter(all, WarehouseDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public WarehouseDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, WarehouseDTO.class)) {
            return super.getById(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public WarehouseDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, WarehouseDTO.class)) {
            return super.getById(id, converters);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public WarehouseDTO createForShop(final WarehouseDTO warehouse, final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            final WarehouseDTO created = super.create(warehouse);
            assignWarehouse(created.getWarehouseId(), shopId);
            return created;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public WarehouseDTO update(final WarehouseDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getWarehouseId(), WarehouseDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc
     */
    public List<WarehouseDTO> findByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return dtoWarehouseService.findByShopId(shopId);
        }
        return Collections.emptyList();
    }

    public void setShopWarehouseRank(long shopWarehouseId, int newRank) {
        try {
            getById(shopWarehouseId); // checks access
        } catch (UnmappedInterfaceException e) {
            // ok
        } catch (UnableToCreateInstanceException e) {
            // ok
        }
        dtoWarehouseService.setShopWarehouseRank(shopWarehouseId, newRank);
    }

    /**
     * {@inheritDoc
     */
    public ShopWarehouseDTO assignWarehouse(final long warehouseId, final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoWarehouseService.assignWarehouse(warehouseId, shopId);
    }

    /**
     * {@inheritDoc
     */
    public void unassignWarehouse(final long warehouseId, final long shopId) {
        dtoWarehouseService.unassignWarehouse(warehouseId, shopId);
    }

    /**
     * {@inheritDoc
     */
    public void removeSkuOnWarehouse(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final SkuWarehouse skuWarehouse = getSkuWarehouseService().findById(skuWarehouseId);
        getById(skuWarehouse.getWarehouse().getWarehouseId()); // check access
        dtoWarehouseService.removeSkuOnWarehouse(skuWarehouseId);
        if (skuWarehouse != null) {
            reindexService.reindexProductSku(skuWarehouse.getSku().getSkuId());
        }
    }

    /**
     * {@inheritDoc
     */
    public SkuWarehouseDTO createSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(skuWarehouseDTO.getWarehouseId()); // check access
        SkuWarehouseDTO rez = dtoWarehouseService.createSkuOnWarehouse(skuWarehouseDTO);
        reindexService.reindexProductSku(rez.getProductSkuId());
        return rez;
    }

    /**
     * {@inheritDoc
     */
    public SkuWarehouseDTO updateSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(skuWarehouseDTO.getWarehouseId()); // check access
        SkuWarehouseDTO rez = dtoWarehouseService.updateSkuOnWarehouse(skuWarehouseDTO);
        reindexService.reindexProductSku(rez.getProductSkuId());
        return rez;
    }

    /**
     * {@inheritDoc
     */
    public List<SkuWarehouseDTO> findProductSkusOnWarehouse(final long productId, final long warehouseId) {
        try {
            getById(warehouseId); // check access
        } catch (UnmappedInterfaceException e) {
            // ok
        } catch (UnableToCreateInstanceException e) {
            // ok
        }
        return dtoWarehouseService.findProductSkusOnWarehouse(productId, warehouseId);
    }


    /**
     * {@inheritDoc}
     */
    public SkuWarehouseService getSkuWarehouseService() {
        return dtoWarehouseService.getSkuWarehouseService();
    }

}
