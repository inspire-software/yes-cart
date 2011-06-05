package org.yes.cart.service.domain.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.ShopTopSellerService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/5/11
 * Time: 10:34 AM
 */
public class ShopTopSellerServiceImplTest extends BaseCoreDBTestCase {

    private ShopTopSellerService shopTopSellerService;
    private OrderAssembler orderAssembler;
    private GenericDAO<CustomerOrder, Long> customerOrderDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        shopTopSellerService = (ShopTopSellerService) ctx.getBean(ServiceSpringKeys.SHOP_TOP_SELLER_SERVICE);
        orderAssembler = (OrderAssembler)  ctx.getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderDao = (GenericDAO<CustomerOrder, Long>) ctx.getBean("customerOrderDao");

    }

    @After
    public void tearDown() {
        shopTopSellerService = null;
        orderAssembler = null;
        customerOrderDao = null;
        super.tearDown();
    }

    @Test
    public void testUpdateTopSellers() throws Exception {

        Customer customer = OrderAssemblerImplTest.createCustomer(ctx, "testTopSellers");

        ShoppingCart shoppingCart = OrderAssemblerImplTest.getShoppingCart(ctx, customer.getEmail());

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);

        customerOrder = customerOrderDao.create(customerOrder);

        shopTopSellerService.updateTopSellers(10);

        dumpDataBase("topsell" , new String[] {"TCUSTOMERORDER" , "TCUSTOMERORDERDET" });

        assertTrue(true);

    }
}
