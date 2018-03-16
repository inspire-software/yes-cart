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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.MailPart;

import java.io.Serializable;
import java.time.Instant;
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
    private Set<MailPart> parts = new HashSet<>();

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    @Override
    public long getId() {
        return mailId;
    }

    @Override
    public long getMailId() {
        return mailId;
    }

    @Override
    public void setMailId(final long mailId) {
        this.mailId = mailId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getShopCode() {
        return shopCode;
    }

    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(final String from) {
        this.from = from;
    }

    @Override
    public String getRecipients() {
        return recipients;
    }

    @Override
    public void setRecipients(final String recipients) {
        this.recipients = recipients;
    }

    @Override
    public String getCc() {
        return cc;
    }

    @Override
    public void setCc(final String cc) {
        this.cc = cc;
    }

    @Override
    public String getBcc() {
        return bcc;
    }

    @Override
    public void setBcc(final String bcc) {
        this.bcc = bcc;
    }

    @Override
    public String getTextVersion() {
        return textVersion;
    }

    @Override
    public void setTextVersion(final String textVersion) {
        this.textVersion = textVersion;
    }

    @Override
    public String getHtmlVersion() {
        return htmlVersion;
    }

    @Override
    public void setHtmlVersion(final String htmlVersion) {
        this.htmlVersion = htmlVersion;
    }

    @Override
    public Set<MailPart> getParts() {
        return parts;
    }

    public void setParts(final Set<MailPart> parts) {
        this.parts = parts;
    }

    @Override
    public MailPart addPart() {
        final MailPartEntity part = new MailPartEntity();
        part.setMail(this);
        parts.add(part);
        return part;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }
}
