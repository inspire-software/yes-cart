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
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImplTest extends BaseCoreDBTestCase {

    public static final long SOBOT_PK = 10000L;
    public static final long REMOVE_ME_PK = 15300L;

    @Test
    public void testGetAllProductSkus() {
        ProductSkuService productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        Collection<ProductSku> skus = productSkuService.getAllProductSkus(SOBOT_PK);
        assertNotNull(skus);
        assertEquals(4, skus.size());
    }

    @Test
    public void testRemoveAllPrices() {

        ProductSkuService productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        Collection<ProductSku> skus = productSkuService.getAllProductSkus(REMOVE_ME_PK);
        assertEquals(2, skus.size());
        final Set<String> skuCodes = new HashSet<String>();
        for (final ProductSku sku : skus) {
            skuCodes.add(sku.getCode());
        }

        List<Pair<String, SkuPrice>> prices = productSkuService.getAllPrices(REMOVE_ME_PK);

        assertEquals(4, prices.size()); // each sku has two prices in USD and EUR
        for (Pair<String, SkuPrice> sku : prices) {
            assertTrue(skuCodes.contains(sku.getFirst()));
            assertNotNull(sku.getSecond());
            assertNotNull(sku.getSecond().getRegularPrice());
        }

        productSkuService.removeAllPrices(REMOVE_ME_PK);

        skus = productSkuService.getAllProductSkus(REMOVE_ME_PK);
        assertEquals(2, skus.size());

        prices = productSkuService.getAllPrices(REMOVE_ME_PK);
        assertTrue("Prices must be removed for sku", prices.isEmpty());

    }

    @Test
    public void testRemoveAllInventory() {

        ProductSkuService productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        Collection<ProductSku> skus = productSkuService.getAllProductSkus(REMOVE_ME_PK);
        assertEquals(2, skus.size());
        final Set<String> skuCodes = new HashSet<String>();
        for (final ProductSku sku : skus) {
            skuCodes.add(sku.getCode());
        }

        List<Pair<String, SkuWarehouse>> inventory = productSkuService.getAllInventory(REMOVE_ME_PK);

        assertEquals(2, inventory.size()); // each sku has one inventory record
        for (Pair<String, SkuWarehouse> sku : inventory) {
            assertTrue(skuCodes.contains(sku.getFirst()));
            assertNotNull(sku.getSecond());
            assertNotNull(sku.getSecond().getQuantity());
        }

        productSkuService.removeAllInventory(REMOVE_ME_PK);

        skus = productSkuService.getAllProductSkus(REMOVE_ME_PK);
        assertEquals(2, skus.size());

        inventory = productSkuService.getAllInventory(REMOVE_ME_PK);
        assertTrue("All items must be removed for sku", inventory.isEmpty());

    }
}
