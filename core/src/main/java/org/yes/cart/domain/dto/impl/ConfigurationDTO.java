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

package org.yes.cart.domain.dto.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/03/2018
 * Time: 20:15
 */
public class ConfigurationDTO implements Serializable {

    private String functionalArea;
    private String name;
    private String cfgInterface;
    private boolean cfgDefault;
    private Map<String, String> properties = new HashMap<>();
    private List<String> targets = new ArrayList<>();
    private String nodeId;
    private String nodeUri;

    public String getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(final String functionalArea) {
        this.functionalArea = functionalArea;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCfgInterface() {
        return cfgInterface;
    }

    public void setCfgInterface(final String cfgInterface) {
        this.cfgInterface = cfgInterface;
    }

    public boolean isCfgDefault() {
        return cfgDefault;
    }

    public void setCfgDefault(final boolean cfgDefault) {
        this.cfgDefault = cfgDefault;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(final List<String> targets) {
        this.targets = targets;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeUri() {
        return nodeUri;
    }

    public void setNodeUri(final String nodeUri) {
        this.nodeUri = nodeUri;
    }

    public ConfigurationDTO() {
    }

    public ConfigurationDTO(final String functionalArea,
                            final String name,
                            final String cfgInterface,
                            final boolean cfgDefault,
                            final Map<String, String> properties,
                            final List<String> targets) {
        this.functionalArea = functionalArea;
        this.name = name;
        this.cfgInterface = cfgInterface;
        this.cfgDefault = cfgDefault;
        this.properties = properties;
        this.targets = targets;
    }
}
