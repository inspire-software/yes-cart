/*
 * Copyright 2009 Inspire-Software.com
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

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Null listener is effective a factory for generating null status objects
 *
 * User: denispavlov
 * Date: 12-07-30
 * Time: 9:50 AM
 */
public class JobStatusListenerNullImpl implements JobStatusListener {

    private final UUID token;
    private final String message;

    public JobStatusListenerNullImpl(final String message) {
        token = UUID.randomUUID();
        this.message = message;
    }

    /** {@inheritDoc} */
    @Override
    public String getJobToken() {
        return token.toString();
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getLatestStatus() {
        return  new JobStatusImpl(getJobToken(), JobStatus.State.UNDEFINED, JobStatus.Completion.ERROR, message);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing() {
        // nothing
    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing(final String msg, Object... args) {
        // nothing
    }

    /** {@inheritDoc} */
    @Override
    public void notifyMessage(final String message, Object... args) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    @Override
    public void notifyWarning(final String warning, Object... args) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, Object... args) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, final Exception exp, Object... args) {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    @Override
    public void notifyCompleted() {
        throw new IllegalArgumentException("Job is UNDEFINED and cannot be updated");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public long getTimeoutValue() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTimedOut() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int count(final String name) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int count(final String name, final int add) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getCounts() {
        return Collections.emptyMap();
    }


    /** {@inheritDoc} */
    @Override
    public int getCount(final String name) {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void reset() {

    }

}
