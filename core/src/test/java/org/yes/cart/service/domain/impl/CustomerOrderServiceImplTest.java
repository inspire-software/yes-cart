package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.*;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderServiceImplTest extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        customerOrderService = (CustomerOrderService) ctx.getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
    }

    @Test
    public void testCreateAndDelete() throws Exception {

        final String customerPrefix = "testCreateAndDelete";


        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());


        final ShoppingCart shoppingCart = getShoppingCart(ctx, customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);

        customerOrderService.delete(order);

        assertNull(customerOrderService.getById(pk));


    }

    @Test
    public void testFindByGuid() throws Exception {
        final String customerPrefix = "testFindByGuid";

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());

        final ShoppingCart shoppingCart = getShoppingCart(ctx, customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);

        CustomerOrder order2 = customerOrderService.findByGuid(shoppingCart.getGuid());
        assertNotNull(order2);

        customerOrderService.delete(order);

        assertNull(customerOrderService.getById(pk));
        assertNull(customerOrderService.findByGuid(shoppingCart.getGuid()));
    }

    @Test
    public void testGetCustomerOrders() throws Exception {
        final String customerPrefix = "cosit2";
        final Date date = new Date();
        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());

        final ShoppingCart shoppingCart = getShoppingCart(ctx, customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertTrue(order.getCustomerorderId() > 0);
        assertEquals(1, customerOrderService.findCustomerOrders(customer, null).size());
    }

    @Test
    public void testPersistReassembledOrder1() throws Exception {

        final String customerPrefix = "cosit3";


        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());


        final ShoppingCart shoppingCart = getShoppingCart(ctx, customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertEquals(4, order.getDelivery().size());


    }

    @Test
    public void testPersistReassembledOrder2() throws Exception {

        final String customerPrefix = "cosit4";

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());


        final ShoppingCart shoppingCart = getShoppingCart(ctx, customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);
        assertEquals(2, order.getDelivery().size());


    }

    @Test
    public void testPersistReassembledOrder3() throws Exception {

        final String customerPrefix = "testPersistReassembledOrder3";


        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());


        final ShoppingCart shoppingCart = getShoppingCart2(customerPrefix);
        assertFalse(customerOrderService.isOrderCanHasMultipleDeliveries(shoppingCart));

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);
        assertEquals(2, order.getDelivery().size());


    }

    /**
     * Get product with standard availability, back order and pre order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    public static ShoppingCart getShoppingCart(final ApplicationContext ctx, final String prefix) {

        ShoppingCart shoppingCart = getEmptyCart(ctx, prefix);

        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST4");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);


        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST5");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "200.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST6");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "3.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST7");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);

        // this digital product not available till date
        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST8");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);

        // this digital product available 
        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST9");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);

        return shoppingCart;
    }

    /**
     * @return cart with one digital available product.
     */
    private ShoppingCart getShoppingCart2(final String prefix) {

        ShoppingCart shoppingCart = getEmptyCart(ctx, prefix);

        Map<String, String> param = new HashMap<String, String>();

        // this digital product available
        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST9");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);


        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST5");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "200.00");

        new SetSkuQuantityToCartEventCommandImpl(ctx,
                param)
                .execute(shoppingCart);

        return shoppingCart;
    }

    private static ShoppingCart getEmptyCart(final ApplicationContext ctx, final String prefix) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();

        Map<String, String> params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, prefix + "jd@domain.com");
        params.put(LoginCommandImpl.NAME, prefix + "John Doe");


        new SetShopCartCommandImpl(ctx, Collections.singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);

        new ChangeCurrencyEventCommandImpl(ctx, Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "USD"))
                .execute(shoppingCart);

        new LoginCommandImpl(null, params)
                .execute(shoppingCart);

        new SetCarrierSlaCartCommandImpl(null, Collections.singletonMap(SetCarrierSlaCartCommandImpl.CMD_KEY, "1"))
                .execute(shoppingCart);

        return shoppingCart;
    }
}
