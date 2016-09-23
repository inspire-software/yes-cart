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

import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:45 PM
 */
public interface DtoPromotionCouponService extends GenericDTOService<PromotionCouponDTO> {

    /**
     * Load all coupons belonging to a promotion.
     *
     * @param promotionId promotion PK
     *
     * @return promotions that satisfy criteria
     */
    List<PromotionCouponDTO> getCouponsByPromotionId(Long promotionId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Generate downloadable csv file containing list of coupons for a promotion.
     *
     * @param promotionId promotion pk
     *
     * @return csv file with list of coupons
     */
    byte[] getCouponsByPromotionIdExport(Long promotionId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Promotions by filter
     *
     * @param promotionId promotion PK
     * @param filter filter
     * @param page start page
     * @param pageSize page size
     * @return coupons
     */
    List<PromotionCouponDTO> findBy(long promotionId, String filter, int page, int pageSize)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Promotions by filter
     *
     * @param promotionId promotion PK
     * @param createdAfter created after
     * @return coupons
     */
    List<PromotionCouponDTO> findBy(long promotionId, Date createdAfter)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
