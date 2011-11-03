package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.queryobject.FiteredNavigationRecord;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestPriceServiceImpl extends BaseCoreDBTestCase {

    ShopService shopService = null;
    PriceService priceService = null;
    ProductService productService = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        productService = (ProductService) ctx.getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        priceService = (PriceService) ctx.getBean(ServiceSpringKeys.PRICE_SERVICE);
        shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
    }

    /**
     * Test .
     */
    @Test
    public void testGetPriceNavigationRecords() {
        Shop shop = shopService.getShopByDomainName("www.gadget.npa.com");

        GenericDAO<Category, Long> categoryDAO = (GenericDAO<Category, Long>) ctx.getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        Category cat = categoryDAO.findById(129L); // this category hold navigation by price tiers
        assertNotNull(cat);
        final PriceTierTree priceTierTree = cat.getNavigationByPriceTree();
        assertNotNull(priceTierTree);
        List<FiteredNavigationRecord> navigationRecords = priceService.getPriceNavigationRecords(priceTierTree, "EUR", shop);
        assertNotNull(navigationRecords);
        assertEquals(3, navigationRecords.size());
        assertEquals("EUR-0-100", navigationRecords.get(0).getValue());
        assertEquals("EUR-100-300", navigationRecords.get(1).getValue());
        assertEquals("EUR-300-500", navigationRecords.get(2).getValue());

        /**
         * In other currency
         */
        navigationRecords = priceService.getPriceNavigationRecords(priceTierTree, "UAH", shop);
        assertNotNull(navigationRecords);
        assertEquals(3, navigationRecords.size());
        assertEquals("UAH-0-1138", navigationRecords.get(0).getValue());
        assertEquals("UAH-1138-3414", navigationRecords.get(1).getValue());
        assertEquals("UAH-3414-5690", navigationRecords.get(2).getValue());

    }


    /**
     * Test than we are can getByKey the minimal price through price tiers for multisku product.
     */
    @Test
    public void getMinimalRegularPriceTest() {
        Shop shop = shopService.getShopByDomainName("www.gadget.npa.com");
        Product product = productService.getProductById(10000L);

        assertNotNull(product);
        assertEquals(4, product.getSku().size());

        SkuPrice skuPrice = priceService.getMinimalRegularPrice(product.getSku(), shop, "EUR", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getSalePrice());
        assertTrue((new BigDecimal("150.00")).equals(skuPrice.getRegularPrice()));

        skuPrice = priceService.getMinimalRegularPrice(product.getSku(), shop, "EUR", new BigDecimal("2"));
        assertNotNull(skuPrice);
        assertNull(skuPrice.getSalePrice());
        assertTrue((new BigDecimal("145.00")).equals(skuPrice.getRegularPrice()));

        //Test than we are can not getByKey the minimal price through price tiers for multisku product for not cofigured currency
        skuPrice = priceService.getMinimalRegularPrice(product.getSku(), shop, "BYR", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertNull(skuPrice.getRegularPrice());
        assertNull(skuPrice.getSalePrice());

        //Test than we are can getByKey the minimal price through price tiers for multisku product.
        skuPrice = priceService.getMinimalRegularPrice(product.getSku(), shop, "UAH", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertTrue(MoneyUtils.isFirstEqualToSecond(new BigDecimal("1707.00"), skuPrice.getRegularPrice()));

    }

    @Test
    public void testGetSkuPricesFilteredByQuantity() {

        Shop shop = shopService.getShopByDomainName("www.gadget.npa.com");
        Product product = productService.getProductById(10000L);

        List<SkuPrice> skus = priceService.getSkuPriceFilteredByShop(product.getSku(), shop);

        skus = priceService.getSkuPriceFilteredByCurrency(
                priceService.getSkuPricesFilteredByQuantity(skus, new BigDecimal(2))
                , "EUR");

        boolean found = false;
        for (SkuPrice skuPrice : skus) {
            if ("SOBOT-LIGHT".equals(skuPrice.getSku().getCode())) {
                assertEquals(new BigDecimal("145.00"), skuPrice.getRegularPrice());
                found = true;
            }
        }

        assertTrue(found);


    }


    /**
     * Test than we are can getByKey the minimal price through price tiers for multisku product.
     */
    @Test
    public void testGetMinimalRegularPriceForNosupportedCurrencyTest() {
        Shop shop = shopService.getShopByDomainName("www.gadget.npa.com");
        Product product = productService.getProductById(10000L);

        assertNotNull(product);
        assertEquals(4, product.getSku().size());

        //Pair<BigDecimal, BigDecimal>

    }

    /**
     * Test than we are can getByKey the minimal price through price tiers for multisku product.
     */

    @Test
    public void testGetMinimalRegularPriceForsupportedCurrencyTest() {

        Product product = productService.getProductById(10000L);

        Shop shop = shopService.getShopByDomainName("www.gadget.npa.com");


        assertNotNull(product);
        assertEquals(4, product.getSku().size());

        SkuPrice skuPrice = priceService.getMinimalRegularPrice(product.getSku(), shop, "UAH", BigDecimal.ONE);
        assertNotNull(skuPrice);
        assertTrue(MoneyUtils.isFirstEqualToSecond(new BigDecimal("1707.00"), skuPrice.getRegularPrice()));
    }

}
