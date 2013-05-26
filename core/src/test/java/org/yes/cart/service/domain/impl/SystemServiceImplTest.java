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
package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.SystemService;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/22/12
 * Time: 1:14 PM
 */
public class SystemServiceImplTest extends BaseCoreDBTestCase {

    private SystemService systemService;

    @Before
    public void setUp() {
        systemService = ctx().getBean(ServiceSpringKeys.SYSTEM_SERVICE, SystemService.class);
        super.setUp();
    }

    @Test
    public void testSetAttributeValue() throws Exception {
        
        String oldValue = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL) ;
        
        String newValue = UUID.randomUUID().toString();

        systemService.updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, newValue);

        assertEquals(
                newValue,
                systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL)
        );

        systemService.updateAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL, oldValue);

        assertEquals(
                oldValue,
                systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL)
        );

    }



}
