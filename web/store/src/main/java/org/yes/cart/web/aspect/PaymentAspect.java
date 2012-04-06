package org.yes.cart.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.aspect.impl.BaseNotificationAspect;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.HashMap;
import java.util.Map;

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
     * @param pjp    {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.authorize(..))")
    public Object doAuthorize(final ProceedingJoinPoint pjp) throws Throwable {
        final String rez =  (String) pjp.proceed();
        
        sendNotification(

                new HashMap<String, Object>() {{

                    final CustomerOrder customerOrder = (CustomerOrder) pjp.getArgs()[0];
                    put(StandardMessageListener.SHOP_CODE, ShopCodeContext.getShopCode());
                    put(StandardMessageListener.CUSTOMER_EMAIL, customerOrder.getCustomer().getEmail());
                    put(StandardMessageListener.RESULT, rez);
                    put(StandardMessageListener.DEFAULT, customerOrder);
                    put(StandardMessageListener.TEMPLATE_FOLDER, ApplicationDirector.getCurrentMailTemplateFolder());

                    put(StandardMessageListener.SHOP, ApplicationDirector.getCurrentShop());
                    put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());

                    put(StandardMessageListener.TEMPLATE_NAME, "payment");


                }}

        );

        return rez;
    }
}
