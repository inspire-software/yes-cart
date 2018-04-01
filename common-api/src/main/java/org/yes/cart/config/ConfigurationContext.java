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

package org.yes.cart.config;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/03/2018
 * Time: 18:08
 */
public interface ConfigurationContext {

    /**
     * Get functional area of configuration.
     *
     * @return functional area
     */
    String getFunctionalArea();

    /**
     * Get configuration name.
     *
     * @return name
     */
    String getName();

    /**
     * Get configuration interface.
     *
     * @return interface
     */
    String getCfgInterface();

    /**
     * Default configuration.
     *
     * @return default configuration
     */
    boolean isCfgDefault();

    /**
     * Get properties.
     *
     * @return properties
     */
    Map<String, String> getProperties();

}
