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

package org.yes.cart.service.order.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Address;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 16:51
 */
public class DefaultOrderAddressFormatterImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testFormatAddress() throws Exception {

        final Address address = context.mock(Address.class, "address");

        context.checking(new Expectations() {{
            allowing(address).getSalutation(); will(returnValue("S"));
            allowing(address).getFirstname(); will(returnValue("N1"));
            allowing(address).getMiddlename(); will(returnValue("N2"));
            allowing(address).getLastname(); will(returnValue("N3"));
            allowing(address).getAddrline1(); will(returnValue("L1"));
            allowing(address).getAddrline2(); will(returnValue("L2"));
            allowing(address).getPostcode(); will(returnValue("PC"));
            allowing(address).getCity(); will(returnValue("C"));
            allowing(address).getCountryCode(); will(returnValue("CC"));
            allowing(address).getStateCode(); will(returnValue("SC"));
            allowing(address).getPhone1(); will(returnValue("P1"));
            allowing(address).getPhone2(); will(returnValue("P2"));
            allowing(address).getMobile1(); will(returnValue("M1"));
            allowing(address).getMobile2(); will(returnValue("M2"));
            allowing(address).getEmail1(); will(returnValue("E1"));
            allowing(address).getEmail2(); will(returnValue("E2"));
            allowing(address).getCustom0(); will(returnValue("C0"));
            allowing(address).getCustom1(); will(returnValue("C1"));
            allowing(address).getCustom2(); will(returnValue("C2"));
            allowing(address).getCustom3(); will(returnValue("C3"));
            allowing(address).getCustom4(); will(returnValue("C4"));
            allowing(address).getCustom5(); will(returnValue("C5"));
            allowing(address).getCustom6(); will(returnValue("C6"));
            allowing(address).getCustom7(); will(returnValue("C7"));
            allowing(address).getCustom8(); will(returnValue("C8"));
            allowing(address).getCustom9(); will(returnValue("C9"));
        }});

        final DefaultOrderAddressFormatterImpl formatter = new DefaultOrderAddressFormatterImpl(
                "{{salutation}} {{firstname}} {{middlename}} {{lastname}} " +
                "{{addrline1}} {{addrline2}} {{postcode}} {{city}} {{countrycode}} {{statecode}} " +
                "{{phone1}} {{phone2}} {{mobile1}} {{mobile2}} " +
                "{{email1}} {{email2}} " +
                "{{custom1}} {{custom2}} {{custom3}} {{custom4}}");

        assertEquals("S N1 N2 N3 L1 L2 PC C CC SC P1 P2 M1 M2 E1 E2 C1 C2 C3 C4", formatter.formatAddress(address));
        assertEquals("N1 N3", formatter.formatAddress(address, "{{firstname}} {{lastname}}"));
        assertEquals("N1 N3 N2", formatter.formatAddress(address, "{{firstname}} {{lastname}} {{middlename}}"));
        assertEquals("C0 C5 C6 C7 C8 C9", formatter.formatAddress(address, "{{custom0}} {{custom5}} {{custom6}} {{custom7}} {{custom8}} {{custom9}}"));

    }

}
