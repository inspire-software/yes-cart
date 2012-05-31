package org.yes.cart.service.domain.aspect.impl;

import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
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
