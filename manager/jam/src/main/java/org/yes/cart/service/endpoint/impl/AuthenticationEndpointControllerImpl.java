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
package org.yes.cart.service.endpoint.impl;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.security.impl.LoginData;
import org.yes.cart.service.endpoint.AuthenticationEndpointController;

/**
 * User: denispavlov
 * Date: 11/05/2020
 * Time: 00:22
 */
@Controller
public class AuthenticationEndpointControllerImpl implements AuthenticationEndpointController {


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    void authenticate(@RequestBody LoginData loginData) {

    }

    @RequestMapping(value = "/refreshtoken", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    void refreshToken() {

    }

    @RequestMapping(value = "/changepwd", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    void changePwd(@RequestBody LoginData loginData) {
        
    }


}
