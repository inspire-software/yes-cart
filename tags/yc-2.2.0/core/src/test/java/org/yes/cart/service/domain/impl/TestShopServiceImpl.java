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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestShopServiceImpl extends BaseCoreDBTestCase {

    private ShopService shopService;

    @Before
    public void setUp()  {
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        super.setUp();
    }

    @Test
    public void testGetShopByCode() {
        assertNull(shopService.getShopByCode("NOTEXISTING-SHOP"));
        assertNotNull(shopService.getShopByCode("SHOIP3"));
    }

    // TODO: YC-64 fix to not depend on order or running
    @Test
    public void testGetAllCategoriesTestOnShopWithoutAssignedCategories() {
        Shop shop = shopService.getShopByDomainName("eddie.lives.somewhere.in.time");
        Set<Long> categorySet = shopService.getShopCategoriesIds(shop.getShopId());
        assertTrue(categorySet.isEmpty());
    }

    /**
     * Test to getByKey assigned categories
     */
    @Test
    public void testGetAllCategoriesTestOnShopWithLimitedAssignedCategories() {
        List<Long> categories = Arrays.asList(200L, 203L, 204L, 205L, 206L, 207L, 208L);
        List<Long> notAvailableCategories = Arrays.asList(201L, 202L);
        Shop shop = shopService.getShopByDomainName("long.live.robots");
        Set<Long> categorySet = shopService.getShopCategoriesIds(shop.getShopId());
        assertFalse(categorySet.isEmpty());
        assertEquals(categories.size(), categorySet.size());
        for (Long categoryId : categorySet) {
            assertTrue(categories.contains(categoryId));
            assertFalse(notAvailableCategories.contains(categoryId));
        }
    }

    /**
     * Prove, that supported currency can be assigned via shop attributes.
     */
    // TODO: YC-64 fix to not depend on order or running
    @Test
    public void testAssignCurrency() {
        Shop shop = shopService.getShopByDomainName("long.live.robots");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.Shop.SUPPORTED_CURRENCIES, "QWE,ASD,ZXC");
        shop = shopService.getShopByDomainName("long.live.robots");
        assertEquals("Supported currency is incorrect",
                "QWE,ASD,ZXC",
                shop.getAttributeByCode(AttributeNamesKeys.Shop.SUPPORTED_CURRENCIES).getVal());
    }

    /**
     * Prove, that supported currency can be assigned via shop attributes.
     */
    @Test
    public void testAssignCurrencys() {
        Shop shop = shopService.getShopByDomainName("long.live.robots");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.Shop.SUPPORTED_CURRENCIES, "QWE,ZXC");
        shop = shopService.getShopByDomainName("eddie.lives.somewhere.in.time");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.Shop.SUPPORTED_CURRENCIES, "ASD,USD,QWE,UAH");
        shop = shopService.getShopByDomainName("gadget.yescart.org");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.Shop.SUPPORTED_CURRENCIES, "");
        Collection<String> currencies = shopService.findAllSupportedCurrenciesByShops();
        assertEquals(5, currencies.size());
        Iterator<String> iter = currencies.iterator();
        assertEquals("ASD", iter.next());
        assertEquals("QWE", iter.next());
        assertEquals("UAH", iter.next());
        assertEquals("USD", iter.next());
        assertEquals("ZXC", iter.next());
    }
}