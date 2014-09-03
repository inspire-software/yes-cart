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

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.ShopCodeContext;

import java.text.MessageFormat;
import java.util.List;
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

    /**
     * Template folder.
     */
    public static final String LOCALE = "locale";

    /**
     * Carrier name.
     */
    public static final String DELIVERY_CARRIER = "deliveryCarrierName";

    /**
     * Delivery sla name.
     */
    public static final String DELIVERY_CARRIER_SLA = "deliveryCarrierSlaName";

    /**
     * Delivery number.
     */
    public static final String DELIVERY_NUM = "deliveryNum";

    /**
     * Delivery external number.
     */
    public static final String DELIVERY_EXTERNAL_NUM = "deliveryExtNum";

    /**
     * Payment gatewa features.
     */
    public static final String PAYMENT_GATEWAY_FEATURE = "paymentGatewayFeatures";

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final ShopService shopService;

    private final Object objectMessage;

    /**
     * Constructor for listener.
     */
    public StandardMessageListener(
            final MailService mailService,
            final MailComposer mailComposer,
            final CustomerService customerService,
            final ShopService shopService,
            final Object objectMessage) {
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.shopService = shopService;
        this.customerService = customerService;
        this.objectMessage = objectMessage;

    }


    /**
     * {@inheritDoc}
     */
    public void run() {

        final Map<String, Object> map = (Map<String, Object>) objectMessage;

        try {
            try {
                if (map.get(CUSTOMER) == null) {
                    enrichMapWithCustomer(map);
                }
                if (map.get(SHOP) == null) {
                    enrichMapWithShop(map);
                }

                final Mail mail = mailService.getGenericDao().getEntityFactory().getByIface(Mail.class);

                final AttrValueShop attrVal = ((Shop)map.get(SHOP)).getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);
                String fromEmail = null;
                if (attrVal != null) {
                    fromEmail = attrVal.getVal();
                }

                mailComposer.composeMessage(
                        mail,
                        (String) map.get(SHOP_CODE),
                        (String) map.get(LOCALE),
                        (List<String>) map.get(TEMPLATE_FOLDER),
                        (String) map.get(TEMPLATE_NAME),
                        fromEmail,
                        (String) map.get(CUSTOMER_EMAIL),  //email recipient - to
                        null,
                        null,
                        map);

                mailService.create(mail);

            } catch (Exception e) {
                ShopCodeContext.getLog(this).error(
                        MessageFormat.format(
                                "Cant compose or send email template {0} with locale {1} folder {2} to {3}",
                                map.get(TEMPLATE_NAME),
                                map.get(LOCALE),
                                map.get(TEMPLATE_FOLDER),
                                map.get(CUSTOMER_EMAIL)
                        ),
                        e);
            }

        } catch(ClassCastException cce) {
            ShopCodeContext.getLog(this).error("Class cast exception ", cce);

        }



    }

    /**
     * Enrich given map with shopper object.
     *
     * @param map given map to enrich
     */
    private void enrichMapWithCustomer(final Map<String, Object> map) {
        map.put(CUSTOMER,
                customerService.getCustomerByEmail((String) map.get(CUSTOMER_EMAIL)));


    }

    /**
     * Enrich given map with shop object.
     *
     * @param map given map to enrich
     */
    private void enrichMapWithShop(final Map<String, Object> map) {

        map.put(SHOP,
                shopService.getShopByCode((String) map.get(SHOP_CODE)));


    }


}
