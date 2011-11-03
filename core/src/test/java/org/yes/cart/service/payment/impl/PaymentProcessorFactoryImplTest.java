package org.yes.cart.service.payment.impl;

import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorFactoryImplTest extends BaseCoreDBTestCase {

    private PaymentProcessorFactory paymentProcessorFactory;


    public void setUp() throws Exception {

        super.setUp();
        paymentProcessorFactory = (PaymentProcessorFactory) ctx.getBean(ServiceSpringKeys.PAYMENT_PROCESSOR_FACTORY);
    }

    /**
     * Test that two instance are different , because of prototypw scope in spring
     */
    public void testCreate() {
        final PaymentProcessor paymentProcessor0 = paymentProcessorFactory.create("testPaymentGatewayLabel");
        final PaymentProcessor paymentProcessor1 = paymentProcessorFactory.create("testPaymentGatewayLabel");
        assertNotSame(paymentProcessor0, paymentProcessor1);
    }


}
