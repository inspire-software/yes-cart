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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.CustomerOrderEndpointController;
import org.yes.cart.service.vo.VoCustomerOrderService;
import org.yes.cart.service.vo.VoPaymentService;

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
    VoSearchResult<VoCustomerOrderInfo> getFilteredOrders(@RequestParam("lang") final String lang, @RequestBody(required = false) final VoSearchContext filter) throws Exception {
        return voCustomerOrderService.getFilteredOrders(lang, filter);
    }

    @Override
    public @ResponseBody
    VoCustomerOrder getOrderById(@RequestParam("lang") final String lang, @PathVariable("id") final long id) throws Exception {
        return voCustomerOrderService.getOrderById(lang, id);
    }

    @Override
    public @ResponseBody
    VoCustomerOrderTransitionResult transitionOrder(@PathVariable("ordernum") final String ordernum, @RequestBody final VoCustomerOrderTransition transition) throws Exception {
        return voCustomerOrderService.transitionOrder(transition.getTransition(), ordernum, transition.getContext());
    }

    @Override
    public @ResponseBody
    VoCustomerOrderTransitionResult transitionDelivery(@PathVariable("ordernum") final String ordernum, @PathVariable("deliverynum") final String deliverynum, @RequestBody final VoCustomerOrderTransition transition) throws Exception {
        return voCustomerOrderService.transitionDelivery(transition.getTransition(), ordernum, deliverynum, transition.getContext());
    }

    @Override
    public @ResponseBody
    VoCustomerOrderTransitionResult transitionOrderLine(@PathVariable("ordernum") final String ordernum, @PathVariable("deliverynum") final String deliverynum, @PathVariable("lineId") final String lineId, @RequestBody final VoCustomerOrderTransition transition) throws Exception {
        return voCustomerOrderService.transitionOrderLine(transition.getTransition(), ordernum, deliverynum, lineId, transition.getContext());
    }


    @Override
    public @ResponseBody
    VoCustomerOrder exportOrder(@RequestParam("lang") final String lang, @PathVariable("id") final long id, @RequestParam("export") final boolean export) throws Exception {
        return voCustomerOrderService.exportOrder(lang, id, export);
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoPayment> getFilteredPayments(@RequestBody final VoSearchContext filter) throws Exception {
        return voPaymentService.getFilteredPayments(filter);
    }
}
