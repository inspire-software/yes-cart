package org.yes.cart.domain.message.consumer;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;

import java.util.HashSet;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerRegistrationMessageListenerTest extends BaseCoreDBTestCase {

    // TODO fix to allow running from IDE
    @Test
    public void testOnMessage0() throws Exception {
        CustomerRegistrationMessageListener customerRegistrationMessageListener = (CustomerRegistrationMessageListener)
                ctx.getBean("customerRegistrationListener");
        RegistrationMessage registrationMessage = createRegistrationMessage();
        SimpleSmtpServer server = SimpleSmtpServer.start(2525);
        customerRegistrationMessageListener.processMessage(registrationMessage);
        server.stop();
        assertThat(server.getReceivedEmailSize(), is(1));
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) emailIter.next();
        assertThat(email.getBody(),
                allOf(containsString("neWpaSswOrd"),
                        containsString("neWpaSswOrd"),
                        containsString("Bender"),
                        containsString("Rodrigez"),
                        containsString("Gadget universe"),
                        containsString("somegadget.com")));
        assertThat(email.getHeaderValue("From"), is("noreply@shop.com"));
        assertThat(email.getHeaderValue("Subject"), is("Password has been changed"));
    }

    private RegistrationMessage createRegistrationMessage() {
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
        return registrationMessage;
    }
}
