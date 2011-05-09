package org.yes.cart.service.domain.aspect.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.Serializable;

/**
 *
 * Base class for notification aspects.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BaseNotificationAspect {

    private final JmsTemplate jmsTemplate;

    /**
     * Construct base notification aspect class.
     * @param jmsTemplate jms teplate to use
     */
    public BaseNotificationAspect(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Send registration notification.
     * @param serializableMessage  object to send
     */
    protected void sendNotification(final Serializable serializableMessage) {
        if (jmsTemplate != null) {
            jmsTemplate.send(new MessageCreator() {
                public Message createMessage(final Session session) throws JMSException {
                    return session.createObjectMessage(serializableMessage);
                }
            });
        }
    }



}
