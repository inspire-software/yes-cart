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

package org.yes.cart.domain.message.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.log.Markers;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ManagerRegistrationMessageListener implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerRegistrationMessageListener.class);

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final RegistrationMessage objectMessage;

    /**
     * Construct listener.
     *
     * @param mailService    mail service.
     * @param mailComposer   mail composer
     * @param objectMessage  message
     */
    public ManagerRegistrationMessageListener(final MailService mailService,
                                              final MailComposer mailComposer,
                                              final RegistrationMessage objectMessage) {
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.objectMessage = objectMessage;
    }


    /**
     * {@inheritDoc}
     */
    public void run() {

        try {
            processMessage(objectMessage);
        } catch (Exception e) {
            LOG.error(Markers.alert(), "Manager registration email error " + objectMessage + ", cause: " + e.getMessage(), e);
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
        model.put("salutation", registrationMessage.getSalutation());
        model.put("firstName", registrationMessage.getFirstname());
        model.put("lastName", registrationMessage.getLastname());
        model.put("middleName", registrationMessage.getMiddlename());
        model.put("additionalData", registrationMessage.getAdditionalData());

        final Mail mail = mailService.getGenericDao().getEntityFactory().getByIface(Mail.class);
        mailComposer.composeMessage(
                mail,
                "DEFAULT",
                registrationMessage.getLocale(),
                registrationMessage.getMailTemplatePathChain(),
                registrationMessage.getTemplateName(),
                registrationMessage.getShopMailFrom(),
                registrationMessage.getEmail(),
                null,
                null,
                model);

        mailService.create(mail);

    }


}
