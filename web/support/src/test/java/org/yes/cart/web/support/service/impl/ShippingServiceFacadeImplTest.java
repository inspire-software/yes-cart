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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.ProductPriceModel;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingContext;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.web.support.service.ShippingServiceFacade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 06/10/2015
 * Time: 13:16
 */
public class ShippingServiceFacadeImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetCartShippingTotalEmptyTotalNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Collections.emptyList()));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("0", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalEmptyTotalWithTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Collections.emptyList()));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("0", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping = context.mock(CartItem.class, "shipping");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

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

        final CartItem shipping = context.mock(CartItem.class, "shipping");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

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
    public void testGetCartItemsTotalFreeNoTaxInfo() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping = context.mock(CartItem.class, "shipping");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(false));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithTaxExclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxExclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxExclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithTaxExclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxExclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxExclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithTaxExclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxExclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxExclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("GROSS-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithTaxExclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxExclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("96.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("16.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxExclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("GROSS"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithTaxInclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("NET-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxInclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("NET-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxInclInfoGross() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("NET-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithTaxInclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxInclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxInclInfoGrossSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(false));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithTaxInclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("NET-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxInclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("NET-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxInclInfoNet() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");
        final CartItem shipping3 = context.mock(CartItem.class, "shipping3");
        final CartItem shipping4 = context.mock(CartItem.class, "shipping4");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2, shipping3, shipping4)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping3).getTaxCode(); will(returnValue("")); // no tax
            allowing(shipping3).getTaxRate(); will(returnValue(new BigDecimal("0.00")));
            allowing(shipping4).getTaxCode(); will(returnValue("NET-2"));
            allowing(shipping4).getTaxRate(); will(returnValue(new BigDecimal("12.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("4", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithTaxInclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalWithSaleWithTaxInclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("80.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("13.33")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

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
    public void testGetCartShippingTotalFreeShipWithTaxInclInfoNetSameTax() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext cartCtx = context.mock(ShoppingContext.class, "cartCtx");
        final Total cartTotal = context.mock(Total.class, "cartTotal");

        final Shop shop = context.mock(Shop.class, "shop");

        final CartItem shipping1 = context.mock(CartItem.class, "shipping1");
        final CartItem shipping2 = context.mock(CartItem.class, "shipping2");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartCtx));
            allowing(cartCtx).getShopId(); will(returnValue(234L));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(shipping1, shipping2)));
            allowing(cart).getTotal(); will(returnValue(cartTotal));
            allowing(cartTotal).getDeliveryListCost(); will(returnValue(new BigDecimal("100.00")));
            allowing(cartTotal).getDeliveryCost(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryCostAmount(); will(returnValue(new BigDecimal("0.00")));
            allowing(cartTotal).getDeliveryTax(); will(returnValue(new BigDecimal("0.00")));
            allowing(shopService).getById(234L); will(returnValue(shop));
            allowing(cartCtx).isTaxInfoEnabled(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoUseNet(); will(returnValue(true));
            allowing(cartCtx).isTaxInfoShowAmount(); will(returnValue(true));
            allowing(shipping1).getTaxCode(); will(returnValue("NET"));
            allowing(shipping1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(shipping2).getTaxCode(); will(returnValue("NET"));
            allowing(shipping2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
        }});

        final ShippingServiceFacade facade = new ShippingServiceFacadeImpl(null, null, shopService, null, null);


        final ProductPriceModel model = facade.getCartShippingTotal(cart);

        assertNotNull(model);

        assertEquals(ShippingServiceFacadeImpl.CART_SHIPPING_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("2", model.getQuantity().toPlainString());

        assertEquals("100.00", model.getRegularPrice().toPlainString());
        assertEquals("0.00", model.getSalePrice().toPlainString());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        context.assertIsSatisfied();

    }

}