/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.service.async.impl.JobStatusListenerNullImpl;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 2:20 PM
 */
public abstract class SingletonJobRunner implements JobRunner {


    private final TaskExecutor executor;

    private final ReentrantLock lock = new ReentrantLock();

    private final Map<String, JobStatusListener> jobListeners = new HashMap<String, JobStatusListener>();

    protected SingletonJobRunner(final TaskExecutor executor) {
        this.executor = executor;
    }

    /** {@inheritDoc} */
    public JobStatus getStatus(final String token) {
        if (token == null || !jobListeners.containsKey(token)) {
            throw new IllegalArgumentException("Job token: " + token + " unknown");
        }
        final JobStatusListener listener = jobListeners.get(token);
        final JobStatus status = listener.getLatestStatus();
        if (status.getState() == JobStatus.State.FINISHED || status.getState() == JobStatus.State.UNDEFINED) {
            jobListeners.remove(token); // remove those for which we ask for the last time
        }
        return status;
    }

    /** {@inheritDoc} */
    public String doJob(final JobContext ctx) {

        if (lock.isLocked()) {
            if (jobListeners.isEmpty()) {
                lock.unlock(); // if we do not have any listeners then it is all done
            } else {
                // check for locks. it would be better to do this at the end of runnable
                // however if that runnable crashes we will be locked forever.
                boolean shouldUnlock = true;
                for (final JobStatusListener listener : jobListeners.values()) {
                    if (!listener.isCompleted() && !listener.isTimedOut()) {
                        shouldUnlock = false;
                        break;
                    }
                }
                if (shouldUnlock) {
                    lock.unlock(); // unlock because all is completed or timed out
                }
            }
        }

        if (!lock.isLocked()) {
            lock.lock();

            final JobStatusListener listener = ctx.getListener();
            jobListeners.put(listener.getJobToken(), listener);

            final Runnable job = createJobRunnable(ctx);
            if (ctx.isAsync()) {
                executor.execute(job);
            } else {
                job.run();
            }

            return listener.getJobToken();
        } else {
            final JobStatusListener listener = new JobStatusListenerNullImpl("ERROR: Import job is already running");
            jobListeners.put(listener.getJobToken(), listener);
            return listener.getJobToken();
        }
    }

    protected abstract Runnable createJobRunnable(final JobContext ctx);

}
