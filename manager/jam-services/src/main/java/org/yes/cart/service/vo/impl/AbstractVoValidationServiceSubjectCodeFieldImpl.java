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

import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 29/08/2016
 * Time: 15:31
 */
public abstract class AbstractVoValidationServiceSubjectCodeFieldImpl implements VoValidationService {

    private final Pattern validPattern;

    public AbstractVoValidationServiceSubjectCodeFieldImpl() {
        this(Pattern.compile("^[A-Za-z0-9\\-_]+$"));
    }

    protected AbstractVoValidationServiceSubjectCodeFieldImpl(Pattern validPattern) {
        this.validPattern = validPattern;
    }

    /**
     * {@inheritDoc}
     */
    public VoValidationResult validate(final VoValidationRequest request) {

        final String valueToCheck = request.getValue();
        if (valueToCheck == null) {
            return new VoValidationResult(request, 0L, null);
        }

        if (validPattern.matcher(valueToCheck).matches()) {

            final Long duplicate = getDuplicateId(request.getSubjectId(), valueToCheck);
            if (duplicate == null) {
                return new VoValidationResult(request);
            }

            return new VoValidationResult(request, duplicate, "DUPLICATE");

        }
        return new VoValidationResult(request, 0L, "INVALID_DATA");
    }

    /**
     * Extension hook.
     *
     * @param currentId current PK (0 for transient)
     * @param valueToCheck value to check passed from UI
     *
     * @return PK of object that uses this value already
     */
    protected abstract Long getDuplicateId(long currentId, String valueToCheck);
}
