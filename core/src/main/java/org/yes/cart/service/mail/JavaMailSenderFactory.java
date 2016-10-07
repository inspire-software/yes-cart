package org.yes.cart.service.mail;

import org.springframework.mail.javamail.JavaMailSender;

/**
 * User: denispavlov
 * Date: 05/09/2015
 * Time: 11:26
 */
public interface JavaMailSenderFactory {

    /**
     * Get mail sender by shop code.
     *
     * @param shopCode shop code (use DEFAULT for Admin mail)
     *
     * @return mail sender object
     */
    JavaMailSender getJavaMailSender(String shopCode);

}
