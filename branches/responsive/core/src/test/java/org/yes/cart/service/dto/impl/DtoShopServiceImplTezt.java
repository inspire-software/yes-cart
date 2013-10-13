/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.service.dto.DtoShopService;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/5/12
 * Time: 9:22 PM
 */
public class DtoShopServiceImplTezt extends BaseCoreDBTestCase {

    private DtoShopService dtoShopService ;

    @Before
    public void setUp() {
        dtoShopService = (DtoShopService) ctx().getBean(ServiceSpringKeys.DTO_SHOP_SERVICE);
        super.setUp();
    }

    @Test
    public void testSetSupportedCurrencies() throws Exception {

        final ShopDTO dto = dtoShopService.getById(10L);

        final String oldcurr = dtoShopService.getSupportedCurrencies(10L);

        dtoShopService.updateSupportedCurrencies(10L, oldcurr + ",ZZZ");

        final String newcurr = dtoShopService.getSupportedCurrencies(10L);

        assertEquals("Updated currency must contains ZZZ currency", oldcurr+ ",ZZZ", newcurr);

    }


}
