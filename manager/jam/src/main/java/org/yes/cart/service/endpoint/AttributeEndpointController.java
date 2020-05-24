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
@Api(value = "Attributes", description = "Attribute controller", tags = "attributes")
public interface AttributeEndpointController {

    @ApiOperation(value = "Attribute value types")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/attributes/etypes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoEtype> getAllEtypes() throws Exception;

    @ApiOperation(value = "Attribute groups")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/attributes/groups", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoAttributeGroup> getAllGroups() throws Exception;

    @ApiOperation(value = "Attributes search")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/attributes/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoAttribute> getFilteredAttributes(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'name', 'displayNameInternal', 'description' and 'code'" +
                    "\n  * [!] prefix - exact match by 'code' (e.g. '!MY_CODE')" +
                    "\n* groups - optional list of group codes", name = "filter")
                                                      @RequestBody(required = false) VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Product types which include provided attribute code")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/attributes/{code}/producttypes", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<Long, String>> getProductTypesByAttributeCode(@ApiParam(value = "Attribute code", required = true) @PathVariable("code") String code) throws Exception;


    @ApiOperation(value = "Retrieve attribute")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/attributes/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAttribute getAttributeById(@ApiParam(value = "Attribute ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Create attribute")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/attributes", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAttribute createAttribute(@ApiParam(value = "Attribute", name = "vo", required = true) @RequestBody VoAttribute vo)  throws Exception;


    @ApiOperation(value = "Update attribute")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/attributes", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoAttribute updateAttribute(@ApiParam(value = "Attribute", name = "vo", required = true) @RequestBody VoAttribute vo)  throws Exception;


    @ApiOperation(value = "Delete attribute")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/attributes/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeAttribute(@ApiParam(value = "Attribute ID", required = true) @PathVariable("id") long id) throws Exception;


}
