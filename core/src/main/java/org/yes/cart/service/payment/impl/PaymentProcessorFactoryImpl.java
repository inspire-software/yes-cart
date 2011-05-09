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
    public PaymentProcessor create(final String paymentGatewayLabel) {

        final PaymentProcessor paymentProcessor = (PaymentProcessor) applicationContext.getBean(paymentProcessorBeanId);

        paymentProcessor.setPaymentGateway(
                paymentModulesManager.getPaymentGateway(paymentGatewayLabel)
        );

        return paymentProcessor;
    }
}
