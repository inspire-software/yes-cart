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

import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteWarehouseService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.dto.DtoWarehouseService;

import java.util.List;

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

    private final ProductSkuService productSkuService;


    /**
     * Construct service.
     *
     * @param service dto service to use
     * @param reindexService product reindex service
     */
    public RemoteWarehouseServiceImpl(
            final DtoWarehouseService service,
            final ReindexService reindexService,
            final ProductSkuService productSkuService) {
        super(service);
        this.dtoWarehouseService = service;
        this.reindexService = reindexService;
        this.productSkuService = productSkuService;
    }


    /**
     * {@inheritDoc
     */
    public List<WarehouseDTO> findByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoWarehouseService.findByShopId(shopId);
    }

    public void setShopWarehouseRank(long shopWarehouseId, int newRank) {
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
        dtoWarehouseService.removeSkuOnWarehouse(skuWarehouseId);
        if (skuWarehouse != null) {
            reindexService.reindexProductSku(skuWarehouse.getSku().getSkuId());
        }
    }

    /**
     * {@inheritDoc
     */
    public SkuWarehouseDTO createSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        SkuWarehouseDTO rez = dtoWarehouseService.createSkuOnWarehouse(skuWarehouseDTO);
        reindexService.reindexProductSku(rez.getProductSkuId());
        return rez;
    }

    /**
     * {@inheritDoc
     */
    public SkuWarehouseDTO updateSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        SkuWarehouseDTO rez = dtoWarehouseService.updateSkuOnWarehouse(skuWarehouseDTO);
        reindexService.reindexProductSku(rez.getProductSkuId());
        return rez;
    }

    /**
     * {@inheritDoc
     */
    public List<SkuWarehouseDTO> findProductSkusOnWarehouse(final long productId, final long warehouseId) {
        return dtoWarehouseService.findProductSkusOnWarehouse(productId, warehouseId);
    }


    /**
     * {@inheritDoc}
     */
    public SkuWarehouseService getSkuWarehouseService() {
        return dtoWarehouseService.getSkuWarehouseService();
    }

}
