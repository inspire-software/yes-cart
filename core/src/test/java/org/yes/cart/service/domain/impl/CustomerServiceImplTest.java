package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerServiceImplTest extends BaseCoreDBTestCase {

    private CustomerService customerService;
    private ShopService shopService;

    @Before
    public void setUp() throws Exception {
        customerService = (CustomerService) ctx.getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
    }

    @Test
    public void testCreate() {
        Customer customer = getCustomer("testCreate");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);
        assertFalse(customer.getShops().isEmpty());
    }

    @Test
    public void testUpdate() {
        Customer customer = getCustomer("testUpdate");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);
        customer.setFirstname("Gordon");
        customer.setLastname("Freeman");
        customer.setPassword("rawpassword");
        customer = customerService.update(customer);
        assertEquals("Gordon", customer.getFirstname());
        assertEquals("Freeman", customer.getLastname());
    }

    @Test
    public void testDelete() {
        Customer customer = getCustomer("testDelete");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);
        long pk = customer.getCustomerId();
        customerService.delete(customer);
        customer = customerService.getById(pk);
        assertNull(customer);
    }

    //TODO refactor to param test
    @Test
    public void testFindCustomer() {
        Customer customer = getCustomer("");
        customer.setEmail("user1@somedomain.com");
        customer.setFirstname("SomeFirsname");
        customer.setLastname("user1LastName");
        customer.setPassword("rawpassword");
        customerService.create(customer, shopService.getById(10L));
        customer = getCustomer("testFindCustomer2");
        customer.setFirstname("SomeFirsname");
        customer.setLastname("Akintola");
        customer.setPassword("rawpassword");
        customer.setEmail("user2@somedomain.com");
        customerService.create(customer, shopService.getById(10L));
        List<Customer> list = customerService.findCustomer("", null, null, null);
        assertNotNull(list);
        list = customerService.findCustomer("user2", null, null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer("omedomain", null, null, null);
        assertEquals(2, list.size());
        list = customerService.findCustomer(null, "SomeFirsname", null, null);
        assertEquals(2, list.size());
        list = customerService.findCustomer(null, null, "user1LastName", null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, "kintola", null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, "SomeFirsname", null, null);
        assertEquals(2, list.size());
        list = customerService.findCustomer(null, "SomeFirsname", "Akintola", null);
        assertEquals(1, list.size());
    }

    private Customer getCustomer(final String prefix) {
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail(prefix + "customer@shopdomain.com");
        customer.setFirstname(prefix + "Firsname");
        customer.setLastname(prefix + "Lastname");
        customer.setPassword("rawpassword");
        return customer;
    }
}
