package org.yes.cart.domain.message.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.service.mail.MailComposer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * This message listener responsible to:
 *
 * 1. handle customer created event
 * 2. compose email from template
 * 3. send composed email
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 *
 *
 *
 */
public class CustomerRegistrationMessageListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerRegistrationMessageListener.class);

    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;

    /**
     * Contruct jms listener.
     * @param javaMailSender mail sender to use.
     * @param mailComposer mail composer
     */
    public CustomerRegistrationMessageListener(
            final JavaMailSender javaMailSender,
            final MailComposer mailComposer) {
        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;
    }

    /**
     * {@inheritDoc}
     */
    public void onMessage(final Message message) {
        final ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            final RegistrationMessage registrationMessage = (RegistrationMessage) objectMessage.getObject();
            processMessage(registrationMessage);
        } catch (Exception e) {
            LOG.error("Cant process " + message, e);
            throw new RuntimeException(e); //rollback message
        }
    }

    /**
     * Process message from queue to mail.
     * @param registrationMessage massage to process
     * @throws Exception in case of compose mail error
     */
    void processMessage(final RegistrationMessage registrationMessage) throws Exception {

        final Map<String, Object> model = new HashMap <String, Object>();
        model.put("password", registrationMessage.getPassword());
        model.put("firstName", registrationMessage.getFirstname());
        model.put("lastName", registrationMessage.getLastname());
        model.put("shopUrl", registrationMessage.getShopUrl());
        model.put("shopName", registrationMessage.getShopName());

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        mailComposer.composeMessage(
                mimeMessage,
                registrationMessage.getShopCode(),
                registrationMessage.isNewPerson()?MailComposer.MAIL_TEMPLATE_CUSTOMER_REGISTERED:MailComposer.MAIL_TEMPLATE_CUSTOMER_CHANGE_PASSWORD,
                registrationMessage.getShopMailFrom(),
                registrationMessage.getEmail(),
                null,
                null,
                model);

        javaMailSender.send(mimeMessage);
    }


}
