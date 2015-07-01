/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.mail.impl;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.PassPhrazeGenerator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * User: igor
 * Date: 31.12.14 10:41
 * Test aspect to prove, that real aspect will be applied too.
 */
@Aspect
public class TestCustomerRegistrationAspect {

    private final HashHelper passwordHashHelper;

    private final PassPhrazeGenerator phrazeGenerator;


    public TestCustomerRegistrationAspect(final HashHelper passwordHashHelper, final PassPhrazeGenerator phrazeGenerator) {
        this.passwordHashHelper = passwordHashHelper;
        this.phrazeGenerator = phrazeGenerator;
    }



    /**
     * Handle reset password operation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.CustomerServiceImpl.create(..))")
    public Object doSetPassword(final ProceedingJoinPoint pjp) throws Throwable {
        setPassword((Customer) pjp.getArgs()[0]);
        return pjp.proceed();
    }

    /**
     * Handle reset password operation.
     *
     * @param pjp {@link org.aspectj.lang.ProceedingJoinPoint}
     * @return Object
     * @throws Throwable in case of target method errors
     */
    @Around("execution(* org.yes.cart.service.domain.impl.CustomerServiceImpl.resetPassword(..))")
    public Object doResetPassword(final ProceedingJoinPoint pjp) throws Throwable {
        setPassword((Customer) pjp.getArgs()[0]);
        return pjp.proceed();
    }

    private void setPassword(final Customer customer) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        final String generatedPassword  = StringUtils.isNotBlank(customer.getPassword()) ? customer.getPassword() : phrazeGenerator.getNextPassPhrase();

        final String passwordHash = passwordHashHelper.getHash(generatedPassword);

        customer.setPassword(passwordHash);
    }
}
