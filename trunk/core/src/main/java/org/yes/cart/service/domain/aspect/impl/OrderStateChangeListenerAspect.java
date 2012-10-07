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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderItemAllocationException;

import javax.servlet.ServletContext;
import java.io.File;
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
public class OrderStateChangeListenerAspect  extends BaseNotificationAspect implements ServletContextAware {


    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;

    private final CustomerService customerService;

    private final CustomerOrderService customerOrderService;

    private final ShopService shopService;

    private final Map<String, String> shopperTemplates;

    private final Map<String, String> adminTemplates;

    private ServletContext servletContext;


    /**
     * Construct base notification aspect class.
     *
     * @param taskExecutor to use
     */
    public OrderStateChangeListenerAspect(final TaskExecutor taskExecutor,
                                          final JavaMailSender javaMailSender,
                                          final MailComposer mailComposer,
                                          final CustomerService customerService,
                                          final CustomerOrderService customerOrderService,
                                          final ShopService shopService,
                                          final Map<String, String> shopperTemplates,
                                          final Map<String, String> adminTemplates) {
        super(taskExecutor);
        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;
        this.customerService = customerService;
        this.customerOrderService = customerOrderService;
        this.shopService = shopService;
        this.shopperTemplates = shopperTemplates;
        this.adminTemplates = adminTemplates;

    }

    /** {@inheritDoc} */
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
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.order.impl.OrderStateManagerImpl.fireTransition(..))")
    public Object performNotifications(final ProceedingJoinPoint pjp) throws Throwable {

        final Object[] args = pjp.getArgs();

        final OrderEvent orderEvent = (OrderEvent) args[0];

        System.out.println("\n >>> old " + orderEvent);
        try {
            Object rez = pjp.proceed();
            if ((Boolean) rez) {

                final String templateKey = getTemplateKey(orderEvent);

                System.out.println("\n >>> templateKey " + templateKey);

                fillNotificationParameters(orderEvent, shopperTemplates.get(templateKey), orderEvent.getCustomerOrder().getCustomer().getEmail());

                fillNotificationParameters(orderEvent, adminTemplates.get(templateKey), "admin@asdasdads.com");

            }
            return rez;
        } catch (OrderItemAllocationException th) {
            throw th;                                  //todo notifications
        } catch (Throwable th) {
            System.out.println("\n >>> can fire transition " + orderEvent + " ex " + th);
            throw th;

        }


    }

    /**
     * Create email and sent it.
     *
     * @param orderEvent       given order event
     * @param emailTempateName optional email tempate name
     * @param emailsAddresses  set of email addresses
     */
    private void fillNotificationParameters(final OrderEvent orderEvent, final String emailTempateName, final String... emailsAddresses) {

        if (StringUtils.isNotBlank(emailTempateName)) {

            final CustomerOrder customerOrder = orderEvent.getCustomerOrder();

            for (String emailAddr : emailsAddresses) {

                final HashMap<String, Object> map = new HashMap<String, Object>();

                map.put(StandardMessageListener.SHOP_CODE, customerOrder.getShop().getCode());
                map.put(StandardMessageListener.CUSTOMER_EMAIL, emailAddr);
                map.put(StandardMessageListener.RESULT, true);
                map.put(StandardMessageListener.ROOT, customerOrder);
                map.put(StandardMessageListener.TEMPLATE_FOLDER, servletContext.getRealPath(customerOrder.getShop().getMailFolder()) + File.separator);
                map.put(StandardMessageListener.SHOP, customerOrder.getShop());
                map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());
                map.put(StandardMessageListener.TEMPLATE_NAME, emailTempateName);

                if (orderEvent.getCustomerOrderDelivery() != null) {
                    final CustomerOrderDelivery delivery = orderEvent.getCustomerOrderDelivery();
                    map.put(StandardMessageListener.DELIVERY_CARRIER, delivery.getCarrierSla().getCarrier().getName());
                    map.put(StandardMessageListener.DELIVERY_CARRIER_SLA, delivery.getCarrierSla().getName());
                    map.put(StandardMessageListener.DELIVERY_NUM, delivery.getDeliveryNum());
                    map.put(StandardMessageListener.DELIVERY_EXTERNAL_NUM, delivery.getRefNo());
                }

                sendNotification(map);

            }



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




    /** {@inheritDoc} */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
