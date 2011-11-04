package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopCategoryService;
import org.yes.cart.service.domain.ShopService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 11:44:52
 */
public class ShopCategoryServiceImplTest extends BaseCoreDBTestCase {

    private ShopCategoryService shopCategoryService;
    private ShopService shopService;
    private CategoryService categoryService;

    @Before
    public void setUp() throws Exception {
        shopCategoryService = (ShopCategoryService)
                ctx.getBean(ServiceSpringKeys.SHOP_CATEGORY_SERVICE);
        shopService = (ShopService)
                ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
        categoryService = (CategoryService)
                ctx.getBean(ServiceSpringKeys.CATEGORY_SERVICE);

    }

    @Test
    public void testFindByShopCategory() {
        assertNotNull(
                shopCategoryService.findByShopCategory(
                        shopService.getById(10L),
                        categoryService.getById(133L))
        );
        assertNull(
                shopCategoryService.findByShopCategory(
                        shopService.getById(777L),
                        categoryService.getById(133L))
        );
    }


    @Test
    public void testDeleteAll() {

        shopCategoryService.deleteAll(categoryService.getById(133L));

        assertNull(
                shopCategoryService.findByShopCategory(
                        shopService.getById(10L),
                        categoryService.getById(133L))
        );

    }


}
