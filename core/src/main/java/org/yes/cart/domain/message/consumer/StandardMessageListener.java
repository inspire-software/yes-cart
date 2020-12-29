/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.utils.MessageFormatUtils;
import org.yes.cart.utils.log.Markers;

import java.util.HashMap;
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

    private static final Logger LOG = LoggerFactory.getLogger(StandardMessageListener.class);

    /**
     * Shop code.  Shop code context variable.
     */
    public static final String SHOP_CODE = "shop_code";

    /**
     * Shop.  {@link Shop} context variable.
     */
    public static final String SHOP = "shop";

    /**
     * Shop code.  Shop code context variable.
     */
    public static final String CUSTOMER_SHOP_CODE = "customerShop_code";

    /**
     * Shop.  {@link Shop} context variable.
     */
    public static final String CUSTOMER_SHOP = "customerShop";

    /**
     * Customer login.  Login context variable.
     */
    public static final String CUSTOMER_LOGIN = "login";

    /**
     * Customer email.  Email context variable.
     */
    public static final String CUSTOMER_EMAIL = "email";

    /**
     * Customer object.  {@link org.yes.cart.domain.entity.Customer} context variable. (could be null)
     */
    public static final String CUSTOMER = "customer";

    /**
     * Customer object.  {@link org.yes.cart.domain.entity.Address} context variable. (could be null)
     */
    public static final String SHIPPING_ADDRESS = "shippingAddress";

    /**
     * Customer object.  {@link org.yes.cart.domain.entity.Address} context variable. (could be null)
     */
    public static final String BILLING_ADDRESS = "billingAddress";

    /**
     * Result.  Result context variable.
     */
    public static final String RESULT = "result";

    /**
     * All order payments. List of {@link CustomerOrderPayment} context variable.
     */
    public static final String PAYMENTS = "payments";

    /**
     * All parameters (from AOP join point) will be passed with index param0, param1, etc
     */
    public static final String PARAM_PREFIX = "param";

    /**
     * Default object, which passed to email template. (E.g. in order notifications it is the {@link org.yes.cart.domain.entity.CustomerOrder})
     */
    public static final String ROOT = "root";

    /**
     * Map of SKU codes to ProductSku entity to allow detailed order notifications
     */
    public static final String PRODUCTS = "products";

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
     * Delivery name. {@link org.yes.cart.domain.entity.CustomerOrderDelivery} id this is a delivery notification.
     */
    public static final String DELIVERY = "delivery";

    /**
     * Delivery name. {@link org.yes.cart.domain.entity.CustomerOrderDelivery} id this is a delivery notification.
     */
    public static final String SUP_DELIVERIES = "supplierDeliveries";

    /**
     * Carrier name. Localised carrier name.
     */
    public static final String DELIVERY_CARRIER = "deliveryCarrierName";

    /**
     * Delivery sla name. Localised SLA name.
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

    private final ProductSkuService productSkuService;

    private final ShopService shopService;

    private final Object objectMessage;

    /**
     * Constructor for listener.
     */
    public StandardMessageListener(final MailService mailService,
                                   final MailComposer mailComposer,
                                   final CustomerService customerService,
                                   final ProductSkuService productSkuService,
                                   final ShopService shopService,
                                   final Object objectMessage) {
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.shopService = shopService;
        this.customerService = customerService;
        this.productSkuService = productSkuService;
        this.objectMessage = objectMessage;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        final Map<String, Object> map = (Map<String, Object>) objectMessage;

        try {
            try {
                if (map.get(SHOP) == null) {
                    enrichMapWithShop(map);
                }
                if (map.get(CUSTOMER) == null) {
                    enrichMapWithCustomer(map);
                }
                if (map.get(ROOT) instanceof CustomerOrder) {
                    enrichMapWithProducts(map);
                }

                final Mail mail = mailService.getGenericDao().getEntityFactory().getByIface(Mail.class);

                final String attrVal = ((Shop)map.get(SHOP)).getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);
                String fromEmail = null;
                if (StringUtils.isNotBlank(attrVal)) {
                    fromEmail = attrVal;
                }

                final String email = (String) map.get(CUSTOMER_EMAIL);  //email recipient - to
                if (StringUtils.isNotBlank(email)) {
                    mailComposer.composeMessage(
                            mail,
                            (String) map.get(SHOP_CODE),
                            (String) map.get(LOCALE),
                            (List<String>) map.get(TEMPLATE_FOLDER),
                            (String) map.get(TEMPLATE_NAME),
                            fromEmail,
                            email,
                            null,
                            null,
                            map);

                    mailService.create(mail);
                } else {
                    LOG.debug("Unable to send email for {} as email does not exist", map.get(CUSTOMER_LOGIN));
                }

            } catch (Exception e) {
                LOG.error(Markers.alert(),
                        MessageFormatUtils.format(
                                "Cannot compose or send email template {} with locale {} theme {} to {}, login: {}, cause: {}",
                                map.get(TEMPLATE_NAME),
                                map.get(LOCALE),
                                map.get(TEMPLATE_FOLDER),
                                map.get(CUSTOMER_EMAIL),
                                map.get(CUSTOMER_LOGIN),
                                e.getMessage()
                        ),
                        e);
            }

        } catch(ClassCastException cce) {
            LOG.error("Class cast exception " + cce.getMessage(), cce);

        }



    }

    /**
     * Enrich given map with product date.
     *
     * @param map given map to enrich
     */
    private void enrichMapWithProducts(final Map<String, Object> map) {

        final Map<String, ProductSku> products = new HashMap<>();

        for (final CustomerOrderDet orderDet : ((CustomerOrder) map.get(ROOT)).getOrderDetail()) {

            final ProductSku sku = productSkuService.getProductSkuBySkuCode(orderDet.getProductSkuCode());
            if (sku != null) {
                products.put(sku.getCode(), sku);
            }

        }

        map.put(PRODUCTS, products);
    }

    /**
     * Enrich given map with shopper object.
     *
     * @param map given map to enrich
     */
    private void enrichMapWithCustomer(final Map<String, Object> map) {
        map.put(CUSTOMER,
                customerService.getCustomerByLogin((String) map.get(CUSTOMER_EMAIL), (Shop) map.get(SHOP)));


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
