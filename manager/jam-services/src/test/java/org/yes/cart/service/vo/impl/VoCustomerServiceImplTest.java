/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttrValueCustomer;
import org.yes.cart.domain.vo.VoCustomer;
import org.yes.cart.domain.vo.VoCustomerInfo;
import org.yes.cart.domain.vo.VoCustomerShopLink;
import org.yes.cart.service.vo.VoCustomerService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/09/2019
 * Time: 15:35
 */
public class VoCustomerServiceImplTest extends BaseCoreDBTestCase {

    private VoCustomerService voCustomerService;

    @Before
    public void setUp() {
        voCustomerService = (VoCustomerService) ctx().getBean("voCustomerService");
        super.setUp();
    }

    @Test
    public void testGetCustomers() throws Exception {

        List<VoCustomerInfo> customerNoFilter = voCustomerService.getFilteredCustomers(null, 10);
        assertNotNull(customerNoFilter);
        assertFalse(customerNoFilter.isEmpty());

        List<VoCustomerInfo> customerFind = voCustomerService.getFilteredCustomers("reg@test.com", 10);
        assertNotNull(customerFind);
        assertFalse(customerFind.isEmpty());
        assertEquals("reg@test.com", customerFind.get(0).getEmail());

        List<VoCustomerInfo> customerByEmailTagOrCompany = voCustomerService.getFilteredCustomers("#JJ", 10);
        assertNotNull(customerByEmailTagOrCompany);
        assertFalse(customerByEmailTagOrCompany.isEmpty());
        assertEquals("reg@test.com", customerByEmailTagOrCompany.get(0).getEmail());

        List<VoCustomerInfo> customerByName = voCustomerService.getFilteredCustomers("?John", 10);
        assertNotNull(customerByName);
        assertFalse(customerByName.isEmpty());
        assertEquals("reg@test.com", customerByName.get(0).getEmail());

        List<VoCustomerInfo> customerByAddress = voCustomerService.getFilteredCustomers("@NW1 6XE", 10);
        assertNotNull(customerByAddress);
        assertFalse(customerByAddress.isEmpty());
        assertEquals("reg@test.com", customerByAddress.get(0).getEmail());

        List<VoCustomerInfo> customerByTypeOrPolicy = voCustomerService.getFilteredCustomers("$TEST", 10);
        assertNotNull(customerByTypeOrPolicy);
        assertFalse(customerByTypeOrPolicy.isEmpty());
        assertEquals("reg@test.com", customerByTypeOrPolicy.get(0).getEmail());

        List<VoCustomerInfo> customerByDate = voCustomerService.getFilteredCustomers("2008-12<2009-01", 10);
        assertNotNull(customerByDate);
        assertFalse(customerByDate.isEmpty());
        assertEquals("reg@test.com", customerByDate.get(0).getEmail());

    }

    @Test
    public void testCustomerCRUD() throws Exception {

        final String email = UUID.randomUUID().toString() + "@test-crud.com";
        final VoCustomer customer = new VoCustomer();
        customer.setFirstname("TCFirst");
        customer.setLastname("TCLast");
        customer.setEmail(email);
        final VoCustomerShopLink shop = new VoCustomerShopLink();
        shop.setShopId(10L);
        customer.setCustomerShops(Collections.singletonList(shop));

        final VoCustomer created = voCustomerService.createCustomer(customer);
        assertTrue(created.getCustomerId() > 0L);

        final VoCustomer afterCreated = voCustomerService.getCustomerById(created.getCustomerId());
        assertNotNull(afterCreated);
        assertEquals(email, afterCreated.getEmail());

        created.setFirstname("TEST CRUD UPDATE");

        final VoCustomer updated = voCustomerService.updateCustomer(created);
        assertEquals("TEST CRUD UPDATE", updated.getFirstname());

        assertFalse(voCustomerService.getFilteredCustomers("TEST CRUD UPDATE", 10).isEmpty());

        final List<VoAttrValueCustomer> attributes = voCustomerService.getCustomerAttributes(updated.getCustomerId());
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        final VoAttrValueCustomer updateAttribute = attributes.stream().filter(
                attr -> "CUSTOMER_PHONE".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttribute.getVal());
        updateAttribute.setVal("1234567890");

        final List<VoAttrValueCustomer> attributesAfterCreate = voCustomerService.updateCustomerAttributes(Collections.singletonList(MutablePair.of(updateAttribute, Boolean.FALSE)));
        final VoAttrValueCustomer attributeAfterCreate = attributesAfterCreate.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertEquals("1234567890", attributeAfterCreate.getVal());

        final List<VoAttrValueCustomer> attributesAfterRemove = voCustomerService.updateCustomerAttributes(Collections.singletonList(MutablePair.of(attributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueCustomer attributeAfterRemove = attributesAfterRemove.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertNull(attributeAfterRemove.getVal());

        voCustomerService.removeCustomer(updated.getCustomerId());

        assertTrue(voCustomerService.getFilteredCustomers("TEST CRUD UPDATE", 10).isEmpty());

    }
}