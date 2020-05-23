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
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 19/08/2016
 * Time: 11:48
 */
@Controller
@Api(value = "Fulfilment", description = "Fulfilment controller", tags = "fulfilment")
public interface FulfilmentEndpointController {

    @ApiOperation(value = "Fulfilment centre search")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/fulfilment/centres/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoFulfilmentCentreInfo> getFilteredFulfilmentCentres(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'description', 'code' and 'guid'" +
                    "\n* shopIds - optional injected automatically according to current user access", name = "filter")
                                                                        @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve fulfilment centre shop assignments", tags = { "fulfilment", "shop" })
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/shops/{id}/centres", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopFulfilmentCentre> getShopFulfilmentCentres(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Retrieve fulfilment centre")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/fulfilment/centres/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre getFulfilmentCentreById(@ApiParam(value = "Centre ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create fulfilment centre")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/fulfilment/centres", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre createFulfilmentCentre(@ApiParam(value = "Centre", name = "vo", required = true) @RequestBody VoFulfilmentCentre vo)  throws Exception;

    @ApiOperation(value = "Create fulfilment centre with shop assignment", tags = { "fulfilment", "shop" })
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/shops/{id}/centres", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre createShopFulfilmentCentre(@ApiParam(value = "Centre", name = "vo", required = true) @RequestBody VoFulfilmentCentreInfo vo, @ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId)  throws Exception;

    @ApiOperation(value = "Update fulfilment centre")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/fulfilment/centres", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre updateFulfilmentCentre(@ApiParam(value = "Centre", name = "vo", required = true) @RequestBody VoFulfilmentCentre vo)  throws Exception;

    @ApiOperation(value = "Update fulfilment centre shop assignments")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/fulfilment/centres/shops", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopFulfilmentCentre> updateShopFulfilmentCentres(@ApiParam(value = "Centre assignments", name = "vo", required = true) @RequestBody List<VoShopFulfilmentCentre> vo)  throws Exception;

    @ApiOperation(value = "Delete fulfilment centre")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/fulfilment/centres/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeFulfilmentCentre(@ApiParam(value = "Centre ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Fulfilment centre inventory search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMWAREHOUSEUSER"})
    @RequestMapping(value = "/fulfilment/inventory/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoInventory> getFilteredInventory(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'skuCode', 'sku.product.code', 'sku.product.name' and 'sku.name'" +
                    "\n  * [-] prefix - 'quantity' less than (e.g. '-100' less than 100 in stock)" +
                    "\n  * [+] prefix - 'reserved' more than (e.g. '+10' more than 10 new reservations)" +
                    "\n  * [!] prefix - exact match by 'skuCode', 'sku.product.code', 'sku.product.manufacturerCode', 'sku.product.pimCode', 'sku.barCode' or 'sku.manufacturerCode' (e.g. '!SKU-0001')" +
                    "\n  * date search - match between 'availablefrom' and 'availableto' (e.g. '2009-01<' - after Jan 2009, '2009-01<2020' - after Jan 2009 but before 2020)" +
                    "\n* centreId - mandatory centre ID", name = "filter")
                                                     @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve inventory")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMWAREHOUSEUSER"})
    @RequestMapping(value = "/fulfilment/inventory/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoInventory getInventoryById(@ApiParam(value = "Inventory ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create inventory")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/fulfilment/inventory", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoInventory createInventory(@ApiParam(value = "Inventory", name = "vo", required = true) @RequestBody VoInventory vo) throws Exception;

    @ApiOperation(value = "Update inventory")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/fulfilment/inventory", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoInventory updateInventory(@ApiParam(value = "Inventory", name = "vo", required = true) @RequestBody VoInventory vo) throws Exception;

    @ApiOperation(value = "Delete inventory")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/fulfilment/inventory/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeInventory(@ApiParam(value = "Inventory ID", required = true) @PathVariable("id") long id) throws Exception;


}
