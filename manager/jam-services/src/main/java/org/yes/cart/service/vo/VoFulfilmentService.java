/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 19/08/2016
 * Time: 10:37
 */
public interface VoFulfilmentService {



    /**
     * Get all vo in the system, filtered according to rights
     *
     * @return all fulfilment centers
     *
     * @throws Exception errors
     */
    VoSearchResult<VoFulfilmentCentreInfo> getFilteredFulfilmentCentres(VoSearchContext filter) throws Exception;

    /**
     * Get all vo in the system, filtered according to rights
     *
     * @param shopId pk
     *
     * @return all fulfilment centers for shop
     *
     * @throws Exception errors
     */
    List<VoShopFulfilmentCentre> getShopFulfilmentCentres(long shopId) throws Exception;

    /**
     * Get summary information for given shop.
     *
     * @param summary summary object to fill data for
     * @param shopId given shop
     * @param lang locale for localised names
     *
     * @throws Exception errors
     */
    void fillShopSummaryDetails(VoShopSummary summary, long shopId, String lang) throws Exception;


    /**
     * Get vo by id.
     *
     * @param id id
     *
     * @return vo
     *
     * @throws Exception errors
     */
    VoFulfilmentCentre getFulfilmentCentreById(long id) throws Exception;

    /**
     * Create new vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoFulfilmentCentre createFulfilmentCentre(VoFulfilmentCentre vo)  throws Exception;

    /**
     * Create new vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoFulfilmentCentre createShopFulfilmentCentre(VoFulfilmentCentreInfo vo, long shopId)  throws Exception;


    /**
     * Create new vo
     *
     * @param vo fulfilment center
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoFulfilmentCentre updateFulfilmentCentre(VoFulfilmentCentre vo)  throws Exception;


    /**
     * Create new vo
     *
     * @param vo fulfilment center
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    List<VoShopFulfilmentCentre> updateShopFulfilmentCentres(List<VoShopFulfilmentCentre> vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id fulfilment center id
     *
     * @throws Exception errors
     */
    void removeFulfilmentCentre(long id) throws Exception;

    /**
     * Get inventory for given fulfilment centre.
     *
     * @param filter filter
     *
     * @return results
     *
     * @throws Exception errors
     */
    VoSearchResult<VoInventory> getFilteredInventory(VoSearchContext filter) throws Exception;

    /**
     * Get inventory by id.
     *
     * @param id inventory id
     *
     * @return inventory vo
     *
     * @throws Exception errors
     */
    VoInventory getInventoryById(long id) throws Exception;

    /**
     * Update given inventory.
     *
     * @param vo inventory to update
     *
     * @return updated instance
     *
     * @throws Exception errors
     */
    VoInventory updateInventory(VoInventory vo) throws Exception;

    /**
     * Create new inventory
     *
     * @param vo given instance to persist
     *
     * @return persisted instance
     *
     * @throws Exception errors
     */
    VoInventory createInventory(VoInventory vo) throws Exception;

    /**
     * Remove inventory by id.
     *
     * @param id inventory id
     *
     * @throws Exception errors
     */
    void removeInventory(long id) throws Exception;


}
