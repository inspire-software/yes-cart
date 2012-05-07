package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ProductCategoryService;

import static org.junit.Assert.assertEquals;

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





}
