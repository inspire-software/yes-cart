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
import org.yes.cart.service.async.model.impl.JobStatusImpl;
import org.yes.cart.utils.MessageFormatUtils;
import org.yes.cart.utils.log.Markers;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: denispavlov
 * Date: 10/11/2015
 * Time: 15:14
 */
public class JobStatusListenerLoggerWrapperImpl implements JobStatusListener {

    private final Logger logger;

    private String lastPing;
    private boolean infoMode = false;

    private String token = "N/A";

    private final Map<String, Integer> counts = new ConcurrentHashMap<>();


    public JobStatusListenerLoggerWrapperImpl(final Logger logger, final String token) {
        this.logger = logger;
        this.token = token;
    }

    public JobStatusListenerLoggerWrapperImpl(final Logger logger, final String token, final boolean infoMode) {
        this.logger = logger;
        this.token = token;
        this.infoMode = infoMode;
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
        if (infoMode) {
            logger.info(message, args);
        } else {
            logger.debug(message, args);
        }
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
        if (!counts.isEmpty()) {
            final StringBuilder out = new StringBuilder();
            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                if (out.length() > 0) {
                    out.append(", ");
                }
                out.append(count.getKey()).append(": ").append(count.getValue());
            }
            notifyMessage("Completed {} ... [{}]", token, out.toString());
            notifyPing("Completed {} ... [{}]", token, out.toString());
        } else {
            notifyMessage("Completed {}", token);
            notifyPing("Completed {}", token);
        }
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

    /** {@inheritDoc} */
    @Override
    public int count(final String name) {
        int updated = counts.getOrDefault(name, 0) + 1;
        counts.put(name, updated);
        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public int count(final String name, final int add) {
        int updated = counts.getOrDefault(name, 0) + add;
        counts.put(name, updated);
        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getCounts() {
        return Collections.unmodifiableMap(counts);
    }

    /** {@inheritDoc} */
    @Override
    public int getCount(final String name) {
        return counts.getOrDefault(name, 0);
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        this.counts.clear();
    }

}
