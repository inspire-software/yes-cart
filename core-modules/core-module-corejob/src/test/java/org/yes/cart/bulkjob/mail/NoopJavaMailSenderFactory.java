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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.service.mail.JavaMailSenderFactory;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * User: denispavlov
 * Date: 26/01/2021
 * Time: 15:31
 */
public class NoopJavaMailSenderFactory implements JavaMailSenderFactory {

    private final Mockery context = new JUnit4Mockery();

    private JavaMailSender valid;
    private JavaMailSender invalid;

    public NoopJavaMailSenderFactory() {

        valid = this.context.mock(JavaMailSender.class, "valid");
        final MimeMessage validMime = new MimeMessage((Session) null);

        this.context.checking(new Expectations() {{
            allowing(valid).createMimeMessage(); will(returnValue(validMime));
            allowing(valid).send(validMime);
        }});

        invalid = this.context.mock(JavaMailSender.class, "invalid");

        final MimeMessage invalidMime = new MimeMessage((Session) null);

        this.context.checking(new Expectations() {{
            allowing(invalid).createMimeMessage(); will(returnValue(invalidMime));
            allowing(invalid).send(invalidMime); will(throwException(new MailSendException("Test")));
        }});

    }

    @Override
    public JavaMailSender getJavaMailSender(final String shopCode) {

        if ("SHOIP1".equals(shopCode)) {

             return valid;

        }

        return invalid;
        
    }

}
