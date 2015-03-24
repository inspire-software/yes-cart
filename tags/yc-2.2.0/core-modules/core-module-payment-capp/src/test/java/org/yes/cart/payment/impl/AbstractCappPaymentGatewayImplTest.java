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

package org.yes.cart.payment.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 28/09/2014
 * Time: 11:57
 */
public class AbstractCappPaymentGatewayImplTest {

    private Mockery mockery = new JUnit4Mockery();


    @Test
    public void testGetParameterValue() throws Exception {

        final PaymentGatewayParameter defParam = mockery.mock(PaymentGatewayParameter.class, "defParam");
        final PaymentGatewayParameter shopParam = mockery.mock(PaymentGatewayParameter.class, "shopParam");

        final PaymentGatewayConfigurationVisitor visitor = mockery.mock(PaymentGatewayConfigurationVisitor.class, "visitor");

        mockery.checking(new Expectations() {{
            allowing(defParam).getLabel(); will(returnValue("param"));
            allowing(defParam).getValue(); will(returnValue("value"));
            allowing(shopParam).getLabel(); will(returnValue("#SHOP10_param"));
            allowing(shopParam).getValue(); will(returnValue("shopvalue"));
            allowing(visitor).getConfiguration("shopCode"); will(returnValue("SHOP10"));
        }});

        final AbstractCappPaymentGatewayImpl pg = new AbstractCappPaymentGatewayImpl() {
            @Override
            public Payment authorizeCapture(final Payment payment) {
                return null;
            }

            @Override
            public Payment authorize(final Payment payment) {
                return null;
            }

            @Override
            public Payment reverseAuthorization(final Payment payment) {
                return null;
            }

            @Override
            public Payment capture(final Payment payment) {
                return null;
            }

            @Override
            public Payment voidCapture(final Payment payment) {
                return null;
            }

            @Override
            public Payment refund(final Payment payment) {
                return null;
            }

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public PaymentGatewayFeature getPaymentGatewayFeatures() {
                return null;
            }

            @Override
            public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
                return Arrays.asList(defParam, shopParam);
            }
        };

        assertEquals("value", pg.getParameterValue("param"));
        pg.accept(visitor);
        assertEquals("shopvalue", pg.getParameterValue("param"));
        assertNull(pg.getParameterValue(null));
        assertNull(pg.getParameterValue("#SHOP10_param")); // Do not allow explicit access

    }
}
