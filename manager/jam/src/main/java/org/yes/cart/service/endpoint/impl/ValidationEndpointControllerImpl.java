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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoValidationRequest;
import org.yes.cart.domain.vo.VoValidationResult;
import org.yes.cart.service.endpoint.ValidationEndpointController;
import org.yes.cart.service.vo.VoValidationService;

/**
 * User: denispavlov
 * Date: 29/08/2016
 * Time: 15:52
 */
@Component
public class ValidationEndpointControllerImpl implements ValidationEndpointController {

    private final VoValidationService voValidationService;

    @Autowired
    public ValidationEndpointControllerImpl(final VoValidationService voValidationService) {
        this.voValidationService = voValidationService;
    }

    public @ResponseBody
    VoValidationResult validate(@RequestBody final VoValidationRequest vo) throws Exception {
        return this.voValidationService.validate(vo);
    }
}
