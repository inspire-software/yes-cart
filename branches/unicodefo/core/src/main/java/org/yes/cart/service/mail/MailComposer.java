package org.yes.cart.service.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

/**
 * Mail composer responsible to compose mails.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54

 */
public interface MailComposer {




    String MAIL_TEMPLATE_MANAGER_CHANGE_PASSWORD = "managerChangePassword";
    String MAIL_TEMPLATE_MANAGER_REGISTERED = "managerRegistered";

    /**
     * Compose mail message. subject will be gathered from mail template property file
     *
     * @param message      mime message to fill
     * @param shopCode     optional shop code
     * @param pathToTemplateFolder path to template folder
     * @param templateName template name
     * @param from         from address
     * @param toEmail           mail desctinatiol address
     * @param ccEmail           optional cc
     * @param bccEmail          optional bcc
     * @param model        model
     * @throws javax.mail.MessagingException in case if mail message can not be converted
     * @throws java.io.IOException    in case of inline resources can not be found
     * @throws ClassNotFoundException  in case if something wrong with template engine
     */
    void composeMessage(
            MimeMessage message,
            String shopCode,
            String pathToTemplateFolder,
            String templateName,
            String from,
            String toEmail,
            String ccEmail,
            String bccEmail,
            Map<String, Object> model)
                throws MessagingException, IOException, ClassNotFoundException;
}
