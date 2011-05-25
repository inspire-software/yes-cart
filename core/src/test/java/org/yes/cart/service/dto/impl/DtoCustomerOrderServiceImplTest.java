package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.domain.impl.CustomerOrderServiceImplTest;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerOrderServiceImplTest   extends BaseCoreDBTestCase {

    private DtoCustomerOrderService dtoService = null;
    private DtoFactory dtoFactory = null;
    private CustomerOrderService customerOrderService = null;
    

    @Before
    public void setUp()  throws Exception {
        super.setUp();
        dtoService = (DtoCustomerOrderService) ctx.getBean(ServiceSpringKeys.DTO_CUSTOMER_ORDER_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
        customerOrderService = (CustomerOrderService) ctx.getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
    }

    @After
    public void tearDown() {
        dtoService = null;
        dtoFactory = null;
        customerOrderService = null;
        super.tearDown();
    }



    @Test
    public void testCreate() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        try {
            dtoService.create(null);
            assertTrue(false);
        } catch (UnableToCreateInstanceException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testUpdate()  throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final String customerPrefix = "testUpdate";

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx, customerPrefix);
        assertFalse(customer.getAddress().isEmpty());


        final ShoppingCart shoppingCart =  CustomerOrderServiceImplTest.getShoppingCart(customerPrefix);
        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        long pk = order.getCustomerorderId();
        assertTrue(pk > 0);


        CustomerOrderDTO dto = dtoService.getById(pk);
        assertNotNull(dto);

    }


}
