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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobStatus;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/04/2020
 * Time: 21:29
 */
public class JobStatusListenerLoggerWrapperImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(JobStatusListenerLoggerWrapperImplTest.class);

    @Test
    public void testListener() throws Exception {

        final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG, "Test");

        listener.notifyPing("Some message {}", "text");
        listener.count("count");
        listener.notifyWarning("Warn");
        listener.notifyError("Error");

        listener.notifyCompleted();

        assertFalse(listener.isCompleted());
        assertEquals(1, listener.getCount("count"));
        assertEquals(JobStatus.State.UNDEFINED, listener.getLatestStatus().getState());
        assertNull(listener.getLatestStatus().getCompletion());
        assertEquals(
                "Completed Test ... [count: 1]", listener.getLatestStatus().getReport());

        listener.reset();

        listener.notifyPing("Some other message {}", "text");
        listener.count("count");
        listener.count("count", 5);

        listener.notifyCompleted();

        assertFalse(listener.isCompleted());
        assertEquals(6, listener.getCount("count"));
        assertEquals(JobStatus.State.UNDEFINED, listener.getLatestStatus().getState());
        assertNull(listener.getLatestStatus().getCompletion());
        assertEquals(
                "Completed Test ... [count: 6]", listener.getLatestStatus().getReport());

    }
}