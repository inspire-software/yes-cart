package org.yes.cart.domain.message.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.ShopCodeContext;

import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Standard message listener, which get the message, extract shopper id, enrich context with customer object
 * and perform mail notification within specified mail template.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 4/7/12
 * Time: 4:12 PM
 */
public class StandardMessageListener implements Runnable {

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
     * Result.  Result context variable.
     */
    public static final String RESULT = "result";

    /**
     * All parameters will be passed with index param0, param1, etc
     */
    public static final String PARAM_PREFIX = "param";

    /**
     * Default object, which passed to email template.
     */
    public static final String ROOT = "root";
    /**
     * Current template folder.
     */
    public static final String TEMPLATE_FOLDER = "templateFolder";
    /**
     * Template folder.
     */
    public static final String TEMPLATE_NAME = "templateName";


    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final ShopService shopService;

    private final Object objectMessage;

    /**
     * Contruct jms listener.
     *
     * @param javaMailSender  mail sender to use.
     * @param mailComposer    mail composer
     * @param shopService     shop service
     * @param customerService customer service
     */
    public StandardMessageListener(
            final JavaMailSender javaMailSender,
            final MailComposer mailComposer,
            final CustomerService customerService,
            final ShopService shopService,
            final Object objectMessage) {
        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;
        this.shopService = shopService;
        this.customerService = customerService;
        this.objectMessage = objectMessage;

    }


    /**
     * {@inheritDoc}
     */
    public void run() {

        try {
            final Map<String, Object> map = (Map<String, Object>) objectMessage;
            if (map.get(CUSTOMER) == null) {
                enrichMapWithCustomer(map);
            }
            if (map.get(SHOP) == null) {
                enrichMapWithShop(map);
            }


            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            mailComposer.composeMessage(
                    mimeMessage,
                    (String) map.get(SHOP_CODE),
                    (String) map.get(TEMPLATE_FOLDER),
                    (String) map.get(TEMPLATE_NAME),
                    null,//todo must be from properties "todo@getfromsho.com", //((Shop)map.get(SHOP)).getAttribute()
                    (String) map.get(CUSTOMER_EMAIL),
                    null,
                    null,
                    map);

            javaMailSender.send(mimeMessage);


        } catch (Exception e) {
            LOG.error("Cant cast object message body to expected format map ", objectMessage);
        }


    }

    /**
     * Enrich given map with shopper object.
     *
     * @param map givem map to enrich
     */
    private void enrichMapWithCustomer(final Map<String, Object> map) {
        map.put(CUSTOMER,
                customerService.findCustomer((String) map.get(CUSTOMER_EMAIL)));


    }

    /**
     * Enrich given map with shop object.
     *
     * @param map givem map to enrich
     */
    private void enrichMapWithShop(final Map<String, Object> map) {

        map.put(SHOP,
                shopService.getShopByCode((String) map.get(SHOP_CODE)));


    }


}
