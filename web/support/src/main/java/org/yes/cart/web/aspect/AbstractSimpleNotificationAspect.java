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

package org.yes.cart.web.aspect;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.RegisteredPerson;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.consumer.CustomerRegistrationMessageListener;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.aspect.impl.BaseNotificationAspect;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple notification aspect uses {@link RegistrationMessage} as the message model.
 *
 * Supports method signatures of the following form:
 * xxxxEmailRequest(final Shop registrationShop, final String email, final Map<String, Object> registrationData)
 *
 * Implementations should contain the @Aspect annotation and declaration of the methods with @Around.
 *
 * E.g.
 *
 * @Around("execution(* xxxx.XxxxImpl.xxxEmailRequest(..))")
 * public Object doXxxxxx(final ProceedingJoinPoint pjp) throws Throwable {
 *     return notifyInternal(pjp, "email-template");
 * }
 *
 * User: denispavlov
 * Date: 29/10/2019
 * Time: 12:16
 */
public abstract class AbstractSimpleNotificationAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ContactFormAspect.class);

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final ThemeService themeService;

    /**
     * Construct customer aspect.
     *
     * @param taskExecutor    {@link TaskExecutor} to execute task.
     * @param mailService     persists mail object to be picked up by bulk email job
     * @param mailComposer    mail composer generates message to be sent
     * @param themeService    theme service
     */
    public AbstractSimpleNotificationAspect(final TaskExecutor taskExecutor,
                                            final MailService mailService,
                                            final MailComposer mailComposer,
                                            final ThemeService themeService) {
        super(taskExecutor);
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.themeService = themeService;
    }


    /**
     * Perform notification message.
     *
     * Supports pjp of the following form:
     * xxxxEmailRequest(final Shop registrationShop, final String email, final Map<String, Object> registrationData)
     *
     * @param pjp join point
     *
     * @return inherited return
     *
     * @throws Throwable in case it was in underlying method
     */
    protected Object notifyInternal(final ProceedingJoinPoint pjp, final String template) throws Throwable {
        final Object[] args = pjp.getArgs();

        final Shop shop = (Shop) args[0];
        final String email = (String) args[1];
        final Map<String, Object> registrationData = (Map<String, Object>) args[2];
        registrationData.put("email", email);


        final RegistrationMessage registrationMessage = new RegistrationMessageImpl();

        final Object registeredPersonObj = registrationData.get("registeredPerson");
        if (registeredPersonObj instanceof RegisteredPerson) {
            final RegisteredPerson registeredPerson = (RegisteredPerson) registeredPersonObj;
            registrationMessage.setSalutation(registeredPerson.getSalutation());
            registrationMessage.setFirstname(registeredPerson.getFirstname());
            registrationMessage.setLastname(registeredPerson.getLastname());
            registrationMessage.setMiddlename(registeredPerson.getMiddlename());
            registrationMessage.setCompanyName1(registeredPerson.getCompanyName1());
            registrationMessage.setCompanyName2(registeredPerson.getCompanyName2());
            registrationMessage.setCompanyDepartment(registeredPerson.getCompanyDepartment());

            if (registeredPerson instanceof Customer) {
                final Customer customer = (Customer) registeredPerson;
                registrationData.put("customerType", customer.getCustomerType());
                registrationData.put("pricingPolicy", customer.getPricingPolicy());
                registrationData.put("tag", customer.getTag());
            }

        }

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        if (cart != null) {
            registrationMessage.setLocale(cart.getCurrentLocale());
        }

        registrationMessage.setMailTemplatePathChain(themeService.getMailTemplateChainByShopId(shop.getShopId()));

        registrationMessage.setTemplateName(template);

        final String emailFrom = determineFromEmail(shop); // contact request to admin
        final Object emailToObj = registrationData.get("emailTo");
        if (emailToObj instanceof String) {
            registrationMessage.setEmail((String) emailToObj);
        } else {
            registrationMessage.setEmail(emailFrom);
        }
        registrationMessage.setShopMailFrom(emailFrom);

        registrationMessage.setShopId(shop.getShopId());
        registrationMessage.setShopCode(shop.getCode());
        registrationMessage.setShopName(shop.getName());
        registrationMessage.setShopUrl(transformShopUrls(shop));
        registrationMessage.setShopSecureUrl(transformShopSecureUrls(shop));

        registrationMessage.setAdditionalData(registrationData);

        sendNotification(registrationMessage);

        LOG.info("{} message was send to queue {}", template, registrationMessage);

        return pjp.proceed();
    }

    private String determineFromEmail(final Shop shop) {
        final String attrVal = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);
        if (StringUtils.isNotBlank(attrVal)) {
            return attrVal;
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Runnable getTask(Serializable serializableMessage) {
        return new CustomerRegistrationMessageListener(
                mailService,
                mailComposer,
                (RegistrationMessage) serializableMessage
        );
    }

    private Set<String> transformShopUrls(final Shop shop) {
        final Set<String> rez = new HashSet<>();
        rez.add(shop.getDefaultShopUrl());
        return rez;
    }

    private Set<String> transformShopSecureUrls(final Shop shop) {
        final Set<String> rez = new HashSet<>();
        rez.add(shop.getDefaultShopSecureUrl());
        return rez;
    }

}
