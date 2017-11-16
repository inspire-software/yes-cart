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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.CartContentsValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 14:00
 */
public abstract class AbstractCartContentsValidatorImpl implements CartContentsValidator {

    protected static final ValidationResult OK = new ValidationResultImpl(false) {
        @Override
        public void append(final ValidationResult validationResult) {
            throw new UnsupportedOperationException("Cannot append to OK result");
        }
    };


    protected static class ValidationResultImpl implements ValidationResult {

        private boolean checkoutBlocked;
        private List<Pair<String, Map<String, Object>>> messages = new ArrayList<Pair<String, Map<String, Object>>>();

        protected ValidationResultImpl(final boolean checkoutBlocked) {
            this.checkoutBlocked = checkoutBlocked;
        }

        protected ValidationResultImpl(final boolean checkoutBlocked, final Pair<String, Map<String, Object>> message) {
            this.checkoutBlocked = checkoutBlocked;
            this.messages.add(message);
        }

        protected ValidationResultImpl(final boolean checkoutBlocked, final List<Pair<String, Map<String, Object>>> messages) {
            this.checkoutBlocked = checkoutBlocked;
            this.messages.addAll(messages);
        }

        /** {@inheritDoc} */
        @Override
        public boolean isCheckoutBlocked() {
            return checkoutBlocked;
        }

        /** {@inheritDoc} */
        @Override
        public List<Pair<String, Map<String, Object>>> getMessages() {
            return Collections.unmodifiableList(this.messages);
        }

        /** {@inheritDoc} */
        @Override
        public void append(final ValidationResult validationResult) {
            this.checkoutBlocked = this.checkoutBlocked || validationResult.isCheckoutBlocked();
            this.messages.addAll(validationResult.getMessages());
        }

    }

}
