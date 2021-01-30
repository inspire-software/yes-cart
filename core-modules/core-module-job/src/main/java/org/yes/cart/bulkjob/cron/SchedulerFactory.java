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

import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.yes.cart.config.ActiveConfiguration;
import org.yes.cart.config.ActiveConfigurationDetector;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.config.impl.ActiveConfigurationImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.utils.log.Markers;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: inspiresoftware
 * Date: 19/01/2021
 * Time: 16:16
 */
public class SchedulerFactory extends SchedulerFactoryBean implements ActiveConfigurationDetector {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerFactory.class);

    private JobTriggerBuilder triggerBuilder;

    private List<Trigger> staticTriggers;

    @Override
    public void setTriggers(final Trigger... triggers) {

        this.staticTriggers = new ArrayList<>();
        this.staticTriggers.addAll(Arrays.asList(triggers));

    }

    @Override
    public void start() throws SchedulingException {

        // load all triggers
        final List<Pair<Trigger, JobDetail>> triggers = this.triggerBuilder.build(true);

        // filter out triggers that will never fire (i.e. past schedules)
        final List<Trigger> allTriggers = triggers.stream().map(Pair::getFirst).filter(trigger -> {
            OperableTrigger trig = (OperableTrigger) trigger;
            final boolean willFire = trig.computeFirstFireTime(null) != null;
            if (!willFire) {
                LOG.info("Trigger {} with schedule {} will not be scheduled as it will never fire",
                        trigger.getKey(), trigger.getJobDataMap().get("_currentSchedule"));
            }
            return willFire;
        }).collect(Collectors.toList());
        if (this.staticTriggers != null) {
            allTriggers.addAll(this.staticTriggers);
        }
        if (!allTriggers.isEmpty()) {
            super.setTriggers(allTriggers.toArray(new Trigger[0]));
        }

        try {
            registerJobsAndTriggers();
        } catch (SchedulerException e) {
            LOG.error(Markers.alert(), "Unable to schedule cron job triggers: " + e.getMessage(), e);
        }

        super.start();
    }

    @Override
    public void setQuartzProperties(final Properties quartzProperties) {
        super.setQuartzProperties(quartzProperties);
        for (final Object key : quartzProperties.keySet()) {
            final String strKey = String.valueOf(key);
            System.setProperty(strKey, quartzProperties.getProperty(strKey));
        }
    }

    /**
     * Spring IoC.
     *
     * @param triggerBuilder trigger builder
     */
    public void setTriggerBuilder(final JobTriggerBuilder triggerBuilder) {
        this.triggerBuilder = triggerBuilder;
    }

    @Override
    public List<ActiveConfiguration> getActive() {

        final List<ActiveConfiguration> active = new ArrayList<>();

        try {
            final StdScheduler scheduler = (StdScheduler) getScheduler();

            for (final String groupName : scheduler.getJobGroupNames()) {

                for (final JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    if (jobDetail != null) {
                        final JobDataMap dataMap = jobDetail.getJobDataMap();
                        if (dataMap != null) {
                            final Object processor = dataMap.get("job");
                            if (processor instanceof Configuration) {
                                if (!isPaused(processor, dataMap)) {
                                    final ConfigurationContext cfg = ((Configuration) processor).getCfgContext();
                                    active.add(new ActiveConfigurationImpl(
                                            cfg.getName(),
                                            cfg.getCfgInterface(),
                                            "cronjob:" + dataMap.get("jobName")
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exp) {
            LOG.error("Unable to read active triggers in scheduler: " + exp.getMessage(), exp);
        }

        return active;
    }

    private boolean isPaused(final Object processor, final JobDataMap dataMap) {
        if (processor instanceof PausableProcessor) {
            return ((PausableProcessor) processor).isPaused();
        } else if (processor instanceof PausableCronJobProcessor) {
            return ((PausableCronJobProcessor) processor).isPaused(dataMap);
        }
        return false;
    }
}
