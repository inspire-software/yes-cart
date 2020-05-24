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

import org.slf4j.Logger;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobStatus;

import java.util.Map;

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
        this.logger = new JobStatusListenerLoggerWrapperImpl(log, listener.getJobToken());
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
    public void notifyPing(final String msg, Object... args) {
        this.wrapped.notifyPing(msg, args);
        this.logger.notifyPing(msg, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyMessage(final String msg, Object... args) {
        this.wrapped.notifyMessage(msg, args);
        this.logger.notifyMessage(msg, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyWarning(final String warning, Object... args) {
        this.wrapped.notifyWarning(warning, args);
        this.logger.notifyWarning(warning, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, Object... args) {
        this.wrapped.notifyError(error, args);
        this.logger.notifyError(error, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, final Exception exp, Object... args) {
        this.wrapped.notifyError(error, exp, args);
        this.logger.notifyError(error, exp, args);
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

    /** {@inheritDoc} */
    @Override
    public int count(final String name) {
        return wrapped.count(name);
    }

    /** {@inheritDoc} */
    @Override
    public int count(final String name, final int add) {
        return wrapped.count(name, add);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getCounts() {
        return wrapped.getCounts();
    }

    /** {@inheritDoc} */
    @Override
    public int getCount(final String name) {
        return wrapped.getCount(name);
    }


    /** {@inheritDoc} */
    @Override
    public void reset() {
        this.wrapped.reset();
    }

}
