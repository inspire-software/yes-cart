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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PriceServiceImplTest extends BaseCoreDBTestCase {

    private ShopService shopService;
    private PriceService priceService;
    private ProductService productService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        priceService = (PriceService) ctx().getBean(ServiceSpringKeys.PRICE_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        dumpDataBase("price_cats_before", "TCATEGORY");
    }

    /**
     * Test than we are can getByKey the minimal price through price tiers for multisku product.
     */
    //@Ignore("java.lang.AssertionError")
    @Test
    public void getMinimalRegularPriceTest() {
        Shop shop = shopService.getShopByDomainName("www.gadget.yescart.org");
        Product product = productService.getProductById(10000L);
        assertNotNull(product);
        assertEquals(4, product.getSku().size());

        // Test default (pricing policy null or empty) prices are returned correctly

        SkuPrice skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, false, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, true, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, false, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, true, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("140.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("140.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("130.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("130.11", skuPrice.getSalePriceForCalculation().getSecond());

        // Test special prices policy

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("120.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("120.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("120.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("120.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("120.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("120.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("120.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("120.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("115.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("115.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("115.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("115.22", skuPrice.getSalePriceForCalculation().getSecond());

        // Make sure that default lower prices still win over the special
        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("3"), false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("100.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("100.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("3"), true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("100.11", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("100.11", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("120.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("120.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("120.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("120.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("115.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("115.22", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertValue("150.85", skuPrice.getRegularPrice());
        assertValue("115.22", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("150.85", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("115.22", skuPrice.getSalePriceForCalculation().getSecond());


        //Test that we can not getByKey the minimal price through price tiers for multisku product for not configured currency
        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "BYR", BigDecimal.ONE, false, null, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertNull(skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "BYR", BigDecimal.ONE, true, null, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertNull(skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "UAH", BigDecimal.ONE, false, null, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertNull(skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "UAH", BigDecimal.ONE, true, null, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertNull(skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "UAH", BigDecimal.ONE, false, null, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertNull(skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "UAH", BigDecimal.ONE, true, null, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertNull(skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

    }

    @Test
    public void testGetAllCurrentProductPrices() throws Exception {

        Shop shop = shopService.getShopByDomainName("www.gadget.yescart.org");

        List<SkuPrice> skuPrice = priceService.getAllCurrentPrices(10000L, null, shop.getShopId(), null, "EUR", null, null);
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());  // must only show unique minimal price at each tier (i.e. tier "starts from" regardless of SKU)
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertNull(skuPrice.get(0).getPricingPolicy());
        assertNull(skuPrice.get(0).getSupplier());
        assertNull(skuPrice.get(0).getTag());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertEquals("SOBOT-PINK", skuPrice.get(0).getSkuCode());
        assertValue("150.85", skuPrice.get(0).getRegularPrice());
        assertValue("140.11", skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertNull(skuPrice.get(1).getPricingPolicy());
        assertNull(skuPrice.get(1).getSupplier());
        assertNull(skuPrice.get(1).getTag());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("150.85", skuPrice.get(1).getRegularPrice());
        assertValue("130.11", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertValue("3", skuPrice.get(2).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());


        skuPrice = priceService.getAllCurrentPrices(10000L, null, shop.getShopId(), null, "EUR", "P1", null);
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());  // must only show unique minimal price at each tier (i.e. tier "starts from" regardless of SKU)
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertEquals("P1", skuPrice.get(0).getPricingPolicy());
        assertNull(skuPrice.get(0).getSupplier());
        assertNull(skuPrice.get(0).getTag());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertEquals("SOBOT-ORIG", skuPrice.get(0).getSkuCode());
        assertValue("150.85", skuPrice.get(0).getRegularPrice());
        assertValue("120.22", skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertEquals("P1", skuPrice.get(1).getPricingPolicy());
        assertNull(skuPrice.get(1).getSupplier());
        assertNull(skuPrice.get(1).getTag());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("150.85", skuPrice.get(1).getRegularPrice());
        assertValue("115.22", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());

        skuPrice = priceService.getAllCurrentPrices(10000L, null, shop.getShopId(), null, "EUR", null, "TEST");
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());  // must only show unique minimal price at each tier (i.e. tier "starts from" regardless of SKU)
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertNull(skuPrice.get(0).getPricingPolicy());
        assertEquals("TEST", skuPrice.get(0).getSupplier());
        assertEquals("sup", skuPrice.get(0).getTag());
        assertEquals("SOBOT-PINK", skuPrice.get(0).getSkuCode());
        assertValue("140.85", skuPrice.get(0).getRegularPrice());
        assertValue("110.11", skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertNull(skuPrice.get(1).getPricingPolicy());
        assertEquals("TEST", skuPrice.get(1).getSupplier());
        assertEquals("sup", skuPrice.get(1).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("140.85", skuPrice.get(1).getRegularPrice());
        assertValue("105.22", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertValue("3", skuPrice.get(2).getQuantity());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());

        skuPrice = priceService.getAllCurrentPrices(10000L, null, shop.getShopId(), null, "EUR", "P1", "TEST");
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());  // must only show unique minimal price at each tier (i.e. tier "starts from" regardless of SKU)
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertEquals("P1", skuPrice.get(0).getPricingPolicy());
        assertEquals("TEST", skuPrice.get(0).getSupplier());
        assertEquals("sup", skuPrice.get(0).getTag());
        assertEquals("SOBOT-PINK", skuPrice.get(0).getSkuCode());
        assertValue("140.85", skuPrice.get(0).getRegularPrice());
        assertValue("100.11", skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertEquals("P1", skuPrice.get(1).getPricingPolicy());
        assertEquals("TEST", skuPrice.get(1).getSupplier());
        assertEquals("sup", skuPrice.get(1).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("140.85", skuPrice.get(1).getRegularPrice());
        assertValue("95.22", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertValue("3", skuPrice.get(2).getQuantity());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());


    }

    @Test
    public void testGetAllCurrentSKUPrices() throws Exception {

        Shop shop = shopService.getShopByDomainName("www.gadget.yescart.org");

        List<SkuPrice> skuPrice = priceService.getAllCurrentPrices(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", null, null);
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertNull(skuPrice.get(0).getPricingPolicy());
        assertNull(skuPrice.get(0).getSupplier());
        assertNull(skuPrice.get(0).getTag());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(0).getSkuCode());
        assertValue("150.0", skuPrice.get(0).getRegularPrice());
        assertValue(null, skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertNull(skuPrice.get(1).getPricingPolicy());
        assertNull(skuPrice.get(1).getSupplier());
        assertNull(skuPrice.get(1).getTag());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("150.85", skuPrice.get(1).getRegularPrice());
        assertValue("130.11", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertValue("3", skuPrice.get(2).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());

        skuPrice = priceService.getAllCurrentPrices(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", "P1", null);
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertEquals("P1", skuPrice.get(0).getPricingPolicy());
        assertNull(skuPrice.get(0).getSupplier());
        assertNull(skuPrice.get(0).getTag());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(0).getSkuCode());
        assertValue("140.00", skuPrice.get(0).getRegularPrice());
        assertValue(null, skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertEquals("P1", skuPrice.get(1).getPricingPolicy());
        assertNull(skuPrice.get(1).getSupplier());
        assertNull(skuPrice.get(1).getTag());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("150.85", skuPrice.get(1).getRegularPrice());
        assertValue("115.22", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());

        skuPrice = priceService.getAllCurrentPrices(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", null, "TEST");
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertNull(skuPrice.get(0).getPricingPolicy());
        assertNull(skuPrice.get(0).getSupplier());
        assertNull(skuPrice.get(0).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(0).getSkuCode());
        assertValue("150.0", skuPrice.get(0).getRegularPrice());
        assertValue(null, skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertNull(skuPrice.get(1).getPricingPolicy());
        assertEquals("TEST", skuPrice.get(1).getSupplier());
        assertEquals("sup", skuPrice.get(1).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("140.85", skuPrice.get(1).getRegularPrice());
        assertValue("105.22", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertValue("3", skuPrice.get(2).getQuantity());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());

        skuPrice = priceService.getAllCurrentPrices(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", "P1", "TEST");
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertValue("1", skuPrice.get(0).getQuantity());
        assertEquals("P1", skuPrice.get(0).getPricingPolicy());
        assertNull(skuPrice.get(0).getSupplier());
        assertNull(skuPrice.get(0).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(0).getSkuCode());
        assertValue("140.0", skuPrice.get(0).getRegularPrice());
        assertValue(null, skuPrice.get(0).getSalePrice());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertValue("2", skuPrice.get(1).getQuantity());
        assertEquals("P1", skuPrice.get(1).getPricingPolicy());
        assertEquals("TEST", skuPrice.get(1).getSupplier());
        assertEquals("sup", skuPrice.get(1).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertValue("140.85", skuPrice.get(1).getRegularPrice());
        assertValue("95.22", skuPrice.get(1).getSalePrice());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertValue("3", skuPrice.get(2).getQuantity());
        assertNull(skuPrice.get(2).getPricingPolicy());
        assertNull(skuPrice.get(2).getSupplier());
        assertNull(skuPrice.get(2).getTag());
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertValue("150.85", skuPrice.get(2).getRegularPrice());
        assertValue("100.11", skuPrice.get(2).getSalePrice());


    }

    @Test
    public void testEnforcedTierPrice() throws Exception {

        SkuPrice skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("0.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("6.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("6.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("0.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("6.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("6.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("6.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("6.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("12.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("12.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("6.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("6.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("12.50", skuPrice.getRegularPrice());
        assertValue("10.50", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("12.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("10.50", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("10.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("6.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("6.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("10.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("30.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("30.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

    }


    @Test
    public void testEnforcedTierPriceWithFallback() throws Exception {

        SkuPrice skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("0.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("0.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("0.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("0.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("12.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("12.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());


        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), false, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("12.50", skuPrice.getRegularPrice());
        assertValue("9.50", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("12.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("9.50", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), true, "P1", null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("12.50", skuPrice.getRegularPrice());
        assertValue("9.50", skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("12.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertValue("9.50", skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("10.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("10.250"), false, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("10.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("30.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("30.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("10.250"), true, null, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertValue("5.50", skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertValue("5.50", skuPrice.getSalePriceForCalculation().getFirst());
        assertNull(skuPrice.getSalePriceForCalculation().getSecond());

    }

    private void assertValue(String expected, BigDecimal price) {
        if (expected == null) {
            assertNull(price);
        } else {
            assertTrue("Price was " + price + ", expected " + expected, new BigDecimal(expected).compareTo(price) == 0);
        }
    }
    
}
