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

package org.yes.cart.domain.message.consumer;

import org.springframework.mail.MailSendException;
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

            ShopCodeContext.getLog(this).info("CustomerRegistrationMessageListener#onMessage responce :" + registrationMessage);

            if (registrationMessage.getPathToTemplateFolder() != null) {
                processMessage(registrationMessage);
            }

        } catch (Exception e) {
            ShopCodeContext.getLog(this).error("Cant process " + objectMessage, e);
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
        
        boolean send = false;
        while (!send) {
            // TODO: This is not good with timeout done by sleep, need to refactor this into a Job task with persistent queue, JMS maybe?
            try {
                javaMailSender.send(mimeMessage);
                send = true;
                ShopCodeContext.getLog(this).info("Customer registration mail send to {}", registrationMessage.getEmail() );
            } catch (MailSendException me) {
                ShopCodeContext.getLog(this).error("Cant send email to {} {}", registrationMessage.getEmail(), me.getMessage());
                Thread.sleep(60000);
                
            }
        }
        
        
    }


}
