package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImplTest extends BaseCoreDBTestCase {

    @Test
    public void testGetAllProductSkus() {
        ProductSkuService productSkuService = (ProductSkuService) ctx.getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        Collection<ProductSku> skus = productSkuService.getAllProductSkus(10000L); //SOBOT
        assertNotNull(skus);
        assertEquals(4, skus.size());
        productSkuService = null;
    }
}
