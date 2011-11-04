package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
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

    @Test
    public void testGetShopByCode() {

        final ShopService shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);

        assertNull(shopService.getShopByCode("NOTEXISTING-SHOP"));

        assertNotNull(shopService.getShopByCode("SHOIP3"));

    }


    /**
     *
     */
    @Test
    public void testGetAllCategoriesTestOnShopWithoutAssignedCategories() {
        final ShopService shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
        final Shop shop = shopService.getShopByDomainName("eddie.lives.somewhere.in.time");
        final Set<Category> categorySet = shopService.getShopCategories(shop);
        assertTrue(categorySet.isEmpty());
    }

    /**
     * Test to getByKey assigneg categories
     */
    @Test
    public void testGetAllCategoriesTestOnShopWithLimitedAssignedCategories() {
        final List<Long> categories = Arrays.asList(200L, 203L, 204L, 205L, 206L, 207L, 208L);
        final List<Long> notAvailableCategories = Arrays.asList(201L, 202L);
        final ShopService shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
        final Shop shop = shopService.getShopByDomainName("long.live.robots");
        final Set<Category> categorySet = shopService.getShopCategories(shop);
        assertFalse(categorySet.isEmpty());
        assertEquals(categories.size(), categorySet.size());
        for (Category category : categorySet) {
            assertTrue(categories.contains(category.getCategoryId()));
            assertFalse(notAvailableCategories.contains(category.getCategoryId()));
        }

    }

    /**
     * Prove, that supperted currency can be assigned via shop attributes.
     */
    @Test
    public void testAssignCurrency() {
        final ShopService shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getShopByDomainName("long.live.robots");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.SUPPORTED_CURRENSIES, "QWE,ASD,ZXC");
        shop = shopService.getShopByDomainName("long.live.robots");
        assertEquals(
                "Supported currency is incorrect",
                "QWE,ASD,ZXC",
                shop.getAttributeByCode(AttributeNamesKeys.SUPPORTED_CURRENSIES).getVal());

    }


    /**
     * Prove, that supperted currency can be assigned via shop attributes.
     */
    @Test
    public void testAssignCurrencys() {
        final ShopService shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getShopByDomainName("long.live.robots");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.SUPPORTED_CURRENSIES, "QWE,ZXC");

        shop = shopService.getShopByDomainName("eddie.lives.somewhere.in.time");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.SUPPORTED_CURRENSIES, "ASD,USD,QWE,UAH");

        shop = shopService.getShopByDomainName("gadget.npa.com");
        shopService.updateAttributeValue(shop.getShopId(), AttributeNamesKeys.SUPPORTED_CURRENSIES, "");


        Collection<String> currencies = shopService.getAllSupportedCurrenciesByShops();
        assertEquals(5, currencies.size());
        Iterator<String> iter = currencies.iterator();
        assertEquals("ASD", iter.next());
        assertEquals("QWE", iter.next());
        assertEquals("UAH", iter.next());
        assertEquals("USD", iter.next());
        assertEquals("ZXC", iter.next());


    }

}