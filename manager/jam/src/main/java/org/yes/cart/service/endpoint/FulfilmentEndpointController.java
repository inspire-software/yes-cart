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
@RequestMapping("/fulfilment")
public interface FulfilmentEndpointController {

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/centre/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoFulfilmentCentreInfo> getFilteredFulfilmentCentres(@RequestBody VoSearchContext filter) throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/centre/shop/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopFulfilmentCentre> getShopFulfilmentCentres(@PathVariable("id") long shopId) throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/centre/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre getFulfilmentCentreById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/centre", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre createFulfilmentCentre(@RequestBody VoFulfilmentCentre vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/centre/shop/{id}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre createShopFulfilmentCentre(@RequestBody VoFulfilmentCentreInfo vo, @PathVariable("id") long shopId)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/centre", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoFulfilmentCentre updateFulfilmentCentre(@RequestBody VoFulfilmentCentre vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/centre/shop", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopFulfilmentCentre> updateShopFulfilmentCentres(@RequestBody List<VoShopFulfilmentCentre> vo)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/centre/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeFulfilmentCentre(@PathVariable("id") long id) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMWAREHOUSEUSER"})
    @RequestMapping(value = "/inventory/centre/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoInventory> getFilteredInventory(@RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMWAREHOUSEUSER"})
    @RequestMapping(value = "/inventory/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoInventory getInventoryById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/inventory", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoInventory createInventory(@RequestBody VoInventory vo) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/inventory", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoInventory updateInventory(@RequestBody VoInventory vo) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN"})
    @RequestMapping(value = "/inventory/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeInventory(@PathVariable("id") long id) throws Exception;


}
