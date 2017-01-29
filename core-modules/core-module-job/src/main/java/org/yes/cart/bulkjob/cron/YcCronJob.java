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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.cluster.node.NodeService;

/**
 * Generic job contained to run Runnable jobs.
 *
 * The configuration for this job bean must contain:
 * jobName - String name
 * job - Spring bean that implements Runnable
 *
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 09:33
 */
public class YcCronJob extends QuartzJobBean implements StatefulJob {

    private final Logger log = ShopCodeContext.getLog(this);

    /** {@inheritDoc} */
    @Override
    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {

        final String jobName = (String) context.getMergedJobDataMap().get("jobName");
        final Runnable job = (Runnable) context.getMergedJobDataMap().get("job");
        final NodeService nodeService = (NodeService) context.getMergedJobDataMap().get("nodeService");

        final String nodeId = nodeService.getCurrentNode().getId();
        final long start = getTimeNow();

        log.info("Starting job {} on {}", jobName, nodeId);

        try {
            job.run();

            final long sec = getExecutionTimeInSeconds(start);
            log.info("Finished job {} on {} in {}s, next run {}", new Object[]{ jobName, nodeId, sec, context.getNextFireTime() });

        } catch (Throwable thw) {

            final long sec = getExecutionTimeInSeconds(start);
            log.error("Terminated job {} on {} in {}s, next run {}", new Object[] { jobName, nodeId, sec, context.getNextFireTime() });
            log.error(thw.getMessage(), thw);

        }

    }

    private long getExecutionTimeInSeconds(final long start) {
        final long finish = getTimeNow();
        final long ms = (finish - start);
        return (ms > 0 ? ms / 1000 : 0);
    }

    private long getTimeNow() {
        return System.currentTimeMillis();
    }
}
