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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-19
 * Time: 1:46 PM
 */
public interface PromotionAction extends Serializable {

    /**
     * Test discount value as percentage of total benefit over sale
     * price.
     *
     * E.g.
     * - item fixed discount of 100 for item priced 150 is 0.67
     * - percentage discount of 30% for item of any price is 0.3
     * - gift item worth 60 for item priced 120 is 0.5 (for 2 items 0.25)
     *
     * @param context context
     *
     * @return value 0 to 1 as percentage discount on overall sale value
     */
    BigDecimal testDiscountValue(Map<String, Object> context);

    /**
     * Apply the promotion action on shopping cart using given
     * context object.
     *
     * @param context context object
     */
    void perform(Map<String, Object> context);

}
