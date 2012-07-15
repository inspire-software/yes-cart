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
        ProductSkuService productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        Collection<ProductSku> skus = productSkuService.getAllProductSkus(10000L); //SOBOT
        assertNotNull(skus);
        assertEquals(4, skus.size());
    }
}
