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

package org.yes.cart.service.image.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.image.ImageNameStrategy;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryImageNameStrategyImplTest extends BaseCoreDBTestCase {

    private CategoryService categoryService;
    private ImageNameStrategy imageNameStrategy;
    private CacheManager cacheManager;

    @Before
    public void setUp() {
        imageNameStrategy = (ImageNameStrategy) ctx().getBean(ServiceSpringKeys.CATEGORY_IMAGE_NAME_STRATEGY);
        categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        cacheManager = (CacheManager) ctx().getBean("cacheManager");
        super.setUp();
    }


    @Test
    public void testGetCodeFromActualObject() {

        //test case to support file names without code with non-seo category
        assertEquals("313", imageNameStrategy.resolveObjectCode("category.jpeg"));
        assertEquals("313", imageNameStrategy.resolveObjectCode("imgvault/category/category.jpeg"));
        assertEquals("313", imageNameStrategy.resolveObjectCode("imgvault/category/category.jpeg?w=10&h=4"));

        final Category cat313 = categoryService.findById(313L);
        cat313.getSeo().setUri("CC-TEST-products");
        categoryService.update(cat313);

        cacheManager.getCache("imageNameStrategy-resolveObjectCode").clear();

        //test case to support file names without code with seo category
        assertEquals("CC-TEST-products", imageNameStrategy.resolveObjectCode("category.jpeg"));
        assertEquals("CC-TEST-products", imageNameStrategy.resolveObjectCode("imgvault/category/category.jpeg"));
        assertEquals("CC-TEST-products", imageNameStrategy.resolveObjectCode("imgvault/category/category.jpeg?w=10&h=4"));

        // test that inexistent are resolved to no image
        assertEquals(Constants.NO_IMAGE, imageNameStrategy.resolveObjectCode("imgvault/category/unknown-category.jpeg"));

    }

}
