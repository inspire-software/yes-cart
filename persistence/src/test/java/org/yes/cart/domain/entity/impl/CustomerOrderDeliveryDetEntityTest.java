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