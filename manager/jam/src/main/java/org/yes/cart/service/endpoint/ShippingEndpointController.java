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
import org.yes.cart.domain.vo.VoCarrier;
import org.yes.cart.domain.vo.VoCarrierLocale;
import org.yes.cart.domain.vo.VoCarrierSla;
import org.yes.cart.domain.vo.VoShopCarrier;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 18:18
 */
@Controller
@RequestMapping("/shipping")
public interface ShippingEndpointController {

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMSHIPPINGADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/carrier/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCarrier> getAllCarriers() throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMSHIPPINGADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/carrier/shop/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopCarrier> getShopCarriers(@PathVariable("id") long shopId) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMSHIPPINGADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/carrier/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier getCarrierById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/carrier", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier createCarrier(@RequestBody VoCarrierLocale vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/carrier/shop/{id}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier createShopCarrier(@RequestBody VoCarrierLocale vo, @PathVariable("id") long shopId)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/carrier", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrier updateCarrier(@RequestBody VoCarrier vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/carrier/shop", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShopCarrier> updateShopCarriers(@RequestBody List<VoShopCarrier> vo)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/carrier/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCarrier(@PathVariable("id") long id) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMSHIPPINGADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/carriersla/all/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCarrierSla> getCarrierSlas(@PathVariable("id") long carrierId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMSHIPPINGADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/carriersla/filtered/{max}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCarrierSla> getFilteredCarrierSlas(@RequestBody String filter, @PathVariable("max") int max) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMSHIPPINGADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/carriersla/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrierSla getCarrierSlaById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/carriersla", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrierSla createCarrierSla(@RequestBody VoCarrierSla vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/carriersla", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCarrierSla updateCarrierSla(@RequestBody VoCarrierSla vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/carriersla/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCarrierSla(@PathVariable("id") long id) throws Exception;



}
