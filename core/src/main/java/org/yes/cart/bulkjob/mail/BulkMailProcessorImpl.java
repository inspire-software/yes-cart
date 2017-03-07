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

package org.yes.cart.bulkjob.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.mail.JavaMailSenderFactory;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.log.Markers;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/11/2013
 * Time: 13:56
 */
public class BulkMailProcessorImpl implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(BulkMailProcessorImpl.class);

    private final MailService mailService;
    private final MailComposer mailComposer;
    private final JavaMailSenderFactory javaMailSenderFactory;

    private long delayBetweenEmailsMs;
    private int cycleExceptionsThreshold;

    public BulkMailProcessorImpl(final MailService mailService,
                                 final MailComposer mailComposer,
                                 final JavaMailSenderFactory javaMailSenderFactory) {
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.javaMailSenderFactory = javaMailSenderFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        LOG.info("Bulk send mail");

        final Map<String, Integer> exceptionsThresholdsByShop = new HashMap<String, Integer>();

        Long lastFailedEmailId = null;
        Mail mail = mailService.findOldestMail(lastFailedEmailId);
        while (mail != null) {

            LOG.info("Preparing mail object {}/{} for {} with subject {}",
                    new Object[] { mail.getMailId(), mail.getShopCode(), mail.getRecipients(), mail.getSubject() });

            final String shopCode = mail.getShopCode();

            if (!exceptionsThresholdsByShop.containsKey(shopCode)) {
                exceptionsThresholdsByShop.put(shopCode, this.cycleExceptionsThreshold);
            }

            int exceptionsThreshold = exceptionsThresholdsByShop.get(shopCode);
            if (exceptionsThreshold <= 0) {
                lastFailedEmailId = mail.getMailId();
                LOG.info("Skipping send mail as exception threshold is exceeded for shop {}", shopCode);
            } else {

                final JavaMailSender javaMailSender = javaMailSenderFactory.getJavaMailSender(shopCode);
                if (javaMailSender == null) {
                    LOG.info("No mail sender configured for {}", shopCode);
                    lastFailedEmailId = mail.getMailId();
                } else {

                    final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

                    boolean sent = false;
                    try {
                        mailComposer.convertMessage(mail, mimeMessage);
                        javaMailSender.send(mimeMessage);
                        sent = true;
                        LOG.info("Sent mail to {} with subject {}", mail.getRecipients(), mail.getSubject());
                        mailService.delete(mail);
                    } catch (Exception exp) {
                        LOG.error(Markers.alert(), "Unable to send mail " + mail.getMailId() + " for shop " + shopCode, exp);
                        lastFailedEmailId = mail.getMailId();
                        exceptionsThresholdsByShop.put(shopCode, exceptionsThreshold - 1);

                    }

                    if (sent && delayBetweenEmailsMs > 0) {
                        try {
                            Thread.sleep(delayBetweenEmailsMs);
                        } catch (InterruptedException e) {
                            // resume
                        }
                    }
                }
            }

            mail = mailService.findOldestMail(lastFailedEmailId);

        }

        LOG.info("Bulk send mail ... completed");

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
