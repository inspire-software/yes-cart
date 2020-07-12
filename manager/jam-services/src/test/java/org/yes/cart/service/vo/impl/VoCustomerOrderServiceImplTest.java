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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoCustomerOrderService;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 23/09/2019
 * Time: 17:56
 */
public class VoCustomerOrderServiceImplTest extends BaseCoreDBTestCase {

    private VoCustomerOrderService voCustomerOrderService;

    @Before
    public void setUp() {
        voCustomerOrderService = (VoCustomerOrderService) ctx().getBean("voCustomerOrderService");
        super.setUp();
    }

    @Test
    public void testGetOrders() throws Exception {

        final VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "#190323063746-1"));
        ctx.setSize(10);
        VoSearchResult<VoCustomerOrderInfo> ordersAll = voCustomerOrderService.getFilteredOrders("en", ctx);
        assertNotNull(ordersAll);
        assertTrue(ordersAll.getTotal() > 0);
        assertFalse(ordersAll.getItems().isEmpty());

        VoCustomerOrder order = voCustomerOrderService.getOrderById("en", ordersAll.getItems().get(0).getCustomerorderId());
        assertNotNull(order);
        assertEquals("190323063746-1", order.getOrdernum());
        assertNotNull(order.getLines());
        assertFalse(order.getLines().isEmpty());
        final VoCustomerOrderLine line = order.getLines().stream().filter(item -> item.getCustomerOrderDeliveryDetId() == 1000111L).findFirst().get();
        assertEquals("CC_TEST1", line.getSkuCode());
        assertNotNull(line.getAllValues());
        final VoAttrValue customVal =
                line.getAllValues().stream().filter(val -> val.getAttribute().getCode().equals(AttributeNamesKeys.Cart.ORDER_LINE_COST_PRICE)).findFirst().get();
        assertEquals("190.99", customVal.getVal());
        assertEquals(I18NModel.DEFAULT, customVal.getDisplayVals().get(0).getFirst());
        assertEquals("SUPPLIER", customVal.getDisplayVals().get(0).getSecond());

        final VoAttrValue customOrderVal =
                order.getAllValues().stream().filter(val -> val.getAttribute().getCode().equals("WS310: 2019-03-21 17:03:05")).findFirst().get();
        assertEquals("3100133903", customOrderVal.getVal());
        assertEquals(I18NModel.DEFAULT, customOrderVal.getDisplayVals().get(0).getFirst());
        assertEquals("AUDITEXPORT", customOrderVal.getDisplayVals().get(0).getSecond());

        assertTrue(Arrays.asList("PROMO001", "PROMO002").containsAll(order.getAppliedPromo()));

        assertEquals("cancel.order", order.getOrderStatusNextOptions().get(0));

    }
}