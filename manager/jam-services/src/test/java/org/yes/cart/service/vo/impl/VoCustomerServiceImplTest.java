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
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
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

        VoSearchContext ctxNoFilter = new VoSearchContext();
        ctxNoFilter.setSize(10);
        VoSearchResult<VoCustomerInfo> customerNoFilter = voCustomerService.getFilteredCustomers(ctxNoFilter);
        assertNotNull(customerNoFilter);
        assertTrue(customerNoFilter.getTotal() > 0);

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("filter", "reg@test.com"));
        ctxFind.setSize(10);
        VoSearchResult<VoCustomerInfo> customerFind = voCustomerService.getFilteredCustomers(ctxFind);
        assertNotNull(customerFind);
        assertTrue(customerFind.getTotal() > 0);
        assertEquals("reg@test.com", customerFind.getItems().get(0).getEmail());
        assertNotNull(customerFind.getItems().get(0).getCustomerShops());
        assertFalse(customerFind.getItems().get(0).getCustomerShops().isEmpty());

        VoSearchContext ctxByEmailTagOrCompany = new VoSearchContext();
        ctxByEmailTagOrCompany.setParameters(createSearchContextParams("filter", "#JJ"));
        ctxByEmailTagOrCompany.setSize(10);
        VoSearchResult<VoCustomerInfo> customerByEmailTagOrCompany = voCustomerService.getFilteredCustomers(ctxByEmailTagOrCompany);
        assertNotNull(customerByEmailTagOrCompany);
        assertTrue(customerByEmailTagOrCompany.getTotal() > 0);
        assertEquals("reg@test.com", customerByEmailTagOrCompany.getItems().get(0).getEmail());

        VoSearchContext ctxByName = new VoSearchContext();
        ctxByName.setParameters(createSearchContextParams("filter", "?John"));
        ctxByName.setSize(10);
        VoSearchResult<VoCustomerInfo> customerByName = voCustomerService.getFilteredCustomers(ctxByName);
        assertNotNull(customerByName);
        assertTrue(customerByName.getTotal() > 0);
        assertEquals("reg@test.com", customerByName.getItems().get(0).getEmail());

        VoSearchContext ctxByAddress = new VoSearchContext();
        ctxByAddress.setParameters(createSearchContextParams("filter", "@NW1 6XE"));
        ctxByAddress.setSize(10);
        VoSearchResult<VoCustomerInfo> customerByAddress = voCustomerService.getFilteredCustomers(ctxByAddress);
        assertNotNull(customerByAddress);
        assertTrue(customerByAddress.getTotal() > 0);
        assertEquals("reg@test.com", customerByAddress.getItems().get(0).getEmail());

        VoSearchContext ctxByTypeOrPolicy = new VoSearchContext();
        ctxByTypeOrPolicy.setParameters(createSearchContextParams("filter", "$TEST"));
        ctxByTypeOrPolicy.setSize(10);
        VoSearchResult<VoCustomerInfo> customerByTypeOrPolicy = voCustomerService.getFilteredCustomers(ctxByTypeOrPolicy);
        assertNotNull(customerByTypeOrPolicy);
        assertTrue(customerByTypeOrPolicy.getTotal() > 0);
        assertEquals("reg@test.com", customerByTypeOrPolicy.getItems().get(0).getEmail());

        VoSearchContext ctxByByDate = new VoSearchContext();
        ctxByByDate.setParameters(createSearchContextParams("filter", "2008-12<2009-01"));
        ctxByByDate.setSize(10);
        VoSearchResult<VoCustomerInfo> customerByDate = voCustomerService.getFilteredCustomers(ctxByByDate);
        assertNotNull(customerByDate);
        assertTrue(customerByDate.getTotal() > 0);
        assertEquals("reg@test.com", customerByDate.getItems().get(0).getEmail());

    }

    @Test
    public void testCustomerCRUD() throws Exception {

        final String email = UUID.randomUUID().toString() + "@test-crud.com";
        final VoCustomer customer = new VoCustomer();
        customer.setFirstname("TCFirst");
        customer.setLastname("TCLast");
        customer.setLogin(email);
        customer.setEmail(email);
        final VoCustomerShopLink shop = new VoCustomerShopLink();
        shop.setShopId(10L);
        customer.setCustomerShops(Collections.singletonList(shop));

        final VoCustomer created = voCustomerService.createCustomer(customer);
        assertTrue(created.getCustomerId() > 0L);

        final VoCustomer afterCreated = voCustomerService.getCustomerById(created.getCustomerId());
        assertNotNull(afterCreated);
        assertEquals(email, afterCreated.getEmail());
        assertNotNull(afterCreated.getCustomerShops());
        assertFalse(afterCreated.getCustomerShops().isEmpty());
        assertEquals(10L, afterCreated.getCustomerShops().get(0).getShopId());
        assertEquals(afterCreated.getCustomerId(), afterCreated.getCustomerShops().get(0).getCustomerId());

        created.setFirstname("TEST CRUD UPDATE");

        final VoCustomer updated = voCustomerService.updateCustomer(created);
        assertEquals("TEST CRUD UPDATE", updated.getFirstname());

        VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "TEST CRUD UPDATE"));
        ctx.setSize(10);
        assertTrue(voCustomerService.getFilteredCustomers(ctx).getTotal() > 0);

        final List<VoAttrValueCustomer> attributes = voCustomerService.getCustomerAttributes(updated.getCustomerId());
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        final VoAttrValueCustomer updateAttributePhone = attributes.stream().filter(
                attr -> "CUSTOMER_PHONE".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttributePhone.getVal());
        updateAttributePhone.setVal("1234567890");

        final List<VoAttrValueCustomer> attributesAfterCreate = voCustomerService.updateCustomerAttributes(Collections.singletonList(MutablePair.of(updateAttributePhone, Boolean.FALSE)));
        final VoAttrValueCustomer phoneAttributeAfterCreate = attributesAfterCreate.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttributePhone.getAttribute().getCode())).findFirst().get();
        assertEquals(0L, phoneAttributeAfterCreate.getAttrvalueId());
        assertEquals("1234567890", phoneAttributeAfterCreate.getVal());

        final List<VoAttrValueCustomer> attributesAfterRemove = voCustomerService.updateCustomerAttributes(Collections.singletonList(MutablePair.of(phoneAttributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueCustomer phoneAttributeAfterRemove = attributesAfterRemove.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttributePhone.getAttribute().getCode())).findFirst().get();
        assertEquals(0L, phoneAttributeAfterRemove.getAttrvalueId());
        assertEquals("1234567890", phoneAttributeAfterRemove.getVal());

        final VoAttrValueCustomer updateAttributeB2BRef = attributes.stream().filter(
                attr -> "CUSTOMER_B2B_REF".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttributeB2BRef.getVal());
        updateAttributeB2BRef.setVal("REF000005");

        final List<VoAttrValueCustomer> attributesAfterCreate2 = voCustomerService.updateCustomerAttributes(Collections.singletonList(MutablePair.of(updateAttributeB2BRef, Boolean.FALSE)));
        final VoAttrValueCustomer b2bRefAttributeAfterCreate = attributesAfterCreate2.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttributeB2BRef.getAttribute().getCode())).findFirst().get();
        assertTrue(b2bRefAttributeAfterCreate.getAttrvalueId() > 0L);
        assertEquals("REF000005", b2bRefAttributeAfterCreate.getVal());

        final List<VoAttrValueCustomer> attributesAfterRemove2 = voCustomerService.updateCustomerAttributes(Collections.singletonList(MutablePair.of(b2bRefAttributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueCustomer b2bRefAttributeAfterRemove = attributesAfterRemove2.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttributeB2BRef.getAttribute().getCode())).findFirst().get();
        assertEquals(0L, b2bRefAttributeAfterRemove.getAttrvalueId());
        assertNull(b2bRefAttributeAfterRemove.getVal());

        voCustomerService.removeCustomer(updated.getCustomerId());

        assertEquals(0, voCustomerService.getFilteredCustomers(ctx).getTotal());

    }
}