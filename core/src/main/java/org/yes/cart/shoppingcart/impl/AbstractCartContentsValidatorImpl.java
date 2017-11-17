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

import org.yes.cart.shoppingcart.CartContentsValidator;
import org.yes.cart.shoppingcart.CartValidityModel;
import org.yes.cart.shoppingcart.CartValidityModelMessage;

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

    protected static final CartValidityModelImpl OK = new CartValidityModelImpl() {
        @Override
        public void append(final CartValidityModel model) {
            throw new UnsupportedOperationException("Cannot append to OK result");
        }
    };

    protected static class CartValidityModelMessageImpl implements CartValidityModelMessage {

        private final boolean checkoutBlocking;
        private final MessageType messageType;
        private final String messageKey;
        private final Map<String, String> messageArgs;


        public CartValidityModelMessageImpl(final boolean checkoutBlocking,
                                            final MessageType messageType,
                                            final String messageKey,
                                            final Map<String, String> messageArgs) {
            this.checkoutBlocking = checkoutBlocking;
            this.messageType = messageType;
            this.messageKey = messageKey;
            this.messageArgs = messageArgs;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isCheckoutBlocking() {
            return checkoutBlocking;
        }

        /** {@inheritDoc} */
        @Override
        public MessageType getMessageType() {
            return messageType;
        }

        /** {@inheritDoc} */
        @Override
        public String getMessageKey() {
            return messageKey;
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, String> getMessageArgs() {
            return messageArgs;
        }
        
    }

    protected static class CartValidityModelImpl implements CartValidityModel {

        private final List<CartValidityModelMessage> messages = new ArrayList<CartValidityModelMessage>();

        protected CartValidityModelImpl() {
        }

        protected CartValidityModelImpl(final CartValidityModelMessage message) {
            this.messages.add(message);
        }

        protected CartValidityModelImpl(final List<CartValidityModelMessage> messages) {
            this.messages.addAll(messages);
        }

        /** {@inheritDoc} */
        @Override
        public boolean isCheckoutBlocked() {
            for (final CartValidityModelMessage message : this.messages) {
                if (message.isCheckoutBlocking()) {
                    return true;
                }
            }
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public List<CartValidityModelMessage> getMessages() {
            return Collections.unmodifiableList(this.messages);
        }

        /** {@inheritDoc} */
        @Override
        public void append(final CartValidityModel model) {
            this.messages.addAll(model.getMessages());
        }

    }

}
