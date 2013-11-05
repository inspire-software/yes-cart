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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Promotion;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-19
 * Time: 11:03 PM
 */
public interface PromotionService extends GenericService<Promotion> {

    /**
     * Get all promotions by shop code.
     *
     *
     *
     * @param shopCode shop code
     * @param currency currency
     * @param active if false then select all promotions for given shop, if true only enabled
     *               promotions with enabledFrom and enabledTo times within current time
     *
     * @return list of promotions
     */
    List<Promotion> getPromotionsByShopCode(String shopCode, final String currency, boolean active);

    /**
     * Customer search function to find promotions by given parameters.
     *
     * @param code promo code
     * @param shopCode optional shop code
     * @param currency optional currency
     * @param tag tag
     * @param type optional type
     * @param action optional action
     * @param enabled optional enabled
     *
     * @return promotions that satisfy criteria
     */
    List<Promotion> findByParameters(String code,
                                     String shopCode,
                                     String currency,
                                     String tag,
                                     String type,
                                     String action,
                                     Boolean enabled);

}
