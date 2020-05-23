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
 * Date: 21/08/2016
 * Time: 16:00
 */
@Controller
@Api(value = "Content", description = "Content controller", tags = "content")
public interface ContentEndpointController {


    @ApiOperation(value = "Retrieve shop content")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shops/{shopId}/content", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoContent> getShopContent(@ApiParam(value = "Shop ID", required = true) @PathVariable("shopId") long shopId) throws Exception;

    @ApiOperation(value = "Retrieve content branch")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shops/{shopId}/content/{id}/branches", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoContent> getShopBranchContent(@ApiParam(value = "Shop ID", required = true) @PathVariable("shopId") long shopId, @ApiParam(value = "Content ID", required = true) @PathVariable("id") long branch, @ApiParam(value = "Content branches to expand (pipe delimited list of ID)") @RequestParam(value = "expand", required = false) String expand) throws Exception;

    @ApiOperation(value = "Retrieve content branch path")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shops/{shopId}/content/branchpaths", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Long> getShopBranchesContentPaths(@ApiParam(value = "Shop ID") @PathVariable("shopId") long shopId, @ApiParam(value = "Content branches to expand (pipe delimited list of ID)") @RequestParam(value = "expand", required = false) String expand) throws Exception;

    @ApiOperation(value = "Shop content search")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shops/{shopId}/content/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoContent> getFilteredContent(@ApiParam(value = "Shop ID", required = true) @PathVariable("shopId") long shopId, @ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'seoInternal.uri' and 'guid'" +
                    "\n  * [^] prefix - exact match by parent 'guid' (e.g. '!MyParentContent')" +
                    "\n  * [@] prefix - match by 'seoInternal.uri' (e.g. '@my-content-uri')" +
                    "\n* contentIds - optional injected automatically according to current user access", name = "filter")
                                                @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve content")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/content/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoContentWithBody getContentById(@ApiParam(value = "Content ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create content")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/content", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoContentWithBody createContent(@ApiParam(value = "Content", name = "vo", required = true) @RequestBody VoContent voContent) throws Exception;

    @ApiOperation(value = "Update content")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/content", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoContentWithBody updateContent(@ApiParam(value = "Content", name = "vo", required = true) @RequestBody VoContentWithBody voContent) throws Exception;

    @ApiOperation(value = "Delete content")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/content/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeContent(@ApiParam(value = "Content ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Retrieve content attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/content/{id}/attributes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueContent> getContentAttributes(@ApiParam(value = "Content ID", required = true) @PathVariable("id") long contentId) throws Exception;


    @ApiOperation(value = "Update content attributes")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/content/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueContent> updateContent(@ApiParam(value = "Attributes", name = "vo", required = true) @RequestBody List<MutablePair<VoAttrValueContent, Boolean>> vo) throws Exception;


    @ApiOperation(value = "Retrieve content body")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/content/{id}/body", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoContentBody> getContentBody(@ApiParam(value = "Content ID", required = true) @PathVariable("id") long contentId) throws Exception;


    @ApiOperation(value = "Show shop mail template preview")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER"})
    @RequestMapping(value = "/shops/{shopId}/mail/{template}/preview", method = RequestMethod.GET,  produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    String getShopMail(@ApiParam(value = "Shop ID", required = true) @PathVariable("shopId") long shopId, @ApiParam(value = "Mail template code", required = true) @PathVariable("template") String template, @ApiParam(value = "Order details to use") @RequestParam(value = "order", required = false) String order, @ApiParam(value = "Order delivery details to use") @RequestParam(value = "delivery", required = false) String delivery, @ApiParam(value = "Customer details to use") @RequestParam(value = "customer", required = false) String customer) throws Exception;

}
