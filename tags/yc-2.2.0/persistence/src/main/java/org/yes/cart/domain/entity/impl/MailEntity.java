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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.MailPart;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 09/11/2013
 * Time: 19:13
 */
public class MailEntity implements Mail, Serializable {


    private long mailId;
    private long version;

    private String shopCode;
    private String subject;
    private String from;
    private String recipients;
    private String cc;
    private String bcc;
    private String textVersion;
    private String htmlVersion;
    private Set<MailPart> parts = new HashSet<MailPart>();

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public long getId() {
        return mailId;
    }

    public long getMailId() {
        return mailId;
    }

    public void setMailId(final long mailId) {
        this.mailId = mailId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(final String recipients) {
        this.recipients = recipients;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(final String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(final String bcc) {
        this.bcc = bcc;
    }

    public String getTextVersion() {
        return textVersion;
    }

    public void setTextVersion(final String textVersion) {
        this.textVersion = textVersion;
    }

    public String getHtmlVersion() {
        return htmlVersion;
    }

    public void setHtmlVersion(final String htmlVersion) {
        this.htmlVersion = htmlVersion;
    }

    public Set<MailPart> getParts() {
        return parts;
    }

    public void setParts(final Set<MailPart> parts) {
        this.parts = parts;
    }

    public MailPart addPart() {
        final MailPartEntity part = new MailPartEntity();
        part.setMail(this);
        parts.add(part);
        return part;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }
}
