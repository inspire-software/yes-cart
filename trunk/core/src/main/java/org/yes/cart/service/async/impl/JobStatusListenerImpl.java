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

package org.yes.cart.service.async.impl;

import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobStatusImpl;

import java.util.UUID;

/**
 * Default listener that builds report from messages and has a basic
 * timeout mechanism. by using current system millis.
 *
 * This is not a thread safe implementation.
 *
 * User: denispavlov
 * Date: 12-07-30
 * Time: 9:50 AM
 */
public class JobStatusListenerImpl implements JobStatusListener {

    private static final int REPORT_MAX_CHARS = 80000;
    private static final int MSG_TIMEOUT = 60000;

    private int reportMaxChars = REPORT_MAX_CHARS;
    private final UUID token;
    private JobStatus.Completion result;

    private int warn = 0;
    private int err = 0;

    private long timeout = MSG_TIMEOUT;
    private long lastMsgTimestamp = System.currentTimeMillis();
    private boolean timedOut = false;

    private final StringBuffer report = new StringBuffer();
    private boolean reportIsCut = false;

    private String pingMsg;

    public JobStatusListenerImpl() {
        token = UUID.randomUUID();
    }

    public JobStatusListenerImpl(final int reportMaxChars, final long timeout) {
        this();
        this.reportMaxChars = reportMaxChars;
        this.timeout = timeout;
    }

    /** {@inheritDoc} */
    public String getJobToken() {
        return token.toString();
    }

    /** {@inheritDoc} */
    public JobStatus getLatestStatus() {

        final JobStatus.State state;
        if (result != null) {
            state = JobStatus.State.FINISHED;
        } else if (report.length() == 0) {
            state = JobStatus.State.STARTED;
        } else {
            state = JobStatus.State.INPROGRESS;
        }

        final String reportOut = formatReport(report, pingMsg, reportIsCut);

        return new JobStatusImpl(getJobToken(), state, result, reportOut);
    }

    private String formatReport(final StringBuffer report, final String pingMsg, final boolean reportIsCut) {
        final StringBuilder reportOut = new StringBuilder();
        if (reportIsCut) {
            reportOut.append("\n\n...\n\n");
        }
        reportOut.append(report);
        if (pingMsg != null) {
            reportOut.append("\n> ").append(pingMsg);
        }
        return reportOut.toString();
    }

    /** {@inheritDoc} */
    public void notifyPing() {
        lastMsgTimestamp = System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    public void notifyPing(final String msg) {
        pingMsg = msg;
        notifyPing();
    }

    /** {@inheritDoc} */
    public void notifyMessage(final String message) {
        if (result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        append(report, "INFO: ", message, "\n");
        notifyPing();
    }

    /** {@inheritDoc} */
    public void notifyWarning(final String warning) {
        if (result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        append(report, "WARNING: ", warning, "\n");
        notifyPing();
        warn++;
    }

    /** {@inheritDoc} */
    public void notifyError(final String error) {
        if (result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        append(report, "ERROR: ", error, "\n");
        notifyPing();
        err++;
    }

    /** {@inheritDoc} */
    public void notifyCompleted() {
        if (this.result != null) {
            throw new IllegalArgumentException("Job " + token.toString() + " has finished and cannot be updated");
        }
        this.result = err > 0 ? JobStatus.Completion.ERROR : JobStatus.Completion.OK;
        this.pingMsg = null; // we have completed the job, clear ping message
        notifyPing();
    }

    private void append(StringBuffer report, String ... text) {
        for (String part : text) {
            report.append(part);
        }
        if (report.length() > reportMaxChars) {
            report.delete(0, report.length() - reportMaxChars);
        }
    }

    /** {@inheritDoc} */
    public boolean isCompleted() {
        return result != null;
    }

    /** {@inheritDoc} */
    public long getTimeoutValue() {
        return timeout;
    }

    /** {@inheritDoc} */
    public boolean isTimedOut() {
        if (timedOut) {
            return true;
        }
        timedOut = lastMsgTimestamp + timeout < System.currentTimeMillis();
        if (timedOut) {
            this.notifyError("Timed out (timeout: " + timeout + "millis)");
            this.result = JobStatus.Completion.ERROR;
        }
        return timedOut;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "JobStatusListenerImpl{" +
                "token=" + token +
                ", warnings=" + warn +
                ", errors=" + err +
                ", timedOut=" + timedOut +
                ", lastMsgTimestamp=" + lastMsgTimestamp +
                '}';
    }
}
