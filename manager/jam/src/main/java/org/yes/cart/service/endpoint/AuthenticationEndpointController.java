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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.security.impl.LoginData;

/**
 * User: denispavlov
 * Date: 11/05/2020
 * Time: 00:10
 */
@Controller
@Api(value = "Authentication", description = "Authentication controller", tags = "authentication")
public interface AuthenticationEndpointController {

    @ApiOperation(value = "Authenticate using credentials")
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void authenticate(@ApiParam(value = "Credentials", name = "loginData", required = true) @RequestBody LoginData loginData);

    @ApiOperation(value = "Refresh token")
    @RequestMapping(value = "/refreshtoken", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void refreshToken();

    @ApiOperation(value = "Change password")
    @RequestMapping(value = "/changepwd", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void changePwd(@ApiParam(value = "Change password details", name = "changePwd", required = true) @RequestBody LoginData loginData);

}
