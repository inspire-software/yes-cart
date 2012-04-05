package org.yes.cart.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
@Aspect
public class PaymentAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    /**
     * Construct aspect.
     * @param jmsTemplate what jms use to send notification.
     */
    public PaymentAspect(final JmsTemplate jmsTemplate) {
        super(jmsTemplate);
    }
    
    
    

    /**
     * Perform shopper notification, about payment authorize.
     * @param pjp
     * @return result of original operation.
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.authorize(..))")
    public Object doAuthorize(final ProceedingJoinPoint pjp) throws Throwable {
        final Object rez =  pjp.proceed();
        sendNotification((String)rez);
        return rez;
    }
}
