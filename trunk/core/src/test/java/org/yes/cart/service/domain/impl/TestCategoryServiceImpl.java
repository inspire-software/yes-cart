package org.yes.cart.service.domain.impl;

import org.junit.Test;
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

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCategoryServiceImpl extends BaseCoreDBTestCase {


    public TestCategoryServiceImpl() {
        super();
    }

    @Test
    public void testGetByProductId() {
        final CategoryService categoryService = (CategoryService) ctx.getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        final ProductCategoryService productCategoryService = (ProductCategoryService) ctx.getBean(ServiceSpringKeys.PRODUCT_CATEGORY_SERVICE);
        final EntityFactory entityFactory = productCategoryService.getGenericDao().getEntityFactory();
        final ProductService productService = (ProductService) ctx.getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        final ProductTypeService productTypeService = (ProductTypeService) ctx.getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        final BrandService brandService = (BrandService) ctx.getBean(ServiceSpringKeys.BRAND_SERVICE);
        final AvailabilityService availabilityService = (AvailabilityService) ctx.getBean(ServiceSpringKeys.AVAILABILITY_SERVICE);

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
        ProductCategory productCategory =  entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.getById(128L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);

        productCategory =  entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.getById(133L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);

        List<Category> list =  categoryService.getByProductId(product.getProductId());
        assertEquals(2, list.size());



    }

    @Test
    public void testAssignUnassignCategoryToShop() {

        final CategoryService categoryService = (CategoryService) ctx.getBean(ServiceSpringKeys.CATEGORY_SERVICE);

        final ShopService shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);

        final EntityFactory entityFactory = shopService.getGenericDao().getEntityFactory();

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
    public void  testGetCategoryAttributeRecursive() {
        CategoryService categoryService = (CategoryService) ctx.getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        GenericDAO<Category, Long> categoryDAO = (GenericDAO<Category, Long>) ctx.getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        String val = categoryService.getCategoryAttributeRecursive(categoryDAO.findById(105L), "SOME_NOT_EXISTING_ATTR");
        assertNull(val);


        val = categoryService.getCategoryAttributeRecursive(categoryDAO.findById(105L), AttributeNamesKeys.CATEGORY_ITEMS_PER_PAGE);
        assertEquals("10,20,50", val);

        val = categoryService.getCategoryAttributeRecursive(categoryDAO.findById(139L), AttributeNamesKeys.CATEGORY_ITEMS_PER_PAGE);
        assertEquals("6,12,24", val);


    }


    /**
     * Test, that we able to getByKey the AttributeEnumeration.CATEGORY_ITEMS_PER_PAGE
     * settings
     */
    @Test
    public void testGetItemsPerPageTest() {

        CategoryService categoryService = (CategoryService) ctx.getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        GenericDAO<Category, Long> categoryDAO = (GenericDAO<Category, Long>) ctx.getBean(DaoServiceBeanKeys.CATEGORY_DAO);

        // Category with seted CATEGORY_ITEMS_PER_PAGE
        Category category = categoryDAO.findById(105L);
        assertNotNull(category);
        assertNotNull(category.getAttributeByCode(AttributeNamesKeys.CATEGORY_ITEMS_PER_PAGE));
        List<String> itemsPerPage = categoryService.getItemsPerPage(category);
        assertNotNull(itemsPerPage);
        assertEquals(3, itemsPerPage.size());
        assertEquals("10", itemsPerPage.get(0));
        assertEquals("20", itemsPerPage.get(1));
        assertEquals("50", itemsPerPage.get(2));


        // Failover part
        category = categoryDAO.findById(139L);
        assertNotNull(category);
        assertNull(category.getAttributesByCode(AttributeNamesKeys
                .CATEGORY_ITEMS_PER_PAGE));
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

        CategoryService categoryService = (CategoryService) ctx.getBean(ServiceSpringKeys.CATEGORY_SERVICE);

        GenericDAO<Category, Long> categoryDAO = (GenericDAO<Category, Long>) ctx.getBean(DaoServiceBeanKeys.CATEGORY_DAO);

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

        CategoryService categoryService = (CategoryService) ctx.getBean(ServiceSpringKeys.CATEGORY_SERVICE);

        Set<Category> categories = categoryService.getChildCategoriesRecursive(101L);

        for (Category category : categories) {
            assertTrue(categoryIds.contains(category.getCategoryId()));
        }

    }


}
