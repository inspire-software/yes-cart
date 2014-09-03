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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.theme.ThemeService;

import javax.servlet.ServletContext;
import java.io.Serializable;

/**
 *
 * This aspect send administrative notifications, in case if money refund will be
 * failed, during order cancel.
 *
 * User: iazarny@yahoo.com
 * Date: 10/20/12
 * Time: 10:20 AM
 */

@Aspect
public class CancelOrderWithRefundAspect extends BaseOrderStateAspect {

    private static final Logger LOG = LoggerFactory.getLogger(CancelOrderWithRefundAspect.class);

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final ShopService shopService;


    /**
     * Construct customer aspect.
     *
     * @param taskExecutor     {@link org.springframework.core.task.TaskExecutor} to execute task.
     */
    public CancelOrderWithRefundAspect(
            final TaskExecutor taskExecutor,
            final MailService mailService,
            final MailComposer mailComposer,
            final CustomerService customerService,
            final ShopService shopService,
            final ThemeService themeService) {
        super(taskExecutor, themeService);

        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.customerService = customerService;
        this.shopService = shopService;



    }

    /**
     * Perform shopper notification, about payment authorize.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.order.impl.handler.CancelOrderWithRefundOrderEventHandlerImpl.handle(..))")
    public Object doAuthorize(final ProceedingJoinPoint pjp) throws Throwable {

        Boolean rez = (Boolean) pjp.proceed();
        if (!rez) {
            final OrderEvent orderEvent = (OrderEvent) pjp.getArgs()[0];

            final AttrValueShop attrVal = orderEvent.getCustomerOrder().getShop().getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);

            if (attrVal == null) {
                LOG.error("Cant get admin email address for shop " + orderEvent.getCustomerOrder().getShop().getCode() );
            }   else {
                fillNotificationParameters(orderEvent, "adm-refund-failed", attrVal.getVal() );
            }

            LOG.error("Cant refund money with order cancelation " + orderEvent.getCustomerOrder().getOrdernum() );

        }
        return rez;


    }

    /** {@inheritDoc} */
    public Runnable getTask(final Serializable serializableMessage) {
        return new StandardMessageListener(
                mailService,
                mailComposer,
                customerService,
                shopService,
                serializableMessage);
    }



}
