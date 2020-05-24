/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.shoppingcart.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.InventoryResolver;
import org.yes.cart.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 18/08/2019
 * Time: 11:50
 */
public class ProductAvailabilityStrategyBackorderInStockOnlyImplTest {

    private final Mockery context = new JUnit4Mockery();

    private InventoryResolver inventoryResolver;
    private WarehouseService warehouseService;

    private Product product;
    private ProductSku sku;
    private SkuWarehouse skuWarehouse;

    private ProductSearchResultDTO productSearch;
    private ProductSkuSearchResultDTO skuSearch;

    private long shopId = 10L;
    private String skuCode = "SKU";
    private String supplier = "Main";



    @Test
    public void testGetAvailabilityModelForBackorderOne() throws Exception {

        final int availability = SkuWarehouse.AVAILABILITY_BACKORDER;
        final BigDecimal qty = BigDecimal.ONE;

        setTestExpectations(availability, null, qty);

        final ProductAvailabilityModel modelProduct = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, product, supplier);

        assertNotNull(modelProduct);
        assertEquals(skuCode, modelProduct.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProduct.getAvailability());
        assertTrue(modelProduct.isAvailable());
        assertTrue(modelProduct.isInStock());
        assertFalse(modelProduct.isPerpetual());
        assertNull(modelProduct.getReleaseDate());
        assertTrue(modelProduct.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSku = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, sku, supplier);

        assertNotNull(modelSku);
        assertEquals(skuCode, modelSku.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSku.getAvailability());
        assertTrue(modelSku.isAvailable());
        assertTrue(modelSku.isInStock());
        assertFalse(modelSku.isPerpetual());
        assertNull(modelSku.getReleaseDate());
        assertTrue(modelSku.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelProductSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, productSearch);

        assertNotNull(modelProductSearch);
        assertEquals(skuCode, modelProductSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProductSearch.getAvailability());
        assertTrue(modelProductSearch.isAvailable());
        assertTrue(modelProductSearch.isInStock());
        assertFalse(modelProductSearch.isPerpetual());
        assertNull(modelProductSearch.getReleaseDate());
        assertTrue(modelProductSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSkuSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, skuSearch);

        assertNotNull(modelSkuSearch);
        assertEquals(skuCode, modelSkuSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSkuSearch.getAvailability());
        assertTrue(modelSkuSearch.isAvailable());
        assertTrue(modelSkuSearch.isInStock());
        assertFalse(modelSkuSearch.isPerpetual());
        assertNull(modelSkuSearch.getReleaseDate());
        assertTrue(modelSkuSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAvailabilityModelForBackorderOnePreorder() throws Exception {

        final int availability = SkuWarehouse.AVAILABILITY_BACKORDER;
        final BigDecimal qty = BigDecimal.ONE;

        setTestExpectations(availability, DateUtils.ldtParseSDT("2099-01-01"), qty);

        final ProductAvailabilityModel modelProduct = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, product, supplier);

        assertNotNull(modelProduct);
        assertEquals(skuCode, modelProduct.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProduct.getAvailability());
        assertTrue(modelProduct.isAvailable());
        assertTrue(modelProduct.isInStock());
        assertFalse(modelProduct.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelProduct.getReleaseDate());
        assertTrue(modelProduct.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSku = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, sku, supplier);

        assertNotNull(modelSku);
        assertEquals(skuCode, modelSku.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSku.getAvailability());
        assertTrue(modelSku.isAvailable());
        assertTrue(modelSku.isInStock());
        assertFalse(modelSku.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelSku.getReleaseDate());
        assertTrue(modelSku.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelProductSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, productSearch);

        assertNotNull(modelProductSearch);
        assertEquals(skuCode, modelProductSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProductSearch.getAvailability());
        assertTrue(modelProductSearch.isAvailable());
        assertTrue(modelProductSearch.isInStock());
        assertFalse(modelProductSearch.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelProductSearch.getReleaseDate());
        assertTrue(modelProductSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSkuSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, skuSearch);

        assertNotNull(modelSkuSearch);
        assertEquals(skuCode, modelSkuSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSkuSearch.getAvailability());
        assertTrue(modelSkuSearch.isAvailable());
        assertTrue(modelSkuSearch.isInStock());
        assertFalse(modelSkuSearch.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelSkuSearch.getReleaseDate());
        assertTrue(modelSkuSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAvailabilityModelForBackorderZero() throws Exception {

        final int availability = SkuWarehouse.AVAILABILITY_BACKORDER;
        final BigDecimal qty = BigDecimal.ZERO;

        setTestExpectations(availability, null, qty);

        final ProductAvailabilityModel modelProduct = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, product, supplier);

        assertNotNull(modelProduct);
        assertEquals(skuCode, modelProduct.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProduct.getAvailability());
        assertFalse(modelProduct.isAvailable());
        assertFalse(modelProduct.isInStock());
        assertFalse(modelProduct.isPerpetual());
        assertNull(modelProduct.getReleaseDate());
        assertTrue(modelProduct.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSku = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, sku, supplier);

        assertNotNull(modelSku);
        assertEquals(skuCode, modelSku.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSku.getAvailability());
        assertFalse(modelSku.isAvailable());
        assertFalse(modelSku.isInStock());
        assertFalse(modelSku.isPerpetual());
        assertNull(modelSku.getReleaseDate());
        assertTrue(modelSku.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelProductSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, productSearch);

        assertNotNull(modelProductSearch);
        assertEquals(skuCode, modelProductSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProductSearch.getAvailability());
        assertFalse(modelProductSearch.isAvailable());
        assertFalse(modelProductSearch.isInStock());
        assertFalse(modelProductSearch.isPerpetual());
        assertNull(modelProductSearch.getReleaseDate());
        assertTrue(modelProductSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSkuSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, skuSearch);

        assertNotNull(modelSkuSearch);
        assertEquals(skuCode, modelSkuSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSkuSearch.getAvailability());
        assertFalse(modelSkuSearch.isAvailable());
        assertFalse(modelSkuSearch.isInStock());
        assertFalse(modelSkuSearch.isPerpetual());
        assertNull(modelSkuSearch.getReleaseDate());
        assertTrue(modelSkuSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAvailabilityModelForBackorderZeroPreorder() throws Exception {

        final int availability = SkuWarehouse.AVAILABILITY_BACKORDER;
        final BigDecimal qty = BigDecimal.ZERO;

        setTestExpectations(availability, DateUtils.ldtParseSDT("2099-01-01"), qty);

        final ProductAvailabilityModel modelProduct = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, product, supplier);

        assertNotNull(modelProduct);
        assertEquals(skuCode, modelProduct.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProduct.getAvailability());
        assertFalse(modelProduct.isAvailable());
        assertFalse(modelProduct.isInStock());
        assertFalse(modelProduct.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelProduct.getReleaseDate());
        assertTrue(modelProduct.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSku = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, sku, supplier);

        assertNotNull(modelSku);
        assertEquals(skuCode, modelSku.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSku.getAvailability());
        assertFalse(modelSku.isAvailable());
        assertFalse(modelSku.isInStock());
        assertFalse(modelSku.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelSku.getReleaseDate());
        assertTrue(modelSku.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelProductSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, productSearch);

        assertNotNull(modelProductSearch);
        assertEquals(skuCode, modelProductSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelProductSearch.getAvailability());
        assertFalse(modelProductSearch.isAvailable());
        assertFalse(modelProductSearch.isInStock());
        assertFalse(modelProductSearch.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelProductSearch.getReleaseDate());
        assertTrue(modelProductSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        final ProductAvailabilityModel modelSkuSearch = new ProductAvailabilityStrategyBackorderInStockOnlyImpl(warehouseService, inventoryResolver).getAvailabilityModel(shopId, skuSearch);

        assertNotNull(modelSkuSearch);
        assertEquals(skuCode, modelSkuSearch.getDefaultSkuCode());
        assertEquals(SkuWarehouse.AVAILABILITY_BACKORDER, modelSkuSearch.getAvailability());
        assertFalse(modelSkuSearch.isAvailable());
        assertFalse(modelSkuSearch.isInStock());
        assertFalse(modelSkuSearch.isPerpetual());
        assertEquals(DateUtils.ldtParseSDT("2099-01-01"), modelSkuSearch.getReleaseDate());
        assertTrue(modelSkuSearch.getAvailableToSellQuantity(skuCode).compareTo(qty) == 0);

        context.assertIsSatisfied();

    }


    private void setTestExpectations(final int availability, final LocalDateTime releaseDate, final BigDecimal qty) {

        inventoryResolver = context.mock(InventoryResolver.class, "inventoryResolver");
        warehouseService = context.mock(WarehouseService.class, "warehouseService");

        final Warehouse warehouse = context.mock(Warehouse.class, "warehouse");
        final Map<String, Warehouse> warehouses = Collections.singletonMap(supplier, warehouse);

        sku = context.mock(ProductSku.class, "sku");
        product = context.mock(Product.class, "product");
        skuWarehouse = context.mock(SkuWarehouse.class, "inventory");
        productSearch = context.mock(ProductSearchResultDTO.class, "productSearch");
        skuSearch = context.mock(ProductSkuSearchResultDTO.class, "skuSearch");

        context.checking(new Expectations() {{
            // Domain
            allowing(warehouseService).getByShopIdMapped(shopId, false); will(returnValue(warehouses));
            allowing(warehouse).getCode(); will(returnValue(supplier));
            allowing(sku).getProduct(); will(returnValue(product));
            allowing(sku).getCode(); will(returnValue(skuCode));
            allowing(product).getProductId(); will(returnValue(123L));
            allowing(product).getDefaultSku(); will(returnValue(sku));
            allowing(product).getSku(); will(returnValue(Collections.singleton(sku)));
            allowing(sku).getCode(); will(returnValue(skuCode));
            allowing(sku).getRank(); will(returnValue(0));
            allowing(inventoryResolver).findByWarehouseSku(warehouse, skuCode); will(returnValue(skuWarehouse));
            allowing(skuWarehouse).isDisabled(); will(returnValue(false));
            allowing(skuWarehouse).getAvailablefrom(); will(returnValue(null));
            allowing(skuWarehouse).getAvailableto(); will(returnValue(null));
            allowing(skuWarehouse).getReleaseDate(); will(returnValue(releaseDate));
            allowing(skuWarehouse).getAvailability(); will(returnValue(availability));
            allowing(skuWarehouse).getSkuCode(); will(returnValue(skuCode));
            allowing(skuWarehouse).getAvailableToSell(); will(returnValue(qty));
            allowing(skuWarehouse).getRestockDate(); will(returnValue(null));
            allowing(skuWarehouse).getRestockNote(); will(returnValue(null));
            // Search
            allowing(productSearch).getFulfilmentCentreCode(); will(returnValue(supplier));
            allowing(productSearch).getQtyOnWarehouse(shopId); will(returnValue(Collections.singletonMap(skuCode, qty)));
            allowing(productSearch).getAvailability(); will(returnValue(availability));
            allowing(productSearch).getAvailablefrom(); will(returnValue(null));
            allowing(productSearch).getAvailableto(); will(returnValue(null));
            allowing(productSearch).getReleaseDate(); will(returnValue(releaseDate));
            allowing(productSearch).getRestockDate(); will(returnValue(null));
            allowing(productSearch).getRestockNotes(); will(returnValue(null));
            allowing(productSearch).getDefaultSkuCode(); will(returnValue(skuCode));
            allowing(skuSearch).getFulfilmentCentreCode(); will(returnValue(supplier));
            allowing(skuSearch).getQtyOnWarehouse(shopId); will(returnValue(qty));
            allowing(skuSearch).getAvailability(); will(returnValue(availability));
            allowing(skuSearch).getAvailablefrom(); will(returnValue(null));
            allowing(skuSearch).getAvailableto(); will(returnValue(null));
            allowing(skuSearch).getReleaseDate(); will(returnValue(releaseDate));
            allowing(skuSearch).getCode(); will(returnValue(skuCode));
            allowing(skuSearch).getRestockDate(); will(returnValue(null));
            allowing(skuSearch).getRestockNotes(); will(returnValue(null));
        }});

    }

}
