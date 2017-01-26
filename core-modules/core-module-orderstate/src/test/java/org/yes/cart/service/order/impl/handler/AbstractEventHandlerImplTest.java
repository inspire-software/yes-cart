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

package org.yes.cart.service.order.impl.handler;

import org.junit.After;
import org.junit.Before;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.impl.TestExtFormPaymentGatewayImpl;
import org.yes.cart.payment.impl.TestPaymentGatewayImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.util.DomainApiUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractEventHandlerImplTest extends BaseCoreDBTestCase {

    private CustomerOrderService orderService;
    private CustomerOrderPaymentService customerOrderPaymentService;
    private WarehouseService warehouseService;
    private ProductService productService;
    private ProductSkuService productSkuService;
    private SkuWarehouseService skuWarehouseService;

    public enum TestOrderType {
        STANDARD,
        PREORDER,
        BACKORDER,
        ELECTRONIC,
        MIXED, // does not include electronic
        FULL // all types
    }

    public static final long WAREHOUSE_ID = 1L;

    private final List<String> testPgAlteredParameters = new ArrayList<String>();
    private final List<String> testExtPgAlteredParameters = new ArrayList<String>();
    private static int COUNTER = 0;

    @Before
    public void setUp()  {
        super.setUp();
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean(ServiceSpringKeys.ORDER_PAYMENT_SERICE);
        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        skuWarehouseService = (SkuWarehouseService) ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
    }


    @After
    public void cleanUpTestPG() {
        for (final String param : testPgAlteredParameters) {
            deactivateTestPgParameter(param);
        }
        testPgAlteredParameters.clear();
        for (final String param : testExtPgAlteredParameters) {
            deactivateTestExtPgParameter(param);
        }
        testExtPgAlteredParameters.clear();
    }

    /**
     * Create new customer and new order for them of given type.
     *
     * @param orderType see enum for different types
     * @param pgLabel payment gateway to use
     * @param onePhysicalDelivery multi delivery option
     *
     * @return customer order for testing
     *
     * @throws Exception
     */
    protected CustomerOrder createTestOrder(TestOrderType orderType, String pgLabel, boolean onePhysicalDelivery) throws Exception {
        return createTestOrder(orderType, pgLabel, onePhysicalDelivery, false);
    }

    /**
     * Create new customer and new order for them of given type.
     *
     * @param orderType see enum for different types
     * @param pgLabel payment gateway to use
     * @param onePhysicalDelivery multi delivery option
     *
     * @return customer order for testing
     *
     * @throws Exception
     */
    protected CustomerOrder createTestSubOrder(TestOrderType orderType, String pgLabel, boolean onePhysicalDelivery) throws Exception {
        return createTestOrder(orderType, pgLabel, onePhysicalDelivery, true);
    }


    /**
     * Create new customer and new order for them of given type.
     *
     * @param orderType see enum for different types
     * @param pgLabel payment gateway to use
     * @param onePhysicalDelivery multi delivery option
     * @param sub sub shop
     *
     * @return customer order for testing
     *
     * @throws Exception
     */
    protected CustomerOrder createTestOrder(TestOrderType orderType, String pgLabel, boolean onePhysicalDelivery, boolean sub) throws Exception {
        Customer customer = sub ? createCustomerB2BSub("" + COUNTER++, false, false) : createCustomer("" + COUNTER++);
        assertFalse(customer.getAddress().isEmpty());

        final ShoppingCart cart;
        switch (orderType) {
            case BACKORDER: cart = getBackCart(customer.getEmail()); break;
            case PREORDER: cart = getPreCart(customer.getEmail()); break;
            case ELECTRONIC: cart = getElectronicCart(customer.getEmail()); break;
            case MIXED: cart = getMixCart(customer.getEmail()); break;
            case FULL: cart = getFullMixCart(customer.getEmail()); break;
            case STANDARD:
            default: cart = getStdCart(customer.getEmail()); break;
        }

        prepareMultiDeliveriesAndRecalculate(cart, !onePhysicalDelivery);

        CustomerOrder customerOrder = orderService.createFromCart(cart);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel(pgLabel);
        orderService.update(customerOrder);

        // Ensure we have correct orders
        if (sub) {
            assertEquals(1010L, customerOrder.getShop().getShopId());
        } else {
            assertEquals(10L, customerOrder.getShop().getShopId());
        }

        return customerOrder;
    }

    /**
     * Simple cart with two sku, three items, standard availability, one payment
     *
     * @return cart
     */
    protected ShoppingCart getStdCart(final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        addStdCartItems(shoppingCart, commands);

        return shoppingCart;
    }

    private  void addStdCartItems(final ShoppingCart shoppingCart, final ShoppingCartCommandFactory commands) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST1");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST2");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

    }

    /**
     * Simple cart with four sku, standard/preorder/backorder availability, one payment
     *
     * @return cart
     */
    protected ShoppingCart getMixCart(final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        addStdCartItems(shoppingCart, commands);
        addPreorderCartItems(shoppingCart, commands);
        addBackorderCartItems(shoppingCart, commands);

        return shoppingCart;
    }

    /**
     * Simple cart with four sku, standard/preorder/backorder availability + electronic, one payment
     *
     * @return cart
     */
    protected ShoppingCart getFullMixCart(final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        addStdCartItems(shoppingCart, commands);
        addPreorderCartItems(shoppingCart, commands);
        addBackorderCartItems(shoppingCart, commands);
        addDigitalCartItems(shoppingCart, commands);

        return shoppingCart;
    }

    /**
     * Simple cart with one sku, preorder availability, one payment
     *
     * @return cart
     */
    protected ShoppingCart getPreCart(final String customerEmail) {

        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        addPreorderCartItems(shoppingCart, commands);

        return shoppingCart;
    }

    /**
     * Simple cart with one sku, backorder availability, one payment
     *
     * @return cart
     */
    protected ShoppingCart getBackCart(final String customerEmail) {

        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        addBackorderCartItems(shoppingCart, commands);

        return shoppingCart;
    }


    private  void addPreorderCartItems(final ShoppingCart shoppingCart, final ShoppingCartCommandFactory commands) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST6");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "3.00");
        commands.execute(shoppingCart, (Map) param);

    }

    private  void addBackorderCartItems(final ShoppingCart shoppingCart, final ShoppingCartCommandFactory commands) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST5-NOINV");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "4.00");
        commands.execute(shoppingCart, (Map) param);

    }


    /**
     * Simple cart with digital one sku, always availability, one payment
     *
     * @return cart
     */
    protected ShoppingCart getElectronicCart(final String customerEmail) {

        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        addDigitalCartItems(shoppingCart, commands);

        return shoppingCart;
    }


    private  void addDigitalCartItems(final ShoppingCart shoppingCart, final ShoppingCartCommandFactory commands) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST9");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "5.00");
        commands.execute(shoppingCart, (Map) param);

    }


    protected ShoppingCart getEmptyCart(final String customerEmail) {
        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customerEmail);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "rawpassword");

        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1-WAREHOUSE_1|1-WAREHOUSE_2|1");

        commands.execute(shoppingCart, (Map) params);

        return shoppingCart;
    }


    // Test PG scenarios configuration ----------------------------------------------

    /**
     * Tes payment gateway specific setup.
     *
     * @param authorise true for AUTH then CAPTURE, false for AUTH_CAPTURE mode
     * @param perShipment true for AUTH/CAPTURE per shipment, false for single payment
     */
    protected void configureTestPG(final boolean authorise, final boolean perShipment) {

        if (authorise) {
            activateTestPgParameterSetOn("SUPPORTED_OPERATIONS_SupportAuthorize");
        } else {
            activateTestPgParameterSetOff("SUPPORTED_OPERATIONS_SupportAuthorize");
        }

        if (perShipment) {
            activateTestPgParameterSetOn("SUPPORTED_OPERATIONS_SupportAuthorizePerShipment");
        } else {
            activateTestPgParameterSetOff("SUPPORTED_OPERATIONS_SupportAuthorizePerShipment");
        }
    }

    /**
     * Tes payment gateway specific setup.
     *
     * @param authorise true for AUTH then CAPTURE, false for AUTH_CAPTURE mode
     * @param perShipment true for AUTH/CAPTURE per shipment, false for single payment
     * @param failure failure setting, see {@link TestPaymentGatewayImpl} constants
     */
    public void configureTestPG(final boolean authorise, final boolean perShipment, final String failure) {
        configureTestPG(authorise, perShipment);
        activateTestPgParameterSetOn(failure);
    }


    /**
     * Activate testPaymentGatewayLabel parameter to simulate scenario.
     *
     * @param param parameter name
     */
    protected void activateTestPgParameterSetOn(final String param) {
        PaymentGatewayParameter p = new PaymentGatewayParameterEntity();
        p.setLabel(param);
        p.setName(param);
        p.setPgLabel("testPaymentGatewayLabel");
        p.setValue("true");  // must be boolean to enable/disable PG features in TestPaymentGatewayImpl

        TestPaymentGatewayImpl.getGatewayConfig().put(p.getLabel(), p);
        testPgAlteredParameters.add(param);
    }

    /**
     * Activate testPaymentGatewayLabel parameter to simulate scenario.
     *
     * @param param parameter name
     */
    protected void activateTestPgParameterSetOff(final String param) {
        PaymentGatewayParameter p = new PaymentGatewayParameterEntity();
        p.setLabel(param);
        p.setName(param);
        p.setPgLabel("testPaymentGatewayLabel");
        p.setValue("false"); // must be boolean to enable/disable PG features in TestPaymentGatewayImpl

        TestPaymentGatewayImpl.getGatewayConfig().put(p.getLabel(), p);
        testPgAlteredParameters.add(param);
    }

    /**
     * De-activate testPaymentGatewayLabel parameter to simulate scenario.
     *
     * @param param parameter name
     */
    protected void deactivateTestPgParameter(final String param) {
        TestPaymentGatewayImpl.getGatewayConfig().remove(param);
    }


    /**
     * Tes payment gateway specific setup.
     *
     * @param refund Refund response: 1 (OK), 3 (Processing), 4 (Manual) and 5 (Failed), default is 4.
     */
    protected void configureTestExtPG(final String refund) {

        if (refund != null) {
            PaymentGatewayParameter p = new PaymentGatewayParameterEntity();
            p.setLabel(TestExtFormPaymentGatewayImpl.REFUND_RESPONSE_CODE_PARAM_KEY);
            p.setName(TestExtFormPaymentGatewayImpl.REFUND_RESPONSE_CODE_PARAM_KEY);
            p.setPgLabel("testExtFormPaymentGatewayLabel");
            p.setValue(refund);

            TestPaymentGatewayImpl.getGatewayConfig().put(p.getLabel(), p);
            testExtPgAlteredParameters.add(TestExtFormPaymentGatewayImpl.REFUND_RESPONSE_CODE_PARAM_KEY);

            TestExtFormPaymentGatewayImpl.getGatewayConfig().put(TestExtFormPaymentGatewayImpl.REFUND_RESPONSE_CODE_PARAM_KEY, p);
        }

    }


    /**
     * De-activate TestExtFormPaymentGatewayImpl parameter to simulate scenario.
     *
     * @param param parameter name
     */
    protected void deactivateTestExtPgParameter(final String param) {
        TestExtFormPaymentGatewayImpl.getGatewayConfig().remove(param);
    }


    // Common operations to assist testing ----------------------------------------------------

    /**
     * Remove quantity from warehouse and assert inventory state.
     *
     * @param warehouseId warehouse to check
     * @param skuCode sku to check
     * @param debitQuantity quantity to remove
     * @param expectedAvailable available inventory
     * @param expectedReserved reserved inventory
     */
    protected void debitInventoryAndAssert(final long warehouseId,
                                           final String skuCode,
                                           final String debitQuantity,
                                           final String expectedAvailable,
                                           final String expectedReserved) {
        final Warehouse warehouse = warehouseService.findById(warehouseId);
        skuWarehouseService.debit(warehouse, skuCode, new BigDecimal(debitQuantity));
        assertInventory(warehouseId, skuCode, expectedAvailable, expectedReserved);

    }

    /**
     * Add quantity to warehouse and assert inventory state.
     *
     * @param warehouseId warehouse to check
     * @param skuCode sku to check
     * @param creditQuantity quantity to add
     * @param expectedAvailable available inventory
     * @param expectedReserved reserved inventory
     */
    protected void creditInventoryAndAssert(final long warehouseId,
                                            final String skuCode,
                                            final String creditQuantity,
                                            final String expectedAvailable,
                                            final String expectedReserved) {
        final Warehouse warehouse = warehouseService.findById(warehouseId);
        skuWarehouseService.credit(warehouse, skuCode, new BigDecimal(creditQuantity));
        assertInventory(warehouseId, skuCode, expectedAvailable, expectedReserved);

    }

    /**
     * Set availability date for product.
     *
     * @param skuCode sku to check
     * @param availableFrom available from date
     */
    protected void changeAvailabilityDatesAndAssert(final String skuCode,
                                                    final Date availableFrom,
                                                    final boolean expectedAvailable) {

        final Product product = productService.getProductBySkuCode(skuCode);
        assertNotNull("No product exists: " + skuCode, product);
        product.setAvailablefrom(availableFrom);
        productService.update(product);
        assertEquals(expectedAvailable, DomainApiUtils.isObjectAvailableNow(true, product.getAvailablefrom(), product.getAvailableto(), new Date()));
    }

    // Common asserts below this line ---------------------------------------------------------


    /**
     * Assert inventory state.
     *
     * @param warehouseId warehouse to check
     * @param skuCode sku to check
     * @param expectedAvailable available inventory
     * @param expectedReserved reserved inventory
     */
    protected void assertInventory(final long warehouseId,
                                   final String skuCode,
                                   final String expectedAvailable,
                                   final String expectedReserved) {
        ProductSku sku = productSkuService.getProductSkuBySkuCode(skuCode);
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.findQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouseService.findById(warehouseId));
                }},
                sku.getCode()
        );
        assertEquals(new BigDecimal(expectedAvailable), qty.getFirst());
        assertEquals(new BigDecimal(expectedReserved), qty.getSecond());
    }

    /**
     * Assert all deliveries have given state
     *
     * @param deliveries deliveries
     * @param expectedState state
     */
    protected void assertDeliveryStates(final Collection<CustomerOrderDelivery> deliveries,
                                        final String expectedState) {
        for (CustomerOrderDelivery delivery : deliveries) {
            assertEquals("Delivery " + delivery.getDeliveryGroup() + ", no " + delivery.getDeliveryNum(),
                    expectedState,
                    delivery.getDeliveryStatus());

        }
    }

    /**
     * Assert delivery state by delivery group.
     *
     * @param deliveries deliveries
     * @param expectedState map of delivery group (key) to expected state (value) mapping
     */
    protected void assertDeliveryStates(final Collection<CustomerOrderDelivery> deliveries,
                                        final Map<String, String> expectedState) {
        for (CustomerOrderDelivery delivery : deliveries) {
            assertEquals("Delivery " + delivery.getDeliveryGroup() + ", no " + delivery.getDeliveryNum(),
                    expectedState.get(delivery.getDeliveryGroup()),
                    delivery.getDeliveryStatus());

        }
    }

    /**
     * Assert that there are no payments associated with given order.
     *
     * @param orderNum order number
     */
    protected void assertNoPaymentEntries(final String orderNum) {
        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(orderNum, null, (String) null, (String) null);
        assertEquals(0, rezList.size());
    }

    /**
     * Assert single payment entry exists for given order.
     *
     * @param orderNum order number
     * @param expectedAmount amount
     * @param expectedOperation operation for given entry
     * @param expectedResult expected result
     * @param expectedSettled batch settlement occurred (only valid for CAPTURE and AUTH_CAPTURE)
     */
    protected void assertSinglePaymentEntry(final String orderNum,
                                            final String expectedAmount,
                                            final String expectedOperation,
                                            final String expectedResult,
                                            final boolean expectedSettled) {

        assertMultiPaymentEntry(orderNum, Arrays.asList(expectedAmount), Arrays.asList(expectedOperation), Arrays.asList(expectedResult), Arrays.asList(expectedSettled));

    }


    /**
     * Assert auth capture operation on the order.
     *
     * @param orderNum order number
     * @param expectedAmount amount
     * @param expectedAuthResult expected result
     * @param expectedCaptureResult expected result
     * @param expectedSettled batch settlement occurred (only valid for CAPTURE and AUTH_CAPTURE)
     */
    protected void assertAuthCapturePaymentEntries(final String orderNum,
                                                   final String expectedAmount,
                                                   final String expectedAuthResult,
                                                   final String expectedCaptureResult,
                                                   final boolean expectedSettled) {

        assertMultiPaymentEntry(orderNum,
                Arrays.asList(expectedAmount, expectedAmount),
                Arrays.asList(PaymentGateway.AUTH, PaymentGateway.CAPTURE),
                Arrays.asList(expectedAuthResult, expectedCaptureResult),
                Arrays.asList(Boolean.FALSE, expectedSettled));

    }


    /**
     * Assert multiple payment entries exist for given order
     *
     * @param orderNum order number
     * @param expectedAmount list of expected amounts (number of items in list must match total number of payment entries)
     * @param expectedOperation list of expected operations (number of items in list must match expectedAmount. each operation item corresponds to amount item of the same index)
     * @param expectedResult expected result
     * @param expectedSettled batch settlement occurred (only valid for CAPTURE and AUTH_CAPTURE)
     */
    protected void assertMultiPaymentEntry(final String orderNum,
                                           final List<String> expectedAmount,
                                           final List<String> expectedOperation,
                                           final List<String> expectedResult,
                                           final List<Boolean> expectedSettled) {

        List<CustomerOrderPayment> rezList = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(orderNum, null, (String) null, (String) null));
        assertEquals(expectedAmount.size(), rezList.size());

        List<String> expected = new ArrayList<String>();
        for (int i = 0; i < expectedAmount.size(); i++) {
            expected.add(expectedAmount.get(i) + "-" + expectedOperation.get(i) + "-" + expectedResult.get(i) + "-" +
                    // batch settlement is only for capture or auth_capture
                    ((PaymentGateway.CAPTURE.equals(expectedOperation.get(i)) || PaymentGateway.AUTH_CAPTURE.equals(expectedOperation.get(i))) && expectedSettled.get(i)));
        }

        for (final String expectedPayment : expected) {
            boolean found = false;
            final StringBuilder failMessage = new StringBuilder();

            final Iterator<CustomerOrderPayment> rezListIt = rezList.iterator();
            while (rezListIt.hasNext()) {

                final CustomerOrderPayment customerOrderPayment = rezListIt.next();

                final String test = customerOrderPayment.getPaymentAmount() + "-"
                        + customerOrderPayment.getTransactionOperation() + "-"
                        + customerOrderPayment.getPaymentProcessorResult() + "-"
                        + customerOrderPayment.isPaymentProcessorBatchSettlement();

                found = test.equals(expectedPayment);
                if (found) {
                    rezListIt.remove(); // we matched this, so remove to avoid double match
                    break;
                }

                failMessage.append('\n').append(test);

            }
            if (!found) {
                fail("Expected payment not found amount/operation/result/settle:\n" + expectedPayment + "\nExisting payments:" + failMessage);
            }
        }

        assertTrue("Not all items matched", rezList.isEmpty());

    }

    /**
     * Assert PG features for test.
     *
     *
     * @param pg label
     * @param external is it external form PG (signifies that callback will contain payment status notification)
     * @param online is it online PG
     * @param authorise true if we have 2 state AUTH then CAPTURE, false if we support only AUTH_CAPTURE
     * @param perShipment true indicates that we have separate payment per delivery
     *
     * @return pg label
     */
    protected String assertPgFeatures(final String pg,
                                      final boolean external,
                                      final boolean online,
                                      final boolean authorise,
                                      final boolean perShipment) {

        final PaymentGateway proto = ctx().getBean(pg, PaymentGateway.class);
        assertEquals(pg + " external feature", external, proto.getPaymentGatewayFeatures().isExternalFormProcessing());
        assertEquals(pg + " online feature", online, proto.getPaymentGatewayFeatures().isOnlineGateway());
        assertEquals(pg + " authorise feature", authorise, proto.getPaymentGatewayFeatures().isSupportAuthorize());
        assertEquals(pg + " authorise per shipment feature", perShipment, proto.getPaymentGatewayFeatures().isSupportAuthorizePerShipment());

        return proto.getLabel() + "Label"; // all descriptors have Label suffix
    }

    /**
     * Assert PG features for test.
     *
     *
     * @param pg label
     * @param external is it external form PG (signifies that callback will contain payment status notification)
     * @param online is it online PG
     * @param authorise true if we have 2 state AUTH then CAPTURE, false if we support only AUTH_CAPTURE
     * @param perShipment true indicates that we have separate payment per delivery
     * @param voidCapture true indicates support of void capture
     * @param refund true indicates support of refund
     *
     * @return pg label
     */
    protected String assertPgFeatures(final String pg,
                                      final boolean external,
                                      final boolean online,
                                      final boolean authorise,
                                      final boolean perShipment,
                                      final boolean voidCapture,
                                      final boolean refund) {

        final PaymentGateway proto = ctx().getBean(pg, PaymentGateway.class);
        assertEquals(pg + " external feature", external, proto.getPaymentGatewayFeatures().isExternalFormProcessing());
        assertEquals(pg + " online feature", online, proto.getPaymentGatewayFeatures().isOnlineGateway());
        assertEquals(pg + " authorise feature", authorise, proto.getPaymentGatewayFeatures().isSupportAuthorize());
        assertEquals(pg + " authorise per shipment feature", perShipment, proto.getPaymentGatewayFeatures().isSupportAuthorizePerShipment());
        assertEquals(pg + " void feature", voidCapture, proto.getPaymentGatewayFeatures().isSupportVoid());
        assertEquals(pg + " refund feature", refund, proto.getPaymentGatewayFeatures().isSupportRefund());

        return proto.getLabel() + "Label"; // all descriptors have Label suffix
    }

}
