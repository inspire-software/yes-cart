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

import org.yes.cart.domain.misc.Pair;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 13:52
 */
public interface CartContentsValidator {

    interface ValidationResult {

        /**
         * Flag if checkout should be blocked
         *
         * @return block flag
         */
        boolean isCheckoutBlocked();

        /**
         * List of message keys and corresponding parameters for the UI.
         *
         * @return
         */
        List<Pair<String, Map<String, Object>>> getMessages();

        /**
         * Append validation result to this one.
         *
         * @param validationResult result to append
         */
        void append(ValidationResult validationResult);

    }

    /**
     * Validate shopping cart.
     *
     * @param cart cart to validate
     *
     * @return validation result
     */
    ValidationResult validate(ShoppingCart cart);

}
