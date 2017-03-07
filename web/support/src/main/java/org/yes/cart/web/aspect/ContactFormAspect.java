/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.constants.AttributeNamesKeys;
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
 * User: denispavlov
 * Date: 03/11/2015
 * Time: 09:09
 */
@Aspect
public class ContactFormAspect extends BaseNotificationAspect {

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
    public ContactFormAspect(final TaskExecutor taskExecutor,
                             final MailService mailService,
                             final MailComposer mailComposer,
                             final ThemeService themeService) {
        super(taskExecutor);
        this.mailService = mailService;
        this.mailComposer = mailComposer;
        this.themeService = themeService;
    }


    /**
     * Perform notification about contact us message.
     *
     * @param pjp join point
     *
     * @return inherited return
     *
     * @throws Throwable in case it was in underlying method
     */
    protected Object notifyInternal(final ProceedingJoinPoint pjp) throws Throwable {
        final Object[] args = pjp.getArgs();

        final Shop shop = (Shop) args[0];
        final String email = (String) args[1];
        final Map<String, Object> registrationData = (Map<String, Object>) args[2];
        registrationData.put("email", email);


        final RegistrationMessage registrationMessage = new RegistrationMessageImpl();
        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        if (cart != null) {
            registrationMessage.setLocale(cart.getCurrentLocale());
        }

        registrationMessage.setMailTemplatePathChain(themeService.getMailTemplateChainByShopId(shop.getShopId()));

        registrationMessage.setTemplateName("adm-contactform-request");

        final String emailTo = determineFromEmail(shop); // contact request to admin
        registrationMessage.setEmail(emailTo);
        registrationMessage.setShopMailFrom(emailTo);

        registrationMessage.setShopId(shop.getShopId());
        registrationMessage.setShopCode(shop.getCode());
        registrationMessage.setShopName(shop.getName());
        registrationMessage.setShopUrl(transformShopUrls(shop));

        registrationMessage.setAdditionalData(registrationData);

        sendNotification(registrationMessage);

        LOG.info("ContactUs message was send to queue {}", registrationMessage);

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
    public Runnable getTask(Serializable serializableMessage) {
        return new CustomerRegistrationMessageListener(
                mailService,
                mailComposer,
                (RegistrationMessage) serializableMessage
        );
    }

    private Set<String> transformShopUrls(final Shop shop) {
        final Set<String> rez = new HashSet<String>();
        rez.add(shop.getDefaultShopUrl());
        return rez;
    }


    /**
     * Handle reset password operation.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.web.support.service.impl.CustomerServiceFacadeImpl.registerEmailRequest(..))")
    public Object doSignupNewsletter(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp);
    }

}
