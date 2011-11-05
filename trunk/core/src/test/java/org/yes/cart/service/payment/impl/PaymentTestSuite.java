package org.yes.cart.service.payment.impl;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/14/11
 * Time: 3:32 PM
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestPaymentCallBackHandlerFacadeImpl.class,
        TestPaymentModulesManagerImpl.class,
        TestPaymentProcessorFactoryImpl.class,
        TestPaymentProcessorImpl.class

})
public class PaymentTestSuite {
}
