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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.SkuWarehouseService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoWarehouseService extends GenericDTOService<WarehouseDTO> {

    /**
     * Find wharehouses, that assigned to given shop id.
     *
     * @param shopId given shop id
     * @return list of assigned warehouses
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<WarehouseDTO> findByShopId(long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Set usage rank to ShopWarehouseDTO.
     * @param shopWarehouseId   pk of given ShopWarehouseDTO
     * @param newRank  rank to set
     */
    void setShopWarehouseRank(long shopWarehouseId, int newRank);

    /**
     * Assign given warehouse to given shop.
     *
     * @param warehouseId warehouse id
     * @param shopId      shop id
     * @return {@link ShopWarehouseDTO}
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    ShopWarehouseDTO assignWarehouse(long warehouseId, long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Unassign given warehouse from shop
     *
     * @param warehouseId warehouse id
     * @param shopId      shop id
     */
    void unassignWarehouse(long warehouseId, long shopId);

    /**
     * Remove sku warehouse object by given pk value
     *
     * @param skuWarehouseId given pk value.
     */
    void removeSkuOnWarehouse(long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create given {@link org.yes.cart.domain.dto.SkuWarehouseDTO}
     *
     * @param skuWarehouseDTO given {@link org.yes.cart.domain.dto.SkuWarehouseDTO}
     * @return created SkuWarehouseDTO.
     */
    SkuWarehouseDTO createSkuOnWarehouse(SkuWarehouseDTO skuWarehouseDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Update given {@link SkuWarehouseDTO}
     *
     * @param skuWarehouseDTO given {@link SkuWarehouseDTO}
     * @return updated SkuWarehouseDTO.
     */
    SkuWarehouseDTO updateSkuOnWarehouse(SkuWarehouseDTO skuWarehouseDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Dind product skus quantity objects on given warehouse.
     *
     * @param productId   given product id
     * @param warehouseId given warehouse id.
     * @return list of founded {@link SkuWarehouseDTO}
     */
    List<SkuWarehouseDTO> findProductSkusOnWarehouse(long productId, long warehouseId);


    /**
     * Get the {@link org.yes.cart.service.domain.SkuWarehouseService}. Test usage only.
     *
     * @return {@link org.yes.cart.service.domain.SkuWarehouseService}
     */
    SkuWarehouseService getSkuWarehouseService();


}
