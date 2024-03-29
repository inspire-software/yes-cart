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
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderItemAllocationException;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.utils.log.Markers;
import org.yes.cart.utils.spring.LinkedHashMapBean;

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
public class OrderStateChangeListenerAspect  extends BaseOrderStateAspect {

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
                                          final ThemeService themeService,
                                          final ProductSkuService productSkuService,
                                          final LinkedHashMapBean<String, String> shopperTemplates,
                                          final LinkedHashMapBean<String, String> adminTemplates) {
        super(taskExecutor, themeService);
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

        final Shop orderShop = orderEvent.getCustomerOrder().getShop();
        final boolean mastered = orderShop.getMaster() != null;
        final Shop emailShop = mastered ? orderShop.getMaster() : orderShop;
        final String adminEmail = emailShop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);
        final String subAdminEmail = mastered ? orderShop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL) : null;

        try {
            Object rez = pjp.proceed();
            if ((Boolean) rez) {

                final String templateKey = getTemplateKey(orderEvent);

                final String shopperTemplate =  shopperTemplates.get(templateKey);

                if (StringUtils.isNotBlank(shopperTemplate)) {
                    LOG.debug("Using shopper template {} for event key {}", shopperTemplate, templateKey);
                    sendOrderNotification(pjp, orderEvent, shopperTemplate, orderEvent.getCustomerOrder().getEmail());
                } else {
                    LOG.debug("Shopper template is not available for event key {}", templateKey);
                }

                if (mastered) {
                    if (StringUtils.isBlank(subAdminEmail)) {
                        LOG.error(Markers.alert(), "No sub-admin email address for shop " + orderShop.getCode());
                    } else if (StringUtils.isNotBlank(shopperTemplate)) {
                        LOG.debug("Using sub shop admin template {} for event key {}", shopperTemplate, templateKey);
                        sendOrderNotification(pjp, orderEvent, shopperTemplate, subAdminEmail);
                    } else {
                        LOG.debug("Sub shop admin template is not available for event key {}", templateKey);
                    }
                }


                if (StringUtils.isBlank(adminEmail)) {
                    LOG.error(Markers.alert(), "No admin email address for shop " + emailShop.getCode());
                } else {

                    final String adminTemplate = adminTemplates.get(templateKey);

                    if (StringUtils.isNotBlank(adminTemplate)) {
                        LOG.debug("Using admin template {} for event key {}", adminTemplate, templateKey);
                        sendOrderNotification(pjp, orderEvent, adminTemplate, adminEmail);
                    } else {
                        LOG.debug("Admin template is not available for event key {}", templateKey);
                    }
                }

            }
            return rez;
        } catch (final OrderItemAllocationException th) {

            final String ref = orderEvent.getCustomerOrderDelivery() != null ? orderEvent.getCustomerOrderDelivery().getDeliveryNum(): orderEvent.getCustomerOrder().getOrdernum();

            LOG.warn("Insufficient inventory for product {}:{}", ref, th.getProductSkuCode());

            final String key = "OUT_OF_STOCK_NOTIFICATION:" + ref;

            final Pair<String, I18NModel> oosn = orderEvent.getCustomerOrder().getValue(key);

            if (oosn != null && StringUtils.isNotBlank(oosn.getFirst())) {

                LOG.debug("Insufficient inventory for product {}:{} ... email already sent {}", ref, th.getProductSkuCode(), oosn.getFirst());

            } else {

                if (StringUtils.isBlank(adminEmail)) {
                    LOG.error(Markers.alert(), "No admin email address for shop {}", emailShop.getCode());
                } else {

                    final ProductSku sku = productSkuService.getProductSkuBySkuCode(th.getProductSkuCode());

                    sendOrderNotification(
                            pjp,
                            orderEvent,
                            "adm-cant-allocate-product-qty",
                            new HashMap<String, Object>() {{
                                put("sku", sku);
                            }},
                            adminEmail);
                }

            }
            throw th;
        } catch (Throwable th) {
            throw th;

        }


    }



    /**
     * Get email template key by given order event.
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
