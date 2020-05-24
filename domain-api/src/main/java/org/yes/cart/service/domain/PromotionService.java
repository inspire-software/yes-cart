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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Promotion;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-19
 * Time: 11:03 PM
 */
public interface PromotionService extends GenericService<Promotion> {

    /**
     * Get all promotions by shop code and currency.
     *
     * @param shopCode shop code
     * @param currency currency
     * @param active if false then select all promotions for given shop, if true only enabled
     *               promotions with enabledFrom and enabledTo times within current time
     *
     * @return list of promotions
     */
    List<Promotion> getPromotionsByShopCode(String shopCode, String currency, boolean active);

    /**
     * Get promotion id by given code
     *
     * @param code given code
     * @param active true if only active promotions to be retrieved
     *
     * @return promotion id if found otherwise null
     */
    Promotion findPromotionByCode(String code, boolean active);

    /**
     * Get promotion id by given code
     *
     * @param code given code
     *
     * @return promotion id if found otherwise null
     */
    Long findPromotionIdByCode(String code);




    /**
     * Find promotions by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list of promotions.
     */
    List<Promotion> findPromotions(int start,
                                   int offset,
                                   String sort,
                                   boolean sortDescending,
                                   Map<String, List> filter);

    /**
     * Find promotions by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findPromotionCount(Map<String, List> filter);



}
