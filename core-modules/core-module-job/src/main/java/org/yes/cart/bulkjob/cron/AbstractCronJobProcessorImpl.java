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

package org.yes.cart.bulkjob.cron;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.JobDefinitionService;
import org.yes.cart.service.domain.JobService;
import org.yes.cart.utils.log.Markers;

import java.io.StringReader;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;

/**
 * User: inspiresoftware
 * Date: 20/01/2021
 * Time: 10:02
 */
public abstract class AbstractCronJobProcessorImpl implements
        CronJobProcessor, PausableCronJobProcessor, Configuration, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCronJobProcessorImpl.class);

    private ConfigurationContext cfgContext;

    private JobDefinitionService jobDefinitionService;
    private JobService jobService;


    /** {@inheritDoc} */
    @Override
    public void process(final Map<String, Object> context) {
        final Long jobId = (Long) context.get("jobId");
        final Long jobDefinitionId = (Long) context.get("jobDefinitionId");
        if (jobId != null && jobDefinitionId != null) {
            final Job job = this.jobService.getById(jobId);
            if (job != null) {
                if (job.getPaused() != null && job.getPaused()) {
                    LOG.debug("Job is PAUSED (pause key: {})", context.get("jobName"));
                    return;
                }
                final JobDefinition definition = this.jobDefinitionService.getById(jobDefinitionId);
                if (definition != null) {
                    final Instant before = Instant.now();
                    final Pair<JobStatus, Instant> statusAndCheckpoint = processInternal(context, job, definition);
                    final Instant after = Instant.now();
                    job.setLastRun(before);
                    job.setLastState(statusAndCheckpoint.getFirst().getCompletion() != null ? statusAndCheckpoint.getFirst().getCompletion().name() : "N/A");
                    job.setLastDurationMs(after.toEpochMilli() - before.toEpochMilli());
                    job.setLastReport(StringUtils.right(statusAndCheckpoint.getFirst().getReport(), 4000));
                    job.setCheckpoint(statusAndCheckpoint.getSecond());
                    if (statusAndCheckpoint.getFirst().getCompletion() != JobStatus.Completion.ERROR
                            && job.getPauseOnError() != null && job.getPauseOnError()) {
                        LOG.warn(Markers.alert(), "Paused {} job because last executed resulted in error", definition.getJobName());
                        job.setPaused(true);
                    }
                    this.jobService.update(job);
                } else {
                    LOG.error("Job trigger {} is misconfigured ... missing job definition {}", context.get("jobName"), jobDefinitionId);
                }
            } else {
                LOG.error("Job trigger {} is misconfigured ... missing job {}", context.get("jobName"), jobId);
            }
        } else {
            LOG.warn("Job trigger {} is misconfigured ... missing jobId and/or jobDefinitionId", context.get("jobName"));
        }
    }

    /**
     * Internal process execution.
     *
     * @param context trigger context
     * @param job job
     * @param definition job definition
     *
     * @return status and optionally checkpoint timestamp
     */
    protected abstract Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition);

    /**
     * Read context from the job definition as property source.
     *
     * @param context trigger context
     * @param job job
     * @param definition job definition
     *
     * @return properties from {@link JobDefinition#getContext()}
     */
    protected Properties readContextAsProperties(final Map<String, Object> context, final Job job, final JobDefinition definition) {
        final Properties properties = new Properties();
        try {
            if (definition.getContext() != null) {
                properties.load(new StringReader(definition.getContext()));
            }
        } catch (Exception exp) {
            LOG.error("Unable to load context as properties for definition {}", definition.getJobName());
        }
        return properties;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPaused(final Map<String, Object> context) {
        final Long jobId = (Long) context.get("jobId");
        if (jobId != null) {
            final Job job = this.jobService.getById(jobId);
            if (job != null) {
                return job.getPaused() != null && job.getPaused();
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setPaused(final Map<String, Object> context, final boolean paused) {
        final Long jobId = (Long) context.get("jobId");
        if (jobId != null) {
            final Job job = this.jobService.getById(jobId);
            if (job != null) {
                job.setPaused(paused);
                this.jobService.update(job);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

    /**
     * Spring IoC.
     *
     * @param jobDefinitionService service
     */
    public void setJobDefinitionService(final JobDefinitionService jobDefinitionService) {
        this.jobDefinitionService = jobDefinitionService;
    }

    /**
     * Spring IoC.
     *
     * @param jobService service
     */
    public void setJobService(final JobService jobService) {
        this.jobService = jobService;
    }
}
