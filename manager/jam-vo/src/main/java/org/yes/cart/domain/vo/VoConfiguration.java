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
import org.yes.cart.domain.misc.MutablePair;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 21/03/2018
 * Time: 22:52
 */
@Dto
public class VoConfiguration {

    @DtoField(readOnly = true)
    private String functionalArea;
    @DtoField(readOnly = true)
    private String name;
    @DtoField(readOnly = true)
    private String cfgInterface;
    @DtoField(readOnly = true)
    private boolean cfgDefault;
    @DtoField(value = "properties", converter = "DisplayValues", readOnly = true)
    private List<MutablePair<String, String>> properties;
    @DtoField(readOnly = true)
    private List<String> targets = new ArrayList<>();
    @DtoField(readOnly = true)
    private String nodeId;
    @DtoField(readOnly = true)
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

    public List<MutablePair<String, String>> getProperties() {
        return properties;
    }

    public void setProperties(final List<MutablePair<String, String>> properties) {
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
}
