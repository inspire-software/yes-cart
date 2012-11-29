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

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ProductCategoryService;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductCategoryServiceImplTest extends BaseCoreDBTestCase {

    @Test
    public void testGetProductById() {
        ProductCategoryService productCategoryService =
                (ProductCategoryService) ctx().getBean(ServiceSpringKeys.PRODUCT_CATEGORY_SERVICE);
        int rez = productCategoryService.getNextRank(211L);
        assertEquals("Next rank must be 450 for 211 category", 450, rez);
        rez = productCategoryService.getNextRank(-777L);
        assertEquals("Next rank must be 50 for unexisting -777 category", 50, rez);
        rez = productCategoryService.getNextRank(116L);
        assertEquals("Next rank must be 50 for existing -116 category without products", 50, rez);
    }
             //<TPRODUCTCATEGORY PRODUCTCATEGORY_ID="10000" GUID="10000"  PRODUCT_ID="10000" CATEGORY_ID="101" RANK="999"/>

    @Test
    public void testFindByCategoryIdProductId() {

        ProductCategoryService productCategoryService =
                (ProductCategoryService) ctx().getBean(ServiceSpringKeys.PRODUCT_CATEGORY_SERVICE);
        assertNotNull(productCategoryService.findByCategoryIdProductId(101, 10000));
        assertNull(productCategoryService.findByCategoryIdProductId(101, 11000));

    }





}
