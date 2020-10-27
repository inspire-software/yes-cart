/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.misc.Pair;
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
public class CustomerServiceImplTest extends BaseCoreDBTestCase {

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
        Customer customer10check = customerService.getCustomerByLogin(customer10.getLogin(), shopService.getById(10L));
        assertNotNull(customer10check);
        assertEquals(customer10check.getCustomerId(), customer10.getCustomerId());

        // Same details but for SHOP20
        Customer customer20 = getCustomer(getTestName());
        customer20 = customerService.create(customer20, shopService.getById(20L));
        assertTrue(customer20.getCustomerId() > 0);
        assertFalse(customer20.getShops().isEmpty());
        assertEquals(customer10.getEmail(), customer20.getEmail());
        assertFalse(customer10.getCustomerId() == customer20.getCustomerId());
        Customer customer20check = customerService.getCustomerByLogin(customer20.getLogin(), shopService.getById(20L));
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

        final List<Long> shop10 = Collections.singletonList(10L);
        final List<Pair<String, List>> blacklist = Collections.singletonList(new Pair<>("email", Collections.singletonList("@findpaginateddomain.com")));


        List<Customer> list;
        int count;

        final Map<String, List> filterNone = null;

        count = customerService.findCustomerCount(filterNone);
        assertTrue(count > 0);
        list = customerService.findCustomers(0, 1, "firstname", false, filterNone);
        assertFalse(list.isEmpty());


        final Map<String, List> filterAny = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterAny);
        filterAny.put("email", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("firstname", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("lastname", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("companyName1", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("companyName2", Collections.singletonList("PaginatedFirsname"));
        filterAny.put("tag", Collections.singletonList("PaginatedFirsname"));

        count = customerService.findCustomerCount(filterAny);
        assertEquals(2, count);
        list = customerService.findCustomers(0, 1, "firstname", false, filterAny);
        assertEquals(1, list.size());
        list = customerService.findCustomers(1, 1, "firstname", false, filterAny);
        assertEquals(1, list.size());

        final Map<String, List> filterAnyWithBlacklist = new HashMap<>(filterAny);
        filterAnyWithBlacklist.put("blacklist", blacklist);
        count = customerService.findCustomerCount(filterAnyWithBlacklist);
        assertEquals(0, count);
        list = customerService.findCustomers(0, 1, "firstname", false, filterAnyWithBlacklist);
        assertEquals(0, list.size());


        final Map<String, List> filterSpecific = new HashMap<>();
        filterSpecific.put("email", Collections.singletonList("user4@findpaginateddomain.com"));
        filterSpecific.put("firstname", Collections.singletonList("PaginatedFirsname"));

        count = customerService.findCustomerCount(filterSpecific);
        assertEquals(1, count);
        list = customerService.findCustomers(0, 1, "firstname", false, filterSpecific);
        assertEquals(1, list.size());

        final Map<String, List> filterNoMatch = Collections.singletonMap("firstname", Collections.singletonList("ZZZZZZZ"));

        count = customerService.findCustomerCount(filterNoMatch);
        assertEquals(0, count);
        list = customerService.findCustomers(0, 1, "firstname", false, filterNoMatch);
        assertEquals(0, list.size());

        final Map<String, List> filterIncludeDisabled = new HashMap<>();
        filterIncludeDisabled.put("disabled", Collections.singletonList(SearchContext.MatchMode.ANY));
        filterIncludeDisabled.put("shopIds", shop10);

        count = customerService.findCustomerCount(filterIncludeDisabled);
        assertTrue(count > 0);
        list = customerService.findCustomers(0, 1, "firstname", false, filterIncludeDisabled);
        assertFalse(list.isEmpty());

        final Map<String, List> filterOr = new HashMap<>();
        SearchContext.JoinMode.OR.setMode(filterOr);
        filterOr.put("email", Collections.singletonList("user3@findpaginateddomain.com"));
        filterOr.put("lastname", Collections.singletonList("user4LastName"));
        filterOr.put("shopIds", shop10);

        count = customerService.findCustomerCount(filterOr);
        assertEquals(2, count);
        list = customerService.findCustomers(0, 2, "firstname", false, filterOr);
        assertEquals(2, list.size());

        final Map<String, List> filterCreated = new HashMap<>();
        filterCreated.put("email", Collections.singletonList("user3@findpaginateddomain.com"));
        filterCreated.put("createdTimestamp", Arrays.asList(
                SearchContext.MatchMode.GE.toParam(DateUtils.iParseSDT("2019-01-01")),
                SearchContext.MatchMode.LT.toParam(DateUtils.iParseSDT("2099-01-01"))
            )
        );
        filterCreated.put("shopIds", shop10);

        count = customerService.findCustomerCount(filterCreated);
        assertEquals(1, count);
        list = customerService.findCustomers(0, 2, "firstname", false, filterCreated);
        assertEquals(1, list.size());

    }

    @Test
    public void testIsPasswordValid() throws Exception {

        final Shop shop = shopService.getById(10L);
        final Shop subShop = shopService.getById(1010L);

        Customer customer = getCustomer(getTestName() + "_1");
        customer.setEmail("user1@passvalid.com");

        assertFalse(customerService.isCustomerExists(customer.getLogin(), shop, true));
        assertFalse(customerService.isPasswordValid(customer.getLogin(), shop, "rawpassword"));

        customerService.create(customer, shop);

        assertTrue(customerService.isCustomerExists(customer.getLogin(), shop, true));
        assertTrue(customerService.isPasswordValid(customer.getLogin(), shop, "rawpassword"));

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

        assertTrue(customerService.isCustomerExists(customer.getLogin(), shop, true));
        assertFalse("Cannot determine shop with multiple assignments",
                customerService.isPasswordValid(customer.getLogin(), shop, "rawpassword"));

    }

    private Customer getCustomer(String prefix) {
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setLogin(prefix + "customer");
        customer.setEmail(prefix + "customer@shopdomain.com");
        customer.setFirstname(prefix + "Firsname");
        customer.setLastname(prefix + "Lastname");
        customer.setPassword("rawpassword");
        return customer;
    }
}
