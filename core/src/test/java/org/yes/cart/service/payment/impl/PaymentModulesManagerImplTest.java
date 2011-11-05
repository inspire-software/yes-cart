package org.yes.cart.service.payment.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.Descriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test some OOTB payment modules.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentModulesManagerImplTest extends BaseCoreDBTestCase {

    PaymentModulesManager paymentModulesManager;

    @Before
    public void setUp() throws Exception {
        paymentModulesManager = (PaymentModulesManager) ctx.getBean(ServiceSpringKeys.PAYMENT_MODULES_MANAGER);
    }

    /**
     * Get list of payment modules. At least one payment module available in OOTB configuration.
     */
    @Test
    public void testGetPaymentModules() {
        Collection<PaymentModule> modules = paymentModulesManager.getPaymentModules();
        assertEquals(1, modules.size());
        Descriptor descriptor = modules.iterator().next().getPaymentModuleDescriptor();
        assertEquals("basePaymentModule", descriptor.getLabel());
    }

    /**
     * Get all payment gateways in specified by given label module.
     */
    @Test
    public void testGetPaymentGateways() {
        Collection<PaymentGatewayDescriptor> paymentGateways = paymentModulesManager.getPaymentGatewaysDescriptors("basePaymentModule");
        assertEquals(3, paymentGateways.size());
        paymentGateways = paymentModulesManager.getPaymentGatewaysDescriptors(true);
        assertEquals(3, paymentGateways.size());
    }

    /**
     * Get payment gateway by given pg label.
     */
    @Test
    public void testGetPaymentGateway() {
        PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway("testPaymentGatewayLabel");
        assertNotNull(paymentGateway);
        assertEquals("testPaymentGateway", paymentGateway.getLabel());
    }
}
