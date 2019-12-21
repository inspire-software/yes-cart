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
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 14:44
 */
@Controller
@RequestMapping("/pricing")
public interface PricingEndpointController {



    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/price/shop/{shopId}/currency/{currency}/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoPriceList> getFilteredPriceLists(@PathVariable("shopId") long shopId, @PathVariable("currency") String currency, @RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
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





    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/tax/shop/{shopCode}/currency/{currency}/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoTax> getFilteredTax(@PathVariable("shopCode") String shopCode, @PathVariable("currency") String currency, @RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/tax/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTax getTaxById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/tax", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTax createTax(@RequestBody VoTax vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/tax", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTax updateTax(@RequestBody VoTax vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/tax/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeTax(@PathVariable("id") long id) throws Exception;






    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/taxconfig/tax/{taxId}/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoTaxConfig> getFilteredTaxConfig(@PathVariable("taxId") long taxId, @RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/taxconfig/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTaxConfig getTaxConfigById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/taxconfig", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTaxConfig createTaxConfig(@RequestBody VoTaxConfig vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/taxconfig/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeTaxConfig(@PathVariable("id") long id) throws Exception;




    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotion/shop/{shopCode}/currency/{currency}/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoPromotion> getFilteredPromotion(@PathVariable("shopCode") String shopCode, @PathVariable("currency") String currency, @RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotion/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPromotion getPromotionById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotion", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPromotion createPromotion(@RequestBody VoPromotion vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotion", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPromotion updatePromotion(@RequestBody VoPromotion vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotion/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removePromotion(@PathVariable("id") long id) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotion/offline/{id}/{state}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updatePromotionDisabledFlag(@PathVariable("id") long id, @PathVariable("state") boolean state) throws Exception;



    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotioncoupon/{promoId}/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoPromotionCoupon> getFilteredPromotionCoupons(@PathVariable("promoId") long promotionId, @RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotioncoupon", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void createPromotionCoupons(@RequestBody VoPromotionCoupon vo) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotioncoupon/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removePromotionCoupon(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotion/test/shop/{shopCode}/currency/{currency}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCart testPromotions(@PathVariable("shopCode") String shopCode, @PathVariable("currency") String currency, @RequestBody VoPromotionTest testData) throws Exception;

}
