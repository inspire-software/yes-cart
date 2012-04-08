package org.yes.cart.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.message.consumer.StandardMessageListener;
import org.yes.cart.service.domain.aspect.impl.BaseNotificationAspect;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.HashMap;

/**
 * Aspect responsible to send notifications about
 * User: iga Igor Azarny
 * Date: 7 Apr 2012
 * Time: 4:41 PM
 */
@Aspect
public class PaymentAspect extends BaseNotificationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    /**
     * Construct aspect.
     *
     * @param jmsTemplate what jms use to send notification.
     */
    public PaymentAspect(final JmsTemplate jmsTemplate) {
        super(jmsTemplate);
    }


    /**
     * Perform shopper notification, about payment authorize.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.authorize(..))")
    public Object doAuthorize(final ProceedingJoinPoint pjp) throws Throwable {
        final String rez = (String) pjp.proceed();

        final HashMap<String, Object> map = new HashMap<String, Object>();

        fillParameters(pjp, map);

        final CustomerOrder customerOrder = (CustomerOrder) pjp.getArgs()[0];
        map.put(StandardMessageListener.SHOP_CODE, ShopCodeContext.getShopCode());
        map.put(StandardMessageListener.CUSTOMER_EMAIL, customerOrder.getCustomer().getEmail());
        map.put(StandardMessageListener.RESULT, rez);
        map.put(StandardMessageListener.ROOT, customerOrder);
        map.put(StandardMessageListener.TEMPLATE_FOLDER, ApplicationDirector.getCurrentMailTemplateFolder());

        map.put(StandardMessageListener.SHOP, ApplicationDirector.getCurrentShop());
        map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());

        map.put(StandardMessageListener.TEMPLATE_NAME, "payment");


        sendNotification(map);

        return rez;
    }


    /**
     * Perform shopper notification, about payment authorize.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return result of original operation.
     * @throws Throwable re throws exception
     */
    @Around("execution(* org.yes.cart.service.payment.impl.PaymentProcessorImpl.shipmentComplete(..))")
    public Object doShipmentComplete(final ProceedingJoinPoint pjp) throws Throwable {

        final String rez = (String) pjp.proceed();

        final HashMap<String, Object> map = new HashMap<String, Object>();

        fillParameters(pjp, map);

        final CustomerOrder customerOrder = (CustomerOrder) pjp.getArgs()[0];
        map.put(StandardMessageListener.SHOP_CODE, ShopCodeContext.getShopCode());
        map.put(StandardMessageListener.CUSTOMER_EMAIL, customerOrder.getCustomer().getEmail());
        map.put(StandardMessageListener.RESULT, rez);
        map.put(StandardMessageListener.ROOT, customerOrder);
        map.put(StandardMessageListener.TEMPLATE_FOLDER, ApplicationDirector.getCurrentMailTemplateFolder());

        map.put(StandardMessageListener.SHOP, ApplicationDirector.getCurrentShop());
        map.put(StandardMessageListener.CUSTOMER, customerOrder.getCustomer());

        map.put(StandardMessageListener.TEMPLATE_NAME, "shipmentComplete");


        sendNotification(map);

        return rez;

    }

    /**
     * Fill all passed parameters into message map.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @param map contxt map
     */
    private void fillParameters(final ProceedingJoinPoint pjp, final HashMap<String, Object> map) {
        if (pjp.getArgs() != null) {
            for (int i = 0; i < pjp.getArgs().length; i++) {
                map.put(StandardMessageListener.PARAM_PREFIX + i, pjp.getArgs()[i]);
            }
        }
    }
}
