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

package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.vo.VoValidationRequest;
import org.yes.cart.domain.vo.VoValidationResult;
import org.yes.cart.service.vo.VoValidationService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 23/01/2020
 * Time: 08:20
 */
public class VoValidationServiceCompositeCheckImpl implements VoValidationService {

    private final List<VoValidationService> services;

    public VoValidationServiceCompositeCheckImpl(final List<VoValidationService> services) {
        this.services = services;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoValidationResult validate(final VoValidationRequest request) {
        VoValidationResult res = null;
        for (final VoValidationService service : services) {
            res = service.validate(request);
            if (res.getErrorCode() != null) {
                break;
            }
        }
        return res;
    }
}
