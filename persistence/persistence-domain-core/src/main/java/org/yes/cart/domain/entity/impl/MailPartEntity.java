/*
 * Copyright 2009 Inspire-Software.com
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

/**
 * User: denispavlov
 * Date: 09/11/2013
 * Time: 19:18
 */
public class MailPartEntity implements MailPart, Serializable {

    private long mailPartId;
    private long version;

    private Mail mail;

    private String resourceId;
    private String filename;
    private byte[] data;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public Mail getMail() {
        return mail;
    }

    public void setMail(final Mail mail) {
        this.mail = mail;
    }

    @Override
    public long getId() {
        return mailPartId;
    }

    @Override
    public long getMailPartId() {
        return mailPartId;
    }

    @Override
    public void setMailPartId(final long mailPartId) {
        this.mailPartId = mailPartId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }

    @Override
    public void setResourceId(final String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(final String filename) {
        this.filename = filename;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public void setData(final byte[] data) {
        this.data = data;
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
