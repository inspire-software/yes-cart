package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCategoryServiceImpl extends BaseCoreDBTestCase {

    private CategoryService categoryService;

    @Before
    public void setUp() throws Exception {
        categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
    }

    @Test
    public void testGetByProductId() {
        ProductCategoryService productCategoryService = (ProductCategoryService) ctx().getBean(ServiceSpringKeys.PRODUCT_CATEGORY_SERVICE);
        EntityFactory entityFactory = productCategoryService.getGenericDao().getEntityFactory();
        ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        ProductTypeService productTypeService = (ProductTypeService) ctx().getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        BrandService brandService = (BrandService) ctx().getBean(ServiceSpringKeys.BRAND_SERVICE);
        AvailabilityService availabilityService = (AvailabilityService) ctx().getBean(ServiceSpringKeys.AVAILABILITY_SERVICE);
        Product product = entityFactory.getByIface(Product.class);
        product.setCode("PROD_CODE");
        product.setName("product");
        product.setDescription("description");
        product.setProducttype(productTypeService.getById(1L));
        product.setAvailability(availabilityService.getById(Availability.ALWAYS));
        product.setBrand(brandService.getById(101L));
        product = productService.create(product);
        assertTrue(product.getProductId() > 0);
        // assign created product it to categories
        ProductCategory productCategory = entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.getById(128L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);
        productCategory = entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.getById(133L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);
        List<Category> list = categoryService.getByProductId(product.getProductId());
        assertEquals(2, list.size());
    }

    @Test
    public void testAssignUnassignCategoryToShop() {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        EntityFactory entityFactory = shopService.getGenericDao().getEntityFactory();
        Category rootCategory = categoryService.getRootCategory();
        Category category = entityFactory.getByIface(Category.class);
        category.setName("test category");
        category.setParentId(rootCategory.getCategoryId());
        category = categoryService.create(category);
        assertNotNull(category);
        Shop shop = shopService.getById(10L); //SHOIP1
        ShopCategory shopCategory = categoryService.assignToShop(category.getCategoryId(), shop.getShopId());
        assertNotNull(shopCategory);
        shop = shopService.getById(10L); //SHOIP1
        boolean found = false;
        for (ShopCategory sc : shop.getShopCategory()) {
            if (sc.getShopCategoryId() == shopCategory.getShopCategoryId()) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        categoryService.unassignFromShop(category.getCategoryId(), shop.getShopId());
        shop = shopService.getById(10L); //SHOIP1
        found = false;
        for (ShopCategory sc : shop.getShopCategory()) {
            if (sc.getShopCategoryId() == shopCategory.getShopCategoryId()) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    /**
     * Test, that we able to getByKey value atrtibutes in category hierarchy
     */
    @Test
    public void testGetCategoryAttributeRecursive() {
        GenericDAO<Category, Long> categoryDAO = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        String val = categoryService.getCategoryAttributeRecursive(categoryDAO.findById(105L), "SOME_NOT_EXISTING_ATTR", null);
        assertNull(val);
        val = categoryService.getCategoryAttributeRecursive(categoryDAO.findById(105L), AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertEquals("10,20,50", val);
        val = categoryService.getCategoryAttributeRecursive(categoryDAO.findById(139L), AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertEquals("6,12,24", val);
    }

    /**
     * Test, that we able to getByKey the AttributeEnumeration.CATEGORY_ITEMS_PER_PAGE settings
     */
    @Test
    public void testGetItemsPerPageTest() {
        GenericDAO<Category, Long> categoryDAO = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        // Category with seted CATEGORY_ITEMS_PER_PAGE
        Category category = categoryDAO.findById(105L);
        assertNotNull(category);
        assertNotNull(category.getAttributeByCode(AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE));
        List<String> itemsPerPage = categoryService.getItemsPerPage(category);
        assertNotNull(itemsPerPage);
        assertEquals(3, itemsPerPage.size());
        assertEquals("10", itemsPerPage.get(0));
        assertEquals("20", itemsPerPage.get(1));
        assertEquals("50", itemsPerPage.get(2));
        // Failover part
        category = categoryDAO.findById(139L);
        assertNotNull(category);
        assertNull(category.getAttributesByCode(AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE));
        itemsPerPage = categoryService.getItemsPerPage(category);
        assertNotNull(itemsPerPage);
        assertEquals(3, itemsPerPage.size());
        assertEquals("6", itemsPerPage.get(0));
        assertEquals("12", itemsPerPage.get(1));
        assertEquals("24", itemsPerPage.get(2));
    }

    /**
     * UI template variation test. Prove that we are able to
     * getByKey ui tepmlate from parent category on any level.
     */
    @Test
    public void testGetUIVariationTest() {
        GenericDAO<Category, Long> categoryDAO = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        Category category = categoryDAO.findById(139L);
        assertNotNull(category);
        assertNull(category.getUitemplate());
        String uiVariation = categoryService.getCategoryTemplateVariation(category);
        assertEquals("default", uiVariation);
    }

    @Test
    public void testGetChildCategoriesRecursiveTest() {
        Set<Long> categoryIds = new HashSet<Long>();
        categoryIds.addAll(Arrays.asList(101L, 102L, 103L, 104L, 105L, 143L, 144L));
        Set<Category> categories = categoryService.getChildCategoriesRecursive(101L);
        for (Category category : categories) {
            assertTrue(categoryIds.contains(category.getCategoryId()));
        }
    }

    @Test
    public void testIsCategoryHasSubcategory() {
        CategoryService categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        assertTrue(categoryService.isCategoryHasSubcategory(300, 304));
        assertTrue(categoryService.isCategoryHasSubcategory(301, 304));
        assertFalse(categoryService.isCategoryHasSubcategory(301, 312));
        assertFalse(categoryService.isCategoryHasSubcategory(50, 312));      // not existing root
        assertFalse(categoryService.isCategoryHasSubcategory(50, 98));      // not existing root   and given sub category
        assertFalse(categoryService.isCategoryHasSubcategory(300, 98));      // existing root   and not existing given sub category
        assertFalse(categoryService.isCategoryHasSubcategory(304, 300)); //reverse
    }
}
