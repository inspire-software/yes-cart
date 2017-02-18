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
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 21/08/2016
 * Time: 16:00
 */
@Controller
@RequestMapping("/customer")
public interface CustomerEndpointController {


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/filtered/{max}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCustomerInfo> getFilteredCustomer(@RequestBody String filter, @PathVariable("max") int max) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/types/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<String, String>> getCustomerTypes(@PathVariable("lang") String lang) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomer getCustomerById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomer createCustomer(@RequestBody VoCustomer vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCustomer updateCustomer(@RequestBody VoCustomer vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCustomer(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/attributes/{customerId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCustomer> getCustomerAttributes(@PathVariable("customerId") long customerId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCustomer> updateCustomer(@RequestBody List<MutablePair<VoAttrValueCustomer, Boolean>> vo) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/reset/{customerId}/{shopId}", method = RequestMethod.POST)
    @ResponseBody
    void resetPassword(@PathVariable("customerId") long customerId, @PathVariable("shopId") long shopId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/addressbook/{id}/{formattingShopId}/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAddressBook getAddressBook(@PathVariable("id") long customerId, @PathVariable("formattingShopId") long formattingShopId, @PathVariable("lang") String lang) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/addressbook", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAddress createAddress(@RequestBody VoAddress vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/addressbook", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAddress updateAddress(@RequestBody VoAddress vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCALLCENTER"})
    @RequestMapping(value = "/addressbook/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeAddress(@PathVariable("id") long id) throws Exception;


}
