/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.bulkimport.service.impl;

import org.yes.cart.bulkimport.model.ImportJobStatus;
import org.yes.cart.bulkimport.model.impl.ImportJobStatusImpl;
import org.yes.cart.bulkimport.service.BulkImportStatusListener;
import org.yes.cart.bulkimport.service.ImportService;

import java.util.UUID;

/**
 * Null listener is effective a factory for generating null status objects
 *
 * User: denispavlov
 * Date: 12-07-30
 * Time: 9:50 AM
 */
public class BulkImportStatusListenerNullImpl implements BulkImportStatusListener {

    private final UUID token;
    private final String message;

    public BulkImportStatusListenerNullImpl(final String message) {
        token = UUID.randomUUID();
        this.message = message;
    }

    /** {@inheritDoc} */
    public String getJobToken() {
        return token.toString();
    }

    /** {@inheritDoc} */
    public ImportJobStatus getLatestStatus() {
        return  new ImportJobStatusImpl(getJobToken(), ImportJobStatus.State.UNDEFINED, message);
    }

    /** {@inheritDoc} */
    public void notifyPing() {
        // nothing
    }

    /** {@inheritDoc} */
    public void notifyMessage(final String message) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    public void notifyWarning(final String warning) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    public void notifyError(final String error) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    public void notifyCompleted(final ImportService.BulkImportResult result) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean isTimedOut() {
        return false;
    }
}
