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
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 14:44
 */
@Controller
@Api(value = "Pricing", description = "Pricing controller", tags = "pricing")
@RequestMapping("/pricing")
public interface PricingEndpointController {



    @ApiOperation(value = "Prices search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/prices/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoPriceList> getFilteredPriceLists(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'skuCode', 'sku.code', 'sku.product.code', 'sku.product.name' or 'sku.name'" +
                    "\n  * [!] prefix - exact match by 'skuCode', 'sku.code', 'sku.product.code', 'sku.product.manufacturerCode', 'sku.product.pimCode', 'sku.barCode' or 'sku.manufacturerCode' (e.g. '!SKU-00001')" +
                    "\n  * [#] prefix - match by 'tag', 'pricingPolicy' or 'ref' (e.g. '#B2B')" +
                    "\n  * [#shipping] - match all shipping prices" +
                    "\n  * date search - match between 'salefrom' and 'saleto' (e.g. '2009-01<' - after Jan 2009, '2009-01<2020' - after Jan 2009 but before 2020)" +
                    "\n* shopCode - mandatory shop code" +
                    "\n* currency - mandatory currency", name = "filter")
                                                      @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve price")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/prices/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPriceList getPriceListById(@ApiParam(value = "Price ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create price")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/prices", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPriceList createPriceList(@ApiParam(value = "Price", name = "vo", required = true) @RequestBody VoPriceList vo)  throws Exception;

    @ApiOperation(value = "Update price")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/prices", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPriceList updatePriceList(@ApiParam(value = "Price", name = "vo", required = true) @RequestBody VoPriceList vo)  throws Exception;

    @ApiOperation(value = "Delete price")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/prices/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removePriceList(@ApiParam(value = "Price ID", required = true) @PathVariable("id") long id) throws Exception;





    @ApiOperation(value = "Taxes search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/taxes/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoTax> getFilteredTax(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'code', 'description'" +
                    "\n  * [%] prefix - exact match by 'taxRate' (e.g. '%10')" +
                    "\n  * [-/+] prefix - match by 'exclusiveOfPrice', can be combined with other prefixes (e.g. '+%10')" +
                    "\n* shopCode - mandatory shop code" +
                    "\n* currency - mandatory currency", name = "filter")
                                         @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve tax")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/taxes/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTax getTaxById(@ApiParam(value = "Tax ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create tax")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/taxes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTax createTax(@ApiParam(value = "Tax", name = "vo", required = true) @RequestBody VoTax vo)  throws Exception;

    @ApiOperation(value = "Update tax")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/taxes", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTax updateTax(@ApiParam(value = "Tax", name = "vo", required = true) @RequestBody VoTax vo)  throws Exception;

    @ApiOperation(value = "Delete tax")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/taxes/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeTax(@ApiParam(value = "Tax ID", required = true) @PathVariable("id") long id) throws Exception;






    @ApiOperation(value = "Tax configs search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/taxconfigs/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoTaxConfig> getFilteredTaxConfig(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'code', 'description'" +
                    "\n  * [@] prefix - exact match by 'countryCode' or partial by 'stateCode' (e.g. '@GB')" +
                    "\n  * [!] prefix - exact match by 'productCode' (e.g. '!SKU-00001')" +
                    "\n  * [!!] - null match by 'productCode', i.e. non-product specific tax configs (e.g. '!')" +
                    "\n  * [#] prefix - match by 'productCode' (e.g. '#SKU-00001')" +
                    "\n  * [^] prefix - match by 'tax.code' (e.g. '^VAT')" +
                    "\n* shopCode - mandatory shop code" +
                    "\n* currency - mandatory currency", name = "filter")
                                                     @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve tax config")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/taxconfigs/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTaxConfig getTaxConfigById(@ApiParam(value = "Tax config ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create tax config")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/taxconfigs", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoTaxConfig createTaxConfig(@ApiParam(value = "Tax config", name = "vo", required = true) @RequestBody VoTaxConfig vo)  throws Exception;

    @ApiOperation(value = "Delete tax config")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/taxconfigs/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeTaxConfig(@ApiParam(value = "Tax config ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Promotions options")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotions/options", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<VoAttribute, List<VoAttribute>>> getPromotionOptions() throws Exception;


    @ApiOperation(value = "Promotions search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotions/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoPromotion> getFilteredPromotion(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'code', 'name' and 'description'" +
                    "\n  * [#] prefix - match by 'code' or 'tag' (e.g. '#SHOP10-PROMO001')" +
                    "\n  * [?] prefix - match by 'eligibilityCondition' or 'promoActionContext' (e.g. '?priceSubTotal > 200')" +
                    "\n  * [-/+] prefix - match by 'active', can be combined with other prefixes (e.g. '+?priceSubTotal > 200')" +
                    "\n  * date search - match between 'enabledFrom' and 'enabledTo' (e.g. '2009-01<' - after Jan 2009, '2009-01<2020' - after Jan 2009 but before 2020)" +
                    "\n* types - optional list of promotion types" +
                    "\n* actions - optional list of promotion actions" +
                    "\n* shopCode - mandatory shop code" +
                    "\n* currency - mandatory currency", name = "filter")
                                                     @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve promotion")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotions/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPromotion getPromotionById(@ApiParam(value = "Promotion ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create promotion")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotions", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPromotion createPromotion(@ApiParam(value = "Promotion", name = "vo", required = true) @RequestBody VoPromotion vo)  throws Exception;

    @ApiOperation(value = "Update promotion")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotions", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoPromotion updatePromotion(@ApiParam(value = "Promotion", name = "vo", required = true) @RequestBody VoPromotion vo)  throws Exception;

    @ApiOperation(value = "Delete promotion")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotions/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removePromotion(@ApiParam(value = "Promotion ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Set promotion status")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotions/{id}/status", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void updatePromotionDisabledFlag(@ApiParam(value = "Promotion ID", required = true) @PathVariable("id") long id, @ApiParam(value = "Status", name = "vo", required = true) @RequestBody VoPromotionStatus state) throws Exception;



    @ApiOperation(value = "Promotion coupons search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotioncoupons/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoPromotionCoupon> getFilteredPromotionCoupons(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'code'" +
                    "\n  * date search - match by 'createdTimestamp' (e.g. '2009-01<' - after Jan 2009, '2009-01<2020' - after Jan 2009 but before 2020)" +
                    "\n* promotionId - mandatory promotion ID", name = "filter")
                                                                  @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Generate coupons")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotioncoupons", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void createPromotionCoupons(@ApiParam(value = "Coupon prototype", name = "vo", required = true) @RequestBody VoPromotionCoupon vo) throws Exception;

    @ApiOperation(value = "Delete coupons")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/promotioncoupons/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removePromotionCoupon(@ApiParam(value = "Coupon ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Test promotion")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/promotions/test", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCart testPromotions(@ApiParam(value = "Test data", required = true) @RequestBody VoPromotionTest testData) throws Exception;

}
