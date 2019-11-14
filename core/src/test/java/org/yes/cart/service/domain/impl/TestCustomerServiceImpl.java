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
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.utils.DateUtils;

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

        final Map<String, List> filterNone = null;

        count = customerService.findCustomerCount(shopAll, filterNone);
        assertTrue(count > 0);
        list = customerService.findCustomers(0, 1, "firstname", false, shopAll, filterNone);
        assertFalse(list.isEmpty());


        final Map<String, List> filterAny = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterAny);
        filterAny.put("email", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("firstname", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("lastname", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("companyName1", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("companyName2", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("tag", Collections.singletonList("PaginatedFirsname"));

        count = customerService.findCustomerCount(shopAll, filterAny);
        assertEquals(2, count);
        list = customerService.findCustomers(0, 1, "firstname", false, shopAll, filterAny);
        assertEquals(1, list.size());
        list = customerService.findCustomers(1, 1, "firstname", false, shopAll, filterAny);
        assertEquals(1, list.size());


        final Map<String, List> filterSpecific = new HashMap<>();
        filterSpecific.put("email", Collections.singletonList("user4@findpaginateddomain.com"));
        filterSpecific.put("firstname", Collections.singletonList("PaginatedFirsname"));

        count = customerService.findCustomerCount(shopAll, filterSpecific);
        assertEquals(1, count);
        list = customerService.findCustomers(0, 1, "firstname", false, shopAll, filterSpecific);
        assertEquals(1, list.size());

        final Map<String, List> filterNoMatch = Collections.singletonMap("firstname", Collections.singletonList("ZZZZZZZ"));

        count = customerService.findCustomerCount(shopAll, filterNoMatch);
        assertEquals(0, count);
        list = customerService.findCustomers(0, 1, "firstname", false, shopAll, filterNoMatch);
        assertEquals(0, list.size());

        final Map<String, List> filterIncludeDisabled = Collections.singletonMap("disabled", Collections.singletonList(SearchContext.MatchMode.ANY));

        count = customerService.findCustomerCount(shop10, filterIncludeDisabled);
        assertTrue(count > 0);
        list = customerService.findCustomers(0, 1, "firstname", false, shop10, filterIncludeDisabled);
        assertFalse(list.isEmpty());

        final Map<String, List> filterOr = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterOr);
        filterOr.put("email", Collections.singletonList("user3@findpaginateddomain.com"));
        filterOr.put("lastname", Collections.singletonList("user4LastName"));

        count = customerService.findCustomerCount(shop10, filterOr);
        assertEquals(2, count);
        list = customerService.findCustomers(0, 2, "firstname", false, shop10, filterOr);
        assertEquals(2, list.size());

        final Map<String, List> filterCreated = new HashMap<>();
        filterCreated.put("email", Collections.singletonList("user3@findpaginateddomain.com"));
        filterCreated.put("createdTimestamp", Arrays.asList(
                SearchContext.MatchMode.GE.toParam(DateUtils.iParseSDT("2019-01-01")),
                SearchContext.MatchMode.LT.toParam(DateUtils.iParseSDT("2099-01-01"))
            )
        );

        count = customerService.findCustomerCount(shop10, filterCreated);
        assertEquals(1, count);
        list = customerService.findCustomers(0, 2, "firstname", false, shop10, filterCreated);
        assertEquals(1, list.size());

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
