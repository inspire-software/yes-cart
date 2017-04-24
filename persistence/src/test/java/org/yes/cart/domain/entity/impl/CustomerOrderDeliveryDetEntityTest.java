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

package org.yes.cart.domain.entity.impl;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 17/02/2017
 * Time: 09:14
 */
public class CustomerOrderDeliveryDetEntityTest {

    @Test
    public void testIsDeliveryRejected() throws Exception {

        final CustomerOrderDeliveryDetEntity entity = new CustomerOrderDeliveryDetEntity();
        assertFalse(entity.isDeliveryRejected());

        entity.setDeliveredQuantity(BigDecimal.ZERO);
        assertTrue(entity.isDeliveryRejected());

        entity.setDeliveredQuantity(BigDecimal.ONE);
        assertFalse(entity.isDeliveryRejected());

    }


    @Test
    public void testIsDeliveryDifferent() throws Exception {


        final CustomerOrderDeliveryDetEntity entity = new CustomerOrderDeliveryDetEntity();
        assertFalse(entity.isDeliveryDifferent());

        entity.setDeliveredQuantity(BigDecimal.ZERO);
        assertFalse(entity.isDeliveryDifferent());

        entity.setDeliveredQuantity(BigDecimal.ONE);
        assertFalse(entity.isDeliveryDifferent());

        entity.setQty(BigDecimal.TEN);
        entity.setDeliveredQuantity(BigDecimal.ZERO);
        assertTrue(entity.isDeliveryDifferent());

        entity.setQty(BigDecimal.TEN);
        entity.setDeliveredQuantity(BigDecimal.ONE);
        assertTrue(entity.isDeliveryDifferent());

        entity.setQty(BigDecimal.ONE);
        entity.setDeliveredQuantity(BigDecimal.ONE);
        assertFalse(entity.isDeliveryDifferent());

    }
}