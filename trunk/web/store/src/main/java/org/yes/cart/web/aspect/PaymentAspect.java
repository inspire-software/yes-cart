package org.yes.cart.web.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.yes.cart.service.domain.aspect.impl.BaseNotificationAspect;
import org.yes.cart.util.ShopCodeContext;

/**
 * Aspect responsible to send notifications about
 * User: iga Igor Azarny
 * Date: 7 Apr 2012
 * Time: 4:41 PM
 *
 */
public class PaymentAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    /**
     * Construct aspect.
     * @param jmsTemplate what jms use to send notification.
     */
    public PaymentAspect(final JmsTemplate jmsTemplate) {
        super(jmsTemplate);
    }
}
