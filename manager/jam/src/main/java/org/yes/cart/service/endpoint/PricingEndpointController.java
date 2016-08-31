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
import org.yes.cart.domain.vo.VoPriceList;

import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 14:44
 */
@Controller
@RequestMapping("/pricing")
public interface PricingEndpointController {



    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/price/shop/{shopId}/currency/{currency}/filtered/{max}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPriceList> getFilteredPriceLists(@PathVariable("shopId") long shopId, @PathVariable("currency") String currency, @RequestBody String filter, @PathVariable("max") int max) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/price/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPriceList getPriceListById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/price", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPriceList createPriceList(@RequestBody VoPriceList vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/price", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPriceList updatePriceList(@RequestBody VoPriceList vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/price/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removePriceList(@PathVariable("id") long id) throws Exception;



}
