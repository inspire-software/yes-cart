package org.yes.cart.domain.entity.impl;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * User: igora Igor Azarny
 * Date: 6/6/13
 * Time: 3:26 PM
 */
public class ProductEntityTest {

    @Test
    public void testGetLocale() {
        ProductEntity pe = new ProductEntity();
        assertEquals("en", pe.getLocale("PRODUCT_DESCRIPTION_en"));
    }

}
