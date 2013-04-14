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

package org.yes.cart.service.payment.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

import static org.junit.Assert.assertNotSame;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorFactoryImplTest extends BaseCoreDBTestCase {

    private PaymentProcessorFactory paymentProcessorFactory;

    @Before
    public void setUp() throws Exception {
        paymentProcessorFactory = (PaymentProcessorFactory) ctx().getBean(ServiceSpringKeys.PAYMENT_PROCESSOR_FACTORY);
    }

    /**
     * Test that two instance are different , because of prototypw scope in spring
     */
    @Test
    public void testCreate() {
        PaymentProcessor paymentProcessor0 = paymentProcessorFactory.create("testPaymentGatewayLabel");
        PaymentProcessor paymentProcessor1 = paymentProcessorFactory.create("testPaymentGatewayLabel");
        assertNotSame(paymentProcessor0, paymentProcessor1);
    }
}
