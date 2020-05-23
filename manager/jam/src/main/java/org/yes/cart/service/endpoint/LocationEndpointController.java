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
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 15:32
 */
@Controller
@Api(value = "Location", description = "Location controller", tags = "location")
@RequestMapping("/locations")
public interface LocationEndpointController {


    @ApiOperation(value = "Countries search")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/countries/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoCountryInfo> getFilteredCountries(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'countryCode', 'isoCode', 'name' and 'guid'", name = "filter")
                                                       @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve country")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/countries/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCountry getCountryById(@ApiParam(value = "Country ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create country")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/countries", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCountry createCountry(@ApiParam(value = "Country", name = "vo", required = true) @RequestBody VoCountry voCountry)  throws Exception;

    @ApiOperation(value = "Update country")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/countries", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoCountry updateCountry(@ApiParam(value = "Country", name = "vo", required = true) @RequestBody VoCountry voCountry)  throws Exception;

    @ApiOperation(value = "Delete country")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/countries/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeCountry(@ApiParam(value = "Country ID", required = true) @PathVariable("id") long id) throws Exception;


    @ApiOperation(value = "Retrieve country states")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/countries/{id}/states", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoState> getCountryStatesAll(@ApiParam(value = "Country ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "States search")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/states/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoSearchResult<VoState> getFilteredStates(@ApiParam(
            value = "Search criteria with the following parameter support:" +
                    "\n* filter - text filter expands to 'countryCode', 'stateCode', 'name' and 'guid'" +
                    "\n* countryCodes - optional list of code to limit search", name = "filter")
                                              @RequestBody VoSearchContext filter) throws Exception;

    @ApiOperation(value = "Retrieve state")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/states/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoState getStateById(@ApiParam(value = "State ID", required = true) @PathVariable("id") long id) throws Exception;

    @ApiOperation(value = "Create state")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/states", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoState createState(@ApiParam(value = "State", name = "vo", required = true) @RequestBody VoState voState)  throws Exception;


    @ApiOperation(value = "Update state")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/states", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoState updateState(@ApiParam(value = "State", name = "vo", required = true) @RequestBody VoState voState)  throws Exception;


    @ApiOperation(value = "Delete state")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/states/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    void removeState(@ApiParam(value = "State ID", required = true) @PathVariable("id") long id) throws Exception;

}
