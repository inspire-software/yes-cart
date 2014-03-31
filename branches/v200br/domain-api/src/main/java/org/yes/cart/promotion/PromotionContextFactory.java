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

package org.yes.cart.promotion;

/**
 * Promotion context factory constructs the promotion context object
 * that holds pre-compiled active promotions for current time frame.
 *
 * The context object should be cached to avoid unnecessary DB access
 * but cache settings should have adequate timeouts to facilitate
 * relevance. (i.e. if we have promotions that are changing on daily
 * basis then context can be cached for full day).
 *
 * User: denispavlov
 * Date: 13-10-29
 * Time: 8:11 AM
 */
public interface PromotionContextFactory {

    /**
     * Get promotion context for given shop for
     * current time frame.
     *
     *
     * @param shopCode shop code
     * @param currency currency
     *
     * @return context
     */
    PromotionContext getInstance(String shopCode, String currency);

    /**
     * Get promotion context for given shop for
     * current time frame.
     *
     *
     * @param shopCode shop code
     * @param currency currency
     * @param ensureNew guaranties that this context is created from fresh
     *                  DB data.
     *  @return context
     */
    PromotionContext getInstance(String shopCode, final String currency, boolean ensureNew);

    /**
     * Get promotion context for given shop for
     * current time frame.
     *
     *
     * @param shopId shop PK
     * @param currency currency
     *
     * @return context
     */
    PromotionContext getInstance(long shopId, final String currency);

    /**
     * Get promotion context for given shop for
     * current time frame.
     *
     *
     *
     * @param shopId shop PK
     * @param currency currency
     * @param ensureNew guaranties that this context is created from fresh
     *                  DB data.
     *
     * @return context
     */
    PromotionContext getInstance(long shopId, final String currency, boolean ensureNew);

}
