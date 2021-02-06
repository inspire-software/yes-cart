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
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.JobDefinitionService;
import org.yes.cart.service.domain.JobService;
import org.yes.cart.utils.RegExUtils;
import org.yes.cart.utils.RuntimeConstants;

import java.util.*;

/**
 * User: inspiresoftware
 * Date: 19/01/2021
 * Time: 16:32
 */
public class JobTriggerBuilderImpl implements JobTriggerBuilder, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(JobTriggerBuilderImpl.class);

    private final JobDefinitionService jobDefinitionService;
    private final JobService jobService;
    private NodeService nodeService;
    private final RuntimeConstants runtimeConstants;

    private ApplicationContext applicationContext;


    public JobTriggerBuilderImpl(final JobDefinitionService jobDefinitionService,
                                 final JobService jobService,
                                 final NodeService nodeService,
                                 final RuntimeConstants runtimeConstants) {
        this.jobDefinitionService = jobDefinitionService;
        this.jobService = jobService;
        this.nodeService = nodeService;
        this.runtimeConstants = runtimeConstants;
    }

    /** {@inheritDoc} */
    @Override
    public List<Pair<Trigger, JobDetail>> build(final boolean verbose) {

        final Node node = this.nodeService.getCurrentNode();

        if (verbose) {
            LOG.info("Loading job definitions for node {}", node.getId());
        }

        final List<JobDefinition> definitions = this.jobDefinitionService.findAll();

        final List<Pair<Trigger, JobDetail>> toSchedule = new ArrayList<>();

        for (final JobDefinition definition : definitions) {

            if (RegExUtils.getInstance(definition.getHostApplicationRegEx()).matches(node.getNodeType())) {

                if (verbose) {
                    LOG.info("Loading job definition {} for node {}", definition.getCode(), node.getId());
                }

                final List<Job> jobs = this.jobService.findByCriteria(" where e.jobDefinitionCode = ?1 and e.nodeId = ?2",
                        definition.getCode(), node.getId());
                if (jobs.isEmpty()) {

                    LOG.info("Creating new job for definition {} for node {}", definition.getCode(), node.getId());

                    final Job job = this.jobService.getGenericDao().getEntityFactory().getByIface(Job.class);
                    job.setCronExpression(determineCronExpression(job, definition));
                    job.setJobDefinitionCode(definition.getCode());
                    job.setNodeId(node.getId());
                    job.setPaused(definition.getDefaultPaused() != null && definition.getDefaultPaused());
                    job.setPauseOnError(false);
                    job.setLastDurationMs(0L);
                    this.jobService.create(job);

                    addTrigger(job, definition, toSchedule, verbose);

                } else {

                    for (final Job job : jobs) {
                        addTrigger(job, definition, toSchedule, verbose);
                    }

                }

            }

        }

        return toSchedule;
    }

    private String determineCronExpression(final Job job, final JobDefinition definition) {
        if (job.getDisabled() == null || job.getDisabled()) {
             return "0 0 0 * * ? 2009"; // schedule to past to reflect disabled, this also will force scheduler to skip it
        }
        return StringUtils.isNotBlank(definition.getDefaultCronExpression()) ?
                definition.getDefaultCronExpression() :
                runtimeConstants.getConstantOrDefault(definition.getDefaultCronExpressionKey(), "0 0 0 * * ? 2099");
    }

    private void addTrigger(final Job job, final JobDefinition definition, final List<Pair<Trigger, JobDetail>> toSchedule, final boolean verbose) {

        try {
            final Map<String, Object> jobDataMap = new HashMap<>();
            jobDataMap.put("jobName", definition.getJobName());
            jobDataMap.put("jobProcessor", definition.getProcessor());
            jobDataMap.put("job", this.applicationContext.getBean(definition.getProcessor()));
            jobDataMap.put("nodeId", this.nodeService.getCurrentNodeId());
            jobDataMap.put("jobId", job.getJobId());
            jobDataMap.put("jobDefinitionId", definition.getJobDefinitionId());

            JobDetailImpl jdi = new JobDetailImpl();
            jdi.setName(definition.getJobName() + " " + job.getJobId());
            jdi.setGroup(Scheduler.DEFAULT_GROUP);
            jdi.setJobClass(CronJob.class);
            jdi.setJobDataMap(new JobDataMap(jobDataMap));
            jdi.setDurability(false);
            jdi.setRequestsRecovery(false);

            jobDataMap.put("jobDetail", jdi);
            jobDataMap.put("_lastRun", job.getLastRun());
            jobDataMap.put("_lastReport", job.getLastReport());
            jobDataMap.put("_lastState", job.getLastState());
            jobDataMap.put("_lastDurationMs", job.getLastDurationMs());
            jobDataMap.put("_originalSchedule", determineCronExpression(job, definition));
            jobDataMap.put("_currentSchedule", job.getCronExpression());
            jobDataMap.put("_jobDefinitionContext", definition.getContext());
            jobDataMap.put("_paused", job.getPaused());
            jobDataMap.put("_pauseOnError", job.getPauseOnError());
            jobDataMap.put("_disabled", job.getDisabled());

            CronTriggerImpl cti = new CronTriggerImpl();
            cti.setName(jdi.getName() + " Trigger");
            cti.setGroup(jdi.getGroup());
            cti.setJobKey(jdi.getKey());
            cti.setJobDataMap(new JobDataMap(jobDataMap));
            cti.setStartTime(new Date());
            cti.setCronExpression(job.getCronExpression());
            cti.setTimeZone(TimeZone.getDefault());

            toSchedule.add(new Pair<>(cti, jdi));

            if (verbose) {
                LOG.info("Creating trigger for {} on {} with schedule {}", definition.getJobName(), job.getNodeId(), job.getCronExpression());
            }

        } catch (NoSuchBeanDefinitionException exp) {
            LOG.error("Unable to schedule trigger for {} on node {}, cause: {}", definition.getJobName(), job.getNodeId(), exp.getMessage());
        } catch (Exception exp) {
            LOG.error(exp.getMessage(), exp);
            LOG.error("Unable to schedule trigger for {} on node {}, cause: {}", definition.getJobName(), job.getNodeId(), exp.getMessage());
        }

    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
