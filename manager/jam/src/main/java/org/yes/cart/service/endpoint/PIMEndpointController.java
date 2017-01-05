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
 * Date: 26/09/2016
 * Time: 18:31
 */
@Controller
@RequestMapping("/pim")
public interface PIMEndpointController {

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/associations/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAssociation> getAllAssociations() throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product/filtered/{max}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProduct> getFilteredProducts(@RequestBody String filter, @PathVariable("max") int max) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductWithLinks getProductById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductWithLinks createProduct(@RequestBody VoProduct vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductWithLinks updateProduct(@RequestBody VoProductWithLinks vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/product/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeProduct(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product/attributes/{productId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProduct> getProductAttributes(@PathVariable("productId") long productId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProduct> updateProduct(@RequestBody List<MutablePair<VoAttrValueProduct, Boolean>> vo) throws Exception;



    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product/sku/{productId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductSku> getProductSkuAll(@PathVariable("productId") long productId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/product/sku/filtered/{max}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductSku> getFilteredProductSkus(@RequestBody String filter, @PathVariable("max") int max) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/sku/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductSku getSkuById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/sku", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductSku createSku(@RequestBody VoProductSku vo)  throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/sku", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductSku updateSku(@RequestBody VoProductSku vo)  throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/sku/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeSku(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/sku/attributes/{skuId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProductSku> getSkuAttributes(@PathVariable("skuId") long skuId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/sku/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProductSku> updateSku(@RequestBody List<MutablePair<VoAttrValueProductSku, Boolean>> vo) throws Exception;


}
