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

package org.yes.cart.service.async.impl;

import org.slf4j.Logger;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobStatus;

/**
 * User: denispavlov
 * Date: 23/06/2016
 * Time: 08:34
 */
public class JobStatusListenerWithLoggerImpl implements JobStatusListener {

    private final JobStatusListener wrapped;
    private final JobStatusListener logger;

    public JobStatusListenerWithLoggerImpl(final JobStatusListener listener, final Logger log) {
        this.wrapped = listener;
        this.logger = new JobStatusListenerLoggerWrapperImpl(log);
    }


    /** {@inheritDoc} */
    @Override
    public JobStatus getLatestStatus() {
        return wrapped.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public String getJobToken() {
        return wrapped.getJobToken();
    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing() {
        this.wrapped.notifyPing();
        this.logger.notifyPing();
    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing(final String msg) {
        this.wrapped.notifyPing(msg);
        this.logger.notifyPing(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyMessage(final String msg) {
        this.wrapped.notifyMessage(msg);
        this.logger.notifyMessage(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyWarning(final String warning) {
        this.wrapped.notifyWarning(warning);
        this.logger.notifyWarning(warning);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error) {
        this.wrapped.notifyError(error);
        this.logger.notifyError(error);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, final Exception exp) {
        this.wrapped.notifyError(error, exp);
        this.logger.notifyError(error, exp);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyCompleted() {
        this.wrapped.notifyCompleted();
        this.logger.notifyCompleted();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return wrapped.isCompleted();
    }

    /** {@inheritDoc} */
    @Override
    public long getTimeoutValue() {
        return wrapped.getTimeoutValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTimedOut() {
        return wrapped.isTimedOut();
    }
}
