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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoValidationRequest;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.vo.VoValidationService;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 25/10/2020
 * Time: 21:26
 */
public class VoValidationServiceCustomerLoginImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testValidateNew() throws Exception {

        final CustomerService managerService = context.mock(CustomerService.class);

        context.checking(new Expectations() {{
            allowing(managerService).findCustomersByLogin(with(any(String.class))); will(returnValue(Collections.emptyList()));
        }});

        final VoValidationService validationService = new VoValidationServiceCustomerLoginImpl(managerService);

        assertNull(validationService.validate(request(0L, "zzzz1234")).getErrorCode());
        assertEquals("INVALID_DATA", validationService.validate(request(0L, "zzzz123")).getErrorCode());

    }

    @Test
    public void testValidateCurrent() throws Exception {

        final CustomerService managerService = context.mock(CustomerService.class);
        final Customer customer = context.mock(Customer.class);
        final Shop shop = context.mock(Shop.class);

        context.checking(new Expectations() {{
            allowing(managerService).findCustomersByLogin(with(any(String.class)));
            will(returnValue(Collections.singletonList(new Pair<>(customer, shop))));
            allowing(customer).getCustomerId(); will(returnValue(123L));
        }});

        final VoValidationService validationService = new VoValidationServiceCustomerLoginImpl(managerService);

        assertNull(validationService.validate(request(123L, "zzzz1234")).getErrorCode());
        assertEquals("INVALID_DATA", validationService.validate(request(123L, "zzzz123")).getErrorCode());

    }

    @Test
    public void testValidateDuplicate() throws Exception {

        final CustomerService managerService = context.mock(CustomerService.class);
        final Customer customer = context.mock(Customer.class);
        final Shop shop = context.mock(Shop.class);

        context.checking(new Expectations() {{
            allowing(managerService).findCustomersByLogin(with(any(String.class)));
            will(returnValue(Collections.singletonList(new Pair<>(customer, shop))));
            allowing(customer).getCustomerId(); will(returnValue(456L));
            allowing(shop).getCode(); will(returnValue("ABC"));
            allowing(shop).getMaster(); will(returnValue(null));
        }});

        final VoValidationService validationService = new VoValidationServiceCustomerLoginImpl(managerService);

        assertNull(validationService.validate(request(123L, "zzzz1234")).getErrorCode());
        final VoValidationRequest request = request(123L, "zzzz1234");
        request.setContext(Collections.singletonMap("shopCodes", "ABC,DEF"));
        assertEquals("DUPLICATE", validationService.validate(request).getErrorCode());

    }

    @Test
    public void testValidateDuplicateMaster() throws Exception {

        final CustomerService managerService = context.mock(CustomerService.class);
        final Customer customer = context.mock(Customer.class);
        final Shop shop = context.mock(Shop.class, "shop");
        final Shop master = context.mock(Shop.class, "master");

        context.checking(new Expectations() {{
            allowing(managerService).findCustomersByLogin(with(any(String.class)));
            will(returnValue(Collections.singletonList(new Pair<>(customer, shop))));
            allowing(customer).getCustomerId(); will(returnValue(456L));
            allowing(shop).getCode(); will(returnValue("BAR"));
            allowing(shop).getMaster(); will(returnValue(master));
            allowing(master).getCode(); will(returnValue("ABC"));
        }});

        final VoValidationService validationService = new VoValidationServiceCustomerLoginImpl(managerService);

        assertNull(validationService.validate(request(123L, "zzzz1234")).getErrorCode());
        final VoValidationRequest request = request(123L, "zzzz1234");
        request.setContext(Collections.singletonMap("shopCodes", "ABC,DEF"));
        assertEquals("DUPLICATE", validationService.validate(request).getErrorCode());

    }

    private VoValidationRequest request(final long id, final String loginToCheck) {
        return new VoValidationRequest(id, "customer", "login", loginToCheck);
    }


}