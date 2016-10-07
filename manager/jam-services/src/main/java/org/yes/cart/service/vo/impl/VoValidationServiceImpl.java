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

import java.util.Map;

/**
 * User: denispavlov
 * Date: 29/08/2016
 * Time: 15:24
 */
public class VoValidationServiceImpl implements VoValidationService {

    private final Map<String, Map<String, VoValidationService>> validators;

    public VoValidationServiceImpl(final Map<String, Map<String, VoValidationService>> validators) {
        this.validators = validators;
    }

    /**
     * {@inheritDoc}
     */
    public VoValidationResult validate(final VoValidationRequest request) {

        final String subject = request.getSubject();

        final Map<String, VoValidationService> subjectValidators = this.validators.get(subject);

        if (subjectValidators != null) {

            final String field = request.getField();

            final VoValidationService subjectFieldValidator = subjectValidators.get(field);

            if (subjectFieldValidator != null) {

                return subjectFieldValidator.validate(request);

            }

            return new VoValidationResult(request, "INVALID_FIELD");

        }

        return new VoValidationResult(request, "INVALID_SUBJECT");
    }
}
