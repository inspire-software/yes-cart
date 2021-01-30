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

package org.yes.cart.domain.entity;

import org.yes.cart.cluster.node.Node;

import java.time.Instant;

/**
 * Job created from job definition and executed on a specific app.
 *
 * User: inspiresoftware
 * Date: 19/01/2021
 * Time: 15:20
 */
public interface Job extends Auditable {

    /**
     * @return primary key
     */
    long getJobId();

    /**
     * @param jobId primary key
     */
    void setJobId(long jobId);

    /**
     * @return {@link Node#getId()} for app that is running this job
     */
    String getNodeId();

    /**
     * @param nodeId primary key
     */
    void setNodeId(String nodeId);

    /**
     * @return job definition code
     */
    String getJobDefinitionCode();

    /**
     * @param jobDefinitionCode job definition code
     */
    void setJobDefinitionCode(String jobDefinitionCode);

    /**
     * @return true if execution of this job is paused
     */
    Boolean getPaused();

    /**
     * @param paused set paused flag
     */
    void setPaused(Boolean paused);

    /**
     * @return true if execution of this job is disabled
     */
    Boolean getDisabled();

    /**
     * @param disabled set disabled flag
     */
    void setDisabled(Boolean disabled);

    /**
     * @return overridden cron schedule
     */
    String getCronExpression();

    /**
     * @param cronExpression overridden cron schedule
     */
    void setCronExpression(String cronExpression);

    /**
     * @return last run timestamp
     */
    Instant getLastRun();

    /**
     * @param lastRun last run timestamp
     */
    void setLastRun(Instant lastRun);

    /**
     * @return last report
     */
    String getLastReport();

    /**
     * @param lastReport last report
     */
    void setLastReport(String lastReport);

    /**
     * @return checkpoint timestamp
     */
    Instant getCheckpoint();

    /**
     * @param checkpoint checkpoint timestamp
     */
    void setCheckpoint(Instant checkpoint);

}
