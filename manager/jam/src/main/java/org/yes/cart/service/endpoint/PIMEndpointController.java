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
 * Date: 26/09/2016
 * Time: 18:31
 */
@Controller
@Api(value = "PIM", description = "PIM controller", tags = "pim")
@RequestMapping("/pim")
public interface PIMEndpointController {


    @ApiOperation(value = "Retrieve all association types")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/associations", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAssociation> getAllAssociations() throws Exception;

    @ApiOperation(value = "Product search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/products/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoProduct> getFilteredProducts(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'seoInternal.uri' and 'guid'" +
                    "\n  * [#] prefix - match by 'guid', 'code', 'manufacturerCode', 'manufacturerPartCode', 'supplierCode', 'pimCode' or 'tag' (e.g. '#SKU-0001')" +
                    "\n  * [?] prefix - match by 'brand.guid', 'brand.name', 'producttype.guid' or 'producttype.name' (e.g. '?Toshiba')" +
                    "\n  * [!] prefix - exact match by 'guid', 'code', 'manufacturerCode', 'manufacturerPartCode', 'supplierCode', 'pimCode' or 'tag' (e.g. '!SKU-0001')" +
                    "\n  * [^] prefix - match by category hierarchy 'productCategory.cotegory.guid' (e.g. '^notebooks')" +
                    "\n  * [^!] prefix - match in specific category 'productCategory.cotegory.guid' (e.g. '^!notebooks')" +
                    "\n  * [\\*] prefix - exact match by 'productId' (e.g. '\\* 1000101')" +
                    "\n* supplierCatalogCodes - optional injected automatically according to current user access", name = "filter")
                                                  @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve product")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductWithLinks getProductById(@ApiParam(value = "Product ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create product")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/products", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductWithLinks createProduct(@ApiParam(value = "Product", name = "vo", required = true) @RequestBody VoProduct vo)  throws Exception;

    @ApiOperation(value = "Update product")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/products", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductWithLinks updateProduct(@ApiParam(value = "Product", name = "vo", required = true) @RequestBody VoProductWithLinks vo)  throws Exception;

    @ApiOperation(value = "Delete product")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeProduct(@ApiParam(value = "Product ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve product attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/products/{id}/attributes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProduct> getProductAttributes(@ApiParam(value = "Product ID", required = true) @PathVariable("id") long productId) throws Exception;


    @ApiOperation(value = "Update product attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/products/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProduct> updateProduct(@ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoAttrValueProduct, Boolean>> vo) throws Exception;



    @ApiOperation(value = "Retrieve all product SKU")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/products/{id}/skus", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductSku> getProductSkuAll(@ApiParam(value = "Product ID", required = true) @PathVariable("id") long productId) throws Exception;


    @ApiOperation(value = "SKU search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/skus/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoProductSku> getFilteredProductSkus(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'manufacturerCode' and 'code'" +
                    "\n  * [!] prefix - exact match by 'guid', 'code', 'manufacturerCode', 'barCode', 'supplierCode' or 'tag' (e.g. '!SKU-00001')" +
                    "\n  * [\\*] prefix - match by 'skuId' or 'product.productId' (e.g. '\\* 10001010')" +
                    "\n* supplierCatalogCodes - optional injected automatically according to current user access", name = "filter")
                                                        @RequestBody VoSearchContext filter) throws Exception;


    @ApiOperation(value = "Retrieve SKU")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/skus/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductSku getSkuById(@ApiParam(value = "SKU ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create SKU")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/skus", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductSku createSku(@ApiParam(value = "SKU", name = "vo", required = true) @RequestBody VoProductSku vo)  throws Exception;

    @ApiOperation(value = "Update SKU")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/skus", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductSku updateSku(@ApiParam(value = "SKU", name = "vo", required = true) @RequestBody VoProductSku vo)  throws Exception;


    @ApiOperation(value = "Delete SKU")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/skus/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeSku(@ApiParam(value = "SKU ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve SKU attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN","ROLE_SMPIMUSER","ROLE_SMWAREHOUSEADMIN","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/skus/{id}/attributes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProductSku> getSkuAttributes(@ApiParam(value = "SKU ID", required = true) @PathVariable("id") long skuId) throws Exception;


    @ApiOperation(value = "Update SKU attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/skus/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueProductSku> updateSku(@ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoAttrValueProductSku, Boolean>> vo) throws Exception;


}
