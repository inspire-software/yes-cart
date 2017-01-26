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
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.OrderInfo;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingContext;

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

        assertEquals(new Pair<String, String>(expectedGroup, supplier), strategy.getDeliveryGroup(item, warehouses));

        context.assertIsSatisfied();
    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProduct() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProductAvailable() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysDigitalProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, true, /* Digital */
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProduct() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProductAvailable() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityAlwaysProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_ALWAYS, false,
                availableFrom, availableTo, now, /* Available */
                null, BigDecimal.TEN, /* No stock, need 10 */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductAvailableNotEnough() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityPreorderProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_PREORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP /* Expected */
        );

    }




    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductAvailableNotEnough() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityBackorderProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_BACKORDER, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityStandardProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductAvailableNotEnough() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityStandardProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_STANDARD, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProduct() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }


    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProductAvailable() throws Exception {

        final Date availableFrom = null;
        final Date availableTo = null;
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2016-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }



    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProductNotAvailableAfter() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }

    @Test
    public void testGetDeliveryGroupAvailabilityShowroomProductNotAvailableBefore() throws Exception {

        final Date availableFrom = new SimpleDateFormat("yyyy-MM-dd").parse("2016-01-01");
        final Date availableTo = new SimpleDateFormat("yyyy-MM-dd").parse("2016-03-01");
        final Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-18");

        testGetDeliveryGroup(
                Product.AVAILABILITY_SHOWROOM, false,
                availableFrom, availableTo, now, /* Available */
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP /* Expected */
        );

    }



    @Test
    public void testGetDeliveryGroupUnknownSku() throws Exception {

        testGetDeliveryGroup(
                0, false,
                null, null, null,
                BigDecimal.TEN, BigDecimal.TEN, /* Enough in stock */
                CustomerOrderDelivery.STANDARD_DELIVERY_GROUP /* Expected */
        );


    }

    @Test
    public void testGetDeliveryGroupUnknownSkuNotEnough() throws Exception {

        testGetDeliveryGroup(
                0, false,
                null, null, null,
                BigDecimal.ONE, BigDecimal.TEN, /* Not enough in stock */
                CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP /* Expected */
        );


    }


    @Test
    public void testGetPhysicalDeliveriesQtyStandard() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d1 = context.mock(DeliveryBucket.class, "d1");

        buckets.put(d1, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d1).getSupplier(); will(returnValue("s1"));
            allowing(d1).getGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(Integer.valueOf(1), counts.get("s1"));

    }

    @Test
    public void testGetPhysicalDeliveriesQtyPreorder() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d2 = context.mock(DeliveryBucket.class, "d2");

        buckets.put(d2, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d2).getSupplier(); will(returnValue("s1"));
            allowing(d2).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(Integer.valueOf(1), counts.get("s1"));

    }

    @Test
    public void testGetPhysicalDeliveriesQtyBackorder() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d3 = context.mock(DeliveryBucket.class, "d3");

        buckets.put(d3, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d3).getSupplier(); will(returnValue("s1"));
            allowing(d3).getGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(Integer.valueOf(1), counts.get("s1"));

    }

    @Test
    public void testGetPhysicalDeliveriesQtyElectronic() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d4 = context.mock(DeliveryBucket.class, "d4");

        buckets.put(d4, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d4).getSupplier(); will(returnValue("s1"));
            allowing(d4).getGroup(); will(returnValue(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(Integer.valueOf(0), counts.get("s1"));

    }

    @Test
    public void testGetPhysicalDeliveriesQtyNoStock() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket oos = context.mock(DeliveryBucket.class, "oos");

        buckets.put(oos, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(oos).getSupplier(); will(returnValue("s1"));
            allowing(oos).getGroup(); will(returnValue(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(Integer.valueOf(0), counts.get("s1"));

    }

    @Test
    public void testGetPhysicalDeliveriesQtyOffline() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket off = context.mock(DeliveryBucket.class, "off");

        buckets.put(off, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(off).getSupplier(); will(returnValue("s1"));
            allowing(off).getGroup(); will(returnValue(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(Integer.valueOf(0), counts.get("s1"));

    }

    @Test
    public void testGetPhysicalDeliveriesQtyMix() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d1 = context.mock(DeliveryBucket.class, "d1");
        final DeliveryBucket d2 = context.mock(DeliveryBucket.class, "d2");
        final DeliveryBucket d3 = context.mock(DeliveryBucket.class, "d3");
        final DeliveryBucket d4 = context.mock(DeliveryBucket.class, "d4");
        final DeliveryBucket oos = context.mock(DeliveryBucket.class, "oos");
        final DeliveryBucket off = context.mock(DeliveryBucket.class, "off");

        buckets.put(d1, Collections.<CartItem>emptyList());
        buckets.put(d2, Collections.<CartItem>emptyList());
        buckets.put(d3, Collections.<CartItem>emptyList());
        buckets.put(d4, Collections.<CartItem>emptyList());
        buckets.put(oos, Collections.<CartItem>emptyList());
        buckets.put(off, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d1).getSupplier();
            will(returnValue("s1"));
            allowing(d1).getGroup();
            will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(d2).getSupplier();
            will(returnValue("s1"));
            allowing(d2).getGroup();
            will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(d3).getSupplier();
            will(returnValue("s1"));
            allowing(d3).getGroup();
            will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(d4).getSupplier();
            will(returnValue("s1"));
            allowing(d4).getGroup();
            will(returnValue(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP));
            allowing(oos).getSupplier();
            will(returnValue("s1"));
            allowing(oos).getGroup();
            will(returnValue(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP));
            allowing(off).getSupplier();
            will(returnValue("s1"));
            allowing(off).getGroup();
            will(returnValue(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(Integer.valueOf(3), counts.get("s1"));

    }

    @Test
    public void testGetPhysicalDeliveriesQtyStandardMultipleSuppliers() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d1 = context.mock(DeliveryBucket.class, "d1");
        final DeliveryBucket d2 = context.mock(DeliveryBucket.class, "d2");
        final DeliveryBucket d3 = context.mock(DeliveryBucket.class, "d3");
        final DeliveryBucket d4 = context.mock(DeliveryBucket.class, "d4");
        final DeliveryBucket oos = context.mock(DeliveryBucket.class, "oos");
        final DeliveryBucket off = context.mock(DeliveryBucket.class, "off");

        buckets.put(d1, Collections.<CartItem>emptyList());
        buckets.put(d2, Collections.<CartItem>emptyList());
        buckets.put(d3, Collections.<CartItem>emptyList());
        buckets.put(d4, Collections.<CartItem>emptyList());
        buckets.put(oos, Collections.<CartItem>emptyList());
        buckets.put(off, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d1).getSupplier();
            will(returnValue("s2"));
            allowing(d1).getGroup();
            will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(d2).getSupplier();
            will(returnValue("s1"));
            allowing(d2).getGroup();
            will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(d3).getSupplier();
            will(returnValue("s2"));
            allowing(d3).getGroup();
            will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(d4).getSupplier();
            will(returnValue("s1"));
            allowing(d4).getGroup();
            will(returnValue(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP));
            allowing(oos).getSupplier();
            will(returnValue("s3"));
            allowing(oos).getGroup();
            will(returnValue(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP));
            allowing(off).getSupplier();
            will(returnValue("s1"));
            allowing(off).getGroup();
            will(returnValue(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP));
        }});

        final Map<String, Integer> counts = new OrderSplittingStrategyImpl(null, null, null, null).getPhysicalDeliveriesQty(buckets);

        assertNotNull(counts);
        assertEquals(3, counts.size());
        assertEquals(Integer.valueOf(1), counts.get("s1"));
        assertEquals(Integer.valueOf(2), counts.get("s2"));
        assertEquals(Integer.valueOf(0), counts.get("s3"));

    }




    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryStandard() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d1 = context.mock(DeliveryBucket.class, "d1");

        buckets.put(d1, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d1).getSupplier(); will(returnValue("s1"));
            allowing(d1).getGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(1, buckets.size());

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryPreorder() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d2 = context.mock(DeliveryBucket.class, "d2");

        buckets.put(d2, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d2).getSupplier(); will(returnValue("s1"));
            allowing(d2).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(1, buckets.size());

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryBackorder() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d3 = context.mock(DeliveryBucket.class, "d3");

        buckets.put(d3, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d3).getSupplier(); will(returnValue("s1"));
            allowing(d3).getGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(1, buckets.size());

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryElectronic() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d4 = context.mock(DeliveryBucket.class, "d4");

        buckets.put(d4, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d4).getSupplier(); will(returnValue("s1"));
            allowing(d4).getGroup(); will(returnValue(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(1, buckets.size());

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryNoStock() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket oos = context.mock(DeliveryBucket.class, "oos");

        buckets.put(oos, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(oos).getSupplier(); will(returnValue("s1"));
            allowing(oos).getGroup(); will(returnValue(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(1, buckets.size());

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryOffline() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket off = context.mock(DeliveryBucket.class, "off");

        buckets.put(off, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(off).getSupplier(); will(returnValue("s1"));
            allowing(off).getGroup(); will(returnValue(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(1, buckets.size());

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryMix() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d1 = context.mock(DeliveryBucket.class, "d1");
        final DeliveryBucket d2 = context.mock(DeliveryBucket.class, "d2");
        final DeliveryBucket d3 = context.mock(DeliveryBucket.class, "d3");
        final DeliveryBucket d4 = context.mock(DeliveryBucket.class, "d4");
        final DeliveryBucket oos = context.mock(DeliveryBucket.class, "oos");
        final DeliveryBucket off = context.mock(DeliveryBucket.class, "off");

        buckets.put(d1, Collections.<CartItem>emptyList());
        buckets.put(d2, Collections.<CartItem>emptyList());
        buckets.put(d3, Collections.<CartItem>emptyList());
        buckets.put(d4, Collections.<CartItem>emptyList());
        buckets.put(oos, Collections.<CartItem>emptyList());
        buckets.put(off, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d1).getSupplier(); will(returnValue("s1"));
            allowing(d1).getGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(d2).getSupplier(); will(returnValue("s1"));
            allowing(d2).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(d3).getSupplier(); will(returnValue("s1"));
            allowing(d3).getGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(d4).getSupplier(); will(returnValue("s1"));
            allowing(d4).getGroup(); will(returnValue(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP));
            allowing(oos).getSupplier(); will(returnValue("s1"));
            allowing(oos).getGroup(); will(returnValue(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP));
            allowing(off).getSupplier(); will(returnValue("s1"));
            allowing(off).getGroup(); will(returnValue(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(4, buckets.size());

        final Set<String> expected = new HashSet<String>(Arrays.asList(
                CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP,
                CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP,
                CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP,
                CustomerOrderDelivery.MIX_DELIVERY_GROUP
        ));

        for (final DeliveryBucket key : buckets.keySet()) {
            assertTrue(expected.contains(key.getGroup()));
        }

    }

    @Test
    public void testGroupDeliveriesIntoMixedIfNecessaryStandardMultipleSuppliers() throws Exception {

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();

        final DeliveryBucket d1 = context.mock(DeliveryBucket.class, "d1");
        final DeliveryBucket d2 = context.mock(DeliveryBucket.class, "d2");
        final DeliveryBucket d3 = context.mock(DeliveryBucket.class, "d3");
        final DeliveryBucket d4 = context.mock(DeliveryBucket.class, "d4");
        final DeliveryBucket oos = context.mock(DeliveryBucket.class, "oos");
        final DeliveryBucket off = context.mock(DeliveryBucket.class, "off");

        buckets.put(d1, Collections.<CartItem>emptyList());
        buckets.put(d2, Collections.<CartItem>emptyList());
        buckets.put(d3, Collections.<CartItem>emptyList());
        buckets.put(d4, Collections.<CartItem>emptyList());
        buckets.put(oos, Collections.<CartItem>emptyList());
        buckets.put(off, Collections.<CartItem>emptyList());

        context.checking(new Expectations() {{
            allowing(d1).getSupplier(); will(returnValue("s2"));
            allowing(d1).getGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(d2).getSupplier(); will(returnValue("s1"));
            allowing(d2).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(d3).getSupplier(); will(returnValue("s2"));
            allowing(d3).getGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(d4).getSupplier(); will(returnValue("s1"));
            allowing(d4).getGroup(); will(returnValue(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP));
            allowing(oos).getSupplier(); will(returnValue("s3"));
            allowing(oos).getGroup(); will(returnValue(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP));
            allowing(off).getSupplier(); will(returnValue("s1"));
            allowing(off).getGroup(); will(returnValue(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP));
        }});

        new OrderSplittingStrategyImpl(null, null, null, null).groupDeliveriesIntoMixedIfNecessary(buckets);

        assertNotNull(buckets);
        assertEquals(5, buckets.size());

        final Map<String, Set<String>> expected = new HashMap<String, Set<String>>() {{
            put("s1", new HashSet<String>(Arrays.asList(
                    CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP,
                    CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP,
                    CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP
            )));
            put("s2", new HashSet<String>(Arrays.asList(
                    CustomerOrderDelivery.MIX_DELIVERY_GROUP
            )));
            put("s3", new HashSet<String>(Arrays.asList(
                    CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP
            )));
        }};

        for (final DeliveryBucket key : buckets.keySet()) {
            assertTrue("Bucket not expected " + key, expected.get(key.getSupplier()).contains(key.getGroup()));
        }

    }


    @Test
    public void testDetermineDeliveryBucketExistingNoChangeNoGroup() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService);

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final DeliveryBucket itemBucket = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "itemBucket");

        final CartItem other = context.mock(CartItem.class, "other");

        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket();
            will(returnValue(itemBucket));
            allowing(item).getProductSkuCode();
            will(returnValue("ABC"));
            allowing(item).isGift();
            will(returnValue(false));
            allowing(other).getDeliveryBucket();
            will(returnValue(null));
            allowing(other).getProductSkuCode();
            will(returnValue("CED"));
            allowing(other).isGift();
            will(returnValue(false));

            allowing(cart).getOrderInfo();
            will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable();
            will(returnValue(true));
            allowing(info).isMultipleDelivery();
            will(returnValue(true));
            allowing(cart).getCartItemList();
            will(returnValue(Arrays.asList(item, other)));

        }});

        assertEquals(itemBucket, strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }


    @Test
    public void testDetermineDeliveryBucketExistingNoChangeSingleGroup() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService);

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final DeliveryBucket itemBucket = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "itemBucket");

        final CartItem other = context.mock(CartItem.class, "other");

        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket(); will(returnValue(itemBucket));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).isGift(); will(returnValue(false));
            allowing(other).getDeliveryBucket(); will(returnValue(itemBucket));
            allowing(other).getProductSkuCode(); will(returnValue("CED"));
            allowing(other).isGift(); will(returnValue(false));

            allowing(cart).getOrderInfo(); will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable(); will(returnValue(true));
            allowing(info).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item, other)));

        }});

        assertEquals(itemBucket, strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryBucketExistingNoChangeMultiGroup() throws Exception {


        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService);

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final DeliveryBucket itemBucket = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1");

        final CartItem other = context.mock(CartItem.class, "other");
        final DeliveryBucket otherBucket = new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "s1");

        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket(); will(returnValue(itemBucket));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).isGift(); will(returnValue(false));
            allowing(other).getDeliveryBucket(); will(returnValue(otherBucket));
            allowing(other).getProductSkuCode(); will(returnValue("CED"));
            allowing(other).isGift(); will(returnValue(false));

            allowing(cart).getOrderInfo(); will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable(); will(returnValue(true));
            allowing(info).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item, other)));

        }});

        assertEquals(itemBucket, strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryBucketExistingChangeMultiGroup() throws Exception {


        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService);

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final DeliveryBucket itemBucket = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1");

        final CartItem other = context.mock(CartItem.class, "other");
        final DeliveryBucket otherBucket = new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "s1");

        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket(); will(returnValue(itemBucket));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).isGift(); will(returnValue(false));
            allowing(other).getDeliveryBucket(); will(returnValue(otherBucket));
            allowing(other).getProductSkuCode(); will(returnValue("CED"));
            allowing(other).isGift(); will(returnValue(false));

            allowing(cart).getOrderInfo(); will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable(); will(returnValue(true));
            allowing(info).isMultipleDelivery(); will(returnValue(false));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item, other)));

        }});

        assertEquals(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "s1"), strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }



    @Test
    public void testDetermineDeliveryBucketNewNoChangeNoGroup() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService) {
            @Override
            Pair<String, String> getDeliveryGroup(final CartItem item, final List<Warehouse> warehouses) {
                return new Pair<String, String>(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1");
            }
        };

        final Warehouse warehouse = context.mock(Warehouse.class, "warehouse");

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final ShoppingContext ctx = context.mock(ShoppingContext.class, "ctx");

        final CartItem other = context.mock(CartItem.class, "other");

        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket(); will(returnValue(null));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).isGift(); will(returnValue(false));
            allowing(other).getDeliveryBucket(); will(returnValue(null));
            allowing(other).getProductSkuCode(); will(returnValue("CED"));
            allowing(other).isGift(); will(returnValue(false));

            allowing(cart).getOrderInfo(); will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable(); will(returnValue(true));
            allowing(info).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getShoppingContext(); will(returnValue(ctx));
            allowing(ctx).getCustomerShopId(); will(returnValue(10L));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item, other)));

            allowing(warehouseService).getByShopId(10L, false); will(returnValue(Arrays.asList(warehouse)));

        }});

        assertEquals(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1"), strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }


    @Test
    public void testDetermineDeliveryBucketNewNoChangeSingleGroup() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService) {
            @Override
            Pair<String, String> getDeliveryGroup(final CartItem item, final List<Warehouse> warehouses) {
                return new Pair<String, String>(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1");
            }
        };

        final Warehouse warehouse = context.mock(Warehouse.class, "warehouse");

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final ShoppingContext ctx = context.mock(ShoppingContext.class, "ctx");

        final CartItem other = context.mock(CartItem.class, "other");
        final DeliveryBucket itemBucket = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1");


        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket(); will(returnValue(null));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).isGift(); will(returnValue(false));
            allowing(other).getDeliveryBucket(); will(returnValue(itemBucket));
            allowing(other).getProductSkuCode(); will(returnValue("CED"));
            allowing(other).isGift(); will(returnValue(false));

            allowing(cart).getOrderInfo(); will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable(); will(returnValue(true));
            allowing(info).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getShoppingContext(); will(returnValue(ctx));
            allowing(ctx).getCustomerShopId(); will(returnValue(10L));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item, other)));

            allowing(warehouseService).getByShopId(10L, false); will(returnValue(Arrays.asList(warehouse)));

        }});

        assertEquals(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1"), strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }




    @Test
    public void testDetermineDeliveryBucketNewNoChangeMultiGroup() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService) {
            @Override
            Pair<String, String> getDeliveryGroup(final CartItem item, final List<Warehouse> warehouses) {
                return new Pair<String, String>(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1");
            }
        };

        final Warehouse warehouse = context.mock(Warehouse.class, "warehouse");

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final ShoppingContext ctx = context.mock(ShoppingContext.class, "ctx");

        final CartItem other = context.mock(CartItem.class, "other");
        final DeliveryBucket itemBucket = new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "s1");


        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket(); will(returnValue(null));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).isGift(); will(returnValue(false));
            allowing(other).getDeliveryBucket(); will(returnValue(itemBucket));
            allowing(other).getProductSkuCode(); will(returnValue("CED"));
            allowing(other).isGift(); will(returnValue(false));

            allowing(cart).getOrderInfo(); will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable(); will(returnValue(true));
            allowing(info).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getShoppingContext(); will(returnValue(ctx));
            allowing(ctx).getCustomerShopId(); will(returnValue(10L));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item, other)));

            allowing(warehouseService).getByShopId(10L, false); will(returnValue(Arrays.asList(warehouse)));

        }});

        assertEquals(new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1"), strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }





    @Test
    public void testDetermineDeliveryBucketNewChangeMultiGroup() throws Exception {

        final ShopService shopService = context.mock(ShopService.class, "shopService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final WarehouseService warehouseService = context.mock(WarehouseService.class, "warehouseService");
        final SkuWarehouseService skuWarehouseService = context.mock(SkuWarehouseService.class, "skuWarehouseService");

        final OrderSplittingStrategyImpl strategy = new OrderSplittingStrategyImpl(shopService, productService, warehouseService, skuWarehouseService) {
            @Override
            Pair<String, String> getDeliveryGroup(final CartItem item, final List<Warehouse> warehouses) {
                return new Pair<String, String>(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "s1");
            }
        };

        final Warehouse warehouse = context.mock(Warehouse.class, "warehouse");

        final CartItem item = context.mock(CartItem.class, "item");
        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final OrderInfo info = context.mock(OrderInfo.class, "info");
        final ShoppingContext ctx = context.mock(ShoppingContext.class, "ctx");

        final CartItem other = context.mock(CartItem.class, "other");
        final DeliveryBucket itemBucket = new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "s1");


        context.checking(new Expectations() {{
            allowing(item).getDeliveryBucket(); will(returnValue(null));
            allowing(item).getProductSkuCode(); will(returnValue("ABC"));
            allowing(item).isGift(); will(returnValue(false));
            allowing(other).getDeliveryBucket(); will(returnValue(itemBucket));
            allowing(other).getProductSkuCode(); will(returnValue("CED"));
            allowing(other).isGift(); will(returnValue(false));

            allowing(cart).getOrderInfo(); will(returnValue(info));
            allowing(info).isMultipleDeliveryAvailable(); will(returnValue(true));
            allowing(info).isMultipleDelivery(); will(returnValue(false));
            allowing(cart).getShoppingContext(); will(returnValue(ctx));
            allowing(ctx).getCustomerShopId(); will(returnValue(10L));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item, other)));

            allowing(warehouseService).getByShopId(10L, false); will(returnValue(Arrays.asList(warehouse)));

        }});

        assertEquals(new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "s1"), strategy.determineDeliveryBucket(item, cart));

        context.assertIsSatisfied();

    }


}