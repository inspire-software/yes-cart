package org.yes.cart.service.domain.aspect.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.PassPhrazeGenerator;
import org.yes.cart.service.mail.MailComposer;
import org.yes.cart.util.ShopCodeContext;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Aspect
public class ManagerRegistrationAspect  extends BaseNotificationAspect {


    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());


    private final HashHelper hashHelper;

    private final PassPhrazeGenerator phrazeGenerator;

    private final JavaMailSender javaMailSender;

    private final MailComposer mailComposer;



    /**
     * Construct base for aspect.
     *
     * @param phrazeGenerator {@link PassPhrazeGenerator}
     * @param hashHelper      {@link HashHelper}
     * @param taskExecutor     {@link JmsTemplate} to execute asyn task.
     */
    public ManagerRegistrationAspect( final TaskExecutor taskExecutor,
                                      final PassPhrazeGenerator phrazeGenerator,
                                      final HashHelper hashHelper,
                                      final JavaMailSender javaMailSender,
                                      final MailComposer mailComposer
                                      ) {
        super(taskExecutor);
        this.hashHelper = hashHelper;
        this.phrazeGenerator = phrazeGenerator;
        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;

    }

    /**
     * Construct manager aspect.
     *
     * @param phrazeGenerator {@link org.yes.cart.service.domain.PassPhrazeGenerator}
     * @param hashHelper      {@link org.yes.cart.service.domain.HashHelper}
     */
    public ManagerRegistrationAspect(
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper,
            final JavaMailSender javaMailSender,
            final MailComposer mailComposer) {
        super(null);
        this.hashHelper = hashHelper;
        this.phrazeGenerator = phrazeGenerator;
        this.javaMailSender = javaMailSender;
        this.mailComposer = mailComposer;

    }

    /*
     * Handle manager creation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    /*@Around("execution(* org.yes.cart.service.domain.impl.ManagerServiceImpl.create(..))")
    public Object doCreateManager(final ProceedingJoinPoint pjp) throws Throwable {
        return notifyInternal(pjp, true);
    }
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

    @Override
    public Runnable getTask(Serializable serializableMessage) {
        return null;  //Todo change body of implemented methods use File | Settings | File Templates.
    }
}
