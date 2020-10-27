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

package org.yes.cart.shoppingcart.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ManagerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.CustomerResolver;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 27/01/2020
 * Time: 17:47
 */
public class CustomerResolverDefaultImplTest extends BaseCoreDBTestCase {

    private ShopService shopService;
    private CustomerResolver customerResolver;
    private ManagerService managerService;
    private CustomerService customerService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        this.shopService = (ShopService) ctx().getBean("shopService");
        this.customerResolver = (CustomerResolver) ctx().getBean("customerResolver");
        this.managerService = (ManagerService) ctx().getBean("managerService");
        this.customerService = (CustomerService) ctx().getBean("customerService");
    }

    @Test
    public void testCustomer() throws Exception {

        final Shop shop = this.shopService.getShopByCode("SHOIP1");

        assertTrue(this.customerResolver.isManagerLoginEnabled(shop));

        final Customer customer = createCustomer();

        final Customer regTest = this.customerResolver.getCustomerByLogin(customer.getLogin(), shop);
        assertNotNull(regTest);
        assertEquals(customer.getGuid(), regTest.getGuid());
        assertTrue(this.customerResolver.authenticate(customer.getLogin(), shop, "rawpassword"));

        getTx().execute(transactionStatus -> {
            this.customerService.updateDeactivate(this.customerService.findById(customer.getCustomerId()), shop, true);
            return null;
        });

        assertNull(this.customerResolver.getCustomerByLogin(customer.getLogin(), shop));
        assertFalse(this.customerResolver.authenticate(customer.getLogin(), shop, "rawpassword"));

    }


    @Test
    public void testManager() throws Exception {

        final Shop shop = this.shopService.getShopByCode("SHOIP1");

        assertTrue(this.customerResolver.isManagerLoginEnabled(shop));

        final String login = UUID.randomUUID().toString();
        final Manager sfManager = createManagerWithSfAccess(shop, login, login + "@manager.com");

        final Customer regTest = this.customerResolver.getCustomerByLogin(sfManager.getLogin(), shop);
        assertNotNull(regTest);
        assertEquals(sfManager.getGuid(), regTest.getGuid());
        assertTrue(this.customerResolver.authenticate(sfManager.getLogin(), shop, "rawpassword"));
        
    }


    @Test
    public void testCustomerAndManagerSameLogin() throws Exception {

        final Shop shop = this.shopService.getShopByCode("SHOIP1");

        assertTrue(this.customerResolver.isManagerLoginEnabled(shop));

        final Customer customer = createCustomer();

        getTx().execute(transactionStatus -> {
            this.customerService.updateDeactivate(this.customerService.findById(customer.getCustomerId()), shop, true);
            return null;
        });

        assertNull(this.customerResolver.getCustomerByLogin(customer.getLogin(), shop));
        assertFalse(this.customerResolver.authenticate(customer.getLogin(), shop, "rawpassword"));

        final Manager sfManager = createManagerWithSfAccess(shop, customer.getLogin(), customer.getEmail());

        // evict cache
        this.customerService.update(this.customerService.findById(customer.getCustomerId()));

        final Customer regTest = this.customerResolver.getCustomerByLogin(sfManager.getLogin(), shop);
        assertNotNull(regTest);
        assertEquals(sfManager.getGuid(), regTest.getGuid());
        assertTrue(this.customerResolver.authenticate(sfManager.getLogin(), shop, "rawpassword"));

        getTx().execute(transactionStatus -> {
            this.customerService.updateActivate(this.customerService.findById(customer.getCustomerId()), shop, false);
            return null;
        });

        // evict cache
        this.customerService.update(this.customerService.findById(customer.getCustomerId()));

        final Customer regTestActive = this.customerResolver.getCustomerByLogin(customer.getLogin(), shop);
        assertNotNull(regTestActive);
        assertEquals(customer.getGuid(), regTestActive.getGuid());
        assertTrue(this.customerResolver.authenticate(customer.getLogin(), shop, "rawpassword"));


    }



    private Manager createManagerWithSfAccess(final Shop shop, final String login, final String email) throws Exception {

        final Manager manager = managerService.getGenericDao().getEntityFactory().getByIface(Manager.class);
        manager.setLogin(login);
        manager.setEmail(email);
        manager.setPassword("rawpassword");
        manager.setFirstname("John");
        manager.setLastname("Doe");
        manager.setEnabled(true);

        return managerService.create(
                manager,
                shop,
                "ROLE_SMCALLCENTERLOGINSF", "ROLE_SMCALLCENTERLOGINONBEHALF", "ROLE_SMCALLCENTERCREATEMANAGEDLISTS"
        );

    }


}