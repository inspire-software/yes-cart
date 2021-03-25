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

package org.yes.cart.service.domain.aspect.impl;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.mail.impl.MailUtils;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.utils.spring.LinkedHashMapBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aspect responsible to send notifications about
 * User: Igor Azarny
 * Date: 7 Apr 2012
 * Time: 4:41 PM
 */
@Aspect
public class PaymentAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentAspect.class);

    private final ProductSkuService productSkuService;

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final CustomerOrderPaymentService customerOrderPaymentService;

    private final ShopService shopService;

    private final ThemeService themeService;

    private final PaymentModulesManager paymentModulesManager;

    private Map<String, String> authoriseShopperTemplates = new HashMap<>();
    private Map<String, String> cancelShopperTemplates = new HashMap<>();
    private Map<String, String> shipmentShopperTemplates = new HashMap<>();
    private Map<String, String> authoriseAdminTemplates = new HashMap<>();
    private Map<String, String> cancelAdminTemplates = new HashMap<>();
    private Map<String, String> shipmentAdminTemplates = new HashMap<>();


    /**
     * Construct aspect.
     *
     */
    public PaymentAspect(final TaskExecutor taskExecutor,
                         final ProductSkuService productService,
                         final MailService mailService,
                         final MailComposer mailComposer,
                         final CustomerService customerService,
                         final CustomerOrderPaymentService customerOrderPaymentService,
                         final ShopService shopService,
                         final ThemeService themeService,
                         final PaymentModulesManager paymentModulesManager) {
        super(taskExecutor);
        this.productSkuService = productService;
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.shopService = shopService;
        this.customerService = customerService;
        this.customerOrderPaymentService = customerOrderPaymentService;
        this.themeService = themeService;
        this.paymentModulesManager = paymentModulesManager;


    }

    @Override
    public Runnable getTask(final Serializable serializableMessage) {
        return new StandardMessageListener(
                mailService,
                mailComposer,
                customerService,
                productSkuService,
                shopService,
                serializableMessage);

    }



    /**
     * Perform shopper notification, about payment authorize/capture.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.authorize(..))")
    public Object doAuthorize(final ProceedingJoinPoint pjp) throws Throwable {

        final String rez = (String) pjp.proceed();

        doNotify(pjp, rez, authoriseShopperTemplates, authoriseAdminTemplates);

        return rez;

    }




    /**
     * Perform shopper notification, about payment cancellation.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.cancelOrder(..))")
    public Object doCancel(final ProceedingJoinPoint pjp) throws Throwable {

        final String rez = (String) pjp.proceed();

        doNotify(pjp, rez, cancelShopperTemplates, cancelAdminTemplates);

        return rez;

    }



    /**
     * Perform shopper notification, about payment capture after authorise.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.shipmentComplete(..))")
    public Object doShipmentComplete(final ProceedingJoinPoint pjp) throws Throwable {

        final String rez = (String) pjp.proceed();

        doNotify(pjp, rez, shipmentShopperTemplates, shipmentAdminTemplates);

        return rez;

    }


    /**
     * Perform generic notification regarding payment.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @param rez payment result {@link org.yes.cart.payment.dto.Payment}
     * @param shopperTemplates shopper templates mapping
     * @param adminTemplates admin templates mapping
     */
    protected void doNotify(final ProceedingJoinPoint pjp,
                            final String rez,
                            final Map<String, String> shopperTemplates,
                            final Map<String, String> adminTemplates) {

        final CustomerOrder order = (CustomerOrder) pjp.getArgs()[0];
        final Shop pgShop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();
        final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(order.getPgLabel(), pgShop.getCode());
        if (gateway == null) {
            LOG.error("Cannot send payment email because gateway {} is not resolved for {}, could it be disabled?", order.getPgLabel(), order.getShop().getCode());
            return;
        }

        final String shopperTemplate = shopperTemplates.get(rez);
        final String adminTemplate = adminTemplates.get(rez);

        final PaymentGatewayFeature feature = gateway.getPaymentGatewayFeatures();

        // We only report online PG result to shoppers, as offline would be made by contacting shopper directly
        if (feature.isOnlineGateway() && StringUtils.isNotBlank(shopperTemplate)) {
            LOG.debug("Using shopper template {} for event key {}", shopperTemplate, rez);
            sendPaymentNotification(pjp, order, gateway, rez, false, shopperTemplate, order.getEmail());
        } else {
            LOG.debug("Shopper template is not available for event key {}", rez);
        }

        // We notify admin with all PG results for audit purposes
        if (StringUtils.isNotBlank(adminTemplate)) {
            final String adminEmail = pgShop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);
            if (StringUtils.isNotBlank(adminEmail)) {
                LOG.debug("Using admin template {} for event key {}", adminTemplate, rez);
                sendPaymentNotification(pjp, order, gateway, rez, true, adminTemplate, adminEmail);
            } else {
                LOG.warn("Shop admin e-mail is not setup for: {}", order.getShop().getCode());
            }
        } else {
            LOG.debug("Admin template is not available for event key {}", rez);
        }

    }

    /**
     * Prepare mail object and send it for each recipient
     *
     * @param pjp             join point
     * @param customerOrder   customer order
     * @param paymentGateway  gateway
     * @param rez             payment result
     * @param listPayments    true if need to send list of all payments
     * @param template        mail template to use
     * @param mailTo          recipients
     */
    protected void sendPaymentNotification(final ProceedingJoinPoint pjp,
                                           final CustomerOrder customerOrder,
                                           final PaymentGateway paymentGateway,
                                           final String rez,
                                           final boolean listPayments,
                                           final String template,
                                           final String ... mailTo) {

        final Shop orderShop = customerOrder.getShop();
        final Shop shop = orderShop.getMaster() != null ? orderShop.getMaster() : orderShop;

        List<CustomerOrderPayment> payments = null;

        if (listPayments) {
            payments = customerOrderPaymentService.findPayments(customerOrder.getOrdernum(), null, (String) null, (String) null);
        }

        for (final String recipient : mailTo) {

            if (StringUtils.isNotBlank(recipient)) {

                final HashMap<String, Object> map = new HashMap<>();

                MailUtils.appendMethodParamaters(map, pjp.getArgs());

                MailUtils.appendPaymentEmailParameters(
                        map,
                        themeService.getMailTemplateChainByShopId(shop.getShopId()),
                        template,
                        recipient, customerOrder,
                        paymentGateway,
                        rez,
                        payments);

                sendNotification(map);
            }

        }

    }

    public void setAuthoriseShopperTemplates(final LinkedHashMapBean<String, String> authoriseShopperTemplates) {
        this.authoriseShopperTemplates = authoriseShopperTemplates;
    }

    public void setCancelShopperTemplates(final LinkedHashMapBean<String, String> cancelShopperTemplates) {
        this.cancelShopperTemplates = cancelShopperTemplates;
    }

    public void setShipmentShopperTemplates(final LinkedHashMapBean<String, String> shipmentShopperTemplates) {
        this.shipmentShopperTemplates = shipmentShopperTemplates;
    }

    public void setAuthoriseAdminTemplates(final LinkedHashMapBean<String, String> authoriseAdminTemplates) {
        this.authoriseAdminTemplates = authoriseAdminTemplates;
    }

    public void setCancelAdminTemplates(final LinkedHashMapBean<String, String> cancelAdminTemplates) {
        this.cancelAdminTemplates = cancelAdminTemplates;
    }

    public void setShipmentAdminTemplates(final LinkedHashMapBean<String, String> shipmentAdminTemplates) {
        this.shipmentAdminTemplates = shipmentAdminTemplates;
    }
}
