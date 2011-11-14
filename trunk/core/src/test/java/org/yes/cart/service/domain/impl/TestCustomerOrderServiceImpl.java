package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.SetSkuQuantityToCartEventCommandImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCustomerOrderServiceImpl extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;

    @Before
    public void setUp() throws Exception {
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
    }

    // TODO fix to not depend on order or running
    @Test
    public void testCreateAndDelete() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);
        customerOrderService.delete(order);
        assertNull(customerOrderService.getById(pk));
    }

    // TODO fix to not depend on order or running
    @Test
    public void testFindByGuid() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);
        CustomerOrder order2 = customerOrderService.findByGuid(shoppingCart.getGuid());
        assertNotNull(order2);
        customerOrderService.delete(order);
        assertNull(customerOrderService.getById(pk));
        assertNull(customerOrderService.findByGuid(shoppingCart.getGuid()));
        assertNull(customerOrderService.findByGuid(null));
    }

    // TODO fix to not depend on order or running
    @Test
    public void testGetCustomerOrders() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertTrue(order.getCustomerorderId() > 0);
        assertEquals(1, customerOrderService.findCustomerOrders(customer, null).size());
    }

    @Test
    public void testPersistReassembledOrder1() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertEquals(3, order.getDelivery().size());
    }

    // TODO fix to not depend on order or running
    @Test
    public void testPersistReassembledOrder2() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart();
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);
        assertEquals(2, order.getDelivery().size());
    }

    // TODO fix to not depend on order or running
    @Test
    public void testPersistReassembledOrder3() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        ShoppingCart shoppingCart = getShoppingCart2(getTestName());
        assertFalse(customerOrderService.isOrderCanHasMultipleDeliveries(shoppingCart));
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, true);
        assertEquals(2, order.getDelivery().size());
    }

    /**
     * @return cart with one digital available product.
     */
    protected ShoppingCart getShoppingCart2(final String prefix) {
        ShoppingCart shoppingCart = getEmptyCart(prefix);
        // this digital product available
        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST9");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");
        new SetSkuQuantityToCartEventCommandImpl(ctx(), param)
                .execute(shoppingCart);
        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST5");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "200.00");
        new SetSkuQuantityToCartEventCommandImpl(ctx(), param)
                .execute(shoppingCart);
        return shoppingCart;
    }
}
