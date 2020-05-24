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
 * Date: 21/08/2016
 * Time: 16:00
 */
@Controller
@Api(value = "Catalog", description = "Catalog controller", tags = "catalog")
@RequestMapping("/catalog")
public interface CatalogEndpointController {

    @ApiOperation(value = "Brands search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/brands/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoBrand> getFilteredBrands(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'description' and 'guid'", name = "filter")
                                              @RequestBody(required = false) VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve brand")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/brands/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoBrand getBrandById(@ApiParam(value = "Brand ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create brand")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/brands", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoBrand createBrand(@ApiParam(value = "Brand", name = "vo", required = true) @RequestBody VoBrand vo)  throws Exception;

    @ApiOperation(value = "Update brand")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/brands", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoBrand updateBrand(@ApiParam(value = "Brand", name = "vo", required = true) @RequestBody VoBrand vo)  throws Exception;

    @ApiOperation(value = "Delete brand")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/brands/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeBrand(@ApiParam(value = "Brand ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve brand attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/brands/{id}/attributes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueBrand> getBrandAttributes(@ApiParam(value = "Brand ID", required = true) @PathVariable("id") long brandId) throws Exception;

    @ApiOperation(value = "Update brand attributes")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/brands/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueBrand> updateBrand(@ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoAttrValueBrand, Boolean>> vo) throws Exception;



    @ApiOperation(value = "Product types search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/producttypes/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoProductTypeInfo> getFilteredProductTypes(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'description' and 'guid'" +
                    "\n  * [!] prefix - exact match by 'guid' or 'name' (e.g. '!My Type')" +
                    "\n  * [#] prefix - exact match by attribute.code assigned to a type (e.g. '#MY_CODE')", name = "filter")
                                                              @RequestBody(required = false) VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve product type")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/producttypes/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductType getProductTypeById(@ApiParam(value = "Product type ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create product type")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/producttypes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductType createProductType(@ApiParam(value = "Product type", name = "vo", required = true) @RequestBody VoProductType vo)  throws Exception;

    @ApiOperation(value = "Update product type")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/producttypes", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductType updateProductType(@ApiParam(value = "Product type", name = "vo", required = true) @RequestBody VoProductType vo)  throws Exception;

    @ApiOperation(value = "Delete product type")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/producttypes/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeProductType(@ApiParam(value = "Product type ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Retrieve product type attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/producttypes/{id}/attributes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductTypeAttr> getProductTypeAttributes(@ApiParam(value = "Product type ID", required = true) @PathVariable("id") long typeId) throws Exception;

    @ApiOperation(value = "Update product type attributes")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/producttypes/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductTypeAttr> updateProductType(@ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoProductTypeAttr, Boolean>> vo) throws Exception;



    @ApiOperation(value = "Retrieve product supplier catalogs")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/productsuppliercatalogs", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductSupplierCatalog> getAllProductSuppliersCatalogs() throws Exception;




    @ApiOperation(value = "Retrieve all categories")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/categories", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCategory> getAllCategories() throws Exception;

    @ApiOperation(value = "Retrieve category branch")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/categories/{id}/branches", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCategory> getBranchCategories(@ApiParam(value = "Category ID", required = true) @PathVariable("id") long branch, @ApiParam(value = "Category branches to expand (pipe delimited list of ID)") @RequestParam(value = "expand", required = false) String expand) throws Exception;

    @ApiOperation(value = "Retrieve category branch path")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/categories/branchpaths", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Long> getBranchesCategoriesPaths(@ApiParam(value = "Category branches to expand (pipe delimited list of ID)") @RequestParam(value = "expand", required = false) String expand) throws Exception;

    @ApiOperation(value = "Categories search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/categories/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoCategory> getFilteredCategories(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'seoInternal.uri' and 'guid'" +
                    "\n  * [^] prefix - exact match by parent 'guid' (e.g. '!MyParentCat')" +
                    "\n  * [@] prefix - match by 'seoInternal.uri' (e.g. '@my-cat-uri')" +
                    "\n* categoryIds - optional injected automatically according to current user access", name = "filter")
                                                     @RequestBody(required = false) VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve category")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCategory getCategoryById(@ApiParam(value = "Category ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create category")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/categories", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCategory createCategory(@ApiParam(value = "Category", name = "vo", required = true) @RequestBody VoCategory voCategory) throws Exception;

    @ApiOperation(value = "Update category")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/categories", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCategory updateCategory(@ApiParam(value = "Category", name = "vo", required = true) @RequestBody VoCategory voCategory) throws Exception;

    @ApiOperation(value = "Delete category")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCategory(@ApiParam(value = "Category ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve category attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/categories/{id}/attributes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCategory> getCategoryAttributes(@ApiParam(value = "Category ID", required = true) @PathVariable("id") long categoryId) throws Exception;

    @ApiOperation(value = "Update category attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/categories/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCategory> updateCategory(@ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoAttrValueCategory, Boolean>> vo) throws Exception;

}
