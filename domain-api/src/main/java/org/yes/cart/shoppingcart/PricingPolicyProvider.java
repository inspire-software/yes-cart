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

/**
 * User: denispavlov
 * Date: 07/06/2016
 * Time: 18:31
 */
public interface PricingPolicyProvider {

    interface PricingPolicy {

        enum Type {
            // Customer specific price list overwrites all other settings
            CUSTOMER(0),
            // Regional pricing set on the location (country or state)
            STATE(100),
            COUNTRY(200),
            // Default pricing available to all customers
            DEFAULT(1000);

            private final int priority;

            Type(final int priority) {
                this.priority = priority;
            }

            public int getPriority() {
                return priority;
            }
        }

        /**
         * @return pricing policy ID {@link SkuPrice#getPricingPolicy()}
         */
        String getID();


        /**
         * @return pricing policy type
         */
        Type getType();

    }

    /**
     * Determine current customer pricing policy.
     *
     * @param shopCode shop code
     * @param currency current currency
     * @param customerEmail optional customer email (use null for anonymous)
     * @param countryCode country code
     * @param stateCode state code
     *
     * @return pricing policy
     */
    PricingPolicy determinePricingPolicy(String shopCode, String currency, String customerEmail, String countryCode, String stateCode);

}
