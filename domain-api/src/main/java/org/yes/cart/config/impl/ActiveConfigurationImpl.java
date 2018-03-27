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

package org.yes.cart.config.impl;

import org.yes.cart.config.ActiveConfiguration;

/**
 * User: denispavlov
 * Date: 26/03/2018
 * Time: 17:12
 */
public class ActiveConfigurationImpl implements ActiveConfiguration {

    private final String name;
    private final String cfgInterface;
    private final String target;


    public ActiveConfigurationImpl(final String name,
                                   final String cfgInterface,
                                   final String target) {
        this.name = name;
        this.cfgInterface = cfgInterface;
        this.target = target;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCfgInterface() {
        return cfgInterface;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "ActiveConfigurationImpl{" +
                "name='" + name + '\'' +
                ", cfgInterface='" + cfgInterface + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
