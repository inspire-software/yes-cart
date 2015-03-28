/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.consumer.ManagerRegistrationMessageListener;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.service.domain.PassPhrazeGenerator;
import org.yes.cart.service.mail.MailComposer;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Aspect
public class ManagerRegistrationAspect extends BaseNotificationAspect {


    private static final Logger LOG = LoggerFactory.getLogger(ManagerRegistrationAspect.class);


    private final HashHelper hashHelper;

    private final PassPhrazeGenerator phrazeGenerator;

    private final MailService mailService;

    private final MailComposer mailComposer;



    /**
     * Constructor for aspect with asynchronous notification.
     */
    public ManagerRegistrationAspect( final TaskExecutor taskExecutor,
                                      final PassPhrazeGenerator phrazeGenerator,
                                      final HashHelper hashHelper,
                                      final MailService mailService,
                                      final MailComposer mailComposer
                                      ) {
        super(taskExecutor);
        this.hashHelper = hashHelper;
        this.phrazeGenerator = phrazeGenerator;
        this.mailService = mailService;
        this.mailComposer = mailComposer;

    }

    /**
     *  Constructor for aspect with synchronous notification.
     */
    public ManagerRegistrationAspect(
            final PassPhrazeGenerator phrazeGenerator,
            final HashHelper hashHelper,
            final MailService mailService,
            final MailComposer mailComposer) {
        super(null);
        this.hashHelper = hashHelper;
        this.phrazeGenerator = phrazeGenerator;
        this.mailService = mailService;
        this.mailComposer = mailComposer;

    }

    /*
     * Handle manager creation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.ManagerServiceImpl.create(..))")
    public Object doCreateManager(final ProceedingJoinPoint pjp) throws Throwable {
        final Manager manager = (Manager) pjp.getArgs()[0];

        setNewPassword(manager);

        return pjp.proceed();
    }

    private void setNewPassword(Manager manager) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final String generatedPassword  = phrazeGenerator.getNextPassPhrase();
        final String passwordHash = hashHelper.getHash(generatedPassword);
        manager.setPassword(passwordHash);

        final RegistrationMessage registrationMessage = new RegistrationMessageImpl();
        registrationMessage.setEmail(manager.getEmail());
        registrationMessage.setFirstname(manager.getFirstname());
        registrationMessage.setLastname(manager.getLastname());
        registrationMessage.setPassword(generatedPassword);
        registrationMessage.setMailTemplatePathChain(Arrays.asList("/admin/mail/", "/default/mail/"));

        registrationMessage.setTemplateName("adm-passwd");

        sendNotification(registrationMessage);
    }

    /** Handle reset password operation.
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
   @Around("execution(* org.yes.cart.service.domain.impl.ManagerServiceImpl.resetPassword(..))")
    public Object doResetPassword(final ProceedingJoinPoint pjp) throws Throwable {

       final Manager manager = (Manager) pjp.getArgs()[0];

       setNewPassword(manager);

       return pjp.proceed();
    }

    @Override
    public Runnable getTask(Serializable serializableMessage) {
        return new ManagerRegistrationMessageListener(
                mailService,
                mailComposer,
                serializableMessage
        );
    }

}
