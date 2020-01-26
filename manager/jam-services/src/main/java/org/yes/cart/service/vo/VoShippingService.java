/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
 * Date: 28/07/2016
 * Time: 18:18
 */
public interface VoShippingService {


    /**
     * Get all vo in the system, filtered according to rights
     *
     * @return all carriers
     *
     * @throws Exception errors
     */
    VoSearchResult<VoCarrierInfo> getFilteredCarriers(VoSearchContext filter) throws Exception;

    /**
     * Get all vo in the system, filtered according to rights
     *
     * @param shopId pk
     *
     * @return all carriers for shop
     *
     * @throws Exception errors
     */
    List<VoShopCarrier> getShopCarriers(long shopId) throws Exception;

    /**
     * Get all vo in the system, filtered according to rights
     *
     * @param shopId pk
     *
     * @return all carriers for shop
     *
     * @throws Exception errors
     */
    List<VoShopCarrierAndSla> getShopCarriersAndSla(long shopId) throws Exception;


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
    VoCarrier getCarrierById(long id) throws Exception;

    /**
     * Create new vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoCarrier createCarrier(VoCarrier vo)  throws Exception;

    /**
     * Create new vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoCarrier createShopCarrier(VoCarrierInfo vo, long shopId)  throws Exception;


    /**
     * Create new vo
     *
     * @param vo carrier
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoCarrier updateCarrier(VoCarrier vo)  throws Exception;


    /**
     * Create new vo
     *
     * @param vo carrier
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    List<VoShopCarrier> updateShopCarriers(List<VoShopCarrier> vo)  throws Exception;


    /**
     * Create new vo
     *
     * @param vo carrier
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    List<VoShopCarrierAndSla> updateShopCarriersAndSla(List<VoShopCarrierAndSla> vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id carrier id
     *
     * @throws Exception errors
     */
    void removeCarrier(long id) throws Exception;


    /**
     * Get all vo in the system
     *
     * @param carrierId pk
     *
     * @return all SLA for carrier
     *
     * @throws Exception errors
     */
    List<VoCarrierSla> getCarrierSlas(long carrierId) throws Exception;


    /**
     * Get all vo in the system, filtered according to rights
     *
     * @return all carriers for shop
     *
     * @throws Exception errors
     */
    VoSearchResult<VoCarrierSlaInfo> getFilteredCarrierSlas(VoSearchContext filter) throws Exception;

    /**
     * Get vo by id.
     *
     * @param id id
     *
     * @return vo
     *
     * @throws Exception errors
     */
    VoCarrierSla getCarrierSlaById(long id) throws Exception;

    /**
     * Create new vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoCarrierSla createCarrierSla(VoCarrierSla vo)  throws Exception;


    /**
     * Create new vo
     *
     * @param vo carrier
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoCarrierSla updateCarrierSla(VoCarrierSla vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id carrier sla id
     *
     * @throws Exception errors
     */
    void removeCarrierSla(long id) throws Exception;



}
