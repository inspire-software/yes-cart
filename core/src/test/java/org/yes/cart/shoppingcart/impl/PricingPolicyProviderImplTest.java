/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.shoppingcart.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.PricingPolicyProvider;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 08/06/2016
 * Time: 09:05
 */
public class PricingPolicyProviderImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testDeterminePricingPolicyCustomer() throws Exception {

        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final Shop shop = context.mock(Shop.class, "shop");
        final Customer customer = context.mock(Customer.class, "customer");

        context.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10"); will(returnValue(shop));
            allowing(customerService).getCustomerByEmail("bob@doe.com", shop); will(returnValue(customer));
            allowing(customer).getPricingPolicy(); will(returnValue("BOB"));
        }});

        final PricingPolicyProviderImpl provider = new PricingPolicyProviderImpl(customerService, shopService);

        PricingPolicyProvider.PricingPolicy policy;

        policy = provider.determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-CAM");
        assertEquals(PricingPolicyProvider.PricingPolicy.Type.CUSTOMER, policy.getType());
        assertEquals("BOB", policy.getID());

        context.assertIsSatisfied();

    }


    @Test
    public void testDeterminePricingPolicyState() throws Exception {

        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10"); will(returnValue(shop));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB_GB-CAM"); will(returnValue("REGIONAL"));
        }});

        final PricingPolicyProviderImpl provider = new PricingPolicyProviderImpl(customerService, shopService);

        PricingPolicyProvider.PricingPolicy policy;

        policy = provider.determinePricingPolicy("SHOP10", "EUR", null, "GB", "GB-CAM");
        assertEquals(PricingPolicyProvider.PricingPolicy.Type.STATE, policy.getType());
        assertEquals("REGIONAL", policy.getID());

        context.assertIsSatisfied();

    }



    @Test
    public void testDeterminePricingPolicyCountry() throws Exception {

        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10"); will(returnValue(shop));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB_GB-CAM"); will(returnValue(null));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB"); will(returnValue("REGIONAL"));
        }});

        final PricingPolicyProviderImpl provider = new PricingPolicyProviderImpl(customerService, shopService);

        PricingPolicyProvider.PricingPolicy policy;

        policy = provider.determinePricingPolicy("SHOP10", "EUR", null, "GB", "GB-CAM");
        assertEquals(PricingPolicyProvider.PricingPolicy.Type.COUNTRY, policy.getType());
        assertEquals("REGIONAL", policy.getID());

        context.assertIsSatisfied();

    }


    @Test
    public void testDeterminePricingPolicyDefaultWithCustomer() throws Exception {

        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final Shop shop = context.mock(Shop.class, "shop");
        final Customer customer = context.mock(Customer.class, "customer");

        context.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10"); will(returnValue(shop));
            allowing(customerService).getCustomerByEmail("bob@doe.com", shop); will(returnValue(customer));
            allowing(customer).getPricingPolicy(); will(returnValue(null));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB_GB-CAM"); will(returnValue(null));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB"); will(returnValue(null));
        }});

        final PricingPolicyProviderImpl provider = new PricingPolicyProviderImpl(customerService, shopService);

        PricingPolicyProvider.PricingPolicy policy;

        policy = provider.determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-CAM");
        assertEquals(PricingPolicyProvider.PricingPolicy.Type.DEFAULT, policy.getType());
        assertNull(policy.getID());

        context.assertIsSatisfied();

    }


    @Test
    public void testDeterminePricingPolicyDefaultWithLocation() throws Exception {

        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10"); will(returnValue(shop));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB_GB-CAM"); will(returnValue(null));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB"); will(returnValue(null));
        }});

        final PricingPolicyProviderImpl provider = new PricingPolicyProviderImpl(customerService, shopService);

        PricingPolicyProvider.PricingPolicy policy;

        policy = provider.determinePricingPolicy("SHOP10", "EUR", null, "GB", "GB-CAM");
        assertEquals(PricingPolicyProvider.PricingPolicy.Type.DEFAULT, policy.getType());
        assertNull(policy.getID());

        context.assertIsSatisfied();

    }


    @Test
    public void testDeterminePricingPolicyDefaultWithoutLocation() throws Exception {

        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(shopService).getShopByCode("SHOP10"); will(returnValue(shop));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB_GB-CAM"); will(returnValue(null));
            allowing(shop).getAttributeValueByCode("SHOP_REGIONAL_PRICING_GB"); will(returnValue(null));
        }});

        final PricingPolicyProviderImpl provider = new PricingPolicyProviderImpl(customerService, shopService);

        PricingPolicyProvider.PricingPolicy policy;

        policy = provider.determinePricingPolicy("SHOP10", "EUR", null, null, null);
        assertEquals(PricingPolicyProvider.PricingPolicy.Type.DEFAULT, policy.getType());
        assertNull(policy.getID());

        context.assertIsSatisfied();

    }
}