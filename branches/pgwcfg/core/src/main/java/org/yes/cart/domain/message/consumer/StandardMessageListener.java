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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
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

            boolean send = false;
            while (!send) {
                try {
                    javaMailSender.send(mimeMessage);
                    send = true;
                    LOG.info("Mail send to " + (String) map.get(CUSTOMER_EMAIL) );
                } catch (MailSendException me) {
                    /**
                     * TODO
                     * In case of failure, thread must use some persisten storage to
                     * store email and send it latter.
                     * Any persistem cache may be used for this purposes.
                     */
                    LOG.error("Cant send email to " + (String) map.get(CUSTOMER_EMAIL) + " " + me.getMessage());
                    Thread.sleep(60000);

                }
            }



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
