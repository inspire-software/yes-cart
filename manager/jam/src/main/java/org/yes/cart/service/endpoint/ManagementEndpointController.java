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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoLicenseAgreement;
import org.yes.cart.domain.vo.VoManager;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 09:19
 */
@Controller
@Api(value = "Management", description = "Management controller", tags = "management")
@RequestMapping("/management")
public interface ManagementEndpointController {

    @ApiOperation(value = "Retrieve currently logged in manager")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/myself", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoManager getMyself() throws Exception;

    @ApiOperation(value = "Retrieve currently logged in manager license")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/license", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoLicenseAgreement getMyAgreement() throws Exception;

    @ApiOperation(value = "Accept license agreement as currently logged in manager")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/license", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoLicenseAgreement acceptMyAgreement() throws Exception;

    @ApiOperation(value = "Retireve applicable UI configurations for currently logged in manager")
    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/myui", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    Map<String, String> getMyUiPreferences() throws Exception;

}
