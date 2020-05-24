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
import org.yes.cart.domain.vo.VoCountry;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoState;
import org.yes.cart.service.vo.VoLocationService;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/09/2019
 * Time: 16:29
 */
public class VoLocationServiceImplTest extends BaseCoreDBTestCase {

    private VoLocationService voLocationService;

    @Before
    public void setUp() {
        voLocationService = (VoLocationService) ctx().getBean("voLocationService");
        super.setUp();
    }

    @Test
    public void testCountryCRUD() throws Exception {

        final VoCountry country = new VoCountry();
        country.setCountryCode("TC");
        country.setIsoCode("123");
        country.setName("TEST CRUD");
        country.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));

        final VoCountry created = voLocationService.createCountry(country);
        assertTrue(created.getCountryId() > 0L);

        VoCountry afterCreated = voLocationService.getCountryById(created.getCountryId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoCountry updated = voLocationService.updateCountry(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams(
                "filter", "TEST CRUD UPDATE"
        ));
        ctxFind.setSize(10);

        assertTrue(voLocationService.getFilteredCountries(ctxFind).getItems().stream().anyMatch(cnt -> cnt.getCountryId() == updated.getCountryId()));

        voLocationService.removeCountry(updated.getCountryId());

        assertFalse(voLocationService.getFilteredCountries(ctxFind).getItems().stream().anyMatch(cnt -> cnt.getCountryId() == updated.getCountryId()));

    }

    @Test
    public void testStateCRUD() throws Exception {

        final VoState state = new VoState();
        state.setName("TEST CRUD");
        state.setCountryCode("GB");
        state.setStateCode("GB-TC");
        state.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));

        final VoState created = voLocationService.createState(state);
        assertTrue(created.getStateId() > 0L);

        assertTrue(voLocationService.getCountryStatesAll(220L).stream().anyMatch(st -> st.getStateId() == created.getStateId()));

        final VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("countryCodes", "GB"));
        ctxFind.setSize(10);
        assertTrue(voLocationService.getFilteredStates(ctxFind).getItems().stream().anyMatch(st -> st.getStateId() == created.getStateId()));

        VoState afterCreated = voLocationService.getStateById(created.getStateId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoState updated = voLocationService.updateState(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        assertTrue(voLocationService.getCountryStatesAll(220L).stream().anyMatch(st -> st.getStateId() == updated.getStateId()));

        voLocationService.removeState(updated.getStateId());

        assertFalse(voLocationService.getCountryStatesAll(220L).stream().anyMatch(st -> st.getStateId() == updated.getStateId()));

    }
}