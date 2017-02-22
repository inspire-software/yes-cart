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

import org.yes.cart.domain.vo.VoPromotion;
import org.yes.cart.domain.vo.VoPromotionCoupon;

import java.util.List;

/**
 * User: denispavlov
 * Date: 20/09/2016
 * Time: 09:14
 */
public interface VoPromotionService {


    /**
     * Get all promotions in the system, filtered by criteria and according to rights, up to max
     * @return list of promotions
     * @throws Exception
     */
    List<VoPromotion> getFilteredPromotion(String shopCode, String currency, String filter, int max) throws Exception;

    /**
     * Get all promotions in the system, filtered by criteria and according to rights, up to max
     * @return list of promotions
     * @throws Exception
     */
    List<VoPromotion> getFilteredPromotion(String shopCode, String currency, String filter, List<String> types, List<String> actions, int max) throws Exception;

    /**
     * Get promotion by id.
     *
     * @param id promotion id
     * @return promotion vo
     * @throws Exception
     */
    VoPromotion getPromotionById(long id) throws Exception;

    /**
     * Update given promotion.
     *
     * @param vo promotion to update
     * @return updated instance
     * @throws Exception
     */
    VoPromotion updatePromotion(VoPromotion vo) throws Exception;

    /**
     * Create new promotion
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoPromotion createPromotion(VoPromotion vo) throws Exception;

    /**
     * Remove promotion by id.
     *
     * @param id promotion id
     * @throws Exception
     */
    void removePromotion(long id) throws Exception;

    /**
     * Update the promotion disabled flag.
     *
     * @param id promotion id
     * @param disabled true if promotion is disabled
     * @throws Exception
     */
    void updateDisabledFlag(long id, boolean disabled) throws Exception;




    /**
     * Get all promotion coupons in the system, filtered by criteria and according to rights, up to max
     * @return list of promotions
     * @throws Exception
     */
    List<VoPromotionCoupon> getFilteredPromotionCoupons(long promotionId, String filter, int max) throws Exception;

    /**
     * Create new promotion coupons.
     *
     * @param vo given instance template to persist
     * @return persisted instance
     * @throws Exception
     */
    List<VoPromotionCoupon> createPromotionCoupons(VoPromotionCoupon vo) throws Exception;

    /**
     * Remove promotion coupon by id.
     *
     * @param id promotion coupon id
     * @throws Exception
     */
    void removePromotionCoupon(long id) throws Exception;


}
