package org.yes.cart.service.mail;

import org.springframework.mail.javamail.JavaMailSender;

/**
 * User: denispavlov
 * Date: 05/09/2015
 * Time: 11:30
 */
public interface JavaMailSenderBuilder {

    /**
     * Create custom shop specific mail sender.
     *
     * @param shopCode shop code.
     *
     * @return mail sender
     */
    JavaMailSender buildJavaMailSender(String shopCode);

}
