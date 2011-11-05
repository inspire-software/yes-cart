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
import org.yes.cart.service.domain.impl.CustomerOrderServiceImplTest;
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

    // TODO fix to not depend on order or running
    @Test
    public void testUpdate() throws Exception {
        final String customerPrefix = "testUpdate";
        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());

        final ShoppingCart shoppingCart = CustomerOrderServiceImplTest.getShoppingCart(ctx, customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);

        CustomerOrderDTO dto = dtoService.getById(pk);
        assertNotNull(dto);
    }
}
