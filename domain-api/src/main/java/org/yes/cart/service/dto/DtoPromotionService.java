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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:45 PM
 */
public interface DtoPromotionService extends GenericDTOService<PromotionDTO> {



    /**
     * Get promotions list by criteria.
     *
     * @param filter filter
     *
     * @return list
     *
     * @throws UnmappedInterfaceException error
     * @throws UnableToCreateInstanceException error
     */
    SearchResult<PromotionDTO> findPromotions(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Customer search function to find promotions by given parameters.
     *
     * @param codes promo codes
     *
     * @return promotions that satisfy criteria
     */
    List<PromotionDTO> findByCodes(Set<String> codes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get all promotion types and applicable actions
     *
     * @return type and actions mapping will contain the codes
     */
    List<Pair<String, List<String>>> findPromotionOptions();

    /**
     * Test promotions.
     *
     * @param shopCode shop code
     * @param currency currency
     * @param language language
     * @param customer customer (optional)
     * @param supplier supplier
     * @param products products SKU and corresponding quantities
     * @param shipping shipping SLA
     * @param coupons  coupon codes
     * @param time     time now
     *
     * @return generated prices
     */
    ShoppingCart testPromotions(String shopCode,
                                String currency,
                                String language,
                                String customer,
                                String supplier,
                                Map<String, BigDecimal> products,
                                String shipping,
                                List<String> coupons,
                                Instant time);

}
