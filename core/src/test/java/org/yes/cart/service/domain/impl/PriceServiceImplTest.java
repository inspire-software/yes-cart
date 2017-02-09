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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.impl.SkuPriceEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

    @Before
    public void setUp() {
        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        priceService = (PriceService) ctx().getBean(ServiceSpringKeys.PRICE_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        try {
            dumpDataBase("x0x0xx_cats", new String[] { "TCATEGORY" });
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setUp();
    }

    @Test
    public void testGetPriceNavigationRecords() {
        Shop shop = shopService.getShopByDomainName("www.gadget.yescart.org");
        CategoryService categoryService = ctx().getBean("categoryService", CategoryService.class);
        Category cat = categoryService.findById(129L); // this category hold navigation by price tiers
        assertNotNull(cat);
        PriceTierTree priceTierTree = cat.getNavigationByPriceTree();
        try {
            dumpDataBase("x1x1xx_cats_nav", new String[] { "TCATEGORY" });
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(priceTierTree);
        List<FilteredNavigationRecord> navigationRecords = priceService.getPriceNavigationRecords(priceTierTree, "EUR", shop);
        assertNotNull(navigationRecords);
        assertEquals(3, navigationRecords.size());
        assertEquals("EUR-_-0-_-100", navigationRecords.get(0).getValue());
        assertEquals("EUR-_-100-_-300", navigationRecords.get(1).getValue());
        assertEquals("EUR-_-300-_-500", navigationRecords.get(2).getValue());
        // In other currency
        navigationRecords = priceService.getPriceNavigationRecords(priceTierTree, "UAH", shop);
        assertNotNull(navigationRecords);
        assertEquals(0, navigationRecords.size()); // we only use explicit navs (no auto exchange)
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

        SkuPrice skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, false, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, true, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, false, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, true, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-PINK", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), false, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), true, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), true, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), false, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), false, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), true, null);
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.getSalePrice()));

        // Test special prices policy

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("120.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("120.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, false, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("120.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", BigDecimal.ONE, true, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("120.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("115.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("115.22")).equals(skuPrice.getSalePrice()));

        // Make sure that default lower prices still win over the special
        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("3"), false, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("100.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "EUR", new BigDecimal("3"), true, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("100.11")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("120.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-ORIG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("120.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), false, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("115.22")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(10000L, "SOBOT-LIGHT", shop.getShopId(), null, "EUR", new BigDecimal("2"), true, "P1");
        assertNotNull(skuPrice);
        assertEquals("SOBOT-LIGHT", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("115.22")).equals(skuPrice.getSalePrice()));


        //Test that we can not getByKey the minimal price through price tiers for multisku product for not configured currency
        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "BYR", BigDecimal.ONE, false, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePriceForCalculation());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "BYR", BigDecimal.ONE, true, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePriceForCalculation());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "UAH", BigDecimal.ONE, false, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePriceForCalculation());

        skuPrice = priceService.getMinimalPrice(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "UAH", BigDecimal.ONE, true, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePriceForCalculation());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "UAH", BigDecimal.ONE, false, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePriceForCalculation());

        skuPrice = priceService.getMinimalPrice(10000L, null, shop.getShopId(), null, "UAH", BigDecimal.ONE, true, null);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePriceForCalculation());

    }

    @Test
    public void testGetAllCurrentPrices() throws Exception {
        Shop shop = shopService.getShopByDomainName("www.gadget.yescart.org");
        Product product = productService.getProductById(10000L);
        assertNotNull(product);
        assertEquals(4, product.getSku().size());

        List<SkuPrice> skuPrice = priceService.getAllCurrentPrices(10000L, product.getDefaultSku().getCode(), shop.getShopId(), null, "EUR", null);
        assertNotNull(skuPrice);
        assertEquals(1, skuPrice.size());
        assertEquals("SOBOT-ORIG", skuPrice.get(0).getSkuCode());
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(0).getRegularPrice()));
        assertTrue((new BigDecimal("140.22")).equals(skuPrice.get(0).getSalePrice()));

        skuPrice = priceService.getAllCurrentPrices(10000L, null, shop.getShopId(), null, "EUR", null);
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());  // must only show unique minimal price at each tier
        assertEquals("SOBOT-PINK", skuPrice.get(0).getSkuCode());
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(0).getRegularPrice()));
        assertTrue((new BigDecimal("140.11")).equals(skuPrice.get(0).getSalePrice()));
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(1).getRegularPrice()));
        assertTrue((new BigDecimal("130.11")).equals(skuPrice.get(1).getSalePrice()));
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(2).getRegularPrice()));
        assertTrue((new BigDecimal("100.11")).equals(skuPrice.get(2).getSalePrice()));


        skuPrice = priceService.getAllCurrentPrices(10000L, null, shop.getShopId(), null, "EUR", "P1");
        assertNotNull(skuPrice);
        assertEquals(3, skuPrice.size());  // must only show unique minimal price at each tier
        assertEquals("SOBOT-ORIG", skuPrice.get(0).getSkuCode());
        assertNotNull(skuPrice.get(0).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(0).getRegularPrice()));
        assertTrue((new BigDecimal("120.22")).equals(skuPrice.get(0).getSalePrice()));
        assertEquals("SOBOT-LIGHT", skuPrice.get(1).getSkuCode());
        assertNotNull(skuPrice.get(1).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(1).getRegularPrice()));
        assertTrue((new BigDecimal("115.22")).equals(skuPrice.get(1).getSalePrice()));
        assertEquals("SOBOT-LIGHT", skuPrice.get(2).getSkuCode());
        assertNotNull(skuPrice.get(2).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(2).getRegularPrice()));
        assertTrue((new BigDecimal("100.11")).equals(skuPrice.get(2).getSalePrice()));



    }

    @Test
    public void testEnforcedTierPrice() throws Exception {

        SkuPrice skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("0.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("6.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("0.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("6.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("6.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("12.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), false, "P1");
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("6.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("1.250"), true, "P1");
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("12.50")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("10.50")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("10.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("6.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 10L, null, "EUR", new BigDecimal("10.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("30.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

    }


    @Test
    public void testEnforcedTierPriceWithFallback() throws Exception {

        SkuPrice skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("0.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("0.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("0.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("0.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("12.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), false, "P1");
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), false, "P1");
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("1.250"), true, "P1");
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("12.50")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("9.50")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("1.250"), true, "P1");
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNotNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("12.50")).equals(skuPrice.getRegularPrice()));
        assertTrue((new BigDecimal("9.50")).equals(skuPrice.getSalePrice()));

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("10.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("10.250"), false, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, 10L, "EUR", new BigDecimal("10.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("30.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

        skuPrice = priceService.getMinimalPrice(null, "WV_CARRIERSLA_KG", 1011L, null, "EUR", new BigDecimal("10.250"), true, null);
        assertNotNull(skuPrice);
        assertEquals("WV_CARRIERSLA_KG", skuPrice.getSkuCode());
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("5.50")).equals(skuPrice.getRegularPrice()));
        assertNull(skuPrice.getSalePrice());

    }


    @Test
    public void testCreatePriceTierNodes() {

        PriceServiceImpl priceService1 = new PriceServiceImpl(null, null);

        assertEquals( new BigDecimal("3000").intValue(), priceService1.niceBigDecimal(new BigDecimal("3000")).intValue());
        assertEquals( new BigDecimal("30").intValue(), priceService1.niceBigDecimal(new BigDecimal("30")).intValue());
        assertEquals( new BigDecimal("0").intValue(), priceService1.niceBigDecimal(new BigDecimal("3")).intValue());
        assertEquals( new BigDecimal("10").intValue(), priceService1.niceBigDecimal(new BigDecimal("5")).intValue());
        assertEquals( new BigDecimal("0").intValue(), priceService1.niceBigDecimal(new BigDecimal("1")).intValue());

        assertEquals(new BigDecimal("10").intValue(), priceService1.niceBigDecimal(new BigDecimal("13")).intValue());

        assertEquals(new BigDecimal("10").intValue(), priceService1.niceBigDecimal(new BigDecimal("9")).intValue());

        assertEquals( new BigDecimal("1200").intValue(), priceService1.niceBigDecimal(new BigDecimal("1234")).intValue());
        assertEquals( new BigDecimal("5700").intValue(), priceService1.niceBigDecimal(new BigDecimal("5678")).intValue());

    }

}
