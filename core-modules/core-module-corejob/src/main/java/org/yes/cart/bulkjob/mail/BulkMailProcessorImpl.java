/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.mail.JavaMailSenderFactory;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.utils.ExceptionUtil;
import org.yes.cart.utils.log.Markers;

import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: denispavlov
 * Date: 10/11/2013
 * Time: 13:56
 */
public class BulkMailProcessorImpl extends AbstractCronJobProcessorImpl implements JobStatusAware, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(BulkMailProcessorImpl.class);

    private static final String SENT_COUNTER = "Mail sent";
    private static final String ERROR_COUNTER = "Mail failed";

    private MailService mailService;
    private MailComposer mailComposer;
    private JavaMailSenderFactory javaMailSenderFactory;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final JobStatusListener listener = new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(), LOG);

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {

        listener.reset();

        final Properties properties = readContextAsProperties(context, job, definition);

        final long delayBetweenEmailsMs = NumberUtils.toLong(properties.getProperty("delay-between-emails-ms"), 1000);
        final int cycleExceptionsThreshold = NumberUtils.toInt(properties.getProperty("exceptions-threshold"), 3);

        final Map<String, Integer> exceptionsThresholdsByShop = new HashMap<>();

        Long lastFailedEmailId = null;
        Mail mail = mailService.findOldestMail(lastFailedEmailId);
        while (mail != null && !isPaused(context)) {

            listener.notifyInfo("Preparing mail object {}/{} for {} with subject {}",
                    mail.getMailId(), mail.getShopCode(), mail.getRecipients(), mail.getSubject());

            final String shopCode = mail.getShopCode();
            listener.notifyPing("Sending mail for " + shopCode);

            if (!exceptionsThresholdsByShop.containsKey(shopCode)) {
                exceptionsThresholdsByShop.put(shopCode, cycleExceptionsThreshold);
            }

            int exceptionsThreshold = exceptionsThresholdsByShop.get(shopCode);
            if (exceptionsThreshold <= 0) {
                lastFailedEmailId = mail.getMailId();
                listener.notifyWarning("Skipping send mail as exception threshold is exceeded for shop {}", shopCode);
            } else {

                final JavaMailSender javaMailSender = javaMailSenderFactory.getJavaMailSender(shopCode);
                if (javaMailSender == null) {
                    listener.notifyWarning("No mail sender configured for {}", shopCode);
                    lastFailedEmailId = mail.getMailId();
                } else {

                    final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

                    boolean sent = false;
                    try {
                        mailComposer.convertMessage(mail, mimeMessage);
                        javaMailSender.send(mimeMessage);
                        sent = true;
                        listener.count(SENT_COUNTER);
                        listener.notifyInfo("Sent mail to {} with subject {}", mail.getRecipients(), mail.getSubject());
                        mailService.delete(mail);
                    } catch (Exception exp) {
                        lastFailedEmailId = mail.getMailId();
                        exceptionsThresholdsByShop.put(shopCode, exceptionsThreshold - 1);
                        listener.notifyError("Unable to send mail " + mail.getMailId() + "/" + mail.getSubject() + " for shop " + shopCode);
                        listener.count(ERROR_COUNTER);
                        if (exceptionContains(exp, "Invalid Addresses") || exceptionContains(exp, "recipient rejected")) {
                            listener.notifyWarning("Mail " + mail.getMailId() + "/" + mail.getSubject() + " for shop " + shopCode + " will be removed because recipient address is invalid");
                            mailService.delete(mail);
                        } else {
                            LOG.error(Markers.alert(), "Unable to send mail " + mail.getMailId() + "/" + mail.getSubject() + " for shop " + shopCode, exp);
                        }
                    }

                    if (sent && delayBetweenEmailsMs > 0) {
                        if (this.shutdown.get()) {
                            break; // we are shutting down
                        }
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

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }

    boolean exceptionContains(final Exception exp, final String contains) {
        return exp != null && ExceptionUtil.stackTraceToString(exp).contains(contains);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        this.shutdown.set(true);
    }

    /**
     * Spring IoC.
     *
     * @param mailService service
     */
    public void setMailService(final MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Spring IoC.
     *
     * @param mailComposer service
     */
    public void setMailComposer(final MailComposer mailComposer) {
        this.mailComposer = mailComposer;
    }

    /**
     * Spring IoC.
     *
     * @param javaMailSenderFactory service
     */
    public void setJavaMailSenderFactory(final JavaMailSenderFactory javaMailSenderFactory) {
        this.javaMailSenderFactory = javaMailSenderFactory;
    }
}
