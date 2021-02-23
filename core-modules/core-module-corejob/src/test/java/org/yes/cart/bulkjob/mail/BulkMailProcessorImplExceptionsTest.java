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

import com.sun.mail.smtp.SMTPAddressFailedException;
import org.junit.Test;
import org.springframework.mail.MailSendException;

import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 23/02/2021
 * Time: 08:17
 */
public class BulkMailProcessorImplExceptionsTest {

    /**
     * Simulation of use case where SMTP server has invalid address rejection policy.
     *
     * This is irrecoverable condition and we need to remove Mail from the send queue.
     *
     * Sample stacktrace:
     * <code>
     *     org.springframework.mail.MailSendException: Failed messages: javax.mail.SendFailedException: Invalid Addresses;
             nested exception is:
             com.sun.mail.smtp.SMTPAddressFailedException: 550 5.1.1 <zzzzz@gmail.vom> recipient rejected (510)
        
             at org.springframework.mail.javamail.JavaMailSenderImpl.doSend(JavaMailSenderImpl.java:491)
             at org.springframework.mail.javamail.JavaMailSenderImpl.send(JavaMailSenderImpl.java:361)
             at org.springframework.mail.javamail.JavaMailSenderImpl.send(JavaMailSenderImpl.java:356)
             at org.yes.cart.bulkjob.mail.BulkMailProcessorImpl.processInternal(BulkMailProcessorImpl.java:116)
             at org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl.process(AbstractCronJobProcessorImpl.java:69)
             at org.yes.cart.bulkjob.cron.CronJob.executeInternal(CronJob.java:56)
             at org.springframework.scheduling.quartz.QuartzJobBean.execute(QuartzJobBean.java:75)
             at org.quartz.core.JobRunShell.run(JobRunShell.java:202)
             at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)
     * </code>
     *
     * @throws Exception
     */
    @Test
    public void testSMTPAddressFailedExceptionUseCase() throws Exception {

        final Map<Object, Exception> failedMessages = new LinkedHashMap<>();

        final SMTPAddressFailedException smtp = new SMTPAddressFailedException(
                new InternetAddress("invalid@address.com"), "SEND", 550, "recipient rejected"
        );

        failedMessages.put("mime", new SendFailedException("Invalid Addresses", smtp));

        final MailSendException mse = new MailSendException(failedMessages);

        assertTrue(new BulkMailProcessorImpl().exceptionContains(mse, "Invalid Addresses"));
        assertTrue(new BulkMailProcessorImpl().exceptionContains(mse, "recipient rejected"));

    }
}
