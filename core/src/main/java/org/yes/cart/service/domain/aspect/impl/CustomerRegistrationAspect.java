package org.yes.cart.service.domain.aspect.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jms.core.JmsTemplate;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.PassPhrazeGenerator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 * 
 */
@Aspect
public class CustomerRegistrationAspect extends BaseRegisteredPersonAspect {

    /**
     * Construct customer aspect.
     *
     * @param phrazeGenerator {@link PassPhrazeGenerator}
     * @param hashHelper      {@link org.yes.cart.service.domain.HashHelper}
     * @param jmsTemplate     {@link JmsTemplate} to send message over JMS, if it null message will not send.
     */
    public CustomerRegistrationAspect(
            final JmsTemplate jmsTemplate,
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper) {
        super(jmsTemplate, hashHelper, phrazeGenerator);

    }

    /**
     * Construct customer aspect.
     *
     * @param phrazeGenerator {@link PassPhrazeGenerator}
     * @param hashHelper      {@link HashHelper}
     */
    public CustomerRegistrationAspect(
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper) {
        super(null, hashHelper, phrazeGenerator);

    }

    /**
     * Handle customer creation.
     *
     * @param pjp {@link ProceedingJoinPoint}
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
