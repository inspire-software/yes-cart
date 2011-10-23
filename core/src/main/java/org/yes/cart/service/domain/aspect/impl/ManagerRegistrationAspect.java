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
 */
@Aspect
public class ManagerRegistrationAspect  extends BaseRegisteredPersonAspect {

    /**
     * Construct manager aspect.
     *
     * @param phrazeGenerator {@link org.yes.cart.service.domain.PassPhrazeGenerator}
     * @param hashHelper      {@link org.yes.cart.service.domain.HashHelper}
     * @param jmsTemplate     {@link org.springframework.jms.core.JmsTemplate} to send message over JMS, if it null message will not send.
     */
    public ManagerRegistrationAspect(
            final JmsTemplate jmsTemplate,
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper) {
        super(jmsTemplate, hashHelper, phrazeGenerator);

    }

    /**
     * Construct manager aspect.
     *
     * @param phrazeGenerator {@link org.yes.cart.service.domain.PassPhrazeGenerator}
     * @param hashHelper      {@link org.yes.cart.service.domain.HashHelper}
     */
    public ManagerRegistrationAspect(
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper) {
        super(null, hashHelper, phrazeGenerator);

    }

    /**
     * Handle manager creation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    /*@Around("execution(* org.yes.cart.service.domain.impl.ManagerServiceImpl.create(..))")
    public Object doCreateManager(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp, true);
    }     */

    /**
     * Handle reset password operation.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
   /* @Around("execution(* org.yes.cart.service.domain.impl.ManagerServiceImpl.resetPassword(..))")
    public Object doResetPassword(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp, false);
    }  */
}
