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

package org.yes.cart.service.domain.aspect.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.RegisteredPerson;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.consumer.CustomerRegistrationMessageListener;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.PassPhrazeGenerator;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.service.theme.ThemeService;

import java.io.Serializable;
import java.util.*;

/**
 * Aspect responsible for send email in case of person registration.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 11:57 AM
 */
@Aspect
public class CustomerRegistrationAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerRegistrationAspect.class);

    private final HashHelper passwordHashHelper;

    private final PassPhrazeGenerator phrazeGenerator;

    private final MailService mailService;

    private final MailComposer mailComposer;

    private final ThemeService themeService;


    /**
     * Construct customer aspect.
     *
     * @param taskExecutor    {@link TaskExecutor} to execute task.
     * @param phrazeGenerator {@link PassPhrazeGenerator}
     * @param passwordHashHelper      {@link HashHelper}
     * @param mailService     persists mail object to be picked up by bulk email job
     * @param mailComposer    mail composer generates message to be sent
     * @param themeService    theme service
     */
    public CustomerRegistrationAspect(final TaskExecutor taskExecutor,
                                      final PassPhrazeGenerator phrazeGenerator,
                                      final HashHelper passwordHashHelper,
                                      final MailService mailService,
                                      final MailComposer mailComposer,
                                      final ThemeService themeService) {
        super(taskExecutor);

        this.passwordHashHelper = passwordHashHelper;
        this.phrazeGenerator = phrazeGenerator;
        this.mailService = mailService;
        this.mailComposer = mailComposer;


        this.themeService = themeService;
    }

    /**
     * Perform notification about person registration.
     *
     * @param pjp join point
     * @param generatePassword in case if new person was created.
     * @param resetPassword in case if this is password reset.
     * @param template template for email.
     * @return inherited return
     * @throws Throwable in case it was in underlying method
     */
    protected Object notifyInternal(final ProceedingJoinPoint pjp, final boolean generatePassword, final boolean resetPassword, final String template) throws Throwable {
        final Object[] args = pjp.getArgs();

        final RegisteredPerson registeredPerson = (RegisteredPerson) args[0];
        final Shop shopArg = (Shop) args[1];
        final Shop shop = shopArg.getMaster() != null ? shopArg.getMaster() : shopArg;
        final String token = resetPassword ? (String) args[2] : null;

        if (isRegisteredPersonGuest(registeredPerson)) {
            // Do not send registration notification to guests
            return pjp.proceed();
        }

        final String generatedPassword;
        final String generatedPasswordHash;
        final String generatedToken;
        final Date generatedTokenExpiry;
        if (generatePassword) {

            if (StringUtils.isNotBlank(registeredPerson.getPassword())) {
                // We need to use password from RegisteredPerson because we auto-login using it during registration
                generatedPassword = registeredPerson.getPassword();
            } else {
                // fallback as we must have a password (worst case customer will need to reset the password)
                generatedPassword = phrazeGenerator.getNextPassPhrase();
            }
            // regenerate hash for new password
            generatedPasswordHash = passwordHashHelper.getHash(generatedPassword);
            generatedToken = null;
            generatedTokenExpiry = null;

        } else if (resetPassword) {
            if (StringUtils.isNotBlank(token)) {
                // Token is present so need to actually reset
                if (!isCallcenterToken(shop, token)) {
                    if (!token.equals(registeredPerson.getAuthToken())
                            || registeredPerson.getAuthTokenExpiry() == null
                            || new Date().after(registeredPerson.getAuthTokenExpiry())) {
                        throw new BadCredentialsException(Constants.PASSWORD_RESET_AUTH_TOKEN_INVALID);
                    }
                }

                // regenerate password
                generatedPassword = phrazeGenerator.getNextPassPhrase();
                generatedPasswordHash = passwordHashHelper.getHash(generatedPassword);
                generatedToken = null;
                generatedTokenExpiry = null;

            } else {
                // Token is null so this is a new password reset request
                generatedPassword = null;
                generatedPasswordHash = registeredPerson.getPassword(); // same as before
                generatedToken = phrazeGenerator.getNextPassPhrase();
                generatedTokenExpiry = determineExpiryTime(shop);
            }
        } else {
            generatedPassword = null;
            generatedPasswordHash = registeredPerson.getPassword(); // same as before
            generatedToken = null;
            generatedTokenExpiry = null;
        }


        final RegistrationMessage registrationMessage = createRegistrationMessage(
                generatePassword,
                registeredPerson,
                shop,
                generatedPassword,
                generatedPasswordHash,
                generatedToken,
                generatedTokenExpiry,
                template
        );

        sendNotification(registrationMessage);

        LOG.info("Person message was send to queue {}", registrationMessage);

        return pjp.proceed();
    }

    private boolean isRegisteredPersonGuest(final RegisteredPerson registeredPerson) {
        return registeredPerson instanceof Customer && ((Customer) registeredPerson).isGuest();
    }

    private boolean isRegisteredPersonRequireApproval(final RegisteredPerson registeredPerson, final Shop shop) {
        if (registeredPerson instanceof Customer) {
            final Customer customer = (Customer) registeredPerson;
            if (CollectionUtils.isNotEmpty(customer.getShops())) {
                for (final CustomerShop accessShop : customer.getShops()) {
                    if (accessShop.getShop().getShopId() == shop.getShopId()) {
                        return accessShop.isDisabled();
                    }
                }
            } else if (StringUtils.isNotBlank(customer.getCustomerType())) {
                return shop.isSfRequireCustomerRegistrationApproval(customer.getCustomerType());
            }
        }
        return false;
    }

    private boolean isRegisteredPersonRequireNotification(final RegisteredPerson registeredPerson, final Shop shop) {
        if (registeredPerson instanceof Customer) {
            final Customer customer = (Customer) registeredPerson;
            if (StringUtils.isNotBlank(customer.getCustomerType())) {
                return shop.isSfRequireCustomerRegistrationNotification(customer.getCustomerType());
            }
        }
        return false;
    }

    private RegistrationMessage createRegistrationMessage(final boolean newPerson,
                                                          final RegisteredPerson registeredPerson,
                                                          final Shop shop,
                                                          final String generatedPassword,
                                                          final String generatedPasswordHash,
                                                          final String generatedToken,
                                                          final Date generatedTokenExpiry,
                                                          final String template) throws Throwable {


        registeredPerson.setPassword(generatedPasswordHash);
        registeredPerson.setAuthToken(generatedToken);
        registeredPerson.setAuthTokenExpiry(generatedTokenExpiry);

        final RegistrationMessage registrationMessage = new RegistrationMessageImpl();
        registrationMessage.setEmail(registeredPerson.getEmail());
        registrationMessage.setSalutation(registeredPerson.getSalutation());
        registrationMessage.setFirstname(registeredPerson.getFirstname());
        registrationMessage.setLastname(registeredPerson.getLastname());
        registrationMessage.setMiddlename(registeredPerson.getMiddlename());
        registrationMessage.setPassword(generatedPassword);
        registrationMessage.setAuthToken(generatedToken);
        if (CollectionUtils.isNotEmpty(shop.getSupportedLanguagesAsList())) {
            registrationMessage.setLocale(shop.getSupportedLanguagesAsList().get(0));
        }

        registrationMessage.setMailTemplatePathChain(themeService.getMailTemplateChainByShopId(shop.getShopId()));

        registrationMessage.setTemplateName(template);

        registrationMessage.setShopMailFrom(determineFromEmail(shop));

        registrationMessage.setShopId(shop.getShopId());
        registrationMessage.setShopCode(shop.getCode());
        registrationMessage.setShopName(shop.getName());
        registrationMessage.setShopUrl(transformShopUrls(shop));

        if (registeredPerson instanceof Customer) {
            final Customer customer = (Customer) registeredPerson;
            final Map<String, Object> registrationData = new HashMap<String, Object>();
            registrationData.put("customerType", customer.getCustomerType());
            registrationData.put("pricingPolicy", customer.getPricingPolicy());
            registrationData.put("tag", customer.getTag());
            registrationData.put("newPerson", newPerson);
            final boolean requireApproval = newPerson && isRegisteredPersonRequireApproval(registeredPerson, shop);
            registrationData.put("requireApproval", requireApproval);
            final boolean requireNotification = requireApproval || (newPerson && isRegisteredPersonRequireNotification(registeredPerson, shop));
            registrationData.put("requireNotification", requireNotification);
            registrationData.put("requireNotificationEmails", getAllRecipients(shop, determineFromEmail(shop), template));
            registrationMessage.setAdditionalData(registrationData);
        }
        return registrationMessage;
    }

    private String determineFromEmail(final Shop shop) {
        final String attrVal = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL);
        if (StringUtils.isNotBlank(attrVal)) {
            return attrVal;
        }
        return null;
    }

    private Date determineExpiryTime(final Shop shop) {

        int secondsTimeout = Constants.DEFAULT_CUSTOMER_TOKEN_EXPIRY_SECONDS;
        final String attrVal = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TOKEN_EXPIRY_SECONDS);
        if (attrVal != null) {
            secondsTimeout = NumberUtils.toInt(attrVal, secondsTimeout);
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, secondsTimeout);
        return calendar.getTime();

    }

    private boolean isCallcenterToken(final Shop shop, final String token) {

        final String attrVal = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_PASSWORD_RESET_CC);
        return StringUtils.isNotBlank(attrVal) && attrVal.equals(token);

    }

    String[] getAllRecipients(final Shop shop, final String adminEmail, final String templateKey) {

        final List<String> recipients = new ArrayList<String>();

        if (StringUtils.isNotBlank(adminEmail) && recipients.isEmpty()) {
            // this is default shop admin email
            recipients.add(adminEmail);
        }

        if (recipients.isEmpty()) {
            return null;
        }
        return recipients.toArray(new String[recipients.size()]);

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
     * Handle customer creation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.CustomerServiceImpl.create(..))")
    public Object doCreateCustomer(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp, true, false, "customer-registered");
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
        return notifyInternal(pjp, false, true, "customer-change-password");
    }


    /**
     * Handle shop activation operation.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.CustomerServiceImpl.updateActivate(..))")
    public Object doActivate(final ProceedingJoinPoint pjp) throws Throwable {
        final Boolean disabled = (Boolean) pjp.getArgs()[2];
        if (disabled) {
            return pjp.proceed(); // Soft activation, no need to send info
        }
        return notifyInternal(pjp, false, false, "customer-activation");
    }


    /**
     * Handle shop de-activation operation.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.CustomerServiceImpl.updateDeactivate(..))")
    public Object doDeactivate(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp, false, false, "customer-deactivation");
    }

}
