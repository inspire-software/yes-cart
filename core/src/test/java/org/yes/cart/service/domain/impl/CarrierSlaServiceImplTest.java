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

package org.yes.cart.service.domain.impl;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.service.domain.CarrierService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CarrierSlaServiceImplTest extends BaseCoreDBTestCase {

    private CarrierService carrierService;

    protected IDataSet createDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream("initialdata_carrier.xml"), false);
    }

    @Before
    public void setUp() {
        carrierService = (CarrierService) ctx().getBean(ServiceSpringKeys.CARRIER_SERVICE);
        super.setUp();
    }

    @Test
    public void testFindCarriersFilterByShop() {
        List<Carrier> carriers;
        carriers = carrierService.getCarriersByShopId(20L); // no shop 20
        assertEquals(0, carriers.size());
        carriers = carrierService.getCarriersByShopId(10L); // 2 Carriers
        assertEquals(2, carriers.size());
        assertFalse(carriers.get(0).getCarrierSla().isEmpty());
        assertFalse(carriers.get(1).getCarrierSla().isEmpty());
    }
}
