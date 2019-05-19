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
import org.yes.cart.service.async.model.impl.JobStatusImpl;
import org.yes.cart.util.MessageFormatUtils;
import org.yes.cart.util.log.Markers;

/**
 * User: denispavlov
 * Date: 10/11/2015
 * Time: 15:14
 */
public class JobStatusListenerLoggerWrapperImpl implements JobStatusListener {

    private final Logger logger;

    private String lastPing;


    public JobStatusListenerLoggerWrapperImpl(final Logger logger) {
        this.logger = logger;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getLatestStatus() {
        return new JobStatusImpl(
                null,
                JobStatus.State.UNDEFINED,
                null,
                lastPing
        );
    }

    /** {@inheritDoc} */
    @Override
    public String getJobToken() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing() {

    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing(final String msg, Object... args) {
        this.lastPing = MessageFormatUtils.format(msg, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyMessage(final String message, Object... args) {
        logger.debug(message, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyWarning(final String warning, Object... args) {
        logger.warn(warning, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, Object... args) {
        logger.error(error, args);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, final Exception exp, Object... args) {
        logger.error(Markers.alert(), MessageFormatUtils.format(error, args), exp);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyCompleted() {

    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return false;
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
}
