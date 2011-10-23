package org.yes.cart.service.mail.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.mail.MailComposer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class MailComposerImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    private SystemService systemService;
    private ShopService shopService;
    private Shop shop;



    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {

        systemService = mockery.mock(SystemService.class);
        shopService = mockery.mock(ShopService.class);
        shop = mockery.mock(Shop.class);




    }



    @Test
    public void testMerge1() throws ClassNotFoundException, IOException {
        final String template = "$name lives...somewhere in time.";
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        final MailComposerImpl mailComposer = new MailComposerImpl(null, null);
        final String result = mailComposer.merge(template, model);
        assertEquals("Bender lives...somewhere in time.", result);
    }

    @Test
    public void testMerge2() throws ClassNotFoundException, IOException {
        final String template = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        List list = new ArrayList();
        list.add("blackjack");
        list.add("poetess");
        model.put("with", list);
        final MailComposerImpl mailComposer = new MailComposerImpl(null, null);
        final String result = mailComposer.merge(template, model);
        assertEquals("Bender lives in theme park with blackjack poetess ", result);
    }



    @Test
    public void testGetResourcesId() {
        final MailComposerImpl mailComposer = new MailComposerImpl(null, null);
                                                            //'cid:identifier1234' "cid:id" 'cid:ident' "cid:identifier5678"
        final List<String> rez = mailComposer.getResourcesId("'cid:identifier1234' \"cid:id\" 'cid:ident' \"cid:identifier5678\"");
        assertEquals(4, rez.size());
        assertEquals("identifier1234", rez.get(0));
        assertEquals("id", rez.get(1));
        assertEquals("ident", rez.get(2));
        assertEquals("identifier5678", rez.get(3)); 
    }

    @Test
    public void getPathToTemplate() {

        mockery.checking(new Expectations() {{

           // allowing(systemService).getMailResourceDirectory();
           // will(returnValue("/a/b/c/"));


            allowing(shopService).getShopByCode("SHOIP1");
            will(returnValue(shop));


            allowing(shop).getMailFolder();
            will(returnValue("/a/b/c/default/"));


        }});
        

        final MailComposerImpl mailComposer = new MailComposerImpl(systemService, shopService);


        assertEquals(
                ("/a/b/c/default" + File.separator + "priceReduced" + File.separator).replace("\\","/"),
                (mailComposer.getPathToTemplate("SHOIP1", "priceReduced")).replace("\\","/")
        );




    }


    @Test
    public void testComposeMessageInternal0() throws MessagingException, IOException, ClassNotFoundException {

        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        final String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        final String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        List list = new ArrayList();
        list.add("blackjack");
        list.add("poetess");
        model.put("with", list);
        final MailComposerImpl mailComposer = new MailComposerImpl(null, null);

        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, null, model);

        assertTrue(helper.isMultipart());


        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        helper.getMimeMessage().writeTo(byteArrayOutputStream);

        String str = byteArrayOutputStream.toString("UTF-8");

        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));

    }

    /** Text template only */
    @Test
    public void testComposeMessageInternal1() throws MessagingException, IOException, ClassNotFoundException {

        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        final String textTemplate = "$name lives in theme park with <% with.each{ out.print(it + ' ');}%>";
        final String htmlTemplate = null;
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        List list = new ArrayList();
        list.add("blackjack");
        list.add("poetess");
        model.put("with", list);
        final MailComposerImpl mailComposer = new MailComposerImpl(null, null);

        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, null, model);

        assertTrue(helper.isMultipart());


        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        helper.getMimeMessage().writeTo(byteArrayOutputStream);

        String str = byteArrayOutputStream.toString("UTF-8");

        assertNotNull(str);
        // html and text present in mail message
        assertTrue(str.contains("Bender lives in theme park with blackjack poetess"));


    }

    /** html only */
    @Test
    public void testComposeMessageInternal2() throws MessagingException, IOException, ClassNotFoundException {

        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        final String textTemplate = null;
        final String htmlTemplate = "<h2>$name</h2> lives in theme park with:<br> <% with.each{ out.print(it + '<br>');}%>";
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        List list = new ArrayList();
        list.add("blackjack");
        list.add("poetess");
        model.put("with", list);
        final MailComposerImpl mailComposer = new MailComposerImpl(null, null);

        mailComposer.composeMessage(helper, textTemplate, htmlTemplate, null, model);

        assertTrue(helper.isMultipart());


        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        helper.getMimeMessage().writeTo(byteArrayOutputStream);

        String str = byteArrayOutputStream.toString("UTF-8");

        assertNotNull(str);
        // html and text present in mail message
        
        assertTrue(str.contains("<h2>Bender</h2> lives in theme park with:<br> blackjack<br>poetess<br>"));

    }


    /**
     * Inline resources
     */
    @Test
    public void testComposeMessageInternal3() throws MessagingException, IOException, ClassNotFoundException {


        mockery.checking(new Expectations() {{

            allowing(systemService).getMailResourceDirectory();
            will(returnValue("src/test/resources/mailtemplates/"));


        }});




        // of course you would use DI in any real-world cases
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Bender");
        List list = new ArrayList();
        list.add("blackjack");
        list.add("poetess");
        model.put("with", list);
        final MailComposer mailComposer = new MailComposerImpl(systemService, null);

        mailComposer.composeMessage(
                message,
                null,
                "src/test/resources/mailtemplates/default/",
                "imtest",
                "test@localhost.lo",
                "to@somedomain.com",
                "cc@somedomain.com",
                "bcc@somedomain.com",
                model);


        assertTrue(helper.isMultipart());


        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

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



    }


}
