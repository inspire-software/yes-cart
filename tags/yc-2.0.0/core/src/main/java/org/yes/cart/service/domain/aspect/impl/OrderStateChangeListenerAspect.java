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
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderItemAllocationException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspect to listed the order state changes and notify shopper.
 * <p/>
 * User: iazarny@yahoo.com
 * Date: 10/7/12
 * Time: 12:21 AM
 */
@Aspect
public class OrderStateChangeListenerAspect  extends BaseOrderStateAspect implements ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStateChangeListenerAspect.class);

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final CustomerOrderService customerOrderService;

    private final ShopService shopService;

    private final Map<String, String> shopperTemplates;

    private final Map<String, String> adminTemplates;

    private final ProductSkuService productSkuService;




    public OrderStateChangeListenerAspect(final TaskExecutor taskExecutor,
                                          final MailService mailService,
                                          final MailComposer mailComposer,
                                          final CustomerService customerService,
                                          final CustomerOrderService customerOrderService,
                                          final ShopService shopService,
                                          final ProductSkuService productSkuService,
                                          final Map<String, String> shopperTemplates,
                                          final Map<String, String> adminTemplates) {
        super(taskExecutor);
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.customerService = customerService;
        this.customerOrderService = customerOrderService;
        this.shopService = shopService;
        this.shopperTemplates = shopperTemplates;
        this.adminTemplates = adminTemplates;

        this.productSkuService = productSkuService;
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

    /**
     * Perform shopper notification, about payment authorize.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.order.impl.OrderStateManagerImpl.fireTransition(..))")
    public Object performNotifications(final ProceedingJoinPoint pjp) throws Throwable {

        final Object[] args = pjp.getArgs();

        final OrderEvent orderEvent = (OrderEvent) args[0];

        final AttrValueShop attrVal = orderEvent.getCustomerOrder().getShop().getAttributeByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);

        try {
            Object rez = pjp.proceed();
            if ((Boolean) rez) {

                final String templateKey = getTemplateKey(orderEvent);

                fillNotificationParameters(orderEvent, shopperTemplates.get(templateKey), orderEvent.getCustomerOrder().getCustomer().getEmail());

                if (attrVal == null) {
                    LOG.error("Cant get admin email address for shop " + orderEvent.getCustomerOrder().getShop().getCode() );
                }   else {
                    fillNotificationParameters(orderEvent, adminTemplates.get(templateKey), attrVal.getVal());
                }

            }
            return rez;
        } catch (final OrderItemAllocationException th) {

            LOG.error("Cant allocation quantity for product " + th.getProductSkuCode() );

            if (attrVal == null) {
                LOG.error("Cant get admin email address for shop " + orderEvent.getCustomerOrder().getShop().getCode() );
            }   else {

                final ProductSku sku = productSkuService.getProductSkuBySkuCode(th.getProductSkuCode());

                fillNotificationParameters(
                        orderEvent,
                        "adm-cant-allocate-product-qty",
                        new HashMap<String, Object>() {{ put("sku", sku); }},
                        attrVal.getVal());
            }

            throw th;
        } catch (Throwable th) {
            throw th;

        }


    }



    /**
     * Get ematil template key by given order event.
     *
     * @param orderEvent given order event
     * @return mail template key.
     */
    private String getTemplateKey(final OrderEvent orderEvent) {

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(orderEvent.getEventId());
        stringBuilder.append('-');
        stringBuilder.append(orderEvent.getCustomerOrder().getOrderStatus());

        if (orderEvent.getCustomerOrderDelivery() != null) {
            stringBuilder.append('-');
            stringBuilder.append(orderEvent.getCustomerOrderDelivery().getDeliveryStatus());
        }

        return stringBuilder.toString();
    }



}
