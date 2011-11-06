package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.impl.TestCustomerOrderServiceImpl;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.shoppingcart.ShoppingCart;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerOrderServiceImplTest extends BaseCoreDBTestCase {

    private DtoCustomerOrderService dtoService;
    private CustomerOrderService customerOrderService;

    @Before
    public void setUp() throws Exception {
        dtoService = (DtoCustomerOrderService) ctx.getBean(ServiceSpringKeys.DTO_CUSTOMER_ORDER_SERVICE);
        customerOrderService = (CustomerOrderService) ctx.getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
    }

    @Test(expected = UnableToCreateInstanceException.class)
    public void testCreate() throws Exception {
        dtoService.create(null);
    }

    @Test
    public void testUpdate() throws Exception {
        String customerPrefix = "testUpdate";
        Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());
        final ShoppingCart shoppingCart = TestCustomerOrderServiceImpl.getShoppingCart(ctx, customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertNotNull(dtoService.getById(pk));
    }
}
