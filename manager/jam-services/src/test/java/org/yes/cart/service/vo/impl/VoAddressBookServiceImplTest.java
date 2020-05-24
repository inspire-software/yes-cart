/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.vo.VoAddress;
import org.yes.cart.domain.vo.VoAddressBook;
import org.yes.cart.service.vo.VoAddressBookService;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/09/2019
 * Time: 10:52
 */
public class VoAddressBookServiceImplTest extends BaseCoreDBTestCase {

    private VoAddressBookService voAddressBookService;

    @Before
    public void setUp() {
        voAddressBookService = (VoAddressBookService) ctx().getBean("voAddressBookService");
        super.setUp();
    }

    @Test
    public void testGetAddressBook() throws Exception {

        VoAddressBook addressBook = voAddressBookService.getAddressBook(10001L, 10L, "en");
        assertNotNull(addressBook);
        assertNotNull(addressBook.getAddresses());
        assertFalse(addressBook.getAddresses().isEmpty());
        assertNotNull(addressBook.getBillingCountries());
        assertFalse(addressBook.getBillingCountries().isEmpty());
        assertNotNull(addressBook.getShippingCountries());
        assertFalse(addressBook.getShippingCountries().isEmpty());
        assertNotNull(addressBook.getBillingCountries().get(0).getSubLocations());
        assertFalse(addressBook.getBillingCountries().get(0).getSubLocations().isEmpty());

    }

    @Test
    public void testAddressCRUD() throws Exception {

        final VoAddress address = new VoAddress();
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setName("TEST CRUD");
        address.setCustomerId(10001L);
        address.setFirstname("John");
        address.setLastname("Doe");
        address.setCountryCode("GB");
        address.setAddrline1("Line 1");
        address.setCity("London");

        final VoAddress created = voAddressBookService.createAddress(address);
        assertTrue(created.getAddressId() > 0L);

        VoAddressBook addressBookAfterCreate = voAddressBookService.getAddressBook(10001L, 10L, "en");
        assertTrue(addressBookAfterCreate.getAddresses().stream().anyMatch(addr -> addr.getAddressId() == created.getAddressId()));

        created.setName("TEST CRUD UPDATE");
        final VoAddress updated = voAddressBookService.updateAddress(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        voAddressBookService.removeAddress(updated.getAddressId());

        VoAddressBook addressBookAfterRemove = voAddressBookService.getAddressBook(10001L, 10L, "en");
        assertFalse(addressBookAfterRemove.getAddresses().stream().anyMatch(addr -> addr.getAddressId() == created.getAddressId()));

    }
}