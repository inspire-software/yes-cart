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

import org.yes.cart.service.async.model.JobStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Listener should be created per job instance and should not be shared by
 * several threads.
 *
 * User: denispavlov
 * Date: 12-07-30
 * Time: 9:43 AM
 */
public interface JobStatusListener {

    /**
     * @return latest job status
     */
    JobStatus getLatestStatus();

    /**
     * @return unique job token
     */
    String getJobToken();

    /**
     * Ping the listener to notify job's healthy state and reset timeout.
     */
    void notifyPing();

    /**
     * Ping the listener to notify job's healthy state and reset timeout.
     * Use msg to have a current progress message (like a progress bar)
     * This message will be appended to the end of the report until a real
     * message method of this listener is invoked - then this message is removed.
     *
     * @param message message (with optional placeholders "{}" for arguments)
     * @param args arguments
     */
    void notifyPing(String message, Object... args);

    /**
     * Notify of a message (equivalent to info)
     *
     * @param message message (with optional placeholders "{}" for arguments)
     * @param args arguments
     */
    void notifyMessage(String message, Object... args);

    /**
     * Notify of a warning message
     *
     * @param warning warning message (with optional placeholders "{}" for arguments)
     * @param args arguments
     */
    void notifyWarning(String warning, Object... args);

    /**
     * Notify of an error message
     *
     * @param error error message (with optional placeholders "{}" for arguments)
     * @param args arguments
     */
    void notifyError(String error, Object... args);

    /**
     * Notify of an error message
     *
     * @param error error message (with optional placeholders "{}" for arguments)
     * @param exp exception that caused the error
     * @param args arguments
     */
    void notifyError(String error, Exception exp, Object... args);

    /**
     * Notify completion
     */
    void notifyCompleted();

    /**
     * @return true if listeners has received result
     */
    boolean isCompleted();

    /**
     * Time when job started.
     *
     * @return start time
     */
    Instant getJobStartTime();

    /**
     * Time when job completed.
     *
     * @return completed time.
     */
    Instant getJobCompletedTime();


    /**
     * @return timeout setting in millis
     */
    long getTimeoutValue();

    /**
     * @return true if listeners last message timestamp exceed timeout
     */
    boolean isTimedOut();

    /**
     * Count +1 for given name.
     *
     * @param name name
     */
    int count(String name);

    /**
     * Count +add for given name.
     *
     * @param name name
     */
    int count(String name, int add);

    /**
     * Get all counters values.
     *
     * @return counts
     */
    Map<String, Integer> getCounts();

    /**
     * Get counter value.
     *
     * @param name name
     *
     * @return count
     */
    int getCount(String name);

    /**
     * Clear counters and state.
     */
    void reset();

}
