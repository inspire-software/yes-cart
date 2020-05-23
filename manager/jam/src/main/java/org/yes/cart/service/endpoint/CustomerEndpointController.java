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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 21/08/2016
 * Time: 16:00
 */
@Controller
@Api(value = "Customer", description = "Customer controller", tags = "customer")
@RequestMapping("/customers")
public interface CustomerEndpointController {


    @ApiOperation(value = "Customers search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoCustomerInfo> getFilteredCustomer(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'email', 'firstname', 'lastname', 'customerType' and 'pricingPolicy'" +
                    "\n  * [#] prefix - exact match by 'customerId' or partial by 'email', 'tag', 'companyName1' or 'companyName2' (e.g. '#1000001')" +
                    "\n  * [?] prefix - match by 'email', 'firstname' or 'lastname' (e.g. '?smith')" +
                    "\n  * [@] prefix - match by 'address.addrline1', 'address.addrline2', 'address.city', 'address.countryCode', 'address.stateCode', 'address.postcode', 'address.companyName1' or 'address.companyName2' (e.g. '@london')" +
                    "\n  * [$] prefix - match by 'customerType' or 'pricingPolicy' (e.g. '$B2B')" +
                    "\n  * date search - match by 'createdTimestamp' (e.g. '2009-01<' - after Jan 2009, '2009-01<2020' - after Jan 2009 but before 2020)" +
                    "\n* shopIds - optional injected automatically according to current user access", name = "filter")
                                                       @RequestBody(required = false) VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Customer types")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/types", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<String, String>> getCustomerTypes(@ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang) throws Exception;

    @ApiOperation(value = "Customer available types")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/{id}/types", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<String, String>> getCustomerTypes(@ApiParam(value = "Customer ID", required = true) @PathVariable("id") long customerId, @ApiParam(value = "Language code") @RequestParam("lang") String lang) throws Exception;

    @ApiOperation(value = "Retrieve customer")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomer getCustomerById(@ApiParam(value = "Customer ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create customer")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomer createCustomer(@ApiParam(value = "Customer", name = "vo", required = true) @RequestBody VoCustomer vo)  throws Exception;

    @ApiOperation(value = "Update customer")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomer updateCustomer(@ApiParam(value = "Customer", name = "vo", required = true) @RequestBody VoCustomer vo)  throws Exception;

    @ApiOperation(value = "Delete customer")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCustomer(@ApiParam(value = "Customer ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve customer attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/{id}/attributes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCustomer> getCustomerAttributes(@ApiParam(value = "Customer ID", required = true) @PathVariable("id") long customerId) throws Exception;


    @ApiOperation(value = "Update customer attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCustomer> updateCustomer(@ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoAttrValueCustomer, Boolean>> vo) throws Exception;


    @ApiOperation(value = "Create password reset in scope of specified shop")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/{id}/reset/{shopId}", method = RequestMethod.POST)
    @ResponseBody
    void resetPassword(@ApiParam(value = "Customer ID", required = true) @PathVariable("id") long customerId, @ApiParam(value = "Shop ID") @PathVariable("shopId") long shopId) throws Exception;


    @ApiOperation(value = "Retrieve customer address book and apply specified shop format")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/{id}/addressbook", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAddressBook getAddressBook(@ApiParam(value = "Customer ID", required = true) @PathVariable("id") long customerId, @ApiParam(value = "Shop ID of shop to use for formatting address") @RequestParam("shopId") long formattingShopId, @ApiParam(value = "Language code") @RequestParam("lang") String lang) throws Exception;


    @ApiOperation(value = "Create customer address")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/addressbook", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAddress createAddress(@ApiParam(value = "Address", name = "vo", required = true) @RequestBody VoAddress vo)  throws Exception;

    @ApiOperation(value = "Update customer address")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/addressbook", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAddress updateAddress(@ApiParam(value = "Address", name = "vo", required = true) @RequestBody VoAddress vo)  throws Exception;

    @ApiOperation(value = "Delete customer address")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTERCUSTOMER"})
    @RequestMapping(value = "/addressbook/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeAddress(@ApiParam(value = "Address ID", required = true) @PathVariable("id") long id) throws Exception;


}
