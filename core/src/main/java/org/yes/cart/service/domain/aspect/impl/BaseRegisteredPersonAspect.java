package org.yes.cart.service.domain.aspect.impl;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.RegisteredPerson;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.PassPhrazeGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BaseRegisteredPersonAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(BaseRegisteredPersonAspect.class);

    private final HashHelper hashHelper;

    private final PassPhrazeGenerator phrazeGenerator;


    /**
     * Construct base for aspect.
     * @param phrazeGenerator {@link PassPhrazeGenerator}
     * @param hashHelper      {@link HashHelper}
     * @param jmsTemplate     {@link JmsTemplate} to send message over JMS, if it null message will not send.
     */
    public BaseRegisteredPersonAspect(final JmsTemplate jmsTemplate,
                                      final HashHelper hashHelper,
                                      final PassPhrazeGenerator phrazeGenerator) {
        super(jmsTemplate);
        this.hashHelper = hashHelper;
        this.phrazeGenerator = phrazeGenerator;
    }

    /**
     *
     * @param pjp
     * @param newPerson in case if new person was created.
     * @return inherited return
     * @throws Throwable
     */
    protected Object notifyInternal(final ProceedingJoinPoint pjp, final boolean newPerson) throws Throwable {
        final Object[] args = pjp.getArgs();
        if (args != null && args.length >= 1) {
            if ( args[0] instanceof RegisteredPerson) {

                final RegisteredPerson registeredPerson = (RegisteredPerson) args[0];
                final String generatedPassword = phrazeGenerator.getNextPassPhrase();
                final String passwordHash = hashHelper.getHash(generatedPassword);

                registeredPerson.setPassword(passwordHash);

                final RegistrationMessage registrationMessage = new RegistrationMessageImpl();
                registrationMessage.setEmail(registeredPerson.getEmail());
                registrationMessage.setFirstname(registeredPerson.getFirstname());
                registrationMessage.setLastname(registeredPerson.getLastname());
                registrationMessage.setPassword(generatedPassword);
                registrationMessage.setNewPerson(newPerson);

                if (args.length >= 2 && args[1] instanceof Shop) {
                    final Shop shop = (Shop)  args[1];
                    registrationMessage.setShopId(shop.getShopId());

                    final AttrValue attrValue = shop.getAttributeByCode(AttributeNamesKeys.SHOP_MAIL_FROM);
                    if (attrValue != null && StringUtils.isNotBlank(attrValue.getVal())) {
                        //if value not set it will be collected from mail properties
                        registrationMessage.setShopMailFrom(attrValue.getVal());                           
                    }

                    registrationMessage.setShopCode(shop.getCode());
                    registrationMessage.setShopName(shop.getName());
                    registrationMessage.setShopUrl(transformShopUrls(shop.getShopUrl()));
                }

                sendNotification(registrationMessage);
                LOG.info("Person message was send to queue " + registrationMessage.toString());

                return pjp.proceed();
            }
        }
        return pjp.proceed();
    }

    private Set<String> transformShopUrls(Set<ShopUrl> urls) {
        final Set<String> rez = new HashSet<String>();
        if (urls != null) {
            for (ShopUrl url : urls) {
                rez.add(url.getUrl());
            }
        }
        return rez;
    }

}
