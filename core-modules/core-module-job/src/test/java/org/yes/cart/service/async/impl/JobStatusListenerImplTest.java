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

package org.yes.cart.service.async.impl;

import org.junit.Test;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.TimeContext;

import java.time.Instant;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/04/2020
 * Time: 21:03
 */
public class JobStatusListenerImplTest {

    @Test
    public void testListener() throws Exception {

        final Instant now = Instant.now();
        TimeContext.setTime(now);
        final String time = DateUtils.formatSDT(now);

        final JobStatusListener listener = new JobStatusListenerImpl();

        listener.notifyPing("Some message {}", "text");
        listener.count("count");
        listener.notifyWarning("Warn");
        listener.notifyError("Error");

        listener.notifyCompleted();

        assertTrue(listener.isCompleted());
        assertEquals(1, listener.getCount("count"));
        assertEquals(JobStatus.State.FINISHED, listener.getLatestStatus().getState());
        assertEquals(JobStatus.Completion.ERROR, listener.getLatestStatus().getCompletion());
        assertEquals(
                "WARNING: Warn\n" +
                "ERROR: Error\n" +
                "[" + time + "] Completed " + listener.getJobToken() + " with status ERROR, err: 1, warn: 1\n" +
                "Counters [count: 1]", listener.getLatestStatus().getReport());

        listener.reset();

        listener.notifyPing("Some other message {}", "text");
        listener.count("count");
        listener.count("count", 5);

        listener.notifyCompleted();

        assertTrue(listener.isCompleted());
        assertEquals(6, listener.getCount("count"));
        assertEquals(JobStatus.State.FINISHED, listener.getLatestStatus().getState());
        assertEquals(JobStatus.Completion.OK, listener.getLatestStatus().getCompletion());
        assertEquals(
                "[" + time + "] Completed " + listener.getJobToken() + " with status OK, err: 0, warn: 0\n" +
                        "Counters [count: 6]", listener.getLatestStatus().getReport());

    }
}