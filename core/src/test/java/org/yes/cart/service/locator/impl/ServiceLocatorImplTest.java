package org.yes.cart.service.locator.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.locator.ServiceLocator;
import org.yes.cart.service.order.DeliveryAssembler;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ServiceLocatorImplTest extends BaseCoreDBTestCase {

    private ServiceLocator serviceLocator;

    @Before
    public void setUp() throws Exception {
        serviceLocator = (ServiceLocator) ctx.getBean(ServiceSpringKeys.SERVICE_LOCATOR);
    }

    @Test
    public void testGetServiceInstance() throws Exception {
        //find self
        assertTrue(
                serviceLocator.getServiceInstance(ServiceSpringKeys.SERVICE_LOCATOR, ServiceLocator.class, null, null)
                        instanceof
                        ServiceLocator
        );

        //find another bean
        assertTrue(
                serviceLocator.getServiceInstance(ServiceSpringKeys.DELIVERY_ASSEMBLER, DeliveryAssembler.class, null, null)
                        instanceof
                        DeliveryAssembler
        );

        //find another bean in different module
        assertTrue(
                serviceLocator.getServiceInstance("basePaymentModule", PaymentModule.class, null, null)
                        instanceof
                        PaymentModule
        );
    }

    @Test
    public void testGetStrategyKey() throws Exception {
        ServiceLocatorImpl serviceLocatorImpl = new ServiceLocatorImpl(null);
        assertNull(serviceLocatorImpl.getStrategyKey("leninAndBenderZhiveeSvesZhivih"));
        assertEquals("http", serviceLocatorImpl.getStrategyKey("http://azarny.com"));
        assertEquals("https", serviceLocatorImpl.getStrategyKey("https://azarny.com"));
        assertEquals("jnp", serviceLocatorImpl.getStrategyKey("jnp://azarny.com/namespace@java:comp/env/jdbc/SavingsAccountDB"));
    }
}
