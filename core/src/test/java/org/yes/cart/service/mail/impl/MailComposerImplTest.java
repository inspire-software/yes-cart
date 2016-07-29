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

package org.yes.cart.service.mail.impl;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.MailPart;
import org.yes.cart.domain.entity.impl.MailEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.impl.GroovyGStringTemplateSupportImpl;
import org.yes.cart.service.mail.MailComposerTemplateSupport;
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

    private final String includeFunc = "<% \ndef include = {\n   func_include.doAction(it, locale, context)\n}\n %>";

    @Test
    public void testMerge$style() throws ClassNotFoundException, IOException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String ext = "txt";
        final String template = "$name lives...somewhere in time.";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, ext);
            will(returnValue(template));
            allowing(cacheManager).getCache("contentService-templateSupport");
            will(returnValue(cache));
            allowing(cache).get(includeFunc + template);
            will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + template)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);
        String result = mailComposer.processTemplate(chain, shopCode, locale, fileName, ext, model);
        assertEquals("Bender lives...somewhere in time.", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testMergeJspStyle() throws ClassNotFoundException, IOException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String ext = "txt";
        final String template = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, ext);
            will(returnValue(template));
            allowing(cacheManager).getCache("contentService-templateSupport");
            will(returnValue(cache));
            allowing(cache).get(includeFunc + template);
            will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + template)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);
        String result = mailComposer.processTemplate(chain, shopCode, locale, fileName, ext, createModel());
        assertEquals("Bender lives in theme park with blackjack poetess ", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testMergeGroovyStyle() throws ClassNotFoundException, IOException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String ext = "txt";
        final String template = "<% def deliverySum = 1.23456789; deliverySum = deliverySum.setScale(2, BigDecimal.ROUND_HALF_UP); out.print(deliverySum); %>";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, ext);
            will(returnValue(template));
            allowing(cacheManager).getCache("contentService-templateSupport");
            will(returnValue(cache));
            allowing(cache).get(includeFunc + template);
            will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + template)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);
        String result = mailComposer.processTemplate(chain, shopCode, locale, fileName, ext, createModel());
        assertEquals("1.23", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetResourcesId() throws ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        MailComposerImpl mailComposer = new MailComposerImpl(null, templates);
        //'cid:identifier1234' "cid:id" 'cid:ident' "cid:identifier5678"
        List<String> rez = mailComposer.getResourcesId("'cid:identifier1234' \"cid:id\" 'cid:ident' \"cid:identifier5678\"");
        assertEquals(4, rez.size());
        assertEquals("identifier1234", rez.get(0));
        assertEquals("id", rez.get(1));
        assertEquals("ident", rez.get(2));
        assertEquals("identifier5678", rez.get(3));
        mockery.assertIsSatisfied();
    }

    @Test
    public void testComposeMimeMessageInternalTextAndHtmlVersion() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String textTemplate = "Bender lives in theme park with blackjack poetess";
        String htmlTemplate = "<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>";
        Map<String, byte[]> attachments = Collections.emptyMap();
        MailComposerImpl mailComposer = new MailComposerImpl(null, templates);
        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, attachments, Collections.EMPTY_LIST, "SHOP10", "en", "test");
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
        mockery.assertIsSatisfied();
    }


    @Test
    public void testComposeMimeMessageInternalTextAndHtmlVersionWithAttach() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String textTemplate = "Bender lives in theme park with blackjack poetess";
        String htmlTemplate = "<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>";
        Map<String, byte[]> attachments = Collections.singletonMap("attachment:image/jpeg;myimage.jpg", new byte[]{1, 1, 1});
        MailComposerImpl mailComposer = new MailComposerImpl(null, templates);
        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, attachments, Collections.EMPTY_LIST, "SHOP10", "en", "test");
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
        // attachment
        assertTrue(str.contains("Content-Type: image/jpeg; name=myimage.jpg"));
        assertTrue(str.contains("Content-Transfer-Encoding: base64"));
        assertTrue(str.contains("Content-Disposition: attachment; filename=myimage.jpg"));
        assertTrue(str.contains("AQEB"));
        mockery.assertIsSatisfied();
    }


    /**
     * Text template only
     */
    @Test
    public void testComposeMimeMessageInternalTextVersionOnly() throws MessagingException, IOException, ClassNotFoundException {


        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));


        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String textTemplate = "Bender lives in theme park with blackjack poetess";
        String htmlTemplate = null;
        Map<String, byte[]> attachments = Collections.emptyMap();
        MailComposerImpl mailComposer = new MailComposerImpl(null, templates);
        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, attachments, Collections.EMPTY_LIST, "SHOP10", "en", "test");
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
        mockery.assertIsSatisfied();
    }

    /**
     * html only
     */
    @Test
    public void testComposeMimeMessageInternalHtmlVersionOnly() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));


        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String textTemplate = null;
        String htmlTemplate = "<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>";
        Map<String, byte[]> attachments = Collections.emptyMap();
        MailComposerImpl mailComposer = new MailComposerImpl(null, templates);
        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, attachments, Collections.EMPTY_LIST, "SHOP10", "en", "test");
        assertTrue(helper.isMultipart());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");
        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));
        mockery.assertIsSatisfied();
    }

    @Test
    public void testComposeMimeMessageInternalFullIntegration() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(with(any(String.class))); will(returnValue(null));
            allowing(cache).put(with(any(String.class)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        final MailTemplateResourcesProvider mailTemplateResourcesProvider = mockery.mock(MailTemplateResourcesProvider.class);

        mockery.checking(new Expectations() {{
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", ".txt");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.txt")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", ".html");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.html")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", ".properties");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.properties")), "UTF-8")));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", "test.gif");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.gif")))));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", "test.jpeg");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.jpeg")))));
        }});

        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        MailComposerImpl mailComposer = new MailComposerImpl(mailTemplateResourcesProvider, templates);
        mailComposer.composeMessage(
                message,
                "SHOP10",
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

    private Map<String, Object> createModelWithAttach() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        model.put("with", Arrays.asList("blackjack", "poetess"));
        model.put("attachment:image/jpeg;myimage.jpg", new byte[] { 1, 1, 1 });
        return model;
    }

    @Test
    public void complexObjectNavigation()  throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);
        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String ext = "txt";
        final String template = "$root.first $root.second";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, ext);
            will(returnValue(template));
            allowing(cacheManager).getCache("contentService-templateSupport");
            will(returnValue(cache));
            allowing(cache).get(includeFunc + template);
            will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + template)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        Map<String, Object> model = new HashMap<String,Object>();
        model.put("root", new Pair("hi", "there"));
        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);
        String result = mailComposer.processTemplate(chain, shopCode, locale, fileName, ext, model);
        assertEquals("hi there", result);
        mockery.assertIsSatisfied();

    }


    @Test
    public void testComposeMailEntityInternalTextAndHtmlVersion() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String txt = "txt";
        final String html = "html";
        final String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        final String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, txt);
            will(returnValue(textTemplate));
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, html);
            will(returnValue(htmlTemplate));
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(includeFunc + textTemplate); will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + textTemplate)), with(any(Object.class)));
            allowing(cache).get(includeFunc + htmlTemplate); will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + htmlTemplate)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);

        String resultHtml = mailComposer.processTemplate(chain, shopCode, locale, fileName, html, createModel());
        String resultTxt = mailComposer.processTemplate(chain, shopCode, locale, fileName, txt, createModel());

        assertEquals("Bender lives in theme park with blackjack poetess ", resultTxt);
        assertEquals("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>", resultHtml);
        mockery.assertIsSatisfied();
    }

    /**
     * Text template only
     */
    @Test
    public void testComposeMailEntityInternalTextVersionOnly() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String ext = "txt";
        final String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, ext);
            will(returnValue(textTemplate));
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(includeFunc + textTemplate); will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + textTemplate)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        // of course you would use DI in any real-world cases
        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);
        String result = mailComposer.processTemplate(chain, shopCode, locale, fileName, ext, createModel());
        assertEquals("Bender lives in theme park with blackjack poetess ", result);
        mockery.assertIsSatisfied();
    }

    /**
     * html only
     */
    @Test
    public void testComposeMailEntityInternalHtmlVersionOnly() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String ext = "txt";
        final String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, ext);
            will(returnValue(htmlTemplate));
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(includeFunc + htmlTemplate); will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + htmlTemplate)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        // of course you would use DI in any real-world cases
        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);
        String result = mailComposer.processTemplate(chain, shopCode, locale, fileName, ext, createModel());
        assertEquals("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testComposeMailEntityInternalFullIntegration() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(with(any(String.class))); will(returnValue(null));
            allowing(cache).put(with(any(String.class)), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        final MailTemplateResourcesProvider mailTemplateResourcesProvider = mockery.mock(MailTemplateResourcesProvider.class);

        mockery.checking(new Expectations() {{
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", ".txt");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.txt")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", ".html");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.html")), "UTF-8")));
            one(mailTemplateResourcesProvider).getTemplate(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", ".properties");
            will(returnValue(IOUtils.toString(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/imtest.properties")), "UTF-8")));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", "test.gif");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.gif")))));
            one(mailTemplateResourcesProvider).getResource(Arrays.asList("default/mail/"), "SHOP10", "en", "imtest", "test.jpeg");
            will(returnValue(IOUtils.toByteArray(new FileInputStream(new File("src/test/resources/mailtemplates/default/imtest/resources/test.jpeg")))));
        }});

        final Mail mail = new MailEntity();
        MailComposerImpl mailComposer = new MailComposerImpl(mailTemplateResourcesProvider, templates);
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

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get("<% \n %>${name} is awesome!"); will(returnValue(null));
            allowing(cache).put(with(equal("<% \n %>${name} is awesome!")), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

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

        MailComposerImpl mailComposer = new MailComposerImpl(null, templates);
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
        mockery.assertIsSatisfied();

    }


    /**
     * html only
     */
    @Test
    public void testConvertMailEntityToMimeMessageWithAttach() throws MessagingException, IOException, ClassNotFoundException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get("<% \n %>${name} is awesome!"); will(returnValue(null));
            allowing(cache).put(with(equal("<% \n %>${name} is awesome!")), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

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
        final MailPart attach = mail.addPart();
        attach.setFilename("myimage.jpg");
        attach.setResourceId("attachment:image/jpeg;myimage.jpg");
        attach.setData(new byte[] { 1, 1, 1 });

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        MailComposerImpl mailComposer = new MailComposerImpl(null, templates);
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
        // attachment
        assertTrue(str.contains("Content-Type: image/jpeg; name=myimage.jpg"));
        assertTrue(str.contains("Content-Transfer-Encoding: base64"));
        assertTrue(str.contains("Content-Disposition: attachment; filename=myimage.jpg"));
        assertTrue(str.contains("AQEB"));
        mockery.assertIsSatisfied();

    }

    @Test
    public void testCollectAttachments() throws Exception {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get("<% \n %>${name} is awesome!"); will(returnValue(null));
            allowing(cache).put(with(equal("<% \n %>${name} is awesome!")), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        final MailComposerImpl composer = new MailComposerImpl(null, templates);

        final Map<String, Object> model = createModelWithAttach();
        final Map<String, byte[]> attach = composer.collectAttachments(model);

        assertNotNull(attach);
        assertFalse(attach.size() == model.size());
        assertEquals(1, attach.size());
        assertTrue(attach.entrySet().iterator().next().getKey().startsWith("attachment:"));

    }

    @Test
    public void testConvertAttachmentKeyIntoContentTypeAndFilename() throws Exception {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);

        mockery.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get("<% \n %>${name} is awesome!"); will(returnValue(null));
            allowing(cache).put(with(equal("<% \n %>${name} is awesome!")), with(any(Object.class)));
        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        final MailComposerImpl composer = new MailComposerImpl(null, templates);

        Pair<String, String> ctaf;

        ctaf = composer.convertAttachmentKeyIntoContentTypeAndFilename(null);
        assertNull(ctaf);
        ctaf = composer.convertAttachmentKeyIntoContentTypeAndFilename("random");
        assertNull(ctaf);
        ctaf = composer.convertAttachmentKeyIntoContentTypeAndFilename("not attach");
        assertNull(ctaf);
        ctaf = composer.convertAttachmentKeyIntoContentTypeAndFilename("attachment:");
        assertNull(ctaf);
        ctaf = composer.convertAttachmentKeyIntoContentTypeAndFilename("attachment:image/jpeg");
        assertNull(ctaf);
        ctaf = composer.convertAttachmentKeyIntoContentTypeAndFilename("attachment:application/pdf;myfile.pdf");
        assertNotNull(ctaf);
        assertEquals("application/pdf", ctaf.getFirst());
        assertEquals("myfile.pdf", ctaf.getSecond());

    }

    @Test
    public void testMergeWithIncludes() throws ClassNotFoundException, IOException {

        final CacheManager cacheManager = mockery.mock(CacheManager.class);
        final Cache cache = mockery.mock(Cache.class);
        final MailTemplateResourcesProvider provider = mockery.mock(MailTemplateResourcesProvider.class);

        final List<String> chain = Collections.EMPTY_LIST;
        final String shopCode = "S001";
        final String locale = "en";
        final String fileName = "tmp";
        final String ext = "txt";
        final String template = "${include('header')}$name lives in theme park with <% with.each{ out.print(it + ' ');}%>${include('footer')}";
        final String templateHeader = "Email header and a logo\n\n";
        final String templateFooter = "\n\nYours truly, team";

        mockery.checking(new Expectations() {{
            allowing(provider).getTemplate(chain, shopCode, locale, fileName, ext);
            will(returnValue(template));
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(includeFunc + template); will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + template)), with(any(Object.class)));
            allowing(provider).getTemplate(chain, shopCode, locale, "header", ext);
            will(returnValue(templateHeader));
            allowing(cache).get(includeFunc + templateHeader); will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + templateHeader)), with(any(Object.class)));
            allowing(provider).getTemplate(chain, shopCode, locale, "footer", ext);
            will(returnValue(templateFooter));
            allowing(cache).get(includeFunc + templateFooter); will(returnValue(null));
            allowing(cache).put(with(equal(includeFunc + templateFooter)), with(any(Object.class)));

        }});

        final MailComposerTemplateSupport templates = new MailComposerTemplateSupportGroovyImpl(new GroovyGStringTemplateSupportImpl(cacheManager));

        MailComposerImpl mailComposer = new MailComposerImpl(provider, templates);
        String result = mailComposer.processTemplate(chain, shopCode, locale, fileName, ext, createModel());
        assertEquals("Email header and a logo\n\nBender lives in theme park with blackjack poetess \n\nYours truly, team", result);
        mockery.assertIsSatisfied();
    }



}
