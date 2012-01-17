package org.yes.cart.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.yes.cart.domain.entity.RegisteredPerson;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.PassPhrazeGenerator;
import org.yes.cart.service.domain.aspect.impl.BaseNotificationAspect;
import org.yes.cart.web.application.ApplicationDirector;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Aspect responsible for send email in case of person registration.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 11:57 AM
 */
@Aspect
public class RegistrationAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationAspect.class);

    private final HashHelper hashHelper;

    private final PassPhrazeGenerator phrazeGenerator;


    /**
     * Construct customer aspect.
     *
     * @param phrazeGenerator {@link org.yes.cart.service.domain.PassPhrazeGenerator}
     * @param hashHelper      {@link org.yes.cart.service.domain.HashHelper}
     * @param jmsTemplate     {@link JmsTemplate} to send message over JMS, if it null message will not send.
     */
    public RegistrationAspect(
            final JmsTemplate jmsTemplate,
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper) {
        super(jmsTemplate);

        this.hashHelper = hashHelper;
        this.phrazeGenerator = phrazeGenerator;

    }

    /**
     * Construct customer aspect.
     *
     * @param phrazeGenerator {@link org.yes.cart.service.domain.PassPhrazeGenerator}
     * @param hashHelper      {@link org.yes.cart.service.domain.HashHelper}
     */
    public RegistrationAspect(
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper) {
        super(null);

        this.hashHelper = hashHelper;
        this.phrazeGenerator = phrazeGenerator;

    }


    /**
     * Perform notification about person registration.
     *
     * @param pjp
     * @param newPerson in case if new person was created.
     * @return inherited return
     * @throws Throwable in case it was in underlaing method
     */
    protected Object notifyInternal(final ProceedingJoinPoint pjp, final boolean newPerson) throws Throwable {
        final Object[] args = pjp.getArgs();

        final RegisteredPerson registeredPerson = (RegisteredPerson) args[0];
        final Shop shop = (Shop) args[1];


        final String generatedPassword;
        if (newPerson && registeredPerson.getPassword() != null) {
            generatedPassword = registeredPerson.getPassword();
        } else {
            generatedPassword = phrazeGenerator.getNextPassPhrase();
        }
        final String passwordHash = hashHelper.getHash(generatedPassword);
        registeredPerson.setPassword(passwordHash);

        final RegistrationMessage registrationMessage = new RegistrationMessageImpl();
        registrationMessage.setEmail(registeredPerson.getEmail());
        registrationMessage.setFirstname(registeredPerson.getFirstname());
        registrationMessage.setLastname(registeredPerson.getLastname());
        registrationMessage.setPassword(generatedPassword);


        registrationMessage.setPathToTemplateFolder(
                ApplicationDirector.getCurrentServletContext().getRealPath(shop.getMailFolder()) + File.separator);
        registrationMessage.setTemplateName(newPerson ? "customerRegistered" : "customerChangePassword");

        registrationMessage.setShopMailFrom(null); // will be used from properties at email template

        registrationMessage.setShopId(shop.getShopId());
        registrationMessage.setShopCode(shop.getCode());
        registrationMessage.setShopName(shop.getName());
        registrationMessage.setShopUrl(transformShopUrls(shop.getShopUrl()));

        sendNotification(registrationMessage);

        LOG.info("Person message was send to queue " + registrationMessage.toString());

        return pjp.proceed();
    }


    private Set<String> transformShopUrls(final Set<ShopUrl> urls) {
        final Set<String> rez = new HashSet<String>();
        if (urls != null) {
            for (ShopUrl url : urls) {
                rez.add(url.getUrl());
            }
        }
        return rez;
    }


    /**
     * Handle customer creation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.CustomerServiceImpl.create(..))")
    public Object doCreateCustomer(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp, true);
    }

    /**
     * Handle reset password operation.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.CustomerServiceImpl.resetPassword(..))")
    public Object doResetPassword(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp, false);
    }


}
