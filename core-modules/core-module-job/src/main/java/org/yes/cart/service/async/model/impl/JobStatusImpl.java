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

package org.yes.cart.service.async.model.impl;

import org.yes.cart.service.async.model.JobStatus;

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 12-07-30
 * Time: 9:32 AM
 */
public class JobStatusImpl implements JobStatus {

    private String token;
    private State state;
    private Completion completion;
    private String report;
    private Instant jobStartTime;
    private Instant jobCompletedTime;

    public JobStatusImpl() {
    }

    public JobStatusImpl(final String token, final State state, final Completion completion, final String report) {
        this.token = token;
        this.state = state;
        this.completion = completion;
        this.report = report;
    }

    public JobStatusImpl(final String token, final State state, final Completion completion, final String report, final Instant jobStartTime, final Instant jobCompletedTime) {
        this.token = token;
        this.state = state;
        this.completion = completion;
        this.report = report;
        this.jobStartTime = jobStartTime;
        this.jobCompletedTime = jobCompletedTime;
    }

    /** {@inheritDoc} */
    @Override
    public String getToken() {
        return token;
    }

    /** {@inheritDoc} */
    @Override
    public State getState() {
        return state;
    }

    /** {@inheritDoc} */
    @Override
    public Completion getCompletion() {
        return completion;
    }

    /** {@inheritDoc} */
    @Override
    public String getReport() {
        return report;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public void setCompletion(final Completion completion) {
        this.completion = completion;
    }

    public void setReport(final String report) {
        this.report = report;
    }

    @Override
    public Instant getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(final Instant jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    @Override
    public Instant getJobCompletedTime() {
        return jobCompletedTime;
    }

    public void setJobCompletedTime(final Instant jobCompletedTime) {
        this.jobCompletedTime = jobCompletedTime;
    }

    @Override
    public String toString() {
        return "JobStatusImpl{" +
                "token='" + token + '\'' +
                ", state=" + state +
                '}';
    }
}
