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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;


/**
 * Created by Igor_Azarny on 3/15/2016.
 */
@Controller
@Api(value = "Shop", description = "Shop controller", tags = "shop")
@RequestMapping("/shops")
public interface ShopEndpointController {

    @ApiOperation(value = "Retireve all shops")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShop> getAll() throws Exception;

    @ApiOperation(value = "Retrieve sub shops")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER"})
    @RequestMapping(value = "/{id}/subs", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoShop> getAllSubs(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve shop")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShop getById(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Create shop")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShop create(@ApiParam(value = "Shop", name = "vo", required = true) @RequestBody VoShop voShop) throws Exception;

    @ApiOperation(value = "Create sub shop")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/subs", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShop createSub(@ApiParam(value = "Sub shop", name = "vo", required = true) @RequestBody VoSubShop voShop) throws Exception;

    @ApiOperation(value = "Update shop")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShop update(@ApiParam(value = "Shop", name = "vo", required = true) @RequestBody VoShop voShop) throws Exception;

    @ApiOperation(value = "Delete shop")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(method = RequestMethod.DELETE,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void remove(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve shop summary")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER"})
    @RequestMapping(value = "/{id}/summary", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopSummary getSummary(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long id, @ApiParam(value = "Language code", required = true) @RequestParam("lang") String lang) throws Exception;

    @ApiOperation(value = "Retrieve shop SEO")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/{id}/seo", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopSeo getLocalization(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Update shop SEO")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/seo", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopSeo update(@ApiParam(value = "SEO", name = "vo", required = true) @RequestBody VoShopSeo voShopSeo)  throws Exception;

    @ApiOperation(value = "Retrieve shop URLs")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/{id}/urls", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopUrl getUrl(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Update shop URLs")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/urls", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopUrl update(@ApiParam(value = "URLs", name = "vo", required = true) @RequestBody VoShopUrl voShopUrl)  throws Exception;


    @ApiOperation(value = "Retrieve shop aliases")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER"})
    @RequestMapping(value = "/{id}/aliases", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopAlias getAlias(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Update shop aliases")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/aliases", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopAlias update(@ApiParam(value = "Aliases", name = "vo", required = true) @RequestBody VoShopAlias voShopAlias)  throws Exception;


    @ApiOperation(value = "Retrieve shop aliases")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMMARKETINGADMIN","ROLE_SMMARKETINGUSER"})
    @RequestMapping(value = "/{id}/currencies", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopSupportedCurrencies getCurrency(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Update shop aliases")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/currencies", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopSupportedCurrencies update(@ApiParam(value = "Currencies", name = "vo", required = true) @RequestBody VoShopSupportedCurrencies supportedCurrencies) throws Exception;

    @ApiOperation(value = "Retrieve shop languages")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER"})
    @RequestMapping(value = "/{id}/languages", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopLanguages getLanguage(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Update shop languages")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/languages", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopLanguages update(@ApiParam(value = "Languages", name = "vo", required = true) @RequestBody VoShopLanguages langs) throws Exception;

    @ApiOperation(value = "Retrieve shop locations")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSHIPPINGADMIN","ROLE_SMSHIPPINGUSER"})
    @RequestMapping(value = "/{id}/locations", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopLocations getLocation(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Update shop locations")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/locations", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShopLocations update(@ApiParam(value = "Locations", name = "vo", required = true) @RequestBody VoShopLocations locations) throws Exception;

    @ApiOperation(value = "Retrieve shop categories")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER"})
    @RequestMapping(value = "/{id}/categories", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCategory> getCategories(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId) throws Exception;

    @ApiOperation(value = "Update shop categories")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/{id}/categories", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCategory> update(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId, @ApiParam(value = "Categories", name = "vo", required = true) @RequestBody List<VoCategory> voCategories) throws Exception;

    @ApiOperation(value = "Update shop status")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/{id}/status", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoShop updateDisabledFlag(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId, @ApiParam(value = "Status", name = "vo", required = true) @RequestBody VoShopStatus state) throws Exception;


    @ApiOperation(value = "Retrieve shop attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER"})
    @RequestMapping(value = "/{id}/attributes", params = "includeSecure=false", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueShop> getShopAttributes(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId, @ApiParam(value = "Include secure attributes", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure) throws Exception;


    @ApiOperation(value = "Retrieve shop attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/{id}/attributes", params = "includeSecure=true", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueShop> getShopAttributesSecure(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopId, @ApiParam(value = "Include secure attributes", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure) throws Exception;


    @ApiOperation(value = "Update shop attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueShop> update(@ApiParam(value = "Update secure attributes", required = false) @RequestParam(value = "includeSecure", required = false) boolean includeSecure, @ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoAttrValueShop, Boolean>> vo) throws Exception;


}
