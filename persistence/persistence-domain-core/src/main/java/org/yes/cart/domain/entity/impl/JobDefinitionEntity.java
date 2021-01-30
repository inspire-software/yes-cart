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

import org.yes.cart.domain.entity.JobDefinition;

import java.io.Serializable;
import java.time.Instant;

/**
 * User: inspiresoftware
 * Date: 19/01/2021
 * Time: 15:51
 */
public class JobDefinitionEntity implements JobDefinition, Serializable {


    private long jobDefinitionId;
    private long version;

    private String jobName;
    private String processor;
    private String context;
    private String hostApplicationRegEx;
    private String defaultCronExpression;
    private String defaultCronExpressionKey;
    private Boolean defaultPaused = Boolean.FALSE;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;


    @Override
    public long getId() {
        return jobDefinitionId;
    }

    @Override
    public long getJobDefinitionId() {
        return jobDefinitionId;
    }

    @Override
    public void setJobDefinitionId(final long jobDefinitionId) {
        this.jobDefinitionId = jobDefinitionId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public void setJobName(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getProcessor() {
        return processor;
    }

    @Override
    public void setProcessor(final String processor) {
        this.processor = processor;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public void setContext(final String context) {
        this.context = context;
    }

    @Override
    public String getHostApplicationRegEx() {
        return hostApplicationRegEx;
    }

    @Override
    public void setHostApplicationRegEx(final String hostApplicationRegEx) {
        this.hostApplicationRegEx = hostApplicationRegEx;
    }

    @Override
    public String getDefaultCronExpression() {
        return defaultCronExpression;
    }

    @Override
    public void setDefaultCronExpression(final String defaultCronExpression) {
        this.defaultCronExpression = defaultCronExpression;
    }

    @Override
    public String getDefaultCronExpressionKey() {
        return defaultCronExpressionKey;
    }

    @Override
    public void setDefaultCronExpressionKey(final String defaultCronExpressionKey) {
        this.defaultCronExpressionKey = defaultCronExpressionKey;
    }

    @Override
    public Boolean getDefaultPaused() {
        return defaultPaused;
    }

    @Override
    public void setDefaultPaused(final Boolean defaultPaused) {
        this.defaultPaused = defaultPaused;
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

    @Override
    public String getCode() {
        return guid;
    }

    @Override
    public void setCode(final String code) {
        this.guid = code;
    }
}
