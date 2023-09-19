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

package org.yes.cart.service.order.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.SkuUnavailableException;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAssemblerImplTest extends BaseCoreDBTestCase {

    private OrderAssembler orderAssembler;
    private DeliveryAssemblerImpl deliveryAssembler;
    //private GenericDAO<CustomerOrder, Long> customerOrderDao;
    private CustomerOrderService customerOrderService;
    private WarehouseService warehouseService;

    @Override
    @Before
    public void setUp() {
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService =  ctx().getBean("customerOrderService", CustomerOrderService.class);
        warehouseService =  ctx().getBean("warehouseService", WarehouseService.class);
        deliveryAssembler = (DeliveryAssemblerImpl) ctx().getBean(ServiceSpringKeys.DELIVERY_ASSEMBLER);
        super.setUp();
    }

    @Test
    public void testGetDeliveryGroupsStandard() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart1(customer.getLogin());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(1, dgroups.size());
        final DeliveryBucket d1 = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d1));
        assertEquals(2, dgroups.get(d1).size());
    }

    @Test
    public void testGetDeliveryGroupsStandardInsufficient() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        // if sku is out of stock then we cannot create order
        ShoppingCart shoppingCart = getShoppingCart2(customer.getLogin());
        try {
            CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
            deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
            fail("Must not allow creating orders with unavailable SKU");
        } catch (SkuUnavailableException sue) {
            assertEquals("Sku CC_TEST4-M:cc test 4 m is not available, cause: offline", sue.getMessage());
        }
    }

    @Test
    public void testGetDeliveryGroupsStandardAndBackorderSufficient() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        //Standard and back order with inventory. Only one delivery must be planned
        ShoppingCart shoppingCart = getShoppingCart3(customer.getLogin());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(1, dgroups.size());
        final DeliveryBucket d1 = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d1));
        assertEquals(2, dgroups.get(d1).size());
    }

    @Test
    public void testGetDeliveryGroupsStandardAndBackorderSingle() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart4(customer.getLogin());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(1, dgroups.size());
        final DeliveryBucket d5 = new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d5));
        assertEquals(2, dgroups.get(d5).size());
    }

    @Test
    public void testGetDeliveryGroupsStandardAndBackorder() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart4(customer.getLogin());
        prepareMultiDeliveriesAndRecalculate(shoppingCart, true);
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(2, dgroups.size());
        final DeliveryBucket d1 = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "WAREHOUSE_1");
        final DeliveryBucket d3 = new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d1));
        assertNotNull(dgroups.get(d3));
        assertEquals(1, dgroups.get(d1).size());
        assertEquals(1, dgroups.get(d3).size());

        // Ensure that they are ordered
        final Iterator<DeliveryBucket> sequence = Arrays.asList(d1, d3).iterator();
        for (final Map.Entry<DeliveryBucket, List<CustomerOrderDet>> entry : dgroups.entrySet()) {
            assertEquals(sequence.next(), entry.getKey());
        }

    }

    @Test
    public void testGetDeliveryGroupsBackorder() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart5(customer.getLogin());
        prepareMultiDeliveriesAndRecalculate(shoppingCart, true);
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(2, dgroups.size());
        final DeliveryBucket d1 = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "WAREHOUSE_1");
        final DeliveryBucket d3 = new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d1));
        assertNotNull(dgroups.get(d3));
        assertEquals(1, dgroups.get(d1).size());
        assertEquals(1, dgroups.get(d3).size());

        // Ensure that they are ordered
        final Iterator<DeliveryBucket> sequence = Arrays.asList(d1, d3).iterator();
        for (final Map.Entry<DeliveryBucket, List<CustomerOrderDet>> entry : dgroups.entrySet()) {
            assertEquals(sequence.next(), entry.getKey());
        }
    }

    @Test
    public void testGetDeliveryGroupsBackorderSplit() throws Exception {

        configureWarehouseSplit(1L, true);

        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart5S(customer.getLogin());
        prepareMultiDeliveriesAndRecalculate(shoppingCart, true);
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(3, dgroups.size());
        final DeliveryBucket d1 = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "WAREHOUSE_1");
        final DeliveryBucket d3 = new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d1));
        assertNull(dgroups.get(d3));
        assertEquals(1, dgroups.get(d1).size());
        assertEquals(2, dgroups.keySet().stream().filter(
                b -> CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP.equals(b.getGroup()) && b.getQualifier().length() > 0).count());
        final Set<DeliveryBucket> orderedSet = new TreeSet<>();
        orderedSet.add(d1);
        dgroups.entrySet().forEach(e -> {
            if (CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP.equals(e.getKey().getGroup())) {
                assertEquals(1, dgroups.get(e.getKey()).size());
                orderedSet.add(e.getKey());
            }
        });

        // Ensure that they are ordered
        final Iterator<DeliveryBucket> sequence = orderedSet.iterator();
        for (final Map.Entry<DeliveryBucket, List<CustomerOrderDet>> entry : dgroups.entrySet()) {
            assertEquals(sequence.next(), entry.getKey());
        }

        configureWarehouseSplit(1L, false);
    }

    private void configureWarehouseSplit(final long pk, final boolean forceBackorderSplit) {
        final Warehouse warehouse = warehouseService.findById(pk);
        warehouse.setForceBackorderDeliverySplit(forceBackorderSplit);
        warehouseService.update(warehouse);
    }

    @Test
    public void testGetDeliveryGroupsPreorder() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart6(customer.getLogin());
        prepareMultiDeliveriesAndRecalculate(shoppingCart, true);
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(2, dgroups.size());
        final DeliveryBucket d1 = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "WAREHOUSE_1");
        final DeliveryBucket d2 = new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d1));
        assertNotNull(dgroups.get(d2));
        assertEquals(1, dgroups.get(d2).size());
        assertEquals(2, dgroups.get(d1).size());

        // Ensure that they are ordered
        final Iterator<DeliveryBucket> sequence = Arrays.asList(d1, d2).iterator();
        for (final Map.Entry<DeliveryBucket, List<CustomerOrderDet>> entry : dgroups.entrySet()) {
            assertEquals(sequence.next(), entry.getKey());
        }
    }

    @Test
    public void testGetDeliveryGroupsFull() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        //Standard, back order & pre order with inventory. Ordered qty covered by inventory.
        //Two  deliveries must be planned, because of pre order will wait 
        //Standard, back order without inventory & pre order with inventory. Ordered qty not covered by inventory.
        // 4 deliveries must be planned, because of pre order will wait
        ShoppingCart shoppingCart = getShoppingCart7(customer.getLogin());
        prepareMultiDeliveriesAndRecalculate(shoppingCart, true);
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        Map<DeliveryBucket, List<CustomerOrderDet>> dgroups = deliveryAssembler.getDeliveryGroups(customerOrder, getMultiSelection(shoppingCart));
        assertEquals(4, dgroups.size());
        final DeliveryBucket d1 = new DeliveryBucketImpl(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, "WAREHOUSE_1");
        final DeliveryBucket d2 = new DeliveryBucketImpl(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, "WAREHOUSE_1");
        final DeliveryBucket d3 = new DeliveryBucketImpl(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, "WAREHOUSE_1");
        final DeliveryBucket d44 = new DeliveryBucketImpl(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, "WAREHOUSE_1");
        assertNotNull(dgroups.get(d1));
        assertNotNull(dgroups.get(d2));
        assertNotNull(dgroups.get(d3));
        assertNotNull(dgroups.get(d44));
        assertEquals(2, dgroups.get(d1).size());
        assertEquals(1, dgroups.get(d2).size());
        assertEquals(1, dgroups.get(d3).size());
        assertEquals(2, dgroups.get(d44).size());

        // Ensure that they are ordered
        final Iterator<DeliveryBucket> sequence = Arrays.asList(d1, d2, d3, d44).iterator();
        for (final Map.Entry<DeliveryBucket, List<CustomerOrderDet>> entry : dgroups.entrySet()) {
            assertEquals(sequence.next(), entry.getKey());
        }
    }

    @Test
    public void testAssembleCustomerOrder() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart7(customer.getLogin());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        assertNotNull("Customer can not be null", shoppingCart.getCustomerLogin());
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        customerOrder = customerOrderService.create(customerOrder);
        assertTrue(customerOrder.getCustomerorderId() > 0);
        for (CustomerOrderDelivery cod : customerOrder.getDelivery()) {
            assertTrue(cod.getCustomerOrderDeliveryId() > 0);
            assertNotNull(cod.getDeliveryNum());
            if (CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(cod.getDeliveryGroup())) {
                assertEquals(BigDecimal.ZERO, cod.getPrice());
                assertEquals(BigDecimal.ZERO, cod.getNetPrice());
                assertEquals(BigDecimal.ZERO, cod.getGrossPrice());
                assertEquals(BigDecimal.ZERO, cod.getTaxRate());
                assertEquals("", cod.getTaxCode());
                assertFalse(cod.isTaxExclusiveOfPrice());
            } else {
                assertEquals(new BigDecimal("16.77"), cod.getPrice());
                assertEquals(new BigDecimal("13.97"), cod.getNetPrice());
                assertEquals(new BigDecimal("16.77"), cod.getGrossPrice());
                assertEquals(new BigDecimal("20.00"), cod.getTaxRate());
                assertEquals("VAT", cod.getTaxCode());
                assertFalse(cod.isTaxExclusiveOfPrice());
            }
            for (CustomerOrderDeliveryDet det : cod.getDetail()) {
                assertTrue(det.getCustomerOrderDeliveryDetId() > 0);
                assertTrue(MoneyUtils.isPositive(det.getQty()));
            }
        }
    }

    /**
     * Create simple cart with products, that have a standard availability and enough qty on warehouses.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart1(final String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params;

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    //Bot sku with standard availability , but one of the has not qty on warehouse
    protected ShoppingCart getShoppingCart2(final String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params;

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4-M");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    /**
     * Get product with standard availability and back order availability both have inventory.
     * Only one delivery must be planned
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart3(String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);


        Map<String, String> params;

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    /**
     * Get product with standard availability has inventory and back order has inventory , but ordered qty
     * more than inventory.
     * Two deliveries must be planned
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart4(String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params;

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "23.00");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    /**
     * Get product with standard availability and back order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart5(String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "23.00");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    /**
     * Get product with standard availability and back order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart5S(String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "23.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5-NOINV");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "10.00");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    /**
     * Get product with standard availability, back order and pre order availability both have inventory.
     * Ordered qty more than qty on warehouses, so two "wait" delivery will be planned.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart6(String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST6");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "3.00");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    /**
     * Get product with standard availability, back order and pre order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    private ShoppingCart getShoppingCart7(String customerLogin) {
        ShoppingCart shoppingCart = getEmptyCart(customerLogin);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);


        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST5");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "200.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST6");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "3.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST7");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST8");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST9");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        params.put(ShoppingCartCommand.CMD_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) params);

        prepareDeliveriesAndRecalculate(shoppingCart);

        return shoppingCart;
    }

    @Override
    protected ShoppingCart getEmptyCart(String login) {
        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        Map<String, String> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_LOGIN, login);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "rawpassword");

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1-WAREHOUSE_1|1-WAREHOUSE_2|1");
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        commands.execute(shoppingCart, (Map) params);

        return shoppingCart;
    }

    private Map<String, Boolean> getMultiSelection(ShoppingCart cart) {
        final Map<String, Boolean> single = new HashMap<>();
        final boolean selected = cart.getOrderInfo().isMultipleDelivery();
        for (final Map.Entry<String, Boolean> allowed : cart.getOrderInfo().getMultipleDeliveryAvailable().entrySet()) {
            single.put(allowed.getKey(), !selected || !allowed.getValue());
        }
        return single;
    }

}
