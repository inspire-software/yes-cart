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

package org.yes.cart.shoppingcart;

import org.yes.cart.domain.entity.SkuPrice;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 5:53 PM
 */
public interface DeliveryCostRegionalPriceResolver {

    /**
     * Resolve regional price for given CarrierSla using pricing framework.
     *
     * Note that this resolver is delivery cost strategy agnostic and can be used simply as regional availability
     * configuration.
     *
     * For example if CarrierSla.SlaType is R (Free) SkuPrice can be defined as 1EUR to denote that this carrier sla
     * is available for EUR currency.
     *
     * @param cart current cart
     * @param carrierSlaBaseCode currently this is CarrierSla.GUID
     * @param policy pricing policy to enforce
     * @param qty quantity tier (this is an arbitrary figure, OOTB it is considered number of deliveries, however it
     *            is possible to treat this as other tier, such as parcel weight calculated from product attributes)
     *
     */
    SkuPrice getSkuPrice(final ShoppingCart cart, final String carrierSlaBaseCode, final PricingPolicyProvider.PricingPolicy policy, final BigDecimal qty);

}
