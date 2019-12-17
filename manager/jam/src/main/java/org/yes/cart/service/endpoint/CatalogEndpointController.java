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
@RequestMapping("/catalog")
public interface CatalogEndpointController {


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/brand/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoBrand> getFilteredBrands(@RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/brand/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoBrand getBrandById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/brand", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoBrand createBrand(@RequestBody VoBrand vo)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/brand", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoBrand updateBrand(@RequestBody VoBrand vo)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/brand/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeBrand(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/brand/attributes/{brandId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueBrand> getBrandAttributes(@PathVariable("brandId") long brandId) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/brand/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueBrand> updateBrand(@RequestBody List<MutablePair<VoAttrValueBrand, Boolean>> vo) throws Exception;





    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/producttypes/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoProductTypeInfo> getFilteredProductTypes(@RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/producttype/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductType getProductTypeById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMPIMADMIN"})
    @RequestMapping(value = "/producttype", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductType createProductType(@RequestBody VoProductType vo)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/producttype", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoProductType updateProductType(@RequestBody VoProductType vo)  throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/producttype/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeProductType(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/producttype/attributes/{typeId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductTypeAttr> getProductTypeAttributes(@PathVariable("typeId") long typeId) throws Exception;

    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/producttype/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductTypeAttr> updateProductType(@RequestBody List<MutablePair<VoProductTypeAttr, Boolean>> vo) throws Exception;



    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/productsuppliercatalogs/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoProductSupplierCatalog> getAllProductSuppliersCatalogs() throws Exception;




    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/category/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCategory> getAllCategories() throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/category/branch/{branch}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCategory> getBranchCategories(@PathVariable("branch") long branch, @RequestParam(value = "expand", required = false) String expand) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/category/branchpaths/", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Long> getBranchesCategoriesPaths(@RequestParam(value = "expand", required = false) String expand) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/category/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoCategory> getFilteredCategories(@RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/category/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCategory getCategoryById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/category", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCategory createCategory(@RequestBody VoCategory voCategory) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/category", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCategory updateCategory(@RequestBody VoCategory voCategory) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/category/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCategory(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN","ROLE_SMCATALOGUSER","ROLE_SMPIMADMIN","ROLE_SMPIMUSER"})
    @RequestMapping(value = "/category/attributes/{categoryId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCategory> getCategoryAttributes(@PathVariable("categoryId") long categoryId) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCATALOGADMIN"})
    @RequestMapping(value = "/category/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueCategory> updateCategory(@RequestBody List<MutablePair<VoAttrValueCategory, Boolean>> vo) throws Exception;

}
