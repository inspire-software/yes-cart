package org.yes.cart.domain.message.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.ShopCodeContext;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * This message listener responsible to:
 * <p/>
 * 1. handle customer created event
 * 2. compose email from template
 * 3. send composed email
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerRegistrationMessageListener implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;

    private final Object objectMessage;

    /**
     * Contruct jms listener.
     *
     * @param javaMailSender mail sender to use.
     * @param mailComposer   mail composer
     */
    public CustomerRegistrationMessageListener(
            final JavaMailSender javaMailSender,
            final MailComposer mailComposer,
            final Object objectMessage) {
        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;
        this.objectMessage = objectMessage;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        try {
            final RegistrationMessage registrationMessage = (RegistrationMessage) objectMessage;

            LOG.info("CustomerRegistrationMessageListener#onMessage responce :" + registrationMessage);

            if (registrationMessage.getPathToTemplateFolder() != null) {
                processMessage(registrationMessage);
            }

        } catch (Exception e) {
            LOG.error("Cant process " + objectMessage, e);
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
        model.put("shopUrl", registrationMessage.getShopUrl());
        model.put("shopName", registrationMessage.getShopName());

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //
        //     registrationMessage.isNewPerson()?MailComposer.MAIL_TEMPLATE_CUSTOMER_REGISTERED:MailComposer.MAIL_TEMPLATE_CUSTOMER_CHANGE_PASSWORD,
        mailComposer.composeMessage(
                mimeMessage,
                registrationMessage.getShopCode(),
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
