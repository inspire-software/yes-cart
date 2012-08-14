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

package org.yes.cart.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.aspect.impl.BaseNotificationAspect;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Aspect responsible to send notifications about
 * User: iga Igor Azarny
 * Date: 7 Apr 2012
 * Time: 4:41 PM
 */
@Aspect
public class PaymentAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final ProductService productService;

    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final ShopService shopService;


    /**
     * Construct aspect.
     *
     */
    public PaymentAspect(final TaskExecutor taskExecutor,
                         final ProductService productService,

                         final JavaMailSender javaMailSender,
                         final MailComposer mailComposer,
                         final CustomerService customerService,
                         final ShopService shopService) {
        super(taskExecutor);
        this.productService = productService;


        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;
        this.shopService = shopService;
        this.customerService = customerService;


    }

    public Runnable getTask(final Serializable serializableMessage) {
        return new StandardMessageListener(
                javaMailSender,
                mailComposer,
                customerService,
                shopService,
                serializableMessage);

    }



    /**
     * Perform shopper notification, about payment authorize.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.authorize(..))")
    public Object doAuthorize(final ProceedingJoinPoint pjp) throws Throwable {
        final String rez = (String) pjp.proceed();

        final HashMap<String, Object> map = new HashMap<String, Object>();

        fillParameters(pjp, map);

        final CustomerOrder customerOrder = (CustomerOrder) pjp.getArgs()[0];
        map.put(StandardMessageListener.SHOP_CODE, ShopCodeContext.getShopCode());
        map.put(StandardMessageListener.CUSTOMER_EMAIL, customerOrder.getCustomer().getEmail());
        map.put(StandardMessageListener.RESULT, rez);
        map.put(StandardMessageListener.ROOT, customerOrder);
        map.put(StandardMessageListener.TEMPLATE_FOLDER, ApplicationDirector.getCurrentMailTemplateFolder());

        map.put(StandardMessageListener.SHOP, ApplicationDirector.getCurrentShop());
        map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());

        map.put(StandardMessageListener.TEMPLATE_NAME, "payment");


        sendNotification(map);

        reindex(customerOrder);

        return rez;
    }


    /**
     * Perform shopper notification, about payment authorize.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.shipmentComplete(..))")
    public Object doShipmentComplete(final ProceedingJoinPoint pjp) throws Throwable {

        final String rez = (String) pjp.proceed();

        final HashMap<String, Object> map = new HashMap<String, Object>();

        fillParameters(pjp, map);

        final CustomerOrder customerOrder = (CustomerOrder) pjp.getArgs()[0];
        map.put(StandardMessageListener.SHOP_CODE, ShopCodeContext.getShopCode());
        map.put(StandardMessageListener.CUSTOMER_EMAIL, customerOrder.getCustomer().getEmail());
        map.put(StandardMessageListener.RESULT, rez);
        map.put(StandardMessageListener.ROOT, customerOrder);
        map.put(StandardMessageListener.TEMPLATE_FOLDER, ApplicationDirector.getCurrentMailTemplateFolder());

        map.put(StandardMessageListener.SHOP, ApplicationDirector.getCurrentShop());
        map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());

        map.put(StandardMessageListener.TEMPLATE_NAME, "shipmentComplete");


        sendNotification(map);

        reindex(customerOrder);

        return rez;

    }



    private void reindex(final CustomerOrder customerOrder) {
        for(CustomerOrderDet det : customerOrder.getOrderDetail()) {
            productService.reindexProduct(det.getSku().getProduct().getProductId()) ;
        }
    }

    /**
     * Fill all passed parameters into message map.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @param map contxt map
     */
    private void fillParameters(final ProceedingJoinPoint pjp, final HashMap<String, Object> map) {
        if (pjp.getArgs() != null) {
            for (int i = 0; i < pjp.getArgs().length; i++) {
                map.put(StandardMessageListener.PARAM_PREFIX + i, pjp.getArgs()[i]);
            }
        }
    }
}
