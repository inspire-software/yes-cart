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

import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.support.InventoryFilter;

import java.util.List;

/**
 * Bulk inventory service.
 *
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:01 PM
 */
public interface DtoInventoryService {

    /**
     * List of all warehouses.
     *
     * @return list of warehouses
     */
    List<WarehouseDTO> getWarehouses() throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Inventory by filter.
     *
     * @param filter inventory filter
     * @return inventory
     */
    List<InventoryDTO> getInventoryList(InventoryFilter filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create or update inventory object.
     *
     * @param inventory inventory
     * @return persistent inventory object
     */
    InventoryDTO createInventory(InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create or update inventory object.
     *
     * @param inventory inventory
     * @return persistent inventory object
     */
    InventoryDTO updateInventory(InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Remove sku warehouse object by given pk value
     *
     * @param skuWarehouseId given pk value.
     */
    InventoryDTO getInventory(long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Remove sku warehouse object by given pk value
     *
     * @param skuWarehouseId given pk value.
     */
    void removeInventory(long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
