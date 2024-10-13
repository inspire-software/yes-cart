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
package org.yes.cart.service.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.vo.*;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 20:30
 */
@Controller
@Api(value = "Order", description = "Orders controller", tags = "order")
@RequestMapping("/customerorders")
public interface CustomerOrderEndpointController {


    @ApiOperation(value = "Order search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSUBSHOPUSER","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoCustomerOrderInfo> getFilteredOrders(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang,
                                                          @ApiParam(
                      value = "Search criteria with the following parameter support:" +
                              "\n* filter - text filter expands to 'ordernum', 'email', 'firstname' and 'lastname'" +
                              "\n  * [#] prefix - match by 'ordernum' or 'cartGuid' (e.g. '#22020202020-1')" +
                              "\n  * [?] prefix - match by 'email', 'firstname' or 'lastname' (e.g. '?john.doe@test.com')" +
                              "\n  * [@] prefix - match by 'billingAddress' or 'shippingAddress' (e.g. '@london')" +
                              "\n  * [!] prefix - exact match by 'delivery.line.sku' for in progress orders (e.g. '!SKU-00001')" +
                              "\n  * [\\*] prefix - exact match by 'customerorderId' (e.g. '\\* 100001')" +
                              "\n  * [^] prefix - match by exact 'shop.code' or partial 'customer.tag', 'customer.companyName1' or 'customer.companyName2' (e.g. '^SHOP10')" +
                              "\n  * date search - match by 'orderTimestamp' (e.g. '2009-01<' - after Jan 2009, '2009-01<2020' - after Jan 2009 but before 2020)" +
                              "\n* statuses - optional list of order statuses" +
                              "\n* shopIds - optional injected automatically according to current user access", name = "filter")
                                                          @RequestBody(required = false) VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve order")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSUBSHOPUSER","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrder getOrderById(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang, @ApiParam(value = "Order ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Transition order through fulfilment")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSUBSHOPUSER","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/transition/{ordernum}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrderTransitionResult transitionOrder(@ApiParam(value = "Order number") @PathVariable("ordernum") String ordernum, @ApiParam(value = "Transition", name = "transition", required = true) @RequestBody VoCustomerOrderTransition transition) throws Exception;

    @ApiOperation(value = "Transition order delivery through fulfilment")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSUBSHOPUSER","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/transition/{ordernum}/{deliverynum}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrderTransitionResult transitionDelivery(@ApiParam(value = "Order number", required = true) @PathVariable("ordernum") String ordernum, @ApiParam(value = "Delivery number", required = true) @PathVariable("deliverynum") String deliverynum, @ApiParam(value = "Transition", name = "transition", required = true) @RequestBody VoCustomerOrderTransition transition) throws Exception;

    @ApiOperation(value = "Transition order line through fulfilment")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSUBSHOPUSER","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/transition/{ordernum}/{deliverynum}/{lineId}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrderTransitionResult transitionOrderLine(@ApiParam(value = "Order number", required = true) @PathVariable("ordernum") String ordernum, @ApiParam(value = "Delivery number", required = true) @PathVariable("deliverynum") String deliverynum, @ApiParam(value = "Line ID", required = true) @PathVariable("lineId") String lineId, @ApiParam(value = "Transition", name = "transition", required = true) @RequestBody VoCustomerOrderTransition transition) throws Exception;


    @ApiOperation(value = "Trigger order export, or clear export eligibility")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/{id}/orderexport", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomerOrder exportOrder(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang, @ApiParam(value = "Order ID", required = true) @PathVariable("id") long id, @ApiParam(value = "Perform export (true to allow export, false to clear export eligibility)", required = true) @RequestParam("export") boolean export) throws Exception;


    @ApiOperation(value = "Payments search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSUBSHOPUSER","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/payments/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoPayment> getFilteredPayments(@ApiParam(value = "Search criteria", name = "filter") @RequestBody VoSearchContext filter) throws Exception;


}
