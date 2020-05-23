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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoPaymentGateway;
import org.yes.cart.domain.vo.VoPaymentGatewayInfo;
import org.yes.cart.domain.vo.VoPaymentGatewayParameter;
import org.yes.cart.domain.vo.VoPaymentGatewayStatus;

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/08/2016
 * Time: 17:55
 */
@Controller
@Api(value = "Payment gateways", description = "Payment gateways controller", tags = "payment")
public interface PaymentGatewaysEndpointController {


    @ApiOperation(value = "Retrieve all payment gateways (basic details)")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/paymentgateways", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getPaymentGateways(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang, @ApiParam(value = "Show only available (enabled) payment gateways", required = false) @RequestParam(value = "enabledOnly", required = false) boolean enabledOnly) throws Exception;

    @ApiOperation(value = "Retrieve shop's payment gateways", tags = { "payment", "shop" })
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/shops/{code}/paymentgateways", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getPaymentGatewaysForShop(@ApiParam(value = "Language code", required = true) @RequestParam("lang")  String lang, @ApiParam(value = "Shop code", required = true) @PathVariable("code") String shopCode) throws Exception;


    @ApiOperation(value = "Retrieve all payment gateways (full details)")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/paymentgateways/details", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGateway> getPaymentGatewaysWithParameters(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang, @ApiParam(value = "Show secure details (attributes)", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure) throws Exception;


    @ApiOperation(value = "Retrieve shop's payment gateways (full details)", tags = { "payment", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/shops/{code}/paymentgateways/details", params = "includeSecure=false", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGateway> getPaymentGatewaysWithParametersForShop(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang, @ApiParam(value = "Shop code", required = true) @PathVariable("code") String shopCode, @ApiParam(value = "Show secure details (attributes)", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure) throws Exception;

    @ApiOperation(value = "Retrieve shop's payment gateways (full details)", tags = { "payment", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/shops/{code}/paymentgateways/details", params = "includeSecure=true", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGateway> getPaymentGatewaysWithParametersForShopSecure(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang, @ApiParam(value = "Shop code", required = true) @PathVariable("code") String shopCode, @ApiParam(value = "Show secure details (attributes)", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure) throws Exception;

    @ApiOperation(value = "Update global payment gateway parameters")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/paymentgateways/{label}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayParameter> update(@ApiParam(value = "Payment gateway label", required = true) @PathVariable("label") String pgLabel, @ApiParam(value = "Update secure details (attributes)", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure, @ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception;

    @ApiOperation(value = "Update shop's payment gateway parameters", tags = { "payment", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/shops/{code}/paymentgateways/{label}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayParameter> update(@ApiParam(value = "Shop code", required = true) @PathVariable("code") String shopCode, @ApiParam(value = "Payment gateway label", required = true) @PathVariable("label") String pgLabel, @ApiParam(value = "Update secure details (attributes)", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure, @ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception;

    @ApiOperation(value = "Enable/disable payment gateway globally")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/paymentgateways/{label}/status", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDisabledFlag(@ApiParam(value = "Payment gateway label", required = true) @PathVariable("label") String pgLabel, @ApiParam(value = "Status", name = "vo", required = true) @RequestBody VoPaymentGatewayStatus status) throws Exception;

    @ApiOperation(value = "Enable/disable payment gateway for shop", tags = { "payment", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/shops/{code}/paymentgateways/{label}/status", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updateDisabledFlag(@ApiParam(value = "Shop code", required = true) @PathVariable("code") String shopCode, @ApiParam(value = "Payment gateway label", required = true) @PathVariable("label") String pgLabel, @ApiParam(value = "Status", name = "vo", required = true) @RequestBody VoPaymentGatewayStatus status) throws Exception;


}
