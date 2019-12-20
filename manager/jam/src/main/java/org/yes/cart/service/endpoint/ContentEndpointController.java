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
@RequestMapping("/content")
public interface ContentEndpointController {


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shop/{shopId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoContent> getShopContent(@PathVariable("shopId") long shopId) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shop/{shopId}/branch/{branch}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoContent> getShopBranchContent(@PathVariable("shopId") long shopId, @PathVariable("branch") long branch, @RequestParam(value = "expand", required = false) String expand) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shop/{shopId}/branchpaths", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Long> getShopBranchesContentPaths(@PathVariable("shopId") long shopId, @RequestParam(value = "expand", required = false) String expand) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/shop/{shopId}/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoContent> getFilteredContent(@PathVariable("shopId") long shopId, @RequestBody VoSearchContext filter) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoContentWithBody getContentById(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoContentWithBody createContent(@RequestBody VoContent voContent) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoContentWithBody updateContent(@RequestBody VoContentWithBody voContent) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeContent(@PathVariable("id") long id) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/attributes/{contentId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueContent> getContentAttributes(@PathVariable("contentId") long contentId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttrValueContent> updateContent(@RequestBody List<MutablePair<VoAttrValueContent, Boolean>> vo) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMCONTENTADMIN","ROLE_SMCONTENTUSER"})
    @RequestMapping(value = "/body/{contentId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoContentBody> getContentBody(@PathVariable("contentId") long contentId) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMSHOPUSER","ROLE_SMSUBSHOPUSER"})
    @RequestMapping(value = "/mail/{shopId}/{template}", method = RequestMethod.GET,  produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    String getShopMail(@PathVariable("shopId") long shopId, @PathVariable("template") String template, @RequestParam(value = "order", required = false) String order, @RequestParam(value = "delivery", required = false) String delivery, @RequestParam(value = "customer", required = false) String customer) throws Exception;

}
