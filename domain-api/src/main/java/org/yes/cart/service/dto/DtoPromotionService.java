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

import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:45 PM
 */
public interface DtoPromotionService extends GenericDTOService<PromotionDTO> {

    /**
     * Customer search function to find promotions by given parameters.
     *
     * @param code promo code
     * @param shopCode optional shop code
     * @param currency optional currency
     * @param tag optional tag
     * @param type optional type
     * @param action optional action
     * @param enabled optional enabled
     *
     * @return promotions that satisfy criteria
     */
    List<PromotionDTO> findByParameters(String code,
                                        String shopCode,
                                        String currency,
                                        String tag,
                                        String type,
                                        String action,
                                        Boolean enabled)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;



    /**
     * Promotions by filter
     *
     * @param shopCode shop
     * @param currency currency
     * @param filter filter
     * @param page start page
     * @param pageSize page size
     * @return promotions
     */
    List<PromotionDTO> findBy(String shopCode, String currency, String filter, int page, int pageSize)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Promotions by filter
     *
     * @param shopCode shop
     * @param currency currency
     * @param filter filter
     * @param types types
     * @param actions actions
     * @param page start page
     * @param pageSize page size
     * @return promotions
     */
    List<PromotionDTO> findBy(String shopCode, String currency, String filter, List<String> types, List<String> actions, int page, int pageSize)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Customer search function to find promotions by given parameters.
     *
     * @param codes promo codes
     *
     * @return promotions that satisfy criteria
     */
    List<PromotionDTO> findByCodes(Set<String> codes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
