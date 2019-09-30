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
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoSystemPreferencesService;
import org.yes.cart.utils.DateUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/09/2019
 * Time: 12:28
 */
public class VoSystemPreferencesServiceImplTest extends BaseCoreDBTestCase {

    private VoSystemPreferencesService voSystemPreferencesService;

    @Before
    public void setUp() {
        voSystemPreferencesService = (VoSystemPreferencesService) ctx().getBean("voSystemPreferencesService");
        super.setUp();
    }

    @Test
    public void testPreferencesRU() throws Exception {

        final List<VoAttrValueSystem> existing = voSystemPreferencesService.getSystemPreferences(true);
        final VoAttrValueSystem av1 = existing.stream().filter(ex -> "SYSTEM_DEFAULT_SHOP".equals(ex.getAttribute().getCode())).findFirst().get();

        assertEquals("http://www.gadget.yescart.org:8080/", av1.getVal());

        av1.setVal("http://abc.com");

        voSystemPreferencesService.update(Collections.singletonList(MutablePair.of(av1, Boolean.FALSE)), true);

        final VoAttrValueSystem av2 = existing.stream().filter(ex -> "SYSTEM_DEFAULT_SHOP".equals(ex.getAttribute().getCode())).findFirst().get();

        assertEquals("http://abc.com", av2.getVal());

    }

}