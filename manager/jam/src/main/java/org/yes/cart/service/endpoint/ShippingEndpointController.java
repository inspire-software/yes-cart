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

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 18:18
 */
@Controller
@Api(value = "Shipping", description = "Shipping controller", tags = "shipping")
public interface ShippingEndpointController {

    @ApiOperation(value = "Shipping carriers search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMSHIPPINGADMIN","ROLE_SMSHIPPINGUSER"})
    @RequestMapping(value = "/shipping/carriers/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoCarrierInfo> getFilteredCarriers(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'description' and 'guid'" +
                    "\n* shopIds - optional injected automatically according to current user access", name = "filter")
                                                      @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve shop carriers", tags = { "shipping", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMSHIPPINGADMIN","ROLE_SMSHIPPINGUSER"})
    @RequestMapping(value = "/shops/{id}/carriers", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopCarrierAndSla> getShopCarriers(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Retrieve carrier")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMSHIPPINGADMIN","ROLE_SMSHIPPINGUSER"})
    @RequestMapping(value = "/shipping/carriers/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier getCarrierById(@ApiParam(value = "Carrier ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create carrier")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/shipping/carriers", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier createCarrier(@ApiParam(value = "Carrier", name = "vo", required = true) @RequestBody VoCarrier vo)  throws Exception;

    @ApiOperation(value = "Create carrier assigned to a shop", tags = { "shipping", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/shops/{id}/carriers", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier createShopCarrier(@ApiParam(value = "Carrier", name = "vo", required = true) @RequestBody VoCarrierInfo vo, @ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId)  throws Exception;

    @ApiOperation(value = "Update carrier")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/shipping/carriers", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier updateCarrier(@ApiParam(value = "Carrier", name = "vo", required = true) @RequestBody VoCarrier vo)  throws Exception;

    @ApiOperation(value = "Update shop carriers", tags = { "shipping", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/shops/carriers", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopCarrierAndSla> updateShopCarriers(@ApiParam(value = "Shop carriers", name = "vo", required = true) @RequestBody List<VoShopCarrierAndSla> vo)  throws Exception;

    @ApiOperation(value = "Delete carrier")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/shipping/carriers/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCarrier(@ApiParam(value = "Carrier ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Retrieve carrier SLAs")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMSHIPPINGADMIN","ROLE_SMSHIPPINGUSER"})
    @RequestMapping(value = "/shipping/carriers/{id}/slas", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCarrierSla> getCarrierSlaAll(@ApiParam(value = "Carrier ID", required = true) @PathVariable("id") long carrierId) throws Exception;


    @ApiOperation(value = "Carrier SLA search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMSHIPPINGADMIN","ROLE_SMSHIPPINGUSER"})
    @RequestMapping(value = "/shipping/carrierslas/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoCarrierSlaInfo> getFilteredCarrierSlas(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'description' and 'guid'" +
                    "\n* shopIds - optional injected automatically according to current user access", name = "filter")
                                                            @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve carrier SLA")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMSHIPPINGADMIN","ROLE_SMSHIPPINGUSER"})
    @RequestMapping(value = "/shipping/carrierslas/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrierSla getCarrierSlaById(@ApiParam(value = "Carrier SLA ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create carrier SLA")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/shipping/carrierslas", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrierSla createCarrierSla(@ApiParam(value = "SLA", name = "vo", required = true) @RequestBody VoCarrierSla vo)  throws Exception;

    @ApiOperation(value = "Update carrier SLA")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/shipping/carrierslas", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrierSla updateCarrierSla(@ApiParam(value = "SLA", name = "vo", required = true) @RequestBody VoCarrierSla vo)  throws Exception;

    @ApiOperation(value = "Delete carrier SLA")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/shipping/carrierslas/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCarrierSla(@ApiParam(value = "Carrier SLA ID", required = true) @PathVariable("id") long id) throws Exception;



}
