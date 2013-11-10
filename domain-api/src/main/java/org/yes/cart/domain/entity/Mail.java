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

package org.yes.cart.domain.entity;

import java.util.Set;

/**
 * Persisted mail object to facilitate delayed bulk email facilities.
 *
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 19:00
 */
public interface Mail extends Auditable {

    /**
     * @return primary key
     */
    long getMailId();

    /**
     * @param mailId primary key
     */
    void setMailId(long mailId);

    /**
     * @return shop code for shop where this email was generated
     */
    String getShopCode();

    /**
     * @param shopCode shop code for shop where this email was generated
     */
    void setShopCode(String shopCode);

    /**
     * @return subject of this email
     */
    String getSubject();

    /**
     * @param subject subject of this email
     */
    void setSubject(String subject);

    /**
     * @return from email address
     */
    String getFrom();

    /**
     * @param from from email address
     */
    void setFrom(String from);

    /**
     * @return semi colon (;) separated list of recipients
     */
    String getRecipients();

    /**
     * @param recipients semi colon (;) separated list of recipients
     */
    void setRecipients(String recipients);

    /**
     * @return copy email address
     */
    String getCc();

    /**
     * @param cc copy email address
     */
    void setCc(String cc);

    /**
     * @return blind copy email address
     */
    String getBcc();

    /**
     * @param bcc blind copy email address
     */
    void setBcc(String bcc);

    /**
     * @return main email body
     */
    String getTextVersion();

    /**
     * @param text main email body
     */
    void setTextVersion(String text);

    /**
     * @return main email body
     */
    String getHtmlVersion();

    /**
     * @param html main email body
     */
    void setHtmlVersion(String html);

    /**
     * @return mail parts
     */
    Set<MailPart> getParts();

    /**
     * @return mail parts added to this mail
     */
    MailPart addPart();

}
