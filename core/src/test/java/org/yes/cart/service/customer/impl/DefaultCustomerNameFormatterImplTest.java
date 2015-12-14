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

package org.yes.cart.service.customer.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 16:14
 */
public class DefaultCustomerNameFormatterImplTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testFormatNameFromAddress() throws Exception {

        final Address address = context.mock(Address.class, "address");

        context.checking(new Expectations() {{
            allowing(address).getFirstname(); will(returnValue("A"));
            allowing(address).getMiddlename(); will(returnValue("B"));
            allowing(address).getLastname(); will(returnValue("C"));
            allowing(address).getSalutation(); will(returnValue("D"));
        }});

        final DefaultCustomerNameFormatterImpl formatter = new DefaultCustomerNameFormatterImpl("{{firstname}} {{middlename}} {{lastname}}");

        assertEquals("A B C", formatter.formatName(address));
        assertEquals("A C", formatter.formatName(address, "{{firstname}} {{lastname}}"));
        assertEquals("A C B", formatter.formatName(address, "{{firstname}} {{lastname}} {{middlename}}"));
        assertEquals("D C B", formatter.formatName(address, "{{salutation}} {{lastname}} {{middlename}}"));
    }

    @Test
    public void testFormatNameFromCustomer() throws Exception {

        final Customer customer = context.mock(Customer.class, "customer");

        context.checking(new Expectations() {{
            allowing(customer).getFirstname(); will(returnValue("A"));
            allowing(customer).getMiddlename(); will(returnValue("B"));
            allowing(customer).getLastname(); will(returnValue("C"));
            allowing(customer).getSalutation(); will(returnValue("D"));
        }});

        final DefaultCustomerNameFormatterImpl formatter = new DefaultCustomerNameFormatterImpl("{{firstname}} {{middlename}} {{lastname}}");

        assertEquals("A B C", formatter.formatName(customer));
        assertEquals("A C", formatter.formatName(customer, "{{firstname}} {{lastname}}"));
        assertEquals("A C B", formatter.formatName(customer, "{{firstname}} {{lastname}} {{middlename}}"));
        assertEquals("D A C B", formatter.formatName(customer, "{{salutation}} {{firstname}} {{lastname}} {{middlename}}"));

    }
}
