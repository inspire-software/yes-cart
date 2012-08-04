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
 * User: denispavlov
 * Date: 12-07-30
 * Time: 9:50 AM
 */
public class BulkImportStatusListenerImpl implements BulkImportStatusListener {

    private static final int REPORT_MAX_CHARS = 80000;
    private static final int MSG_TIMEOUT = 60000;

    private int reportMaxChars = REPORT_MAX_CHARS;
    private final UUID token;
    private ImportService.BulkImportResult result;

    private int warn = 0;
    private int err = 0;

    private long timeout = MSG_TIMEOUT;
    private long lastMsgTimestamp = System.currentTimeMillis();
    private boolean timedOut = false;

    private final StringBuilder report = new StringBuilder();

    public BulkImportStatusListenerImpl() {
        token = UUID.randomUUID();
    }

    public BulkImportStatusListenerImpl(final int reportMaxChars, final long timeout) {
        this();
        this.reportMaxChars = reportMaxChars;
        this.timeout = timeout;
    }

    /** {@inheritDoc} */
    public String getJobToken() {
        return token.toString();
    }

    /** {@inheritDoc} */
    public ImportJobStatus getLatestStatus() {

        final ImportJobStatus.State state;
        if (result != null) {
            state = ImportJobStatus.State.FINISHED;
        } else if (report.length() == 0) {
            state = ImportJobStatus.State.STARTED;
        } else {
            state = ImportJobStatus.State.INPROGRESS;
        }

        if (report.length() > reportMaxChars) {
            return new ImportJobStatusImpl(getJobToken(), state,
                    "\n\n...\n\n" + report.substring(report.length() - reportMaxChars));
        }

        return new ImportJobStatusImpl(getJobToken(), state, report.toString());
    }

    /** {@inheritDoc} */
    public void notifyPing() {
        lastMsgTimestamp = System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    public void notifyMessage(final String message) {
        if (result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        report.append("INFO: ").append(message).append('\n');
        notifyPing();
    }

    /** {@inheritDoc} */
    public void notifyWarning(final String warning) {
        if (result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        report.append("WARNING: ").append(warning).append('\n');
        notifyPing();
        warn++;
    }

    /** {@inheritDoc} */
    public void notifyError(final String error) {
        if (result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        report.append("ERROR: ").append(error).append('\n');
        notifyPing();
        err++;
    }

    /** {@inheritDoc} */
    public void notifyCompleted(final ImportService.BulkImportResult result) {
        if (this.result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        this.result = result;
        notifyPing();
    }

    /** {@inheritDoc} */
    public boolean isCompleted() {
        return result != null;
    }

    /** {@inheritDoc} */
    public boolean isTimedOut() {
        if (timedOut) {
            return true;
        }
        timedOut = lastMsgTimestamp + timeout < System.currentTimeMillis();
        if (timedOut) {
            this.notifyError("Timed out (timeout: " + timeout + "millis)");
            this.result = ImportService.BulkImportResult.ERROR;
        }
        return timedOut;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "BulkImportStatusListenerImpl{" +
                "token=" + token +
                ", warnings=" + warn +
                ", errors=" + err +
                ", timedOut=" + timedOut +
                ", lastMsgTimestamp=" + lastMsgTimestamp +
                '}';
    }
}
