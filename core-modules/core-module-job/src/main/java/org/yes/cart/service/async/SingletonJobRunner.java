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

package org.yes.cart.service.async;

import org.springframework.core.task.TaskExecutor;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 2:20 PM
 */
public abstract class SingletonJobRunner implements JobRunner {


    private final TaskExecutor executor;

    private final Object mutex = new Object();
    private final Map<String, JobStatusListener> jobListeners = new ConcurrentHashMap<>();

    private int maxNumberOfOldReports = 50;
    private final List<JobStatus> oldReports = new ArrayList<>();

    protected SingletonJobRunner(final TaskExecutor executor) {
        this.executor = executor;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        if (token == null) {
            throw new IllegalArgumentException("Job token: no token provided");
        }
        if (!jobListeners.containsKey(token)) {
            for (final JobStatus status : oldReports) {
                if (token.equals(status.getToken())) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Job token: " + token + " unknown");
        }
        final JobStatusListener listener = jobListeners.get(token);
        final JobStatus status = listener.getLatestStatus();
        cleanUpListeners(token, status);
        return status;
    }

    private void cleanUpListeners(final String token, final JobStatus status) {
        if (status.getState() == JobStatus.State.FINISHED || status.getState() == JobStatus.State.UNDEFINED) {
            // remove listeners for which we ask status for the last time
            if (jobListeners.remove(token) != null) {
                // ensure we keep the last "max" number of statuses
                oldReports.add(status);
                if (oldReports.size() > maxNumberOfOldReports) {
                    oldReports.remove(0);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus doJob(final JobContext ctx) {

        final JobStatusListener listener = ctx.getListener();
        jobListeners.put(listener.getJobToken(), listener);

        final Runnable job = createMutexJobRunnable(ctx);
        if (ctx.isAsync()) {
            // if this is async then mutex will kick in in another thread
            executor.execute(job);
        } else {
            // if this is sync then mutex will hold the execution
            job.run();
        }
        final JobStatus status = listener.getLatestStatus();
        cleanUpListeners(listener.getJobToken(), status);
        return status;
    }

    private Runnable createMutexJobRunnable(final JobContext ctx) {
        final Runnable job = createJobRunnable(ctx);
        return new Runnable() {

            private final Runnable jobRunnable = job;

            @Override
            public void run() {
                // ensure that we run one at a time by supplying mutex
                synchronized (mutex) {
                    jobRunnable.run();
                }
            }
        };
    }

    protected abstract Runnable createJobRunnable(final JobContext ctx);

    /**
     * Number of max status object to keep.
     *
     * @param maxNumberOfOldReports number of old statuses
     */
    public void setMaxNumberOfOldReports(final int maxNumberOfOldReports) {
        this.maxNumberOfOldReports = maxNumberOfOldReports;
    }
}
