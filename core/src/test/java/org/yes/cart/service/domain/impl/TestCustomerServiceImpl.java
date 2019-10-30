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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCustomerServiceImpl extends BaseCoreDBTestCase {

    private CustomerService customerService;
    private ShopService shopService;

    @Override
    @Before
    public void setUp() {
        customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        super.setUp();
    }

    @Test
    public void testCreate() {

        Customer customer10 = getCustomer(getTestName());
        customer10 = customerService.create(customer10, shopService.getById(10L));
        assertTrue(customer10.getCustomerId() > 0);
        assertFalse(customer10.getShops().isEmpty());
        Customer customer10check = customerService.getCustomerByEmail(customer10.getEmail(), shopService.getById(10L));
        assertNotNull(customer10check);
        assertEquals(customer10check.getCustomerId(), customer10.getCustomerId());

        // Same details but for SHOP20
        Customer customer20 = getCustomer(getTestName());
        customer20 = customerService.create(customer20, shopService.getById(20L));
        assertTrue(customer20.getCustomerId() > 0);
        assertFalse(customer20.getShops().isEmpty());
        assertEquals(customer10.getEmail(), customer20.getEmail());
        assertFalse(customer10.getCustomerId() == customer20.getCustomerId());
        Customer customer20check = customerService.getCustomerByEmail(customer20.getEmail(), shopService.getById(20L));
        assertNotNull(customer20check);
        assertEquals(customer20check.getCustomerId(), customer20.getCustomerId());
    }

    @Test
    public void testUpdate() {
        Customer customer = getCustomer(getTestName());
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
        Customer customer = getCustomer(getTestName());
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);
        long pk = customer.getCustomerId();
        customerService.delete(customer);
        customer = customerService.findById(pk);
        assertNull(customer);
    }

    @Test
    public void testFindCustomer() {
        Customer customer = getCustomer(getTestName());
        customer.setEmail("user1@finddomain.com");
        customer.setFirstname("SomeFirsname");
        customer.setLastname("user1LastName");
        customer.setPassword("rawpassword");
        customerService.create(customer, shopService.getById(10L));
        customer = getCustomer(getTestName() + "2");
        customer.setFirstname("SomeFirsname");
        customer.setLastname("Akintola");
        customer.setPassword("rawpassword");
        customer.setEmail("user2@finddomain.com");
        customer.setTag("tag1 tag2 tag3");
        customer.setCustomerType("B2B");
        customer.setPricingPolicy("P1 P2");
        customerService.create(customer, shopService.getById(10L));
        List<Customer> list = customerService.findCustomer(getTestName(), null, null, null, null, null, null);
        assertNotNull(list);
        list = customerService.findCustomer("user2", null, null, null, null, null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer("finddomain", null, null, null, null, null, null);
        assertEquals(2, list.size());
        list = customerService.findCustomer(null, "SomeFirsname", null, null, null, null, null);
        assertEquals(2, list.size());
        list = customerService.findCustomer(null, null, "user1LastName", null, null, null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, "kintola", null, null, null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, "SomeFirsname", null, null, null, null, null);
        assertEquals(2, list.size());
        list = customerService.findCustomer(null, "SomeFirsname", "Akintola", null, null, null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, null, null, "tag1", null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, null, null, "tag2", null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, null, null, "tag3", null, null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, null, null, null, "B2B", null);
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, null, null, null, null, "P1");
        assertEquals(1, list.size());
        list = customerService.findCustomer(null, null, null, null, null, null, "P2");
        assertEquals(1, list.size());
    }

    @Test
    public void testFindCustomerPaginated() {
        Customer customer = getCustomer(getTestName() + "3");
        customer.setEmail("user3@findpaginateddomain.com");
        customer.setFirstname("PaginatedFirsname");
        customer.setLastname("user3LastName");
        customer.setPassword("rawpassword");
        customerService.create(customer, shopService.getById(10L));
        customer = getCustomer(getTestName() + "4");
        customer.setFirstname("PaginatedFirsname");
        customer.setLastname("user4LastName");
        customer.setPassword("rawpassword");
        customer.setEmail("user4@findpaginateddomain.com");
        customerService.create(customer, shopService.getById(10L));

        final Set<Long> shopAll = null;
        final Set<Long> shop10 = Collections.singleton(10L);


        List<Customer> list;
        int count;

        final Map<String, Object> filterNone = null;

        count = customerService.findCustomerCount(shopAll, filterNone);
        assertTrue(count > 0);
        list = customerService.findCustomer(0, 1, "firstname", false, shopAll, filterNone);
        assertFalse(list.isEmpty());

        count = customerService.findCustomerCount(shop10, filterNone);
        assertTrue(count > 0);
        list = customerService.findCustomer(0, 1, "firstname", false, shop10, filterNone);
        assertFalse(list.isEmpty());


        final Map<String, Object> filterAny = Collections.singletonMap("any", "PaginatedFirsname");

        count = customerService.findCustomerCount(shopAll, filterAny);
        assertEquals(2, count);
        list = customerService.findCustomer(0, 1, "firstname", false, shopAll, filterAny);
        assertEquals(1, list.size());
        list = customerService.findCustomer(1, 1, "firstname", false, shopAll, filterAny);
        assertEquals(1, list.size());

        count = customerService.findCustomerCount(shop10, filterAny);
        assertEquals(2, count);
        list = customerService.findCustomer(0, 1, "firstname", false, shop10, filterAny);
        assertEquals(1, list.size());
        list = customerService.findCustomer(1, 1, "firstname", false, shop10, filterAny);
        assertEquals(1, list.size());


        final Map<String, Object> filterSpecific = new HashMap<>();
        filterSpecific.put("email", "user4@findpaginateddomain.com");
        filterSpecific.put("firstname", "PaginatedFirsname");

        count = customerService.findCustomerCount(shopAll, filterSpecific);
        assertEquals(1, count);
        list = customerService.findCustomer(0, 1, "firstname", false, shopAll, filterSpecific);
        assertEquals(1, list.size());

        count = customerService.findCustomerCount(shop10, filterSpecific);
        assertEquals(1, count);
        list = customerService.findCustomer(0, 1, "firstname", false, shop10, filterSpecific);
        assertEquals(1, list.size());

        final Map<String, Object> filterNoMatch = Collections.singletonMap("any", "ZZZZZZZ");

        count = customerService.findCustomerCount(shopAll, filterNoMatch);
        assertEquals(0, count);
        list = customerService.findCustomer(0, 1, "firstname", false, shopAll, filterNoMatch);
        assertEquals(0, list.size());

        count = customerService.findCustomerCount(shop10, filterNoMatch);
        assertEquals(0, count);
        list = customerService.findCustomer(0, 1, "firstname", false, shop10, filterNoMatch);
        assertEquals(0, list.size());

        final Map<String, Object> filterIncludeDisabled = Collections.singletonMap("disabled", "*");

        count = customerService.findCustomerCount(shop10, filterIncludeDisabled);
        assertTrue(count > 0);
        list = customerService.findCustomer(0, 1, "firstname", false, shop10, filterIncludeDisabled);
        assertFalse(list.isEmpty());

    }

    @Test
    public void testIsPasswordValid() throws Exception {

        final Shop shop = shopService.getById(10L);
        final Shop subShop = shopService.getById(1010L);

        Customer customer = getCustomer(getTestName() + "_1");
        customer.setEmail("user1@passvalid.com");

        assertFalse(customerService.isCustomerExists(customer.getEmail(), shop));
        assertFalse(customerService.isPasswordValid(customer.getEmail(), shop, "rawpassword"));

        customerService.create(customer, shop);

        assertTrue(customerService.isCustomerExists(customer.getEmail(), shop));
        assertTrue(customerService.isPasswordValid(customer.getEmail(), shop, "rawpassword"));

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                final Customer cust = customerService.findById(customer.getId());
                final CustomerShop customerShop = customerService.getGenericDao().getEntityFactory().getByIface(CustomerShop.class);
                customerShop.setShop(subShop);
                customerShop.setCustomer(cust);
                customerShop.setDisabled(false);
                cust.getShops().add(customerShop);
                customerService.update(cust);
            }
        });

        assertTrue(customerService.isCustomerExists(customer.getEmail(), shop));
        assertFalse("Cannot determine shop with multiple assignments",
                customerService.isPasswordValid(customer.getEmail(), shop, "rawpassword"));

    }

    private Customer getCustomer(String prefix) {
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail(prefix + "customer@shopdomain.com");
        customer.setFirstname(prefix + "Firsname");
        customer.setLastname(prefix + "Lastname");
        customer.setPassword("rawpassword");
        return customer;
    }
}
