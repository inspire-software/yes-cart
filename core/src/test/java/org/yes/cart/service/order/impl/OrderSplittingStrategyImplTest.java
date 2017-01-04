/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.order.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.SkuUnavailableException;
import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 18/02/2016
 * Time: 18:01
 */
public class OrderSplittingStrategyImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetPhysicalDeliveriesQty() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> groups = new HashMap<DeliveryBucket, List<CartItem>>();

        assertEquals(0, groups.size());
        assertTrue(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).isEmpty());

        // 1st supplier
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"), Collections.<CartItem>emptyList());
        assertEquals(1, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(0, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S001"), Collections.<CartItem>emptyList());
        assertEquals(2, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(1, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>emptyList());
        assertEquals(3, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(2, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>emptyList());
        assertEquals(4, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(3, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S001"), Collections.<CartItem>emptyList());
        assertEquals(5, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(4, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());

        // 2nd supplier
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S002"), Collections.<CartItem>emptyList());
        assertEquals(6, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(4, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());
        assertEquals(0, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S002"), Collections.<CartItem>emptyList());
        assertEquals(7, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(4, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());
        assertEquals(1, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S002"), Collections.<CartItem>emptyList());
        assertEquals(8, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(4, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());
        assertEquals(2, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S002"), Collections.<CartItem>emptyList());
        assertEquals(9, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(4, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());
        assertEquals(3, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002").intValue());

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S002"), Collections.<CartItem>emptyList());
        assertEquals(10, groups.size());
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001"));
        assertNotNull(new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002"));
        assertEquals(4, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S001").intValue());
        assertEquals(4, new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(groups).get("S002").intValue());


    }


    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryOnlyElectronic() throws Exception {

        final CartItem d4 = context.mock(CartItem.class, "d4");

        final Map<DeliveryBucket, List<CartItem>> groups = new TreeMap<DeliveryBucket, List<CartItem>>();

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d4));

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(groups);

        assertEquals(1, groups.size());

        final List<CartItem> electronic = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"));

        assertNotNull(electronic);
        assertEquals(1, electronic.size());
        assertSame(d4, electronic.get(0));

    }


    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryOnlyElectronicTwoSuppliers() throws Exception {

        final CartItem d4_1 = context.mock(CartItem.class, "d4_1");
        final CartItem d4_2 = context.mock(CartItem.class, "d4_2");

        final Map<DeliveryBucket, List<CartItem>> groups = new TreeMap<DeliveryBucket, List<CartItem>>();

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d4_1));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S002"), Collections.<CartItem>singletonList(d4_2));

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(groups);

        assertEquals(2, groups.size());

        final List<CartItem> electronic_1 = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"));

        assertNotNull(electronic_1);
        assertEquals(1, electronic_1.size());
        assertSame(d4_1, electronic_1.get(0));

        final List<CartItem> electronic_2 = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S002"));

        assertNotNull(electronic_2);
        assertEquals(1, electronic_2.size());
        assertSame(d4_2, electronic_2.get(0));

    }


    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryWithElectronic() throws Exception {

        final CartItem d1 = context.mock(CartItem.class, "d1");
        final CartItem d2 = context.mock(CartItem.class, "d2");
        final CartItem d3 = context.mock(CartItem.class, "d3");
        final CartItem d4 = context.mock(CartItem.class, "d4");

        final Map<DeliveryBucket, List<CartItem>> groups = new TreeMap<DeliveryBucket, List<CartItem>>();

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d1));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d2));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d3));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d4));

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(groups);

        assertEquals(2, groups.size());

        final List<CartItem> electronic = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"));
        final List<CartItem> mixed = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S001"));

        assertNotNull(electronic);
        assertEquals(1, electronic.size());
        assertSame(d4, electronic.get(0));

        assertNotNull(mixed);
        assertEquals(3, mixed.size());
        assertSame(d1, mixed.get(0));
        assertSame(d2, mixed.get(1));
        assertSame(d3, mixed.get(2));

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryWithElectronicTwoSuppliers() throws Exception {

        final CartItem d1 = context.mock(CartItem.class, "d1");
        final CartItem d2 = context.mock(CartItem.class, "d2");
        final CartItem d3 = context.mock(CartItem.class, "d3");
        final CartItem d4 = context.mock(CartItem.class, "d4");

        final Map<DeliveryBucket, List<CartItem>> groups = new TreeMap<DeliveryBucket, List<CartItem>>();

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S002"), Collections.<CartItem>singletonList(d1));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d2));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d3));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d4));

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(groups);

        assertEquals(3, groups.size());

        final List<CartItem> electronic = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "S001"));
        final List<CartItem> mixed = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S001"));
        final List<CartItem> standard2 = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S002"));

        assertNotNull(electronic);
        assertEquals(1, electronic.size());
        assertSame(d4, electronic.get(0));

        assertNotNull(mixed);
        assertEquals(2, mixed.size());
        assertSame(d2, mixed.get(0));
        assertSame(d3, mixed.get(1));

        assertNotNull(standard2);
        assertEquals(1, standard2.size());
        assertSame(d1, standard2.get(0));

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessary() throws Exception {

        final CartItem d1 = context.mock(CartItem.class, "d1");
        final CartItem d2 = context.mock(CartItem.class, "d2");
        final CartItem d3 = context.mock(CartItem.class, "d3");

        final Map<DeliveryBucket, List<CartItem>> groups = new TreeMap<DeliveryBucket, List<CartItem>>();

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d1));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d2));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d3));

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(groups);

        assertEquals(1, groups.size());

        final List<CartItem> mixed = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S001"));

        assertNotNull(mixed);
        assertEquals(3, mixed.size());
        assertSame(d1, mixed.get(0));
        assertSame(d2, mixed.get(1));
        assertSame(d3, mixed.get(2));

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryMultipleSuppliers() throws Exception {

        final CartItem d1_1 = context.mock(CartItem.class, "d1_1");
        final CartItem d2_1 = context.mock(CartItem.class, "d2_1");
        final CartItem d3_1 = context.mock(CartItem.class, "d3_1");
        final CartItem d1_2 = context.mock(CartItem.class, "d1_2");
        final CartItem d2_2 = context.mock(CartItem.class, "d2_2");
        final CartItem d3_2 = context.mock(CartItem.class, "d3_2");
        final CartItem d1_3 = context.mock(CartItem.class, "d1_3");
        final CartItem d2_3 = context.mock(CartItem.class, "d2_3");
        final CartItem d3_3 = context.mock(CartItem.class, "d3_3");

        final Map<DeliveryBucket, List<CartItem>> groups = new TreeMap<DeliveryBucket, List<CartItem>>();

        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d1_1));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d2_1));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S001"), Collections.<CartItem>singletonList(d3_1));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S002"), Collections.<CartItem>singletonList(d1_2));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S002"), Collections.<CartItem>singletonList(d2_2));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S002"), Collections.<CartItem>singletonList(d3_2));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "S003"), Collections.<CartItem>singletonList(d1_3));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "S003"), Collections.<CartItem>singletonList(d2_3));
        groups.put(new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "S003"), Collections.<CartItem>singletonList(d3_3));

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(groups);

        assertEquals(3, groups.size());

        final List<CartItem> mixed_1 = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S001"));

        assertNotNull(mixed_1);
        assertEquals(3, mixed_1.size());
        assertSame(d1_1, mixed_1.get(0));
        assertSame(d2_1, mixed_1.get(1));
        assertSame(d3_1, mixed_1.get(2));

        final List<CartItem> mixed_2 = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S002"));

        assertNotNull(mixed_2);
        assertEquals(3, mixed_2.size());
        assertSame(d1_2, mixed_2.get(0));
        assertSame(d2_2, mixed_2.get(1));
        assertSame(d3_2, mixed_2.get(2));

        final List<CartItem> mixed_3 = groups.get(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "S003"));

        assertNotNull(mixed_3);
        assertEquals(3, mixed_3.size());
        assertSame(d1_3, mixed_3.get(0));
        assertSame(d2_3, mixed_3.get(1));
        assertSame(d3_3, mixed_3.get(2));

    }


    private void testGetDeliveryGroup(final int availability,
                                      final boolean digital,
                                      final Date availableFrom,
                                      final Date availableTo,
                                      final Date now,
                                      final BigDecimal stock,
                                      final BigDecimal required,
                                      final boolean expectedException,
                                      final String expectedGroup) throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService) {
            @Override
            Date now() {
                return now;
            }
        };

        final Warehouse warehouse = context.mock(Warehouse.class, "warehouse");
        final SkuWarehouse skuWarehouse = context.mock(SkuWarehouse.class, "skuWarehouse");

        final String skuCode = "001";
        final String supplier = "Main";
        final BigDecimal qty = required;
        final List<Warehouse> warehouses = Collections.singletonList(warehouse);

        final ProductSku sku = context.mock(ProductSku.class, "sku");
        final Product product = context.mock(Product.class, "product");
        final ProductType productType = context.mock(ProductType.class, "productType");

        final CartItem item = context.mock(CartItem.class, "item");

        context.checking(new Expectations() {{
            allowing(warehouse).getCode(); will(returnValue(supplier));
            allowing(productService).getProductSkuByCode(skuCode);
            if (availability == 0) {
                will(returnValue(null));
            } else {
                will(returnValue(sku));
            }
            allowing(sku).getCode(); will(returnValue(skuCode));
            allowing(sku).getProduct(); will(returnValue(product));
            allowing(product).getAvailability(); will(returnValue(availability));
            allowing(product).getAvailablefrom(); will(returnValue(availableFrom));
            allowing(product).getAvailableto(); will(returnValue(availableTo));
            allowing(product).getProducttype(); will(returnValue(productType));
            allowing(productType).isDigital(); will(returnValue(digital));
            allowing(item).getProductSkuCode(); will(returnValue(skuCode));
            allowing(item).getProductName(); will(returnValue(skuCode));
            allowing(item).getSupplierCode(); will(returnValue(supplier));
            allowing(item).getQty(); will(returnValue(qty));

            if (stock != null) {
                allowing(skuWarehouseService).findByWarehouseSku(warehouse, skuCode); will(returnValue(skuWarehouse));
                allowing(skuWarehouse).isAvailableToSell(qty); will(returnValue(stock.compareTo(qty) >= 0));
            }
        }});

        try {
            assertEquals(new Pair<String, String>(expectedGroup, supplier), strategy.getDeliveryGroup(item, warehouses));
            assertFalse("Expecting unavailable sku", expectedException);
        } catch (SkuUnavailableException sue) {
            assertTrue("Not expecting unavailable sku", expectedException);
        }

        context.assertIsSatisfied();
    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProduct() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                false, CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProductAvailable() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                false, CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProduct() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProductAvailable() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                true, null /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* AVailable */
                null, BigDecimal.TEN, /* No stock, need 10 */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductAvailableNotEnough() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                true, CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP /* Expected */
        );

    }




    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductAvailableNotEnough() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                false, CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityStandardProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductAvailableNotEnough() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                true, null /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }



    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* AVailable */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                true, null /* Expected */
        );

    }



    @Test
    public void testGetDeliveryGroupUnknownSku() throws Exception {

        testGetDeliveryGroup(
                0, false,
                null, null, null,
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                false, CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );


    }

    @Test
    public void testGetDeliveryGroupUnknownSkuNotEnough() throws Exception {

        testGetDeliveryGroup(
                0, false,
                null, null, null,
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                true, null /* Expected */
        );


    }


}