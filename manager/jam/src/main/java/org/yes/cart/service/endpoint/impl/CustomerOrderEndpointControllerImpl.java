/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoCustomerOrder;
import org.yes.cart.domain.vo.VoCustomerOrderInfo;
import org.yes.cart.domain.vo.VoCustomerOrderTransitionResult;
import org.yes.cart.domain.vo.VoPayment;
import org.yes.cart.service.endpoint.CustomerOrderEndpointController;
import org.yes.cart.service.vo.VoCustomerOrderService;
import org.yes.cart.service.vo.VoPaymentService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 20:34
 */
@Component
public class CustomerOrderEndpointControllerImpl implements CustomerOrderEndpointController {

    private final VoCustomerOrderService voCustomerOrderService;
    private final VoPaymentService voPaymentService;

    @Autowired
    public CustomerOrderEndpointControllerImpl(final VoCustomerOrderService voCustomerOrderService,
                                               final VoPaymentService voPaymentService) {
        this.voCustomerOrderService = voCustomerOrderService;
        this.voPaymentService = voPaymentService;
    }

    @Override
    public @ResponseBody
    List<VoCustomerOrderInfo> getFilteredOrders(@PathVariable("lang") final String lang, @RequestBody final Map<String, Object> filter, @PathVariable("max") final int max) throws Exception {
        return voCustomerOrderService.getFiltered(lang, (String) filter.get("filter"), (List) filter.get("statuses"), max);
    }

    @Override
    public @ResponseBody
    VoCustomerOrder getOrderById(@PathVariable("lang") final String lang, @PathVariable("id") final long id) throws Exception {
        return voCustomerOrderService.getById(lang, id);
    }

    @Override
    public @ResponseBody
    VoCustomerOrderTransitionResult transitionOrder(@PathVariable("transition") final String transition, @PathVariable("ordernum") final String ordernum, @RequestBody final String message) throws Exception {
        return voCustomerOrderService.transitionOrder(transition, ordernum, message);
    }

    @Override
    public @ResponseBody
    VoCustomerOrderTransitionResult transitionDelivery(@PathVariable("transition") final String transition, @PathVariable("ordernum") final String ordernum, @PathVariable("deliverynum") final String deliverynum, @RequestBody final String message) throws Exception {
        return voCustomerOrderService.transitionDelivery(transition, ordernum, deliverynum, message);
    }

    @Override
    public @ResponseBody
    List<VoPayment> getFilteredPayments(@RequestBody final Map<String, Object> filter, @PathVariable("max") final int max) throws Exception {
        return voPaymentService.getFiltered((String) filter.get("filter"), (List) filter.get("operations"), (List) filter.get("statuses"), max);
    }
}
