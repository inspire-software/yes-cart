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

package org.yes.cart.payment.service.impl;

import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.service.ConfigurablePaymentGateway;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;

import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 27/09/2014
 * Time: 14:22
 */
public class PaymentGatewayConfigurationVisitorImpl implements PaymentGatewayConfigurationVisitor {

    private final Map<String, Object> configuration;

    public PaymentGatewayConfigurationVisitorImpl(final Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getConfigurationKeys() {
        return configuration.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getConfiguration(final String key) {
        return (T) configuration.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public void visit(final PaymentGateway paymentGateway) {

        if (paymentGateway instanceof ConfigurablePaymentGateway) {
            // only configurable PG's can be configured
            ((ConfigurablePaymentGateway) paymentGateway).accept(this);
        }

    }
}
