package org.yes.cart.service.domain.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerServiceImplTest extends BaseCoreDBTestCase {

    private CustomerService customerService;
    private ShopService shopService;

    @Before
    public void setUp()  throws Exception {
        super.setUp(new String [] {"testApplicationContext.xml" , "core-aspects.xml" });
        customerService = (CustomerService) ctx.getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        shopService  = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
    }

    @After
    public void tearDown() {
        customerService = null;
        shopService  = null;
        super.tearDown();
    }

    @Test
    public void testCreate() {
        Customer customer = getCustomer();
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue (customer.getCustomerId() > 0);
        assertFalse(customer.getShops().isEmpty());
    }

    @Test
    public void testUpdate() {
        Customer customer = getCustomer();
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue (customer.getCustomerId() > 0);
        customer.setFirstname("Gordon");
        customer.setLastname("Freeman");
        customer = customerService.update(customer);
        assertEquals("Gordon", customer.getFirstname());
        assertEquals("Freeman", customer.getLastname());
    }

    @Test
    public void testDelete() {
        Customer customer = getCustomer();
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue (customer.getCustomerId() > 0);
        long pk = customer.getCustomerId();
        customerService.delete(customer);
        customer = customerService.getById(pk);
        assertNull(customer);
    }

    @Test
    public void testResetPasswd() {
        Customer customer = getCustomer();
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue (customer.getCustomerId() > 0);
        String oldHash = customer.getPassword();
        assertNotNull("pwd can not be null" , oldHash);
        assertTrue("pwd can not be empty" , oldHash.length() > 0);
        
        long pk = customer.getCustomerId();

        customerService.resetPassword(customer, shopService.getById(10L));
        Customer customerWithUpdatedPwd = customerService.getById(pk);
        String newHash = customer.getPassword();
        assertNotNull("pwd can not be null" , newHash);
        assertTrue("pwd can not be empty" , newHash.length() > 0);

        assertEquals(customer.getCustomerId(), customerWithUpdatedPwd.getCustomerId());
        assertEquals(customer.getFirstname(), customerWithUpdatedPwd.getFirstname());
        assertEquals(customer.getLastname(), customerWithUpdatedPwd.getLastname());
        
        assertTrue(!newHash.equals(oldHash));
        
    }

    @Test
    public void testFindCustomer() {

        dumpDataBase("cust_addr" , new String [] {"TADDRESS"});

        Customer customer = getCustomer();
        customerService.create(customer, shopService.getById(10L));
        customer = getCustomer();
        customer.setFirstname("Firsname");
        customer.setLastname("Akintola");
        customer.setEmail("akintola@shopdomain.com");
        customerService.create(customer, shopService.getById(10L));

        List<Customer> list = customerService.findCustomer("", null, null, null);
        assertNotNull(list);

        list = customerService.findCustomer("ola", null, null, null);
        assertEquals(1, list.size());

        list = customerService.findCustomer("opdomain", null, null, null);
        assertEquals(2, list.size());

        list = customerService.findCustomer(null, "Firsname", null, null);
        assertEquals(2, list.size());

        list = customerService.findCustomer(null, null, "Lastname", null);
        assertEquals(1, list.size());

        list = customerService.findCustomer(null, null, "name", null);
        assertEquals(1, list.size());

        list = customerService.findCustomer(null, "name", null , null);
        assertEquals(2, list.size());

        list = customerService.findCustomer(null, "Firsname", "Lastname", null);
        assertEquals(1, list.size());


    }



    private Customer getCustomer() {
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail("customer@shopdomain.com");
        customer.setFirstname("Firsname");
        customer.setLastname("Lastname");
        return customer;
    }


}
