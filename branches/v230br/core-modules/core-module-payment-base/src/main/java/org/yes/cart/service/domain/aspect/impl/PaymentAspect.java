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

package org.yes.cart.service.domain.aspect.impl;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.util.ShopCodeContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspect responsible to send notifications about
 * User: Igor Azarny
 * Date: 7 Apr 2012
 * Time: 4:41 PM
 */
@Aspect
public class PaymentAspect extends BaseNotificationAspect {

    private final ProductService productService;

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final ShopService shopService;

    private final ThemeService themeService;

    private final PaymentModulesManager paymentModulesManager;

    private Map<String, String> authoriseShopperTemplates = new HashMap<String, String>();
    private Map<String, String> cancelShopperTemplates = new HashMap<String, String>();
    private Map<String, String> shipmentShopperTemplates = new HashMap<String, String>();
    private Map<String, String> authoriseAdminTemplates = new HashMap<String, String>();
    private Map<String, String> cancelAdminTemplates = new HashMap<String, String>();
    private Map<String, String> shipmentAdminTemplates = new HashMap<String, String>();


    /**
     * Construct aspect.
     *
     */
    public PaymentAspect(final TaskExecutor taskExecutor,
                         final ProductService productService,
                         final MailService mailService,
                         final MailComposer mailComposer,
                         final CustomerService customerService,
                         final ShopService shopService,
                         final ThemeService themeService,
                         final PaymentModulesManager paymentModulesManager) {
        super(taskExecutor);
        this.productService = productService;
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.shopService = shopService;
        this.customerService = customerService;
        this.themeService = themeService;
        this.paymentModulesManager = paymentModulesManager;


    }

    public Runnable getTask(final Serializable serializableMessage) {
        return new StandardMessageListener(
                mailService,
                mailComposer,
                customerService,
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

        final String shopperTemplate = shopperTemplates.get(rez);
        final String adminTemplate = adminTemplates.get(rez);

        final HashMap<String, Object> map = new HashMap<String, Object>();

        fillParameters(pjp, map);

        fillPaymentParameters(pjp, rez, map);

        final CustomerOrder order = (CustomerOrder) pjp.getArgs()[0];
        final PaymentGatewayFeature feature = paymentModulesManager.getPaymentGateway(order.getPgLabel(), order.getShop().getCode()).getPaymentGatewayFeatures();

        // We only report online PG result to shoppers, as offline would be made by contacting shopper directly
        if (feature.isOnlineGateway() && StringUtils.isNotBlank(shopperTemplate)) {
            final HashMap<String, Object> userMap = new HashMap<String, Object>(map);
            userMap.put(StandardMessageListener.TEMPLATE_NAME, shopperTemplate);
            sendNotification(userMap);
        }

        // We notify admin with all PG results for audit purposes
        if (StringUtils.isNotBlank(adminTemplate)) {
            final AttrValueShop attrVal = order.getShop().getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);
            if (attrVal != null && StringUtils.isNotBlank(attrVal.getVal())) {
                final HashMap<String, Object> adminMap = new HashMap<String, Object>(map);
                adminMap.put(StandardMessageListener.TEMPLATE_NAME, adminTemplate);
                adminMap.put(StandardMessageListener.CUSTOMER_EMAIL, attrVal.getVal());
                sendNotification(adminMap);
            } else {
                ShopCodeContext.getLog(this).warn("Shop admin e-mail is not setup for: " + order.getShop().getCode());
            }
        }

    }


    /**
     * Fill extra parameters associated with payment.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @param rez payment result {@link org.yes.cart.payment.dto.Payment}
     * @param map context map
     */
    protected void fillPaymentParameters(final ProceedingJoinPoint pjp, final String rez, final HashMap<String, Object> map) {

        final CustomerOrder customerOrder = (CustomerOrder) pjp.getArgs()[0];
        final Shop shop = shopService.getById(customerOrder.getShop().getShopId());
        map.put(StandardMessageListener.SHOP_CODE, shop.getCode());
        map.put(StandardMessageListener.CUSTOMER_EMAIL, customerOrder.getCustomer().getEmail());
        map.put(StandardMessageListener.RESULT, rez);
        map.put(StandardMessageListener.ROOT, customerOrder);
        map.put(StandardMessageListener.TEMPLATE_FOLDER, themeService.getMailTemplateChainByShopId(shop.getShopId()));

        map.put(StandardMessageListener.SHOP, shop);
        map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());
        map.put(StandardMessageListener.LOCALE, customerOrder.getLocale());

        final PaymentGatewayFeature feature = paymentModulesManager.getPaymentGateway(customerOrder.getPgLabel(), shop.getCode()).getPaymentGatewayFeatures();
        map.put(StandardMessageListener.PAYMENT_GATEWAY_FEATURE, feature);
    }


    public void setAuthoriseShopperTemplates(final Map<String, String> authoriseShopperTemplates) {
        this.authoriseShopperTemplates = authoriseShopperTemplates;
    }

    public void setCancelShopperTemplates(final Map<String, String> cancelShopperTemplates) {
        this.cancelShopperTemplates = cancelShopperTemplates;
    }

    public void setShipmentShopperTemplates(final Map<String, String> shipmentShopperTemplates) {
        this.shipmentShopperTemplates = shipmentShopperTemplates;
    }

    public void setAuthoriseAdminTemplates(final Map<String, String> authoriseAdminTemplates) {
        this.authoriseAdminTemplates = authoriseAdminTemplates;
    }

    public void setCancelAdminTemplates(final Map<String, String> cancelAdminTemplates) {
        this.cancelAdminTemplates = cancelAdminTemplates;
    }

    public void setShipmentAdminTemplates(final Map<String, String> shipmentAdminTemplates) {
        this.shipmentAdminTemplates = shipmentAdminTemplates;
    }
}
