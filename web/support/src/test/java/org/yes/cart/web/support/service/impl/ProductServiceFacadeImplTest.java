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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.ShoppingCartCalculator;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 02/10/2015
 * Time: 17:46
 */
public class ProductServiceFacadeImplTest {

    private final Mockery context = new JUnit4Mockery();



    @Test
    public void testGetSkuPriceSearchAndProductDetailsHidePrice() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetSkuPriceSearchAndProductDetailsNoPrice() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(null));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetSkuPriceSearchAndProductDetailsNoPriceB2BStrict() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final Shop b2b = context.mock(Shop.class, "b2b");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(345L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 345L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(null));
            allowing(shopService).getById(345L); will(returnValue(b2b));
            allowing(b2b).isB2BStrictPriceActive(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetSkuPriceSearchAndProductDetailsNoPriceB2B() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final Shop b2b = context.mock(Shop.class, "b2b");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(345L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 345L, 234L, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(null));
            allowing(shopService).getById(345L); will(returnValue(b2b));
            allowing(b2b).isB2BStrictPriceActive(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceSearchAndProductDetailsEmptyPriceNoTaxInfo() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue(null));
            allowing(skuPrice).getQuantity(); will(returnValue(null));
            allowing(skuPrice).getRegularPrice(); will(returnValue(null));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceSearchAndProductDetailsEmptyPriceWithTaxInfoGross() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue(null));
            allowing(skuPrice).getQuantity(); will(returnValue(null));
            allowing(skuPrice).getRegularPrice(); will(returnValue(null));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceSearchAndProductDetailsEmptyPriceWithTaxInfoNet() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue(null));
            allowing(skuPrice).getQuantity(); will(returnValue(null));
            allowing(skuPrice).getRegularPrice(); will(returnValue(null));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListOnlyNoTaxInfo() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListAndSaleNoTaxInfo() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListOnlyWithTaxExclInfoGross() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("100.00")); will(returnValue(priceModel));
            allowing(priceModel).getGrossPrice(); will(returnValue(new BigDecimal("120.00")));
            allowing(priceModel).getTaxCode(); will(returnValue("GROSS"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(true));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("120.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("20.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListOnlyWithTaxExclInfoNet() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("100.00")); will(returnValue(priceModel));
            allowing(priceModel).getNetPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(priceModel).getTaxCode(); will(returnValue("GROSS"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(true));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("20.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }




    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListOnlyWithTaxInclInfoGross() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("100.00")); will(returnValue(priceModel));
            allowing(priceModel).getGrossPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(priceModel).getTaxCode(); will(returnValue("NET"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(false));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("16.67")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListOnlyWithTaxInclInfoNet() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("100.00")); will(returnValue(priceModel));
            allowing(priceModel).getNetPrice(); will(returnValue(new BigDecimal("83.33")));
            allowing(priceModel).getTaxCode(); will(returnValue("NET"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(false));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("16.67")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.33", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }




    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListAndSaleWithTaxExclInfoGross() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("80.00")); will(returnValue(priceModel));
            allowing(priceModel).getGrossPrice(); will(returnValue(new BigDecimal("96.00")));
            allowing(priceModel).getTaxCode(); will(returnValue("GROSS"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(true));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("16.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("120.00", model.getRegularPrice().toPlainString());
        assertEquals("96.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListAndSaleWithTaxExclInfoNet() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("80.00")); will(returnValue(priceModel));
            allowing(priceModel).getNetPrice(); will(returnValue(new BigDecimal("80.00")));
            allowing(priceModel).getTaxCode(); will(returnValue("GROSS"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(true));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("16.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }




    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListAndSaleWithTaxInclInfoGross() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("80.00")); will(returnValue(priceModel));
            allowing(priceModel).getGrossPrice(); will(returnValue(new BigDecimal("80.00")));
            allowing(priceModel).getTaxCode(); will(returnValue("NET"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(false));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("13.33")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetSkuPriceSearchAndProductDetailsPriceListAndSaleWithTaxInclInfoNet() throws Exception {

        final PriceService priceService = context.mock(PriceService.class, "priceService");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ShoppingCartCalculator calculator = context.mock(ShoppingCartCalculator.class, "calculator");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final PricingPolicyProvider.PricingPolicy policy = context.mock(PricingPolicyProvider.PricingPolicy.class, "policy");

        final SkuPrice skuPrice = context.mock(SkuPrice.class, "skuPrice");

        final Shop shop = context.mock(Shop.class, "shop");
        final ShoppingCartCalculator.PriceModel priceModel = context.mock(ShoppingCartCalculator.PriceModel.class, "priceModel");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cartCtx).getCustomerShopId(); will(returnValue(234L));
            allowing(cartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(cartCtx).getStateCode(); will(returnValue("GB-LON"));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(pricingPolicyProvider).determinePricingPolicy("SHOP10", "EUR", "bob@doe.com", "GB", "GB-LON"); will(returnValue(policy));
            allowing(policy).getID(); will(returnValue("P1"));
            allowing(priceService).getMinimalPrice(123L, "ABC", 234L, null, "EUR", BigDecimal.ONE, false, "P1"); will(returnValue(skuPrice));
            allowing(skuPrice).getSkuCode(); will(returnValue("ABC"));
            allowing(skuPrice).getQuantity(); will(returnValue(BigDecimal.ONE));
            allowing(skuPrice).getRegularPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(skuPrice).getSalePriceForCalculation(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(calculator).calculatePrice(cart, "ABC", new BigDecimal("80.00")); will(returnValue(priceModel));
            allowing(priceModel).getNetPrice(); will(returnValue(new BigDecimal("66.67")));
            allowing(priceModel).getTaxCode(); will(returnValue("NET"));
            allowing(priceModel).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(priceModel).isTaxExclusive(); will(returnValue(false));
            allowing(priceModel).getTaxAmount(); will(returnValue(new BigDecimal("13.33")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, pricingPolicyProvider, priceService, calculator, null, null, shopService, null);


        final ProductPriceModel model = facade.getSkuPrice(cart, 123L, "ABC", BigDecimal.ONE);

        assertNotNull(model);

        assertEquals("ABC", model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.33", model.getRegularPrice().toPlainString());
        assertEquals("66.67", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetSkuPriceCartItemsHidePrice() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertNull(modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertNull(modelPrice.getQuantity());

        assertNull(modelPrice.getRegularPrice());
        assertNull(modelPrice.getSalePrice());

        assertFalse(modelPrice.isTaxInfoEnabled());
        assertFalse(modelPrice.isTaxInfoUseNet());
        assertFalse(modelPrice.isTaxInfoShowAmount());

        assertNull(modelPrice.getPriceTaxCode());
        assertNull(modelPrice.getPriceTaxRate());
        assertFalse(modelPrice.isPriceTaxExclusive());
        assertNull(modelPrice.getPriceTax());


        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertNull(modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertNull(modelTotal.getQuantity());

        assertNull(modelTotal.getRegularPrice());
        assertNull(modelTotal.getSalePrice());

        assertFalse(modelTotal.isTaxInfoEnabled());
        assertFalse(modelTotal.isTaxInfoUseNet());
        assertFalse(modelTotal.isTaxInfoShowAmount());

        assertNull(modelTotal.getPriceTaxCode());
        assertNull(modelTotal.getPriceTaxRate());
        assertFalse(modelTotal.isPriceTaxExclusive());
        assertNull(modelTotal.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceCartItemsEmptyPriceNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue(null));
            allowing(item).getQty(); will(returnValue(null));
            allowing(item).getListPrice(); will(returnValue(null));
            allowing(item).getSalePrice(); will(returnValue(null));
            allowing(item).getPrice(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertNull(modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertNull(modelPrice.getQuantity());

        assertNull(modelPrice.getRegularPrice());
        assertNull(modelPrice.getSalePrice());

        assertFalse(modelPrice.isTaxInfoEnabled());
        assertFalse(modelPrice.isTaxInfoUseNet());
        assertFalse(modelPrice.isTaxInfoShowAmount());

        assertNull(modelPrice.getPriceTaxCode());
        assertNull(modelPrice.getPriceTaxRate());
        assertFalse(modelPrice.isPriceTaxExclusive());
        assertNull(modelPrice.getPriceTax());


        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertNull(modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertNull(modelTotal.getQuantity());

        assertNull(modelTotal.getRegularPrice());
        assertNull(modelTotal.getSalePrice());

        assertFalse(modelTotal.isTaxInfoEnabled());
        assertFalse(modelTotal.isTaxInfoUseNet());
        assertFalse(modelTotal.isTaxInfoShowAmount());

        assertNull(modelTotal.getPriceTaxCode());
        assertNull(modelTotal.getPriceTaxRate());
        assertFalse(modelTotal.isPriceTaxExclusive());
        assertNull(modelTotal.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceCartItemsEmptyPriceWithTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue(null));
            allowing(item).getQty(); will(returnValue(null));
            allowing(item).getListPrice(); will(returnValue(null));
            allowing(item).getSalePrice(); will(returnValue(null));
            allowing(item).getPrice(); will(returnValue(null));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertNull(modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertNull(modelPrice.getQuantity());

        assertNull(modelPrice.getRegularPrice());
        assertNull(modelPrice.getSalePrice());

        assertFalse(modelPrice.isTaxInfoEnabled());
        assertFalse(modelPrice.isTaxInfoUseNet());
        assertFalse(modelPrice.isTaxInfoShowAmount());

        assertNull(modelPrice.getPriceTaxCode());
        assertNull(modelPrice.getPriceTaxRate());
        assertFalse(modelPrice.isPriceTaxExclusive());
        assertNull(modelPrice.getPriceTax());

        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertNull(modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertNull(modelTotal.getQuantity());

        assertNull(modelTotal.getRegularPrice());
        assertNull(modelTotal.getSalePrice());

        assertFalse(modelTotal.isTaxInfoEnabled());
        assertFalse(modelTotal.isTaxInfoUseNet());
        assertFalse(modelTotal.isTaxInfoShowAmount());

        assertNull(modelTotal.getPriceTaxCode());
        assertNull(modelTotal.getPriceTaxRate());
        assertFalse(modelTotal.isPriceTaxExclusive());
        assertNull(modelTotal.getPriceTax());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetSkuPriceCartItemsPriceListOnlyNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).getQty(); will(returnValue(BigDecimal.TEN));
            allowing(item).getListPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getSalePrice(); will(returnValue(null));
            allowing(item).getPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertEquals("ABC", modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertEquals("10", modelPrice.getQuantity().toPlainString());

        assertEquals("100.00", modelPrice.getRegularPrice().toPlainString());
        assertNull(modelPrice.getSalePrice());

        assertFalse(modelPrice.isTaxInfoEnabled());
        assertFalse(modelPrice.isTaxInfoUseNet());
        assertFalse(modelPrice.isTaxInfoShowAmount());

        assertNull(modelPrice.getPriceTaxCode());
        assertNull(modelPrice.getPriceTaxRate());
        assertFalse(modelPrice.isPriceTaxExclusive());
        assertNull(modelPrice.getPriceTax());

        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertEquals("ABC", modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertEquals("10", modelTotal.getQuantity().toPlainString());

        assertEquals("1000.00", modelTotal.getRegularPrice().toPlainString());
        assertNull(modelTotal.getSalePrice());

        assertFalse(modelTotal.isTaxInfoEnabled());
        assertFalse(modelTotal.isTaxInfoUseNet());
        assertFalse(modelTotal.isTaxInfoShowAmount());

        assertNull(modelTotal.getPriceTaxCode());
        assertNull(modelTotal.getPriceTaxRate());
        assertFalse(modelTotal.isPriceTaxExclusive());
        assertNull(modelTotal.getPriceTax());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetSkuPriceCartItemsPriceListAndSaleNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).getQty(); will(returnValue(BigDecimal.TEN));
            allowing(item).getListPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getSalePrice(); will(returnValue(new BigDecimal("80.00")));
            allowing(item).getPrice(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertEquals("ABC", modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertEquals("10", modelPrice.getQuantity().toPlainString());

        assertEquals("100.00", modelPrice.getRegularPrice().toPlainString());
        assertEquals("80.00", modelPrice.getSalePrice().toPlainString());

        assertFalse(modelPrice.isTaxInfoEnabled());
        assertFalse(modelPrice.isTaxInfoUseNet());
        assertFalse(modelPrice.isTaxInfoShowAmount());

        assertNull(modelPrice.getPriceTaxCode());
        assertNull(modelPrice.getPriceTaxRate());
        assertFalse(modelPrice.isPriceTaxExclusive());
        assertNull(modelPrice.getPriceTax());

        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertEquals("ABC", modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertEquals("10", modelTotal.getQuantity().toPlainString());

        assertEquals("800.00", modelTotal.getRegularPrice().toPlainString());
        assertNull(modelTotal.getSalePrice());

        assertFalse(modelTotal.isTaxInfoEnabled());
        assertFalse(modelTotal.isTaxInfoUseNet());
        assertFalse(modelTotal.isTaxInfoShowAmount());

        assertNull(modelTotal.getPriceTaxCode());
        assertNull(modelTotal.getPriceTaxRate());
        assertFalse(modelTotal.isPriceTaxExclusive());
        assertNull(modelTotal.getPriceTax());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetSkuPriceCartItemsPriceListOnlyWithTaxExclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).getQty(); will(returnValue(BigDecimal.TEN));
            allowing(item).getListPrice(); will(returnValue(new BigDecimal("120.00")));
            allowing(item).getSalePrice(); will(returnValue(null));
            allowing(item).getPrice(); will(returnValue(new BigDecimal("120.00")));
            allowing(item).getNetPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getGrossPrice(); will(returnValue(new BigDecimal("120.00")));
            allowing(item).getTaxCode(); will(returnValue("GROSS"));
            allowing(item).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertEquals("ABC", modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertEquals("10", modelPrice.getQuantity().toPlainString());

        assertEquals("120.00", modelPrice.getRegularPrice().toPlainString());
        assertNull(modelPrice.getSalePrice());

        assertTrue(modelPrice.isTaxInfoEnabled());
        assertFalse(modelPrice.isTaxInfoUseNet());
        assertTrue(modelPrice.isTaxInfoShowAmount());

        assertEquals("GROSS", modelPrice.getPriceTaxCode());
        assertEquals("20.00", modelPrice.getPriceTaxRate().toPlainString());
        assertTrue(modelPrice.isPriceTaxExclusive());
        assertEquals("20.00", modelPrice.getPriceTax().toPlainString());

        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertEquals("ABC", modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertEquals("10", modelTotal.getQuantity().toPlainString());

        assertEquals("1200.00", modelTotal.getRegularPrice().toPlainString());
        assertNull(modelTotal.getSalePrice());

        assertTrue(modelTotal.isTaxInfoEnabled());
        assertFalse(modelTotal.isTaxInfoUseNet());
        assertTrue(modelTotal.isTaxInfoShowAmount());

        assertEquals("GROSS", modelTotal.getPriceTaxCode());
        assertEquals("20.00", modelTotal.getPriceTaxRate().toPlainString());
        assertTrue(modelTotal.isPriceTaxExclusive());
        assertEquals("200.00", modelTotal.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetSkuPriceCartItemsPriceListOnlyWithTaxExclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).getQty(); will(returnValue(BigDecimal.TEN));
            allowing(item).getListPrice(); will(returnValue(new BigDecimal("120.00")));
            allowing(item).getSalePrice(); will(returnValue(null));
            allowing(item).getPrice(); will(returnValue(new BigDecimal("120.00")));
            allowing(item).getNetPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getGrossPrice(); will(returnValue(new BigDecimal("120.00")));
            allowing(item).getTaxCode(); will(returnValue("GROSS"));
            allowing(item).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertEquals("ABC", modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertEquals("10", modelPrice.getQuantity().toPlainString());

        assertEquals("100.00", modelPrice.getRegularPrice().toPlainString());
        assertNull(modelPrice.getSalePrice());

        assertTrue(modelPrice.isTaxInfoEnabled());
        assertTrue(modelPrice.isTaxInfoUseNet());
        assertTrue(modelPrice.isTaxInfoShowAmount());

        assertEquals("GROSS", modelPrice.getPriceTaxCode());
        assertEquals("20.00", modelPrice.getPriceTaxRate().toPlainString());
        assertTrue(modelPrice.isPriceTaxExclusive());
        assertEquals("20.00", modelPrice.getPriceTax().toPlainString());

        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertEquals("ABC", modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertEquals("10", modelTotal.getQuantity().toPlainString());

        assertEquals("1000.00", modelTotal.getRegularPrice().toPlainString());
        assertNull(modelTotal.getSalePrice());

        assertTrue(modelTotal.isTaxInfoEnabled());
        assertTrue(modelTotal.isTaxInfoUseNet());
        assertTrue(modelTotal.isTaxInfoShowAmount());

        assertEquals("GROSS", modelTotal.getPriceTaxCode());
        assertEquals("20.00", modelTotal.getPriceTaxRate().toPlainString());
        assertTrue(modelTotal.isPriceTaxExclusive());
        assertEquals("200.00", modelTotal.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetSkuPriceCartItemsPriceListOnlyWithTaxInclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).getQty(); will(returnValue(BigDecimal.TEN));
            allowing(item).getListPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getSalePrice(); will(returnValue(null));
            allowing(item).getPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getNetPrice(); will(returnValue(new BigDecimal("83.33")));
            allowing(item).getGrossPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getTaxCode(); will(returnValue("NET"));
            allowing(item).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertEquals("ABC", modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertEquals("10", modelPrice.getQuantity().toPlainString());

        assertEquals("100.00", modelPrice.getRegularPrice().toPlainString());
        assertNull(modelPrice.getSalePrice());

        assertTrue(modelPrice.isTaxInfoEnabled());
        assertFalse(modelPrice.isTaxInfoUseNet());
        assertTrue(modelPrice.isTaxInfoShowAmount());

        assertEquals("NET", modelPrice.getPriceTaxCode());
        assertEquals("20.00", modelPrice.getPriceTaxRate().toPlainString());
        assertFalse(modelPrice.isPriceTaxExclusive());
        assertEquals("16.67", modelPrice.getPriceTax().toPlainString());

        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertEquals("ABC", modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertEquals("10", modelTotal.getQuantity().toPlainString());

        assertEquals("1000.00", modelTotal.getRegularPrice().toPlainString());
        assertNull(modelTotal.getSalePrice());

        assertTrue(modelTotal.isTaxInfoEnabled());
        assertFalse(modelTotal.isTaxInfoUseNet());
        assertTrue(modelTotal.isTaxInfoShowAmount());

        assertEquals("NET", modelTotal.getPriceTaxCode());
        assertEquals("20.00", modelTotal.getPriceTaxRate().toPlainString());
        assertFalse(modelTotal.isPriceTaxExclusive());
        assertEquals("166.70", modelTotal.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetSkuPriceCartItemsPriceListOnlyWithTaxInclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).getQty(); will(returnValue(BigDecimal.TEN));
            allowing(item).getListPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getSalePrice(); will(returnValue(null));
            allowing(item).getPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getNetPrice(); will(returnValue(new BigDecimal("83.33")));
            allowing(item).getGrossPrice(); will(returnValue(new BigDecimal("100.00")));
            allowing(item).getTaxCode(); will(returnValue("NET"));
            allowing(item).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel modelPrice = facade.getSkuPrice(cart, item, false);

        assertNotNull(modelPrice);

        assertEquals("ABC", modelPrice.getRef());

        assertEquals("EUR", modelPrice.getCurrency());
        assertEquals("10", modelPrice.getQuantity().toPlainString());

        assertEquals("83.33", modelPrice.getRegularPrice().toPlainString());
        assertNull(modelPrice.getSalePrice());

        assertTrue(modelPrice.isTaxInfoEnabled());
        assertTrue(modelPrice.isTaxInfoUseNet());
        assertTrue(modelPrice.isTaxInfoShowAmount());

        assertEquals("NET", modelPrice.getPriceTaxCode());
        assertEquals("20.00", modelPrice.getPriceTaxRate().toPlainString());
        assertFalse(modelPrice.isPriceTaxExclusive());
        assertEquals("16.67", modelPrice.getPriceTax().toPlainString());

        final ProductPriceModel modelTotal = facade.getSkuPrice(cart, item, true);

        assertNotNull(modelTotal);

        assertEquals("ABC", modelTotal.getRef());

        assertEquals("EUR", modelTotal.getCurrency());
        assertEquals("10", modelTotal.getQuantity().toPlainString());

        assertEquals("833.30", modelTotal.getRegularPrice().toPlainString());
        assertNull(modelTotal.getSalePrice());

        assertTrue(modelTotal.isTaxInfoEnabled());
        assertTrue(modelTotal.isTaxInfoUseNet());
        assertTrue(modelTotal.isTaxInfoShowAmount());

        assertEquals("NET", modelTotal.getPriceTaxCode());
        assertEquals("20.00", modelTotal.getPriceTaxRate().toPlainString());
        assertFalse(modelTotal.isPriceTaxExclusive());
        assertEquals("166.70", modelTotal.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetCartItemsTotalHidePrices() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(true));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertNull(model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertNull(model.getQuantity());

        assertNull(model.getRegularPrice());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalEmptyTotalNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("0.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalEmptyTotalWithTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("0.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetCartItemsTotalNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("80.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithTaxExclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("96.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS,GROSS-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleWithTaxExclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("120.00", model.getRegularPrice().toPlainString());
        assertEquals("96.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS,GROSS-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithTaxExclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("96.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleWithTaxExclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("120.00", model.getRegularPrice().toPlainString());
        assertEquals("96.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithTaxExclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("80.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS,GROSS-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleWithTaxExclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS,GROSS-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithTaxExclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("80.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleWithTaxExclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("GROSS"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("GROSS"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("GROSS", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.00", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetCartItemsTotalWithTaxInclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("NET-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("80.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET,NET-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetCartItemsTotalWithSaleWithTaxInclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("NET-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET,NET-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithTaxInclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("80.00", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleWithTaxInclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("80.00", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetCartItemsTotalWithTaxInclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("NET-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("66.67", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET,NET-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleWithTaxInclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final CartItem item4 = context.mock(CartItem.class, "item4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2, item3, item4)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item3).getTaxCode(); will(returnValue("")); // no tax
            allowing(item3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(item4).getTaxCode(); will(returnValue("NET-2"));
            allowing(item4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.33", model.getRegularPrice().toPlainString());
        assertEquals("66.67", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET,NET-2", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetCartItemsTotalWithTaxInclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("66.67", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetCartItemsTotalWithSaleWithTaxInclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).isHidePrices(); will(returnValue(false));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getListSubTotal(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getSubTotal(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getSubTotalTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getTaxCode(); will(returnValue("NET"));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).getTaxCode(); will(returnValue("NET"));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, null, null, shopService, null);


        final ProductPriceModel model = facade.getCartItemsTotal(cart);

        assertNotNull(model);

        assertEquals(ProductServiceFacadeImpl.CART_ITEMS_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.33", model.getRegularPrice().toPlainString());
        assertEquals("66.67", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertTrue(model.isTaxInfoShowAmount());

        assertEquals("NET", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("13.33", model.getPriceTax().toPlainString());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetPromotionModelForNull() throws Exception {

        final PromotionService promotionService = context.mock(PromotionService.class, "promotionService");


        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, promotionService, null, null, null);

        final Map<String, ProductPromotionModel> model = facade.getPromotionModel(null);

        assertNotNull(model);

        assertTrue(model.isEmpty());

    }

    @Test
    public void testGetPromotionModelForSingleNotFound() throws Exception {

        final PromotionService promotionService = context.mock(PromotionService.class, "promotionService");

        context.checking(new Expectations() {{
            allowing(promotionService).findByParameters("CODE1", null, null, null, null, null, Boolean.TRUE); will(returnValue(Collections.emptyList()));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, promotionService, null, null, null);

        final Map<String, ProductPromotionModel> model = facade.getPromotionModel("CODE1");

        assertNotNull(model);

        assertTrue(model.isEmpty());

    }

    @Test
    public void testGetPromotionModelForSingle() throws Exception {

        final PromotionService promotionService = context.mock(PromotionService.class, "promotionService");

        final Promotion code1 = context.mock(Promotion.class, "code1");

        final Date start = new Date();
        final Date end = new Date();

        context.checking(new Expectations() {{
            allowing(promotionService).findByParameters("CODE1", null, null, null, null, null, Boolean.TRUE); will(returnValue(Arrays.asList(code1)));
            allowing(code1).getCode(); will(returnValue("CODE1"));
            allowing(code1).getPromoType(); will(returnValue("T1"));
            allowing(code1).getPromoAction(); will(returnValue("A1"));
            allowing(code1).getPromoActionContext(); will(returnValue("CTX1"));
            allowing(code1).getDisplayName(); will(returnValue(null));
            allowing(code1).getName(); will(returnValue("Promo 1"));
            allowing(code1).getDisplayDescription(); will(returnValue(null));
            allowing(code1).getDescription(); will(returnValue("Desc 1"));
            allowing(code1).getEnabledFrom(); will(returnValue(start));
            allowing(code1).getEnabledTo(); will(returnValue(end));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, promotionService, null, null, null);

        final Map<String, ProductPromotionModel> model = facade.getPromotionModel("CODE1");

        assertNotNull(model);

        assertFalse(model.isEmpty());

        final ProductPromotionModel code1model = model.get("CODE1");

        assertEquals("CODE1", code1model.getCode());
        assertNull(code1model.getCouponCode());
        assertEquals("T1", code1model.getType());
        assertEquals("A1", code1model.getAction());
        assertEquals("CTX1", code1model.getContext());
        assertEquals("Promo 1", code1model.getName().getValue("en"));
        assertEquals("Desc 1", code1model.getDescription().getValue("en"));
        assertSame(start, code1model.getActiveFrom());
        assertSame(end, code1model.getActiveTo());

    }

    @Test
    public void testGetPromotionModelForSingleCoupon() throws Exception {

        final PromotionService promotionService = context.mock(PromotionService.class, "promotionService");

        final Promotion code1 = context.mock(Promotion.class, "code1");

        final Date start = new Date();
        final Date end = new Date();

        context.checking(new Expectations() {{
            allowing(promotionService).findByParameters("CODE1", null, null, null, null, null, Boolean.TRUE); will(returnValue(Arrays.asList(code1)));
            allowing(code1).getCode(); will(returnValue("CODE1"));
            allowing(code1).getPromoType(); will(returnValue("T1"));
            allowing(code1).getPromoAction(); will(returnValue("A1"));
            allowing(code1).getPromoActionContext(); will(returnValue("CTX1"));
            allowing(code1).getDisplayName(); will(returnValue(null));
            allowing(code1).getName(); will(returnValue("Promo 1"));
            allowing(code1).getDisplayDescription(); will(returnValue(null));
            allowing(code1).getDescription(); will(returnValue("Desc 1"));
            allowing(code1).getEnabledFrom(); will(returnValue(start));
            allowing(code1).getEnabledTo(); will(returnValue(end));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, promotionService, null, null, null);

        final Map<String, ProductPromotionModel> model = facade.getPromotionModel("CODE1:COUPON1");

        assertNotNull(model);

        assertFalse(model.isEmpty());

        final ProductPromotionModel code1model = model.get("CODE1:COUPON1");

        assertEquals("CODE1", code1model.getCode());
        assertEquals("COUPON1", code1model.getCouponCode());
        assertEquals("T1", code1model.getType());
        assertEquals("A1", code1model.getAction());
        assertEquals("CTX1", code1model.getContext());
        assertEquals("Promo 1", code1model.getName().getValue("en"));
        assertEquals("Desc 1", code1model.getDescription().getValue("en"));
        assertSame(start, code1model.getActiveFrom());
        assertSame(end, code1model.getActiveTo());

    }

    @Test
    public void testGetPromotionModelForMultiMixed() throws Exception {

        final PromotionService promotionService = context.mock(PromotionService.class, "promotionService");

        final Promotion code0 = context.mock(Promotion.class, "code0");
        final Promotion code1 = context.mock(Promotion.class, "code1");
        final Promotion code2 = context.mock(Promotion.class, "code2");

        final Date start = new Date();
        final Date end = new Date();

        context.checking(new Expectations() {{
            allowing(promotionService).findByParameters("CODE0", null, null, null, null, null, Boolean.TRUE);
            will(returnValue(Arrays.asList(code0)));
            allowing(code0).getCode();
            will(returnValue("CODE0"));
            allowing(code0).getPromoType();
            will(returnValue("T0"));
            allowing(code0).getPromoAction();
            will(returnValue("A0"));
            allowing(code0).getPromoActionContext();
            will(returnValue("CTX0"));
            allowing(code0).getDisplayName();
            will(returnValue(null));
            allowing(code0).getName();
            will(returnValue("Promo 0"));
            allowing(code0).getDisplayDescription();
            will(returnValue(null));
            allowing(code0).getDescription();
            will(returnValue("Desc 0"));
            allowing(code0).getEnabledFrom();
            will(returnValue(start));
            allowing(code0).getEnabledTo();
            will(returnValue(end));
            allowing(promotionService).findByParameters("CODE1", null, null, null, null, null, Boolean.TRUE);
            will(returnValue(Arrays.asList(code1)));
            allowing(code1).getCode();
            will(returnValue("CODE1"));
            allowing(code1).getPromoType();
            will(returnValue("T1"));
            allowing(code1).getPromoAction();
            will(returnValue("A1"));
            allowing(code1).getPromoActionContext();
            will(returnValue("CTX1"));
            allowing(code1).getDisplayName();
            will(returnValue(null));
            allowing(code1).getName();
            will(returnValue("Promo 1"));
            allowing(code1).getDisplayDescription();
            will(returnValue(null));
            allowing(code1).getDescription();
            will(returnValue("Desc 1"));
            allowing(code1).getEnabledFrom();
            will(returnValue(start));
            allowing(code1).getEnabledTo();
            will(returnValue(end));
            allowing(promotionService).findByParameters("CODE2", null, null, null, null, null, Boolean.TRUE);
            will(returnValue(Arrays.asList(code2)));
            allowing(code2).getCode();
            will(returnValue("CODE2"));
            allowing(code2).getPromoType();
            will(returnValue("T2"));
            allowing(code2).getPromoAction();
            will(returnValue("A2"));
            allowing(code2).getPromoActionContext();
            will(returnValue("CTX2"));
            allowing(code2).getDisplayName();
            will(returnValue(null));
            allowing(code2).getName();
            will(returnValue("Promo 2"));
            allowing(code2).getDisplayDescription();
            will(returnValue(null));
            allowing(code2).getDescription();
            will(returnValue("Desc 2"));
            allowing(code2).getEnabledFrom();
            will(returnValue(start));
            allowing(code2).getEnabledTo();
            will(returnValue(end));
        }});

        final ProductServiceFacade facade = new ProductServiceFacadeImpl(null, null, null, null, null, null, null, null, null, promotionService, null, null, null);

        final Map<String, ProductPromotionModel> model = facade.getPromotionModel("CODE2:COUPON2,CODE1,CODE0");

        assertNotNull(model);

        assertFalse(model.isEmpty());

        final ProductPromotionModel code0model = model.get("CODE0");

        assertEquals("CODE0", code0model.getCode());
        assertNull(code0model.getCouponCode());
        assertEquals("T0", code0model.getType());
        assertEquals("A0", code0model.getAction());
        assertEquals("CTX0", code0model.getContext());
        assertEquals("Promo 0", code0model.getName().getValue("en"));
        assertEquals("Desc 0", code0model.getDescription().getValue("en"));
        assertSame(start, code0model.getActiveFrom());
        assertSame(end, code0model.getActiveTo());

        final ProductPromotionModel code1model = model.get("CODE1");

        assertEquals("CODE1", code1model.getCode());
        assertNull(code1model.getCouponCode());
        assertEquals("T1", code1model.getType());
        assertEquals("A1", code1model.getAction());
        assertEquals("CTX1", code1model.getContext());
        assertEquals("Promo 1", code1model.getName().getValue("en"));
        assertEquals("Desc 1", code1model.getDescription().getValue("en"));
        assertSame(start, code1model.getActiveFrom());
        assertSame(end, code1model.getActiveTo());

        final ProductPromotionModel code2model = model.get("CODE2:COUPON2");

        assertEquals("CODE2", code2model.getCode());
        assertEquals("COUPON2", code2model.getCouponCode());
        assertEquals("T2", code2model.getType());
        assertEquals("A2", code2model.getAction());
        assertEquals("CTX2", code2model.getContext());
        assertEquals("Promo 2", code2model.getName().getValue("en"));
        assertEquals("Desc 2", code2model.getDescription().getValue("en"));
        assertSame(start, code2model.getActiveFrom());
        assertSame(end, code2model.getActiveTo());

        final String[] sequence = StringUtils.split("CODE2:COUPON2,CODE1,CODE0", ',');
        int i = 0;
        for (final String key : model.keySet()) { // ensure ordering is correct
            assertEquals(key, sequence[i++]);
        }

    }
}