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
import org.yes.cart.util.MoneyUtils;

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
        assertEquals("EUR-0-100", navigationRecords.get(0).getValue());
        assertEquals("EUR-100-300", navigationRecords.get(1).getValue());
        assertEquals("EUR-300-500", navigationRecords.get(2).getValue());
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

        SkuPrice skuPrice = priceService.getMinimalRegularPrice(10000L, product.getDefaultSku().getCode(), shop, "EUR", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));

        skuPrice = priceService.getMinimalRegularPrice(10000L, null, shop, "EUR", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.00")).equals(skuPrice.getRegularPrice()));

        skuPrice = priceService.getMinimalRegularPrice(10000L, null, shop, "EUR", new BigDecimal("2"));
        assertNotNull(skuPrice);
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("145.00")).equals(skuPrice.getRegularPrice()));

        skuPrice = priceService.getMinimalRegularPrice(10000L, product.getDefaultSku().getCode(), shop, "EUR", new BigDecimal("2"));
        assertNotNull(skuPrice);
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.getRegularPrice()));

        skuPrice = priceService.getMinimalRegularPrice(10000L, "SOBOT-LIGHT", shop, "EUR", new BigDecimal("2"));
        assertNotNull(skuPrice);
        assertNull(skuPrice.getSalePriceForCalculation());
        assertTrue((new BigDecimal("145.00")).equals(skuPrice.getRegularPrice()));

        //Test than we are can not getByKey the minimal price through price tiers for multisku product for not cofigured currency
        skuPrice = priceService.getMinimalRegularPrice(10000L, product.getDefaultSku().getCode(), shop, "BYR", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePriceForCalculation());
        //Test than we are can getByKey the minimal price through price tiers for multisku product.
        skuPrice = priceService.getMinimalRegularPrice(10000L, product.getDefaultSku().getCode(), shop, "UAH", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertTrue(MoneyUtils.isFirstEqualToSecond(new BigDecimal("1716.67"), skuPrice.getRegularPrice()));

        skuPrice = priceService.getMinimalRegularPrice(10000L, null, shop, "UAH", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertTrue(MoneyUtils.isFirstEqualToSecond(new BigDecimal("1707.00"), skuPrice.getRegularPrice()));
    }

    @Test
    public void testGetAllCurrentPrices() throws Exception {
        Shop shop = shopService.getShopByDomainName("www.gadget.yescart.org");
        Product product = productService.getProductById(10000L);
        assertNotNull(product);
        assertEquals(4, product.getSku().size());

        List<SkuPrice> skuPrice = priceService.getAllCurrentPrices(10000L, product.getDefaultSku().getCode(), shop, "EUR");
        assertNotNull(skuPrice);
        assertEquals(1, skuPrice.size());
        assertNull(skuPrice.get(0).getSalePriceForCalculation());
        assertTrue((new BigDecimal("150.85")).equals(skuPrice.get(0).getRegularPrice()));

        skuPrice = priceService.getAllCurrentPrices(10000L, null, shop, "EUR");
        assertNotNull(skuPrice);
        assertEquals(5, skuPrice.size());  // 4 regular prices + 1 tier 2
        for (int i = 0; i < skuPrice.size(); i++) {
            assertNotNull(skuPrice.get(i).getRegularPrice());

        }


    }

    @Test
    public void testUpdateDerivedPrices() {

        Shop shop = shopService.getShopByDomainName("www.gadget.yescart.org");

        priceService.updateDerivedPrices(shop, "UAH");

        priceService.deleteDerivedPrices(shop, "UAH");


    }

    @Test
    public void testCreatePriceTierNodes() {

        PriceServiceImpl priceService1 = new PriceServiceImpl(null,null,null,null);

        assertEquals( new BigDecimal("3000").intValue(), priceService1.niceBigDecimal(new BigDecimal("3000")).intValue());
        assertEquals( new BigDecimal("30").intValue(), priceService1.niceBigDecimal(new BigDecimal("30")).intValue());
        assertEquals( new BigDecimal("0").intValue(), priceService1.niceBigDecimal(new BigDecimal("3")).intValue());
        assertEquals( new BigDecimal("10").intValue(), priceService1.niceBigDecimal(new BigDecimal("5")).intValue());
        assertEquals( new BigDecimal("0").intValue(), priceService1.niceBigDecimal(new BigDecimal("1")).intValue());

        assertEquals( new BigDecimal("10").intValue(), priceService1.niceBigDecimal(new BigDecimal("13")).intValue());

        assertEquals( new BigDecimal("10").intValue(), priceService1.niceBigDecimal(new BigDecimal("9")).intValue());

        assertEquals( new BigDecimal("1200").intValue(), priceService1.niceBigDecimal(new BigDecimal("1234")).intValue());
        assertEquals( new BigDecimal("5700").intValue(), priceService1.niceBigDecimal(new BigDecimal("5678")).intValue());

    }
    
    
    @Test
    public void testAddAllTimePrice() {
        List<Pair<String, SkuPrice>> skuPricesForOneSku = getSkuPrices("sku1");
        skuPricesForOneSku.addAll(getSkuPrices("sku2"));
        PriceServiceImpl priceServiceImpl = new PriceServiceImpl(null, null, null, null);
        priceServiceImpl.reorderSkuPrices(skuPricesForOneSku);
        List<Pair<String, SkuPrice>> rez = new LinkedList<Pair<String, SkuPrice>>();
        assertTrue(priceServiceImpl.addAllTimePrice(rez, skuPricesForOneSku , System.currentTimeMillis()));
        assertEquals(1, rez.size());
        assertEquals(11, rez.get(0).getSecond().getSkuPriceId());
    }

    @Test
    public void testAddStartPrice() {
        List<Pair<String, SkuPrice>> skuPricesForOneSku = getSkuPrices("sku1");
        PriceServiceImpl priceServiceImpl = new PriceServiceImpl(null, null, null, null);
        priceServiceImpl.reorderSkuPrices(skuPricesForOneSku);
        List<Pair<String, SkuPrice>> rez = new LinkedList<Pair<String, SkuPrice>>();
        assertTrue(priceServiceImpl.addStartPrice(rez, skuPricesForOneSku, System.currentTimeMillis()));
        assertEquals(1, rez.size());
        assertEquals(8, rez.get(0).getSecond().getSkuPriceId());
    }

    @Test
    public void testAddEndPrice() {
        List<Pair<String, SkuPrice>> skuPricesForOneSku = getSkuPrices("sku1");
        PriceServiceImpl priceServiceImpl = new PriceServiceImpl(null, null, null, null);
        priceServiceImpl.reorderSkuPrices(skuPricesForOneSku);
        List<Pair<String, SkuPrice>> rez = new LinkedList<Pair<String, SkuPrice>>();
        assertTrue(priceServiceImpl.addEndPrice(rez, skuPricesForOneSku, System.currentTimeMillis()));
        assertEquals(1, rez.size());
        assertEquals(5, rez.get(0).getSecond().getSkuPriceId());
    }

    @Test
    public void testAllFramedPrice() {
        List<Pair<String, SkuPrice>> skuPricesForOneSku = getSkuPrices("sku1");
        PriceServiceImpl priceServiceImpl = new PriceServiceImpl(null, null, null, null);
        priceServiceImpl.reorderSkuPrices(skuPricesForOneSku);
        List<Pair<String, SkuPrice>> rez = new LinkedList<Pair<String, SkuPrice>>();
        assertTrue(priceServiceImpl.addFramedPrice(rez, skuPricesForOneSku, System.currentTimeMillis()));
        assertEquals(1, rez.size());
        assertEquals(1, rez.get(0).getSecond().getSkuPriceId());
    }

    @Test
    public void testGetSkuPricesFilteredByTimeFrame() {

        List<Pair<String, SkuPrice>> skuPricesForOneSku = getSkuPrices("sku1");

        skuPricesForOneSku.addAll(getSkuPrices("sku2"));

        PriceServiceImpl priceServiceImpl = new PriceServiceImpl(null, null, null, null);

        List<Pair<String, SkuPrice>> rez = priceServiceImpl.getSkuPricesFilteredByTimeFrame(skuPricesForOneSku);

        assertEquals(2, rez.size());

        assertEquals(1, rez.get(0).getSecond().getSkuPriceId());
        assertEquals(1, rez.get(1).getSecond().getSkuPriceId());
    }


    private List<Pair<String, SkuPrice>> getSkuPrices(final String skuCode) {


        //this price will be overwrited by skuPrice1  because of order of adding -  skuPriceId
        final SkuPrice skuPrice0 = new SkuPriceEntity();
        skuPrice0.setSalefrom(new DateTime(1999,10,10,0,0,0,0).toDate());
        skuPrice0.setSaleto(new DateTime(2025,10,10,0,0,0,0).toDate());
        skuPrice0.setSkuPriceId(0);
        final SkuPrice skuPrice1 = new SkuPriceEntity();
        skuPrice1.setSalefrom(new DateTime(2000,10,10,0,0,0,0).toDate());
        skuPrice1.setSaleto(new DateTime(2024,10,10,0,0,0,0).toDate());
        skuPrice1.setSkuPriceId(1);
        //start and end in past
        final SkuPrice skuPrice2 = new SkuPriceEntity();
        skuPrice2.setSalefrom(new DateTime(2000,10,10,0,0,0,0).toDate());
        skuPrice2.setSaleto(new DateTime(2010,10,10,0,0,0,0).toDate());
        skuPrice2.setSkuPriceId(2);
        //start and end in future
        final SkuPrice skuPrice3 = new SkuPriceEntity();
        skuPrice3.setSalefrom(new DateTime(2020,10,10,0,0,0,0).toDate());
        skuPrice3.setSaleto(new DateTime(2021,10,10,0,0,0,0).toDate());
        skuPrice3.setSkuPriceId(3);


        // price end in future but   skuPrice5 has higher priority 
        final SkuPrice skuPrice4 = new SkuPriceEntity();
        skuPrice4.setSaleto(new DateTime(2024,9,9,0,0,0,0).toDate());
        skuPrice4.setSkuPriceId(4);
        final SkuPrice skuPrice5 = new SkuPriceEntity();
        skuPrice5.setSaleto(new DateTime(2040,9,9,0,0,0,0).toDate());
        skuPrice5.setSkuPriceId(5);
        // already end
        final SkuPrice skuPrice6 = new SkuPriceEntity();
        skuPrice6.setSaleto(new DateTime(1999,9,9,0,0,0,0).toDate());
        skuPrice6.setSkuPriceId(6);


        // this price starts in past, by 8 has more priority
        final SkuPrice skuPrice7 = new SkuPriceEntity();
        skuPrice7.setSalefrom(new DateTime(2001,10,10,0,0,0,0).toDate());
        skuPrice7.setSkuPriceId(7);
        final SkuPrice skuPrice8 = new SkuPriceEntity();
        skuPrice8.setSalefrom(new DateTime(2000,10,10,0,0,0,0).toDate());
        skuPrice8.setSkuPriceId(8);
        //will start in the future
        final SkuPrice skuPrice9 = new SkuPriceEntity();
        skuPrice9.setSalefrom(new DateTime(2050,10,10,0,0,0,0).toDate());
        skuPrice9.setSkuPriceId(9);



        //never end price with low priority
        final SkuPrice skuPrice10 = new SkuPriceEntity();
        skuPrice10.setSkuPriceId(10);
        //never end price with high priority
        final SkuPrice skuPrice11 = new SkuPriceEntity();
        skuPrice11.setSkuPriceId(11);






        List<Pair<String, SkuPrice>> rez =  new ArrayList<Pair<String, SkuPrice>>() {{
            add(new Pair<String, SkuPrice>(skuCode, skuPrice0));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice1));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice2));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice3));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice4));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice5));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice6));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice7));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice8));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice9));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice10));
            add(new Pair<String, SkuPrice>(skuCode, skuPrice11));
        }};

        Collections.shuffle(rez);

        return rez;
        
    }




}
