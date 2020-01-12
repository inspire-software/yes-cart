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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

/**
 * Bulk inventory service.
 *
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:01 PM
 */
public interface DtoInventoryService {


    /**
     * Get inventory list by criteria.
     *
     * @param filter filter
     *
     * @return list
     *
     * @throws UnmappedInterfaceException error
     * @throws UnableToCreateInstanceException error
     */
    SearchResult<InventoryDTO> findInventory(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;


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
