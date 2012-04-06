package org.yes.cart.domain.message.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.ShopCodeContext;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Standard message listener, which get the message, extract shopper id, enrich context with customer object
 * and perform mail notification within specified mail template.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 4/7/12
 * Time: 4:12 PM
 */
public class StandardMessageListener  implements MessageListener {

    /**
     * Shop code.  Email context variable.
     */
    public static final String SHOP_CODE = "shop_code";

    /**
     * Shop.  Email context variable.
     */
    public static final String SHOP = "shop";

    /**
     * Customer email.  Email context variable.
     */
    public static final String CUSTOMER_EMAIL = "email";

    /**
     * Customer object.  Email context variable.
     */
    public static final String CUSTOMER = "customer";

    /**
     * Result.  Email context variable.
     */
    public static final String RESULT = "result";

    /** Default object, which passed to email template.  */
    public static final String DEFAULT = "default";
    /** Current template folder. */
    public static final String TEMPLATE_FOLDER = "templateFolder";
    /** Template folder. */
    public static final String TEMPLATE_NAME = "templateName";


    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;

    /**
     * Contruct jms listener.
     *
     * @param javaMailSender mail sender to use.
     * @param mailComposer   mail composer
     */
    public StandardMessageListener(
            final JavaMailSender javaMailSender,
            final MailComposer mailComposer) {
        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;
    }
    

    /**
     * {@inheritDoc}
     */
    public void onMessage(final Message message) {

        final ObjectMessage objectMessage = (ObjectMessage) message;

        try {
            final Map<String, Object> map = (Map<String, Object>) objectMessage.getObject();
            if (map.get(CUSTOMER) == null) {
                enrichMapWithCustomer(map);
            }
            if (map.get(SHOP) == null) {
                enrichMapWithShop(map);
            }



            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            mailComposer.composeMessage(
                    mimeMessage,
                    null,
                    (String) map.get(TEMPLATE_FOLDER),
                    (String) map.get(TEMPLATE_NAME),
                    null,//must be from properties "todo@getfromsho.com", //((Shop)map.get(SHOP)).getAttribute()
                    (String) map.get(CUSTOMER_EMAIL),
                    null,
                    null,
                    map);

            javaMailSender.send(mimeMessage);



        } catch (Exception e) {
            LOG.error("Cant cast object message body to expected format " , message);
        }


    }

    /**
     * Enrich given map with shopper object.
     * @param map givem map to enrich
     */
    private void enrichMapWithCustomer(final Map<String, Object> map) {


    }

    /**
     * Enrich given map with shop object.
     * @param map givem map to enrich
     */
    private void enrichMapWithShop(final Map<String, Object> map) {


    }

}
