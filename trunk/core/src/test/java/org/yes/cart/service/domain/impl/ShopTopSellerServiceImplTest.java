package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.ShopTopSeller;
import org.yes.cart.service.domain.ShopTopSellerService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/5/11
 * Time: 10:34 AM
 */
public class ShopTopSellerServiceImplTest extends BaseCoreDBTestCase {

    private ShopTopSellerService shopTopSellerService;
    private OrderAssembler orderAssembler;
    private GenericDAO<CustomerOrder, Long> customerOrderDao;
    private GenericDAO<ShopTopSeller, Long> shopTopSellerDao;

    @Before
    public void setUp() throws Exception {
        shopTopSellerService = (ShopTopSellerService) ctx.getBean(ServiceSpringKeys.SHOP_TOP_SELLER_SERVICE);
        orderAssembler = (OrderAssembler) ctx.getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderDao = (GenericDAO<CustomerOrder, Long>) ctx.getBean(DaoServiceBeanKeys.CUSTOMER_ORDER_DAO);
        shopTopSellerDao = (GenericDAO<ShopTopSeller, Long>) ctx.getBean(DaoServiceBeanKeys.SHOP_TOP_SELLLER_DAO);
    }

    @Test
    public void testUpdateTopSellers() throws Exception {

        Map<Long, BigDecimal> expectation = new HashMap<Long, BigDecimal>();
        expectation.put(15120L, new BigDecimal("2"));
        expectation.put(15122L, new BigDecimal("4"));
        expectation.put(15123L, new BigDecimal("2"));
        expectation.put(15125L, new BigDecimal("400"));
        expectation.put(15126L, new BigDecimal("6"));
        expectation.put(15127L, new BigDecimal("2"));
        expectation.put(15128L, new BigDecimal("2"));
        expectation.put(15129L, new BigDecimal("2"));
        expectation.put(10L, new BigDecimal("34"));

        Customer customer = OrderAssemblerImplTest.createCustomer(ctx, "testTopSellers");
        ShoppingCart shoppingCart = OrderAssemblerImplTest.getShoppingCart2(ctx, customer.getEmail());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = customerOrderDao.create(customerOrder);

        Customer customer2 = OrderAssemblerImplTest.createCustomer(ctx, "testTopSellers2");
        ShoppingCart shoppingCart2 = OrderAssemblerImplTest.getShoppingCart2(ctx, customer2.getEmail());
        CustomerOrder customerOrder2 = orderAssembler.assembleCustomerOrder(shoppingCart2);
        customerOrder = customerOrderDao.create(customerOrder2);

        shopTopSellerService.updateTopSellers(10);

        List<ShopTopSeller> allTopSellers = shopTopSellerDao.findAll();
        System.out.println(allTopSellers.size());
        for (ShopTopSeller ts : allTopSellers) {
            Long key = ts.getProduct().getId();
            assertEquals(expectation.remove(key), ts.getCounter());
            expectation.remove(key);
        }
        //dumpDataBase("topsell", new String[]{"TCUSTOMERORDER", "TCUSTOMERORDERDET", "TSHOPTOPSELLER"});
        assertTrue("Expectation must be empty but has " + expectation.size(), expectation.isEmpty());
    }
}
