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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: denispavlov
 */
@Dto
public class VoClusterNode {

    @DtoField(readOnly = true)
    private boolean current;
    @DtoField(readOnly = true)
    private boolean admin;

    @DtoField(readOnly = true)
    private String id;
    @DtoField(readOnly = true)
    private String nodeId;
    @DtoField(readOnly = true)
    private String nodeType;
    @DtoField(readOnly = true)
    private String nodeConfig;
    @DtoField(readOnly = true)
    private String clusterId;
    @DtoField(readOnly = true)
    private boolean ftIndexDisabled;

    @DtoField(readOnly = true)
    private String channel;

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(final boolean current) {
        this.current = current;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(final String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeConfig() {
        return nodeConfig;
    }

    public void setNodeConfig(final String nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(final String clusterId) {
        this.clusterId = clusterId;
    }

    public boolean isFtIndexDisabled() {
        return ftIndexDisabled;
    }

    public void setFtIndexDisabled(final boolean ftIndexDisabled) {
        this.ftIndexDisabled = ftIndexDisabled;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }
}
