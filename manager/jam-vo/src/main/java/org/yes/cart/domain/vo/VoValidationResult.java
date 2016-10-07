/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.domain.vo;

/**
 * User: denispavlov
 * Date: 29/08/2016
 * Time: 15:14
 */
public class VoValidationResult extends VoValidationRequest {

    private long duplicateId;
    private String errorCode;

    public VoValidationResult() {
    }

    public VoValidationResult(final VoValidationRequest request) {
        this(request.getSubjectId(), request.getSubject(), request.getField(), request.getValue(), 0L, null);
    }

    public VoValidationResult(final VoValidationRequest request, final String errorCode) {
        this(request.getSubjectId(), request.getSubject(), request.getField(), request.getValue(), 0L, errorCode);
    }

    public VoValidationResult(final VoValidationRequest request, final long duplicateId, final String errorCode) {
        this(request.getSubjectId(), request.getSubject(), request.getField(), request.getValue(), duplicateId, errorCode);
    }

    public VoValidationResult(final long subjectId, final String subject, final String field, final String value, final long duplicateId, final String errorCode) {
        super(subjectId, subject, field, value);
        this.duplicateId = duplicateId;
        this.errorCode = errorCode;
    }

    public long getDuplicateId() {
        return duplicateId;
    }

    public void setDuplicateId(final long duplicateId) {
        this.duplicateId = duplicateId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
}
