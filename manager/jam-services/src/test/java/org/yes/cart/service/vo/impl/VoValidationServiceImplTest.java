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
import org.yes.cart.domain.vo.VoValidationRequest;
import org.yes.cart.domain.vo.VoValidationResult;
import org.yes.cart.service.vo.VoValidationService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 25/01/2020
 * Time: 16:28
 */
public class VoValidationServiceImplTest extends BaseCoreDBTestCase {

    private VoValidationService voValidationService;

    @Before
    public void setUp() {
        voValidationService = (VoValidationService) ctx().getBean("voValidationService");
        super.setUp();
    }

    @Test
    public void testValidatorsInvalid() throws Exception {

        VoValidationRequest request = new VoValidationRequest();

        request.setSubject("non-existent");
        request.setField("field");
        request.setSubjectId(1000L);
        request.setValue("ABC");

        assertEquals("INVALID_SUBJECT", voValidationService.validate(request).getErrorCode());

    }

    private void assertSubjectField(final String subject, final String field, final long duplicateId, final String duplicateValue) {

        VoValidationRequest request = new VoValidationRequest();

        request.setSubject(subject);
        request.setField(field);
        request.setSubjectId(0L);
        request.setValue(duplicateValue);

        final VoValidationResult resDup = voValidationService.validate(request);
        assertEquals("DUPLICATE", resDup.getErrorCode());
        assertEquals(duplicateId, resDup.getDuplicateId());

        request.setSubjectId(duplicateId);

        final VoValidationResult resOk = voValidationService.validate(request);
        assertNull(resOk.getErrorCode());
        assertEquals(0L, resOk.getDuplicateId());
    }

    private void assertSubjectFieldComposite(final String subject, final String field, final long duplicateId, long otherId, final String duplicateValue) {

        VoValidationRequest request = new VoValidationRequest();

        request.setSubject(subject);
        request.setField(field);
        request.setSubjectId(0L);
        request.setValue(duplicateValue);

        final VoValidationResult resDup = voValidationService.validate(request);
        assertEquals("DUPLICATE", resDup.getErrorCode());
        assertEquals(otherId, resDup.getDuplicateId());

        request.setSubjectId(duplicateId);

        final VoValidationResult resDup2 = voValidationService.validate(request);
        assertEquals("DUPLICATE", resDup2.getErrorCode());
        assertEquals(otherId, resDup2.getDuplicateId());

        request.setSubjectId(otherId);

        final VoValidationResult resDup3 = voValidationService.validate(request);
        assertEquals("DUPLICATE", resDup3.getErrorCode());
        assertEquals(otherId, resDup3.getDuplicateId());
    }

    @Test
    public void testValidators() throws Exception {

        assertSubjectField("category", "guid", 102L, "102");
        assertSubjectField("category", "uri", 101L, "big-boys-Gadgets");

        assertSubjectField("content", "guid", 10105L, "SHOIP2");
        assertSubjectField("content", "uri", 10113L, "SHOIP1_mail_customer-registered.html");

        assertSubjectField("attribute", "code", 1000L, "SYSTEM_DEFAULT_SHOP");

        assertSubjectField("carrier", "guid", 1L, "1_CARRIER");

        assertSubjectField("carriersla", "guid", 1L, "1_CARRIERSLA");
        assertSubjectFieldComposite("carriersla", "guid", 1L, 9998L, "BENDER-ua");

        assertSubjectField("manager", "email", 10001L, "test@test.com");

        assertSubjectField("producttype", "guid", 1L, "1");

        assertSubjectField("product", "guid", 9998L, "BENDER-ua");
        assertSubjectField("product", "code", 9998L, "BENDER-ua");
        assertSubjectField("product", "uri", 9999L, "bender-rodriguez");

        assertSubjectField("sku", "guid", 9998L, "BENDER-ua");
        assertSubjectField("sku", "code", 9998L, "BENDER-ua");
        assertSubjectField("sku", "uri", 10001L, "i-sobot-pink");
        assertSubjectFieldComposite("sku", "code", 1L, 1L, "1_CARRIERSLA");

        assertSubjectField("shop", "code", 10L, "SHOIP1");
        assertSubjectField("shop", "alias", 10L, "SHOIP1");

        assertSubjectField("warehouse", "code", 1L, "WAREHOUSE_1");

        assertSubjectField("promotion", "code", 1L, "PROMO001");

        assertSubjectField("tax", "code", 1010L, "VAT");
    }
}