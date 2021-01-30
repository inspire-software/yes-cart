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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.Job;

import java.io.Serializable;
import java.time.Instant;

/**
 * User: inspiresoftware
 * Date: 19/01/2021
 * Time: 15:46
 */
public class JobEntity implements Job, Serializable {

    private long jobId;
    private long version;

    private String nodeId;
    private String jobDefinitionCode;
    private String cronExpression;
    private Boolean paused = Boolean.FALSE;
    private Boolean disabled = Boolean.FALSE;
    private Instant lastRun;
    private String lastReport;
    private Instant checkpoint;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    @Override
    public long getId() {
        return jobId;
    }

    @Override
    public long getJobId() {
        return jobId;
    }

    @Override
    public void setJobId(final long jobId) {
        this.jobId = jobId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getJobDefinitionCode() {
        return jobDefinitionCode;
    }

    @Override
    public void setJobDefinitionCode(final String jobDefinitionCode) {
        this.jobDefinitionCode = jobDefinitionCode;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public void setCronExpression(final String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public Boolean getPaused() {
        return paused;
    }

    @Override
    public void setPaused(final Boolean paused) {
        this.paused = paused;
    }

    @Override
    public Boolean getDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Instant getLastRun() {
        return lastRun;
    }

    @Override
    public void setLastRun(final Instant lastRun) {
        this.lastRun = lastRun;
    }

    @Override
    public String getLastReport() {
        return lastReport;
    }

    @Override
    public void setLastReport(final String lastReport) {
        this.lastReport = lastReport;
    }

    @Override
    public Instant getCheckpoint() {
        return checkpoint;
    }

    @Override
    public void setCheckpoint(final Instant checkpoint) {
        this.checkpoint = checkpoint;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }



}
