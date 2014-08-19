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

package org.yes.cart.bulkjob.mail;

import org.slf4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.ShopCodeContext;

import javax.mail.internet.MimeMessage;

/**
 * User: denispavlov
 * Date: 10/11/2013
 * Time: 13:56
 */
public class BulkMailProcessorImpl implements Runnable {

    private static final String PAUSE_PREF = "JOB_SEND_MAIL_PAUSE";

    private final MailService mailService;
    private final MailComposer mailComposer;
    private final JavaMailSender javaMailSender;
    private final SystemService systemService;
    private final RuntimeAttributeService runtimeAttributeService;

    private long delayBetweenEmailsMs;
    private int cycleExceptionsThreshold;

    private boolean pauseInitialised = false;

    public BulkMailProcessorImpl(final MailService mailService,
                                 final MailComposer mailComposer,
                                 final JavaMailSender javaMailSender,
                                 final SystemService systemService,
                                 final RuntimeAttributeService runtimeAttributeService) {
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.javaMailSender = javaMailSender;
        this.systemService = systemService;
        this.runtimeAttributeService = runtimeAttributeService;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        if (!pauseInitialised) {
            if (!systemService.getAttributeValues().keySet().contains(PAUSE_PREF)) {
                runtimeAttributeService.create(PAUSE_PREF, "SYSTEM", "Boolean");
                systemService.updateAttributeValue(PAUSE_PREF, Boolean.FALSE.toString());
            }
            pauseInitialised = true;
        }

        final String paused = systemService.getAttributeValue(PAUSE_PREF);
        if (Boolean.valueOf(paused)) {
            return;
        }

        final Logger log = ShopCodeContext.getLog(this);

        log.info("Starting bulk send mail");

        int exceptionsThreshold = this.cycleExceptionsThreshold;

        Mail mail = mailService.findOldestMail();
        while (mail != null) {

            log.info("Preparing mail object {}/{} for {} with subject {}",
                    new Object[] { mail.getMailId(), mail.getShopCode(), mail.getRecipients(), mail.getSubject() });

            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            boolean sent = false;
            try {
                mailComposer.convertMessage(mail, mimeMessage);
                javaMailSender.send(mimeMessage);
                sent = true;
                log.info("Sent mail to {} with subject {}", mail.getRecipients(), mail.getSubject());
                mailService.delete(mail);
            } catch (Exception exp) {
                //
                log.error("Unable to send mail " + mail.getMailId(), exp);
                exceptionsThreshold--;
                if (exceptionsThreshold <= 0) {
                    return;
                }
            }

            if (sent && delayBetweenEmailsMs > 0) {
                try {
                    Thread.sleep(delayBetweenEmailsMs);
                } catch (InterruptedException e) {
                    // resume
                }
            }

            mail = mailService.findOldestMail();

        }

        log.info("Finished bulk send mail");

    }

    /**
     * Setting to allow delay interval between sending mail. This is useful to prevent
     * bulk message be treated as spam.
     *
     * @param delayBetweenEmailsMs delay in millisecond between each email
     */
    public void setDelayBetweenEmailsMs(final long delayBetweenEmailsMs) {
        this.delayBetweenEmailsMs = delayBetweenEmailsMs;
    }

    /**
     * Setting to set limit of exceptions during single job cycle. Prevents exhausting
     * the server with send mail job that fails all the time.
     *
     * @param cycleExceptionsThreshold number of exceptions allowed in each job cycle.
     */
    public void setCycleExceptionsThreshold(final int cycleExceptionsThreshold) {
        this.cycleExceptionsThreshold = cycleExceptionsThreshold;
    }


}
