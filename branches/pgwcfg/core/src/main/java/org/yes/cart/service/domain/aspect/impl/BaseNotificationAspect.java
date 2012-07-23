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

package org.yes.cart.service.domain.aspect.impl;

import org.springframework.core.task.TaskExecutor;

import java.io.Serializable;

/**
 *
 * Base class for notification aspects.  All aspect across solution derived from this one.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class BaseNotificationAspect {

    private final TaskExecutor taskExecutor;

    /**
     * Construct base notification aspect class.
     * @param taskExecutor to use
     */
    public BaseNotificationAspect(final TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Send registration notification.
     * @param serializableMessage  object to send
     */
    protected void sendNotification(final Serializable serializableMessage) {
        if (taskExecutor != null) {
            final Runnable task = getTask(serializableMessage);
            if (task != null) {
                taskExecutor.execute(task);
            }
        }
    }

    /**
     * Get task to execute.
     * @return {@link Runnable}
     */
    public abstract Runnable getTask(final Serializable serializableMessage);


}
