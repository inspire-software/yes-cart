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
import org.yes.cart.web.service.ws.node.NodeService;

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

        final String nodeId = nodeService.getCurrentNodeId();

        log.info("Starting job {} on {}", jobName, nodeId);

        job.run();

        log.info("Finished job {} on {}, next run {}", new Object[] { jobName, nodeId, context.getNextFireTime() });

    }
}
