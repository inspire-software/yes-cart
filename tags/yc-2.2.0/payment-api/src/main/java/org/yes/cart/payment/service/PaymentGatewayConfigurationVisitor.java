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

package org.yes.cart.payment.service;

import org.yes.cart.payment.PaymentGateway;

import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 27/09/2014
 * Time: 14:19
 */
public interface PaymentGatewayConfigurationVisitor {

    /**
     * @return configuration keys
     */
    Set<String> getConfigurationKeys();

    /**
     * @param key config key
     *
     * @return configuration value
     */
    <T> T getConfiguration(String key);

    /**
     * Visit payment gateway to configure it with runtime parameters.
     *
     * @param paymentGateway payment gateway
     */
    void visit(PaymentGateway paymentGateway);

}
