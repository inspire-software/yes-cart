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

package org.yes.cart.bulkjob.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobStatusImpl;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.spring.WrappingBean;

/**
 * User: denispavlov
 * Date: 27/01/2017
 * Time: 10:00
 */
public class PausableProcessorWrapperImpl implements Runnable, PausableProcessor, WrappingBean<Runnable>, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(PausableProcessorWrapperImpl.class);

    private static final JobStatus UNSUPPORTED = new JobStatusImpl(null, JobStatus.State.UNDEFINED, null, null);

    private Runnable processor;
    private SystemService systemService;
    private String pausePreferenceKey;
    private boolean pausePreferenceDefault = true;

    private boolean pauseInitialised = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        if (!pauseInitialised) {

            final String paused = systemService.createOrGetAttributeValue(pausePreferenceKey, "Boolean");

            if (paused == null) {
                systemService.updateAttributeValue(pausePreferenceKey, String.valueOf(pausePreferenceDefault));
            }
            pauseInitialised = true;
        }

        final String paused = systemService.getAttributeValue(pausePreferenceKey);
        if (Boolean.valueOf(paused)) {
            LOG.debug("Job is PAUSED (pause key: {})", this.pausePreferenceKey);
            return;
        }

        this.processor.run();

    }

    @Override
    public Runnable getSource() {
        return processor;
    }

    public void setProcessor(final Runnable processor) {
        this.processor = processor;
    }

    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }

    public void setPausePreferenceKey(final String pausePreferenceKey) {
        this.pausePreferenceKey = pausePreferenceKey;
    }

    public void setPausePreferenceDefault(final boolean pausePreferenceDefault) {
        this.pausePreferenceDefault = pausePreferenceDefault;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPaused() {
        final String paused = systemService.getAttributeValue(pausePreferenceKey);
        return Boolean.valueOf(paused);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaused(final boolean paused) {
        systemService.createOrGetAttributeValue(pausePreferenceKey, "Boolean");
        systemService.updateAttributeValue(pausePreferenceKey, String.valueOf(paused));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobStatus getStatus(final String token) {
        if (this.processor instanceof JobStatusAware) {
            return ((JobStatusAware) this.processor).getStatus(token);
        }
        return UNSUPPORTED;
    }
}
