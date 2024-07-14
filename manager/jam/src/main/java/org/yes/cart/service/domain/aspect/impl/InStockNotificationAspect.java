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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.utils.DomainApiUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: inspiresoftware
 * Date: 10/03/2024
 * Time: 14:42
 */
@Aspect
public class InStockNotificationAspect extends BaseNotificationAspect {


    public static final String TEMPLATE = "in-stock-product";
    private final Logger LOG = LoggerFactory.getLogger(InStockNotificationAspect.class);


    private final MailService mailService;

    private final MailComposer mailComposer;

    private final ThemeService themeService;



    /**
     * Constructor for aspect with asynchronous notification.
     */
    public InStockNotificationAspect(final TaskExecutor taskExecutor,
                                     final MailService mailService,
                                     final MailComposer mailComposer,
                                     final ThemeService themeService) {
        super(taskExecutor);
        this.mailService = mailService;
        this.mailComposer = mailComposer;

        this.themeService = themeService;
    }

    /**
     *  Constructor for aspect with synchronous notification.
     */
    public InStockNotificationAspect(final MailService mailService,
                                     final MailComposer mailComposer,
                                     final ThemeService themeService) {
        super(null);
        this.mailService = mailService;
        this.mailComposer = mailComposer;

        this.themeService = themeService;
    }

    /*
     * Handle in-stock mail creation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.bulkjob.customer.BulkInStockNotificationsProcessorInternal.createNotificationEmail(..))")
    public Object doCreateManager(final ProceedingJoinPoint pjp) throws Throwable {
        final Shop shop = (Shop) pjp.getArgs()[0];
        final Customer customer = (Customer) pjp.getArgs()[1];
        final List<Pair<CustomerWishList, ProductSku>> notifications = (List<Pair<CustomerWishList, ProductSku>>) pjp.getArgs()[2];

        setInStock(shop, customer, notifications);

        return pjp.proceed();
    }

    private void setInStock(final Shop shop,
                            final Customer customer,
                            final List<Pair<CustomerWishList, ProductSku>> notifications) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        final Shop main = shop.getMaster() != null ? shop.getMaster() : shop;

        final HashMap<String, Object> mailModel = new HashMap<>();

        mailModel.put(StandardMessageListener.TEMPLATE_FOLDER, themeService.getMailTemplateChainByShopId(shop.getShopId()));
        mailModel.put(StandardMessageListener.TEMPLATE_NAME, TEMPLATE);
        mailModel.put(StandardMessageListener.CUSTOMER_EMAIL, customer.getEmail());

        mailModel.put(StandardMessageListener.SHOP_CODE, main.getCode());
        mailModel.put(StandardMessageListener.SHOP, main);
        mailModel.put(StandardMessageListener.CUSTOMER_SHOP_CODE, shop.getCode());
        mailModel.put(StandardMessageListener.CUSTOMER_SHOP, shop);

        mailModel.put(StandardMessageListener.ROOT, notifications);
        mailModel.put(StandardMessageListener.CUSTOMER, customer);
        mailModel.put(StandardMessageListener.LOCALE, shop.getSupportedLanguagesAsList().get(0));

        sendNotification(mailModel);

        LOG.info("InStock message was send to queue {}", mailModel);
    }


    @Override
    public Runnable getTask(Serializable serializableMessage) {
        return new StandardMessageListener(
                mailService,
                mailComposer,
                null,
                null,
                null,
                (HashMap<String, Object>) serializableMessage
        );
    }

}
