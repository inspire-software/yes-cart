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

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.MailService;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 26/01/2021
 * Time: 15:29
 */
public class BulkMailProcessorImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final MailService mailService = ctx().getBean("mailService", MailService.class);
        final CronJobProcessor bulkMailProcessor = ctx().getBean("bulkMailProcessor", CronJobProcessor.class);

        assertTrue(mailService.findAll().isEmpty());

        createEmail(mailService, "SHOIP1");
        createEmail(mailService, "SHOIP1");
        createEmail(mailService, "SHOIP2");
        createEmail(mailService, "SHOIP1");

        assertEquals(4, mailService.findAll().size());

        final Map<String, Object> ctx = configureJobContext("bulkMailProcessor", "delay-between-emails-ms=1\nexceptions-threshold=1");

        bulkMailProcessor.process(ctx);

        final JobStatus status = ((JobStatusAware) bulkMailProcessor).getStatus(null);

        assertNotNull(status);
        assertTrue(status.getReport(), status.getReport().contains("with status ERROR, err: 1, warn: 0\n" +
                "Counters [Mail failed: 1, Mail sent: 3]"));

        final List<Mail> emails = mailService.findAll();
        assertEquals(1, emails.size());
        assertEquals("NoopJavaMailSenderFactory is configured to have valid emails only from SHOIP1", "SHOIP2", emails.get(0).getShopCode());

    }

    private void createEmail(final MailService mailService, final String shopCode) {

        final Mail mail = mailService.getGenericDao().getEntityFactory().getByIface(Mail.class);

        mail.setShopCode(shopCode);
        mail.setFrom("from@test.com");
        mail.setRecipients("to@test.com");
        mail.setSubject("test bulk mail");
        mail.setTextVersion("test bulk mail");
        mail.setHtmlVersion("<html><body>test bulk mail</body></html>");

        mailService.update(mail);

    }
}