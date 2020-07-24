/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.order.impl;

import org.yes.cart.domain.misc.Result;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.order.OrderFlowAction;

import java.util.Map;

/**
 * Date: 24/08/2020
 * Time: 17:43
 */
public class OrderNoteAction implements OrderFlowAction {

    private final DtoCustomerOrderService dtoCustomerOrderService;

    public OrderNoteAction(final DtoCustomerOrderService dtoCustomerOrderService) {
        this.dtoCustomerOrderService = dtoCustomerOrderService;
    }

    @Override
    public Result doTransition(final String orderNum, final Object params) {

        final Map<String, String> map = (Map<String, String>) params;

        return dtoCustomerOrderService.updateOrderSetNotes(orderNum, map.get("message"));
    }
}
