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

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.mail.MailTemplateResourcesProvider;
import org.yes.cart.service.mail.impl.MailComposerImpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerRegistrationMessageListenerTest extends BaseCoreDBTestCase {

    private Mockery mockery = new JUnit4Mockery();

    @Test
    public void testOnMessage0() throws Exception {

        final MailService mailService = (MailService) ctx().getBean("mailService");
        final MailTemplateResourcesProvider mailTemplateResourcesProvider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> templateChain = Arrays.asList("SHOIP1/mail/");

        mockery.checking(new Expectations() {{
            one(mailTemplateResourcesProvider).getTemplate(templateChain, "en", "customerChangePassword", ".txt");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/SHOIP1/customerChangePassword/customerChangePassword.txt")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(templateChain, "en", "customerChangePassword", ".html");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/SHOIP1/customerChangePassword/customerChangePassword.html")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(templateChain, "en", "customerChangePassword", ".properties");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/SHOIP1/customerChangePassword/customerChangePassword.properties")), "UTF-8")));
        }});

        final MailComposerImpl mailComposer = new MailComposerImpl(mailTemplateResourcesProvider);

        RegistrationMessage registrationMessage = createRegistrationMessage(templateChain);
        new CustomerRegistrationMessageListener(
                mailService,
                mailComposer,
                registrationMessage
        ).run();
        final List<Mail> mail = mailService.findAll();
        assertEquals(1, mail.size());
        final Mail email = mail.get(0);
        assertTrue(email.getHtmlVersion().contains("neWpaSswOrd"));
        assertTrue(email.getHtmlVersion().contains("Bender"));
        assertTrue(email.getHtmlVersion().contains("Rodrigez"));
        assertTrue(email.getHtmlVersion().contains("Gadget universe"));
        assertTrue(email.getHtmlVersion().contains("somegadget.com"));
        assertEquals(email.getFrom(), "noreply@shop.com");
        assertEquals(email.getSubject(), "Password has been changed");

        mockery.assertIsSatisfied();
    }

    private RegistrationMessage createRegistrationMessage(final List<String> templateChain) {
        RegistrationMessage registrationMessage = new RegistrationMessageImpl();
        registrationMessage.setFirstname("Bender");
        registrationMessage.setLastname("Rodrigez");
        registrationMessage.setEmail("bender@bar.localhost");
        registrationMessage.setPassword("neWpaSswOrd");
        registrationMessage.setShopId(10L);
        registrationMessage.setShopCode("SHOIP1");
        registrationMessage.setShopMailFrom("noreply@shop.com");
        registrationMessage.setShopName("Gadget universe");
        registrationMessage.setShopUrl(new HashSet<String>());
        registrationMessage.getShopUrl().add("www.somegadget.com");
        registrationMessage.getShopUrl().add("somegadget.com");
        registrationMessage.setMailTemplatePathChain(templateChain);
        registrationMessage.setTemplateName("customerChangePassword");
        registrationMessage.setLocale("en");
        return registrationMessage;
    }
}
