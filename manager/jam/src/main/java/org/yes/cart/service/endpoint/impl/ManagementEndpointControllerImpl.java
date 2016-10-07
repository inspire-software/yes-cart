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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.ManagementEndpointController;
import org.yes.cart.service.vo.VoManagementService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 09:21
 */
@Component
public class ManagementEndpointControllerImpl implements ManagementEndpointController {

    private final VoManagementService voManagementService;

    @Autowired
    public ManagementEndpointControllerImpl(final VoManagementService voManagementService) {
        this.voManagementService = voManagementService;
    }

    @Override
    public @ResponseBody
    VoManagerInfo getMyself() throws Exception {
        return this.voManagementService.getMyself();
    }

    @Override
    public @ResponseBody
    VoLicenseAgreement getMyAgreement() throws Exception {
        return voManagementService.getMyAgreement();
    }

    @Override
    public @ResponseBody
    VoLicenseAgreement acceptMyAgreement() throws Exception {
        return voManagementService.acceptMyAgreement();
    }

}
