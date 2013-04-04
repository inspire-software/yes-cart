/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestAddressServiceImpl extends BaseCoreDBTestCase {

    private AddressService addressService;
    private CustomerService customerService;
    private ShopService shopService;

    @Before
    public void setUp() throws Exception {
        addressService = (AddressService) ctx().getBean(ServiceSpringKeys.ADDRESS_SERVICE);
        customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
    }

    @Test
    public void testGetAddressesByCustomerId() {
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail("bender@domain.com");
        customer.setFirstname("Bender");
        customer.setLastname("Rodriguez");
        customer.setPassword("rawpassword");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);
        Address address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("Bender");
        address.setLastname("Rodriguez");
        address.setCity("LA");
        address.setAddrline1("line1");
        address.setCountryCode("US");
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setCustomer(customer);
        addressService.create(address);
        address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("Bender");
        address.setLastname("Rodriguez");
        address.setCity("New-Vasyki");
        address.setAddrline1("line0");
        address.setCountryCode("ZH");
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setCustomer(customer);
        addressService.create(address);
        customer = customerService.getById(customer.getCustomerId());
        assertEquals(2, customer.getAddress().size());
        assertEquals(2, addressService.getAddressesByCustomerId(customer.getCustomerId()).size());
        assertTrue(addressService.getAddressesByCustomerId(customer.getCustomerId(), Address.ADDR_TYPE_SHIPING).isEmpty());
        assertEquals(2, addressService.getAddressesByCustomerId(customer.getCustomerId(), Address.ADDR_TYPE_BILLING).size());
    }
}
