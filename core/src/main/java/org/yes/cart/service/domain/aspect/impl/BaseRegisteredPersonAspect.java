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
import org.yes.cart.util.ShopCodeContext;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BaseRegisteredPersonAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final HashHelper hashHelper;

    private final PassPhrazeGenerator phrazeGenerator;


    /**
     * Construct base for aspect.
     *
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




    /*protected String getTemplateFolder(final Shop shop, final String templateName) {
        StorefrontApplication
        return null;
    }   */

}
