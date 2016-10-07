/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.vo.VoCountry;
import org.yes.cart.domain.vo.VoState;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 15:32
 */
@Controller
@RequestMapping("/location")
public interface LocationEndpointController {

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/country/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCountry> getAllCountries() throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/country/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCountry getCountryById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/country", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCountry createCountry(@RequestBody VoCountry voCategory)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/country", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCountry updateCountry(@RequestBody VoCountry voCategory)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/country/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCountry(@PathVariable("id") long id) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/state/all/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoState> getAllStates(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/state/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoState getStateById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/state", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoState createState(@RequestBody VoState voState)  throws Exception;


    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/state", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoState updateState(@RequestBody VoState voState)  throws Exception;


    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/state/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeState(@PathVariable("id") long id) throws Exception;

}
