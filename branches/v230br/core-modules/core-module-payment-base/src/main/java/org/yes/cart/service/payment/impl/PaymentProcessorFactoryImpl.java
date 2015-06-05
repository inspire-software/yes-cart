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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorFactoryImpl implements ApplicationContextAware, PaymentProcessorFactory {

    private ApplicationContext applicationContext;

    private final PaymentModulesManager paymentModulesManager;
    private final String paymentProcessorBeanId;

    /** {@inheritDoc} */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * Construct factory of payment parocessors.
     * @param paymentModulesManager {@link PaymentModulesManager}
     * @param paymentProcessorBeanId bean id of selected payment gateway.
     */
    public PaymentProcessorFactoryImpl(final PaymentModulesManager paymentModulesManager, final String paymentProcessorBeanId) {
        this.paymentModulesManager = paymentModulesManager;
        this.paymentProcessorBeanId = paymentProcessorBeanId;
    }
    

    /** {@inheritDoc} */
    public PaymentProcessor create(final String paymentGatewayLabel, final String shopCode) {

        final PaymentProcessor paymentProcessor = (PaymentProcessor) applicationContext.getBean(paymentProcessorBeanId);

        paymentProcessor.setPaymentGateway(
                paymentModulesManager.getPaymentGateway(paymentGatewayLabel, shopCode)
        );

        return paymentProcessor;
    }
}
