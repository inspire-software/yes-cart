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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 09/08/2016
 * Time: 18:38
 */
@Controller
@RequestMapping("/attributes")
public interface AttributeEndpointController {

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/etype/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoEtype> getAllEtypes() throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/group/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttributeGroup> getAllGroups() throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/attribute/filtered", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoAttribute> getFilteredAttributes(@RequestBody VoSearchContext filter) throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/attribute/producttype/{code}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<Long, String>> getProductTypesByAttributeCode(@PathVariable("code") String code) throws Exception;


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/attribute/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAttribute getAttributeById(@PathVariable("id") long id) throws Exception;



    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/attribute", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAttribute createAttribute(@RequestBody VoAttribute vo)  throws Exception;


    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/attribute", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAttribute updateAttribute(@RequestBody VoAttribute vo)  throws Exception;


    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/attribute/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeAttribute(@PathVariable("id") long id) throws Exception;


}
