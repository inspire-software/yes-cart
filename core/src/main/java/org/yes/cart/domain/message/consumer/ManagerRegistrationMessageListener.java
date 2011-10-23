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
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ManagerRegistrationMessageListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerRegistrationMessageListener.class);

    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;

    /**
     * Contruct jms listener.
     *
     * @param javaMailSender mail sender to use.
     * @param mailComposer   mail composer
     */
    public ManagerRegistrationMessageListener(
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
     *
     * @param registrationMessage massage to process
     * @throws Exception in case of compose mail error
     */
    void processMessage(final RegistrationMessage registrationMessage) throws Exception {

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("password", registrationMessage.getPassword());
        model.put("firstName", registrationMessage.getFirstname());
        model.put("lastName", registrationMessage.getLastname());


        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        /*
                registrationMessage.isNewPerson()? MailComposer.MAIL_TEMPLATE_MANAGER_REGISTERED:MailComposer.MAIL_TEMPLATE_MANAGER_CHANGE_PASSWORD,*/

        mailComposer.composeMessage(
                mimeMessage,
                null,
                registrationMessage.getPathToTemplateFolder(),
                registrationMessage.getTemplateName(),
                registrationMessage.getShopMailFrom(),
                registrationMessage.getEmail(),
                null,
                null,
                model);

        javaMailSender.send(mimeMessage);

    }


}
