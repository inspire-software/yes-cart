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
package org.yes.cart.service.endpoint;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.vo.VoCustomerOrder;
import org.yes.cart.domain.vo.VoCustomerOrderInfo;
import org.yes.cart.domain.vo.VoCustomerOrderTransitionResult;
import org.yes.cart.domain.vo.VoPayment;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 20:30
 */
@Controller
@RequestMapping("/customerorder")
public interface CustomerOrderEndpointController {


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/filtered/{max}/{lang}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCustomerOrderInfo> getFilteredOrders(@PathVariable("lang") String lang, @RequestBody Map<String, Object> filter, @PathVariable("max") int max) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/order/{id}/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrder getOrderById(@PathVariable("lang") String lang, @PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/transition/{transition}/{ordernum}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrderTransitionResult transitionOrder(@PathVariable("transition") String transition, @PathVariable("ordernum") String ordernum, @RequestBody String message) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/transition/{transition}/{ordernum}/{deliverynum}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrderTransitionResult transitionDelivery(@PathVariable("transition") String transition, @PathVariable("ordernum") String ordernum, @PathVariable("deliverynum") String deliverynum, @RequestBody String message) throws Exception;



    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/payments/filtered/{max}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPayment> getFilteredPayments(@RequestBody Map<String, Object> filter, @PathVariable("max") int max) throws Exception;


}
