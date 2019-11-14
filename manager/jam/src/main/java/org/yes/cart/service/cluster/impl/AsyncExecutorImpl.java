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

package org.yes.cart.service.cluster.impl;

import org.springframework.core.task.TaskExecutor;
import org.yes.cart.service.cluster.AsyncExecutor;

/**
 * User: denispavlov
 * Date: 14/11/2019
 * Time: 07:27
 */
public class AsyncExecutorImpl implements AsyncExecutor {

    private final TaskExecutor taskExecutor;

    public AsyncExecutorImpl(final TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /** {@inheritDoc} */
    @Override
    public void asyncExecute(final Runnable runnable) {
        taskExecutor.execute(runnable);
    }
}
