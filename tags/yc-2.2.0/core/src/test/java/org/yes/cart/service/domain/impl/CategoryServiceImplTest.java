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
import org.yes.cart.dao.EntityFactory;
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
public class CategoryServiceImplTest extends BaseCoreDBTestCase {



    private CategoryService categoryService;

    @Before
    public void setUp() {
        categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        super.setUp();
    }

    @Test
    public void testGetByProductId() {
        ProductCategoryService productCategoryService = (ProductCategoryService) ctx().getBean(ServiceSpringKeys.PRODUCT_CATEGORY_SERVICE);
        EntityFactory entityFactory = productCategoryService.getGenericDao().getEntityFactory();
        ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        ProductTypeService productTypeService = (ProductTypeService) ctx().getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        BrandService brandService = (BrandService) ctx().getBean(ServiceSpringKeys.BRAND_SERVICE);
        Product product = entityFactory.getByIface(Product.class);
        product.setCode("PROD_CODE");
        product.setName("product");
        product.setDescription("description");
        product.setProducttype(productTypeService.findById(1L));
        product.setAvailability(Product.AVAILABILITY_ALWAYS);
        product.setBrand(brandService.findById(101L));
        product = productService.create(product);
        assertTrue(product.getProductId() > 0);
        // assign created product it to categories
        ProductCategory productCategory = entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.findById(128L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);
        productCategory = entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.findById(133L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);
        List<Category> list = categoryService.findByProductId(product.getProductId());
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

    @Test
    public void testGetCategoryAttributeRecursive() {
        String val = categoryService.getCategoryAttributeRecursive(null, 105L, "SOME_NOT_EXISTING_ATTR", null);
        assertNull(val);
        val = categoryService.getCategoryAttributeRecursive(null, 105L, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertEquals("10,20,50", val);
        val = categoryService.getCategoryAttributeRecursive(null, 139L, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertEquals("6,12,24", val);
    }

    @Test
    public void testGetCategoryAttributeRecursiveMulti() {
        String[] val = categoryService.getCategoryAttributeRecursive(null, 105L, new String[] { "SOME_NOT_EXISTING_ATTR", "SOME_NOT_EXISTING_ATTR_2" });
        assertNull(val);
        val = categoryService.getCategoryAttributeRecursive(null, 105L, new String[] { "SOME_NOT_EXISTING_ATTR", AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE });
        assertNotNull(val);
        assertNull(val[0]);
        assertEquals("10,20,50", val[1]);
    }

    @Test
    public void testGetUIVariationTestNoFailover() {
        Category category = categoryService.findById(139L);
        assertNotNull(category);
        assertNull(category.getUitemplate());
        String uiVariation = categoryService.getCategoryTemplate(139L);
        assertNull(uiVariation);
    }

    @Test
    public void testGetUIVariationTestExists() {
        Category category = categoryService.findById(101L);
        assertNotNull(category);
        assertEquals("boys", category.getUitemplate());
        String uiVariation = categoryService.getCategoryTemplate(101L);
        assertEquals(category.getUitemplate(), uiVariation);
    }

    @Test
    public void testGetUIVariationTestFailover() {
        Category category = categoryService.findById(107L);
        assertNotNull(category);
        assertNull(category.getUitemplate());
        String uiVariation = categoryService.getCategoryTemplate(107L);
        assertEquals("fun", uiVariation);
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
    public void testGetChildCategoriesRecursiveNullTest() {
        Set<Category> categories = categoryService.getChildCategoriesRecursive(0l);
        assertTrue(categories.isEmpty());
    }

    @Test
    public void testIsCategoryHasChildrenTrue() throws Exception {
        assertTrue(categoryService.isCategoryHasChildren(101L));
    }

    @Test
    public void testIsCategoryHasChildrenFalse() throws Exception {

        final Category newCategory = categoryService.getGenericDao().getEntityFactory().getByIface(Category.class);
        newCategory.setGuid("TEST-CHILDREN");
        newCategory.setName("TEST-CHILDREN");

        final Category saved = categoryService.create(newCategory);
        assertFalse(categoryService.isCategoryHasChildren(saved.getCategoryId()));

        categoryService.delete(saved);

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
