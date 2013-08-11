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
    public void setUp() {
        shopCategoryService = (ShopCategoryService) ctx().getBean(ServiceSpringKeys.SHOP_CATEGORY_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        super.setUp();
    }

    @Test
    public void testFindByShopCategory() {
        assertNotNull(shopCategoryService.findByShopCategory(
                shopService.getById(10L),
                categoryService.getById(133L)));
        assertNull(shopCategoryService.findByShopCategory(
                shopService.getById(777L),
                categoryService.getById(133L)));
    }

    @Test
    public void testDeleteAll() {
        shopCategoryService.deleteAll(categoryService.getById(133L));
        assertNull(shopCategoryService.findByShopCategory(
                shopService.getById(10L),
                categoryService.getById(133L)));
    }
}
