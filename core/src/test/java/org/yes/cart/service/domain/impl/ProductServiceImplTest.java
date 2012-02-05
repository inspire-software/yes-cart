package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Availability;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImplTest extends BaseCoreDBTestCase {

    private ProductService productService;

    @Before
    public void setUp() throws Exception {
        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
    }

    @Test
    public void testCreate() {
        ProductTypeService productTypeService = (ProductTypeService) ctx().getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        BrandService brandService = (BrandService) ctx().getBean(ServiceSpringKeys.BRAND_SERVICE);
        AvailabilityService availabilityService = (AvailabilityService) ctx().getBean(ServiceSpringKeys.AVAILABILITY_SERVICE);
        EntityFactory entityFactory = productService.getGenericDao().getEntityFactory();
        Product product = entityFactory.getByIface(Product.class);
        product.setCode("PROD_CODE");
        product.setName("product");
        product.setDescription("description");
        product.setProducttype(productTypeService.getById(1L));
        product.setAvailability(availabilityService.getById(Availability.ALWAYS));
        product.setBrand(brandService.getById(101L));
        product = productService.create(product);
        assertTrue(product.getProductId() > 0);
        //test that default sku is created
        assertFalse(product.getSku().isEmpty());
        //code the same
        assertEquals(product.getCode(), product.getSku().iterator().next().getCode());
    }

    @Test
    public void testGetProductById() {
        Product product = productService.getProductById(11004L);
        assertNotNull(product);
        assertEquals("PRODUCT5", product.getCode());
        product = productService.getProductById(654321987456L); //not existing product
        assertNull(product);
    }

    @Test
    public void testGetProductQuantity0() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(10L);
        assertNotNull(shop);
        Product product = productService.getProductById(10000L);
        assertNotNull(product);
        assertEquals("SOBOT", product.getCode());
        /* test that sobot has 10 skus on all warehouses  */
        BigDecimal qty = productService.getProductQuantity(product);
        assertNotNull(qty);
        assertEquals(BigDecimal.TEN, qty);
    }

    @Test
    public void testGetProductQuantity1() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(10L);
        assertNotNull(shop);
        BigDecimal qty;
        Product product = productService.getProductById(14004L);
        //PRODUCT5-ACC has no price records
        assertNotNull(product);
        assertEquals("PRODUCT5-ACC", product.getCode());
        /* Ttest that sobot has 10 skus on warehouse */
        qty = productService.getProductQuantity(product);
        assertNull(qty);
    }

    @Test
    public void testGetProductQuantity2() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(10L);
        assertNotNull(shop);
        BigDecimal qty;
        Product product = productService.getProductById(15007L);
        //product has null qty
        assertNotNull(product);
        assertEquals("PRODUCT8", product.getCode());
        /* Ttest that sobot has 0 skus on warehouse */
        qty = productService.getProductQuantity(product);
        assertNull("Product  " + product.getProductId() + " has " + qty + " quyantity, but expected null", qty);
    }

    /**
     * The same tests for particular shop, that has skus on warehouses
     */
    @Test
    public void testGetProductQuantityByProdShop0() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(10L);
        assertNotNull(shop);
        Product product = productService.getProductById(10000L);
        assertNotNull(product);
        assertEquals("SOBOT", product.getCode());
        /* test that sobot has 10 skus on all warehouses  */
        BigDecimal qty = productService.getProductQuantity(product, shop);
        assertNotNull(qty);
        assertEquals(BigDecimal.TEN, qty);
    }

    /**
     * The same tests for particular shop, that has skus on warehouses
     */
    @Test
    public void testGetProductQuantityByProdShop1() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(10L);
        assertNotNull(shop);
        Product product = productService.getProductById(14004L);
        //PRODUCT5-ACC has no price records
        assertNotNull(product);
        assertEquals("PRODUCT5-ACC", product.getCode());
        /* Ttest that sobot has 10 skus on warehouse */
        BigDecimal qty = productService.getProductQuantity(product, shop);
        assertNull(qty);
    }

    /**
     * The same tests for particular shop, that has skus on warehouses
     */
    @Test
    public void testGetProductQuantityByProdShop2() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(10L);
        assertNotNull(shop);
        Product product = productService.getProductById(15005L);
        //PRODUCT4 has 0 qty
        assertNotNull(product);
        assertEquals("PRODUCT6", product.getCode());
        /* Ttest that 0 skus on warehouses */
        BigDecimal qty = productService.getProductQuantity(product, shop);
        assertNotNull(qty);
        assertEquals(BigDecimal.ZERO, qty);
    }

    /**
     * The same tests for particular shop, that has not skus on warehouses
     */
    @Test
    public void testGetProductQuantityByProdShop3() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(50L);
        assertNotNull(shop);
        Product product = productService.getProductById(10000L);
        assertNotNull(product);
        assertEquals("SOBOT", product.getCode());
        /* test that sobot has 10 skus on all warehouses  */
        BigDecimal qty = productService.getProductQuantity(product, shop);
        assertNull(qty);
        //PRODUCT5-ACC has no price records
        product = productService.getProductById(14004L);
        assertNotNull(product);
        assertEquals("PRODUCT5-ACC", product.getCode());
        /* Ttest that sobot has 10 skus on warehouse */
        qty = productService.getProductQuantity(product, shop);
        assertNull(qty);
        //PRODUCT4 has 0 qty
        product = productService.getProductById(11003L);
        assertNotNull(product);
        assertEquals("PRODUCT4", product.getCode());
        /* Ttest that sobot has 10 skus on warehouse */
        qty = productService.getProductQuantity(product, shop);
        assertNull(qty);
    }

    @Test
    public void testGetProductByIdList() {
        List<Long> ids = new ArrayList<Long>() {{
            add(12004L);
            add(15004L);
        }};
        List<Product> prods = productService.getProductByIdList(ids);
        assertEquals(2, prods.size());
        ids = new ArrayList<Long>() {{
            add(12004L);
            add(15004L);
            add(777L);
        }};
        prods = productService.getProductByIdList(ids);
        assertEquals(2, prods.size());
        new ArrayList<String>() {{
            add("12004");
            add("15004");
        }};
        prods = productService.getProductByIdList(ids);
        assertEquals(2, prods.size());
    }

    @Test
    public void testGetRandomProductByCategory() {
        CategoryService categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        Category category = categoryService.getById(211L);
        Set<Long> list = new HashSet<Long>();
        for (int i = 0; i < 10; i++) {
            list.add(productService.getRandomProductByCategory(category).getProductId());
        }
        //assume, that we select at least two different products in ten times
        assertTrue("Set is " + list + " his size is " + list.size() + " but expected more that 1", list.size() > 1);
    }

    //@Ignore("java.lang.AssertionError")
    @Test
    public void testGetFeaturedProducts() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        CategoryService categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        Shop shop = shopService.getById(10L);
        assertNotNull(shop);
        Set<Category> shopCategories = shopService.getShopCategories(shop);
        assertNotNull(shopCategories);
        assertFalse(shopCategories.isEmpty());
        List<Long> shopCategoryIds = categoryService.transform(shopCategories);
        assertNotNull(shopCategoryIds);
        assertFalse(shopCategoryIds.isEmpty());
        assertTrue(shopCategoryIds.contains(211L));
        List<Product> rezLimit = productService.getFeaturedProducts(shopCategoryIds, 2);
        assertNotNull(rezLimit);
        try {
            dumpDataBase("x2x2", new String[] {"TPRODUCT"});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertFalse(rezLimit.isEmpty());
        assertEquals(2, rezLimit.size());
        rezLimit = productService.getFeaturedProducts(shopCategoryIds, 0);
        assertNotNull(rezLimit);
        assertTrue(rezLimit.isEmpty());
        List<Product> rez = productService.getFeaturedProducts(shopCategoryIds, 100);
        assertNotNull(rez);
        assertFalse(rez.isEmpty());
        assertEquals(4, rez.size());
        List<String> expectedProductCodes = new ArrayList<String>();
        expectedProductCodes.add("FEATURED-PRODUCT9");
        expectedProductCodes.add("FEATURED-PRODUCT7");
        expectedProductCodes.add("FEATURED-PRODUCT5");
        expectedProductCodes.add("FEATURED-PRODUCT1");
        for (Product prod : rez) {
            assertTrue("add " + prod.getCode(), expectedProductCodes.contains(prod.getCode()));
            expectedProductCodes.remove(prod.getCode());
        }
        assertTrue(expectedProductCodes.isEmpty());
    }
}
