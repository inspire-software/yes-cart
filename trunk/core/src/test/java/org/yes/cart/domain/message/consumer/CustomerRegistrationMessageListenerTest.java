package org.yes.cart.domain.message.consumer;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.Test;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;

import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerRegistrationMessageListenerTest extends BaseCoreDBTestCase {

    @Test
    public void testOnMessage0() throws Exception {
        CustomerRegistrationMessageListener customerRegistrationMessageListener = (CustomerRegistrationMessageListener)
                ctx.getBean("customerRegistrationListener");
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
        registrationMessage.setPathToTemplateFolder("src/test/resources/mailtemplates/SHOIP1/");
        registrationMessage.setTemplateName("customerChangePassword");

        SimpleSmtpServer server = SimpleSmtpServer.start(2525);

        customerRegistrationMessageListener.processMessage(registrationMessage);

        server.stop();
        assertTrue(server.getReceivedEmailSize() == 1);
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) emailIter.next();

        assertTrue(email.getBody().contains("neWpaSswOrd"));
        assertTrue(email.getBody().contains("Bender"));
        assertTrue(email.getBody().contains("Rodrigez"));
        assertTrue(email.getBody().contains("Gadget universe"));
        assertTrue(email.getBody().contains("somegadget.com"));
        assertEquals("noreply@shop.com", email.getHeaderValue("From"));
        assertEquals("Password has been changed", email.getHeaderValue("Subject"));
    }
}
