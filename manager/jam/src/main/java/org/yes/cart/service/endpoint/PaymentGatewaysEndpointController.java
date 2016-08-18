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
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoPaymentGateway;
import org.yes.cart.domain.vo.VoPaymentGatewayInfo;
import org.yes.cart.domain.vo.VoPaymentGatewayParameter;

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/08/2016
 * Time: 17:55
 */
@Controller
@RequestMapping("/payment")
public interface PaymentGatewaysEndpointController {


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/gateways/all/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getPaymentGateways(@PathVariable("lang") String lang) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/gateways/shop/{code}/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getPaymentGatewaysForShop(@PathVariable("lang") String lang, @PathVariable("code") String shopCode) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/gateways/shop/allowed/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getAllowedPaymentGatewaysForShops(@PathVariable("lang") String lang) throws Exception;


    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/gateways/configure/all/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGateway> getPaymentGatewaysWithParameters(@PathVariable("lang") String lang) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/gateways/configure/shop/{code}/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGateway> getPaymentGatewaysWithParametersForShop(@PathVariable("lang") String lang, @PathVariable("code") String shopCode) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/gateways/configure/{label}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayParameter> update(@PathVariable("label") String pgLabel, @RequestBody List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/gateways/configure/{label}/{code}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayParameter> update(@PathVariable("code") String shopCode, @PathVariable("label") String pgLabel, @RequestBody List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/gateways/offline/{label}/{state}", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDisabledFlag(@PathVariable("label") String pgLabel, @PathVariable("state") boolean disabled) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/gateways/offline/{code}/{label}/{state}", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDisabledFlag(@PathVariable("code") String shopCode,@PathVariable("label") String pgLabel, @PathVariable("state") boolean disabled) throws Exception;


}
