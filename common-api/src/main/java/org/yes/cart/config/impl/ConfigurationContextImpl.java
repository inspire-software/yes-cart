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

package org.yes.cart.config.impl;

import org.yes.cart.config.ConfigurationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/03/2018
 * Time: 18:14
 */
public class ConfigurationContextImpl implements ConfigurationContext {

    private String functionalArea;
    private String name;
    private String cfgInterface;
    private boolean cfgDefault;
    private Map<String, String> properties = new HashMap<>();

    @Override
    public String getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(final String functionalArea) {
        this.functionalArea = functionalArea;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getCfgInterface() {
        return cfgInterface;
    }

    public void setCfgInterface(final String cfgInterface) {
        this.cfgInterface = cfgInterface;
    }

    @Override
    public boolean isCfgDefault() {
        return cfgDefault;
    }

    public void setCfgDefault(final boolean cfgDefault) {
        this.cfgDefault = cfgDefault;
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties.putAll(properties);
    }

    @Override
    public String toString() {
        return "ConfigurationContextImpl{" +
                "functionalArea='" + functionalArea + '\'' +
                ", name='" + name + '\'' +
                ", cfgInterface='" + cfgInterface + '\'' +
                ", cfgDefault=" + cfgDefault +
                ", properties=" + properties +
                '}';
    }
}
