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


import org.yes.cart.domain.entity.Mailtemplategroup;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class MailtemplateEntity implements org.yes.cart.domain.entity.Mailtemplate, java.io.Serializable {

    private long mailtemplateId;
    private long version;

    private String code;
    private String fspointer;
    private String name;
    private String description;
    private Mailtemplategroup mailTemplateGroup;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public MailtemplateEntity() {
    }


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getFspointer() {
        return this.fspointer;
    }

    @Override
    public void setFspointer(final String fspointer) {
        this.fspointer = fspointer;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public Mailtemplategroup getMailTemplateGroup() {
        return this.mailTemplateGroup;
    }

    @Override
    public void setMailTemplateGroup(final Mailtemplategroup mailTemplateGroup) {
        this.mailTemplateGroup = mailTemplateGroup;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getMailtemplateId() {
        return this.mailtemplateId;
    }

    @Override
    public long getId() {
        return this.mailtemplateId;
    }


    @Override
    public void setMailtemplateId(final long mailtemplateId) {
        this.mailtemplateId = mailtemplateId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


