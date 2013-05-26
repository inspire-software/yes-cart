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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.Descriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.domain.SystemService;
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

    private Mockery mockery = new JUnit4Mockery();

    

    PaymentModulesManager paymentModulesManager;


    @Before
    public void setUp() {
        
        
        paymentModulesManager = (PaymentModulesManager) ctx().getBean(ServiceSpringKeys.PAYMENT_MODULES_MANAGER);

        super.setUp();


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


    @Test
    public void testAllowPaymentGateway() {
        final SystemService systemService = mockery.mock(SystemService.class);

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("aaa,bbb,ccc,ddd"));
        }});
        mockery.checking(new Expectations() {{
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "aaa,bbb,ccc,ddd,eee");
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("aaa,bbb"));
        }});


        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("xx,yy,"));
        }});
        mockery.checking(new Expectations() {{
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "xx,yy,zz");
        }});



        PaymentModulesManager pmm = new PaymentModulesManagerImpl( null, systemService );
        pmm.allowPaymentGateway("eee");

        pmm = new PaymentModulesManagerImpl( null, systemService );
        pmm.allowPaymentGateway("aaa");

        pmm = new PaymentModulesManagerImpl( null, systemService );
        pmm.allowPaymentGateway("zz");


    }

    @Test
    public void testdisallowPaymentGateway() {
        final SystemService systemService = mockery.mock(SystemService.class);

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("aaa,bbb,ccc,ddd"));
        }});
        mockery.checking(new Expectations() {{
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "aaa,bbb,ddd");
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("1,2,3"));
        }});
        mockery.checking(new Expectations() {{
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "2,3");
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("xxx,yyy,zzz"));
        }});
        mockery.checking(new Expectations() {{
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "xxx,yyy");
        }});




        PaymentModulesManager pmm = new PaymentModulesManagerImpl( null, systemService );
        pmm.disallowPaymentGateway("ccc");

        pmm = new PaymentModulesManagerImpl( null, systemService );
        pmm.disallowPaymentGateway("1");

        pmm = new PaymentModulesManagerImpl( null, systemService );
        pmm.disallowPaymentGateway("zzz");


    }

}
