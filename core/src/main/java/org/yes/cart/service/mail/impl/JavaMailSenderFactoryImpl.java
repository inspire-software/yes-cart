package org.yes.cart.service.mail.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.service.mail.JavaMailSenderBuilder;
import org.yes.cart.service.mail.JavaMailSenderFactory;

/**
 * User: denispavlov
 * Date: 05/09/2015
 * Time: 11:28
 */
public class JavaMailSenderFactoryImpl implements JavaMailSenderFactory {

    private final JavaMailSender defaultJavaMailSender;
    private final JavaMailSenderBuilder javaMailSenderBuilder;

    public JavaMailSenderFactoryImpl(final JavaMailSender defaultJavaMailSender,
                                     final JavaMailSenderBuilder javaMailSenderBuilder) {
        this.defaultJavaMailSender = defaultJavaMailSender;
        this.javaMailSenderBuilder = javaMailSenderBuilder;
    }

    /**
     * {@inheritDoc}
     */
    public JavaMailSender getJavaMailSender(final String shopCode) {
        JavaMailSender sender = null;
        if (StringUtils.isNotEmpty(shopCode) && !"DEFAULT".equals(shopCode)) {
            sender = javaMailSenderBuilder.buildJavaMailSender(shopCode);
        }
        if (sender == null) {
            sender = defaultJavaMailSender;
        }
        return sender;
    }
}
