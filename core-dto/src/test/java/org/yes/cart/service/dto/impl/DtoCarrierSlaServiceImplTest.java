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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.CarrierSlaDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.service.dto.DtoCarrierSlaService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 21/01/2020
 * Time: 18:14
 */
public class DtoCarrierSlaServiceImplTest extends BaseCoreDBTestCase {

    private DtoCarrierSlaService dtoService;


    @Before
    public void setUp() {
        dtoService = (DtoCarrierSlaService) ctx().getBean(DtoServiceSpringKeys.DTO_CARRIER_SLA_SERVICE);
        super.setUp();
    }


    @Test
    public void testFindCarrierSlas() throws Exception {

        final List<Long> carrierIdsNone = Collections.singletonList(9999L);
        final List<Long> carrierIds = Arrays.asList(1L, 2L, 3L);
        SearchContext ctx;
        SearchResult<CarrierSlaDTO> rez;

        // check by code
        ctx = createSearchContext("guid", false, 0, 10,
                "filter", "1_CARRIER",
                "carrierIds", carrierIds
        );
        rez = dtoService.findCarrierSlas(ctx);
        assertEquals(3, rez.getTotal());
        assertEquals(3, rez.getItems().size());
        assertEquals(1L, rez.getItems().get(0).getCarrierId());
        assertEquals(1L, rez.getItems().get(1).getCarrierId());
        assertEquals(1L, rez.getItems().get(2).getCarrierId());

        // check non for wrong carrier IDS
        ctx = createSearchContext("guid", false, 0, 10,
                "carrierIds", carrierIdsNone
        );
        rez = dtoService.findCarrierSlas(ctx);
        assertEquals(0, rez.getTotal());

        // check all carriers
        ctx = createSearchContext("guid", false, 0, 10,
                "carrierIds", carrierIds
        );
        rez = dtoService.findCarrierSlas(ctx);
        assertEquals(4, rez.getTotal());
        assertEquals(4, rez.getItems().size());
        assertEquals("1_CARRIERSLA", rez.getItems().get(0).getCode());
        assertEquals("2_CARRIERSLA", rez.getItems().get(1).getCode());
        assertEquals("3_CARRIERSLA", rez.getItems().get(2).getCode());

    }

}