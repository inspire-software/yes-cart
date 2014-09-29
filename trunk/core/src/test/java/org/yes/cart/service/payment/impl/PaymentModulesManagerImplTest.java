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
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentModule;
import org.yes.cart.payment.persistence.entity.Descriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.domain.ShopService;
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
        paymentGateways = paymentModulesManager.getPaymentGatewaysDescriptors(true, null);
        assertEquals(3, paymentGateways.size());
        paymentGateways = paymentModulesManager.getPaymentGatewaysDescriptors(true, "DEFAULT");
        assertEquals(3, paymentGateways.size());
        paymentGateways = paymentModulesManager.getPaymentGatewaysDescriptors(true, "SHOIP1");
        assertEquals(3, paymentGateways.size());
        paymentGateways = paymentModulesManager.getPaymentGatewaysDescriptors(false, "SHOIP1");
        assertEquals(2, paymentGateways.size());
    }

    /**
     * Get payment gateway by given pg label.
     */
    @Test
    public void testGetPaymentGateway() {
        PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway("testPaymentGatewayLabel", "SHOIP1");
        assertNotNull(paymentGateway);
        assertEquals("testPaymentGateway", paymentGateway.getLabel());
    }


    @Test
    public void testAllowPaymentGateway() {
        final SystemService systemService = mockery.mock(SystemService.class);

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL); will(returnValue("aaa,bbb,ccc,ddd"));
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "aaa,bbb,ccc,ddd,eee");
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("aaa,bbb"));
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("xx,yy,"));
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "xx,yy,zz");
        }});


        PaymentModulesManager pmm = new PaymentModulesManagerImpl( null, systemService, null);
        pmm.allowPaymentGateway("eee");

        pmm = new PaymentModulesManagerImpl( null, systemService, null);
        pmm.allowPaymentGateway("aaa");

        pmm = new PaymentModulesManagerImpl( null, systemService, null);
        pmm.allowPaymentGateway("zz");


    }


    @Test
    public void testAllowPaymentGatewayForShop() {
        final ShopService shopService = mockery.mock(ShopService.class);
        final Shop shop = mockery.mock(Shop.class);
        final AttrValueShop attrValueShop = mockery.mock(AttrValueShop.class);

        mockery.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10");  will(returnValue(shop));
            allowing(shop).getShopId();  will(returnValue(10L));
            allowing(shop).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue(attrValueShop));
        }});

        mockery.checking(new Expectations() {{
            oneOf(attrValueShop).getVal();  will(returnValue("aaa,bbb,ccc,ddd"));
            oneOf(shopService).updateAttributeValue(10L, AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL, "aaa,bbb,ccc,ddd,eee");
        }});

        mockery.checking(new Expectations() {{
            oneOf(attrValueShop).getVal();  will(returnValue("aaa,bbb"));
        }});


        mockery.checking(new Expectations() {{
            oneOf(attrValueShop).getVal();  will(returnValue("xx,yy,"));
            oneOf(shopService).updateAttributeValue(10L, AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL, "xx,yy,zz");
        }});


        PaymentModulesManager pmm = new PaymentModulesManagerImpl( null, null, shopService);
        pmm.allowPaymentGatewayForShop("eee", "SHOP10");

        pmm = new PaymentModulesManagerImpl( null, null, shopService);
        pmm.allowPaymentGatewayForShop("aaa", "SHOP10");

        pmm = new PaymentModulesManagerImpl( null, null, shopService);
        pmm.allowPaymentGatewayForShop("zz", "SHOP10");


    }

    @Test
    public void testdisallowPaymentGateway() {
        final SystemService systemService = mockery.mock(SystemService.class);

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("aaa,bbb,ccc,ddd"));
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "aaa,bbb,ddd");
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("1,2,3"));
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "2,3");
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("xxx,yyy,zzz"));
            oneOf(systemService).updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, "xxx,yyy");
        }});

        mockery.checking(new Expectations() {{
            oneOf(systemService).getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue("xxx,yyy"));
        }});


        PaymentModulesManager pmm = new PaymentModulesManagerImpl( null, systemService, null);
        pmm.disallowPaymentGateway("ccc");

        pmm = new PaymentModulesManagerImpl( null, systemService, null);
        pmm.disallowPaymentGateway("1");

        pmm = new PaymentModulesManagerImpl( null, systemService, null);
        pmm.disallowPaymentGateway("zzz");

        pmm = new PaymentModulesManagerImpl( null, systemService, null);
        pmm.disallowPaymentGateway("zzz");


    }

    @Test
    public void testdisallowPaymentGatewayForShop() {
        final ShopService shopService = mockery.mock(ShopService.class);
        final Shop shop = mockery.mock(Shop.class);
        final AttrValueShop attrValueShop = mockery.mock(AttrValueShop.class);

        mockery.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10");  will(returnValue(shop));
            allowing(shop).getShopId();  will(returnValue(10L));
            allowing(shop).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL);  will(returnValue(attrValueShop));
        }});

        mockery.checking(new Expectations() {{
            oneOf(attrValueShop).getVal();  will(returnValue("aaa,bbb,ccc,ddd"));
            oneOf(shopService).updateAttributeValue(10L, AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL, "aaa,bbb,ddd");
        }});

        mockery.checking(new Expectations() {{
            oneOf(attrValueShop).getVal();  will(returnValue("1,2,3"));
            oneOf(shopService).updateAttributeValue(10L, AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL, "2,3");
        }});


        mockery.checking(new Expectations() {{
            oneOf(attrValueShop).getVal();  will(returnValue("xxx,yyy,zzz"));
            oneOf(shopService).updateAttributeValue(10L, AttributeNamesKeys.Shop.SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL, "xxx,yyy");
        }});


        mockery.checking(new Expectations() {{
            oneOf(attrValueShop).getVal();  will(returnValue("xxx,yyy"));
        }});


        PaymentModulesManager pmm = new PaymentModulesManagerImpl( null, null, shopService);
        pmm.disallowPaymentGatewayForShop("ccc", "SHOP10");

        pmm = new PaymentModulesManagerImpl( null, null, shopService);
        pmm.disallowPaymentGatewayForShop("1", "SHOP10");

        pmm = new PaymentModulesManagerImpl( null, null, shopService);
        pmm.disallowPaymentGatewayForShop("zzz", "SHOP10");

        pmm = new PaymentModulesManagerImpl( null, null, shopService);
        pmm.disallowPaymentGatewayForShop("zzz", "SHOP10");


    }

}
