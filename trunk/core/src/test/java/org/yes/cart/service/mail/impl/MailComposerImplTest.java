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

package org.yes.cart.service.mail.impl;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.impl.MailEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.mail.MailTemplateResourcesProvider;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class MailComposerImplTest {

    private Mockery mockery = new JUnit4Mockery();

    @Test
    public void testMerge$style() throws ClassNotFoundException, IOException {
        String template = "$name lives...somewhere in time.";
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        String result = mailComposer.merge(template, model);
        assertEquals("Bender lives...somewhere in time.", result);
    }

    @Test
    public void testMergeJspStyle() throws ClassNotFoundException, IOException {
        String template = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        String result = mailComposer.merge(template, createModel());
        assertEquals("Bender lives in theme park with blackjack poetess ", result);
    }

    @Test
    public void testMergeGroovyStyle() throws ClassNotFoundException, IOException {
        String template = "<% def deliverySum = 1.23456789; deliverySum = deliverySum.setScale(2, BigDecimal.ROUND_HALF_UP); out.print(deliverySum); %>";
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        String result = mailComposer.merge(template, createModel());
        assertEquals("1.23", result);
    }

    @Test
    public void testGetResourcesId() throws ClassNotFoundException {
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        //'cid:identifier1234' "cid:id" 'cid:ident' "cid:identifier5678"
        List<String> rez = mailComposer.getResourcesId("'cid:identifier1234' \"cid:id\" 'cid:ident' \"cid:identifier5678\"");
        assertEquals(4, rez.size());
        assertEquals("identifier1234", rez.get(0));
        assertEquals("id", rez.get(1));
        assertEquals("ident", rez.get(2));
        assertEquals("identifier5678", rez.get(3));
    }

    @Test
    public void testComposeMimeMessageInternalTextAndHtmlVersion() throws MessagingException, IOException, ClassNotFoundException {
        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, Collections.EMPTY_LIST, "en", "test", createModel());
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
    }

    /**
     * Text template only
     */
    @Test
    public void testComposeMimeMessageInternalTextVersionOnly() throws MessagingException, IOException, ClassNotFoundException {
        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        String htmlTemplate = null;
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, Collections.EMPTY_LIST, "en", "test", createModel());
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
    }

    /**
     * html only
     */
    @Test
    public void testComposeMimeMessageInternalHtmlVersionOnly() throws MessagingException, IOException, ClassNotFoundException {
        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String textTemplate = null;
        String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, Collections.EMPTY_LIST, "en", "test", createModel());
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
    }

    @Test
    public void testComposeMimeMessageInternalFullIntegration() throws MessagingException, IOException, ClassNotFoundException {

        final MailTemplateResourcesProvider mailTemplateResourcesProvider = mockery.mock(MailTemplateResourcesProvider.class);

        mockery.checking(new Expectations() {{
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "en", "imtest", ".txt");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.txt")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "en", "imtest", ".html");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.html")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "en", "imtest", ".properties");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.properties")), "UTF-8")));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "en", "imtest", "test.gif");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.gif")))));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "en", "imtest", "test.jpeg");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.jpeg")))));
        }});

        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        MailComposerImpl mailComposer = new MailComposerImpl(mailTemplateResourcesProvider);
        mailComposer.composeMessage(
                message,
                null,
                "en",
                Arrays.asList("default/mail/"),
                "imtest",
                "test@localhost.lo",
                "to@somedomain.com",
                "cc@somedomain.com",
                "bcc@somedomain.com",
                createModel());
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
        assertTrue(str.contains("From: test@localhost.lo"));
        assertTrue(str.contains("To: to@somedomain.com"));
        assertTrue(str.contains("cc@somedomain.com"));
        assertTrue(str.contains("Bcc: bcc@somedomain.com"));
        assertEquals("Тема письма", message.getSubject());

        mockery.assertIsSatisfied();
    }

    private Map<String, Object> createModel() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        model.put("with", Arrays.asList("blackjack", "poetess"));
        return model;
    }

    @Test
    public void complexObjectNavigation()  throws MessagingException, IOException, ClassNotFoundException {
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("root", new Pair("hi", "there"));
        String textTemplate = "$root.first $root.second";
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.composeMessage(helper, textTemplate, null, Collections.EMPTY_LIST, "en", "test", map);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("hi there"));
    }


    @Test
    public void testComposeMailEntityInternalTextAndHtmlVersion() throws MessagingException, IOException, ClassNotFoundException {
        final Mail mail = new MailEntity();
        String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.composeMessage(mail, textTemplate, htmlTemplate, Collections.EMPTY_LIST, "en", "test", createModel());

        String strTxt = mail.getTextVersion();
        assertNotNull(strTxt);
        assertTrue(strTxt.contains("Bender lives in theme park with blackjack poetess"));
        String strHtml = mail.getHtmlVersion();
        assertNotNull(strHtml);
        assertTrue(strHtml.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
    }

    /**
     * Text template only
     */
    @Test
    public void testComposeMailEntityInternalTextVersionOnly() throws MessagingException, IOException, ClassNotFoundException {
        // of course you would use DI in any real-world cases
        final Mail mail = new MailEntity();
        String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        String htmlTemplate = null;
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.composeMessage(mail, textTemplate, htmlTemplate, Collections.EMPTY_LIST, "en", "test", createModel());

        String strTxt = mail.getTextVersion();
        assertNotNull(strTxt);
        assertTrue(strTxt.contains("Bender lives in theme park with blackjack poetess"));
        String strHtml = mail.getHtmlVersion();
        assertNull(strHtml);
    }

    /**
     * html only
     */
    @Test
    public void testComposeMailEntityInternalHtmlVersionOnly() throws MessagingException, IOException, ClassNotFoundException {
        // of course you would use DI in any real-world cases
        final Mail mail = new MailEntity();
        String textTemplate = null;
        String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";
        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.composeMessage(mail, textTemplate, htmlTemplate, Collections.EMPTY_LIST, "en", "test", createModel());


        String strTxt = mail.getTextVersion();
        assertNull(strTxt);
        String strHtml = mail.getHtmlVersion();
        assertNotNull(strHtml);
        assertTrue(strHtml.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
    }

    @Test
    public void testComposeMailEntityInternalFullIntegration() throws MessagingException, IOException, ClassNotFoundException {

        final MailTemplateResourcesProvider mailTemplateResourcesProvider = mockery.mock(MailTemplateResourcesProvider.class);

        mockery.checking(new Expectations() {{
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "en", "imtest", ".txt");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.txt")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "en", "imtest", ".html");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.html")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "en", "imtest", ".properties");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.properties")), "UTF-8")));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "en", "imtest", "test.gif");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.gif")))));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "en", "imtest", "test.jpeg");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.jpeg")))));
        }});

        final Mail mail = new MailEntity();
        MailComposerImpl mailComposer = new MailComposerImpl(mailTemplateResourcesProvider);
        mailComposer.composeMessage(
                mail,
                "SHOP10",
                "en",
                Arrays.asList("default/mail/"),
                "imtest",
                "test@localhost.lo",
                "to@somedomain.com",
                "cc@somedomain.com",
                "bcc@somedomain.com",
                createModel());

        assertTrue(mail.getTextVersion().contains("Bender lives in theme park with blackjack poetess"));
        assertTrue(mail.getHtmlVersion().contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
        assertEquals("test@localhost.lo", mail.getFrom());
        assertEquals("to@somedomain.com", mail.getRecipients());
        assertEquals("cc@somedomain.com", mail.getCc());
        assertEquals("bcc@somedomain.com", mail.getBcc());
        assertEquals("Тема письма", mail.getSubject());
        assertEquals("SHOP10", mail.getShopCode());

        mockery.assertIsSatisfied();
    }


    /**
     * html only
     */
    @Test
    public void testConvertMailEntityToMimeMessage() throws MessagingException, IOException, ClassNotFoundException {
        // of course you would use DI in any real-world cases

        final Mail mail = new MailEntity();
        mail.setShopCode("SHOP10");
        mail.setSubject("Тема письма");
        mail.setFrom("test@localhost.lo");
        mail.setRecipients("to@somedomain.com");
        mail.setCc("cc@somedomain.com");
        mail.setBcc("bcc@somedomain.com");
        mail.setTextVersion("Bender lives in theme park with blackjack poetess");
        mail.setHtmlVersion("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>");

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        MailComposerImpl mailComposer = new MailComposerImpl(null);
        mailComposer.convertMessage(mail, message);

        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
        assertTrue(str.contains("From: test@localhost.lo"));
        assertTrue(str.contains("To: to@somedomain.com"));
        assertTrue(str.contains("cc@somedomain.com"));
        assertTrue(str.contains("Bcc: bcc@somedomain.com"));
        assertEquals("Тема письма", message.getSubject());


    }




}
