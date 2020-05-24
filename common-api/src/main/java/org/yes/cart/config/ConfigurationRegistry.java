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

package org.yes.cart.config;

/**
 * User: denispavlov
 * Date: 11/07/2017
 * Time: 12:47
 */
public interface ConfigurationRegistry<K, C> {

    /**
     * Check if this configuration is supported.
     *
     * @param cfgProperty configuration property
     * @param configuration configuration object
     *
     * @return true is supports this configuration
     */
    boolean supports(String cfgProperty, Object configuration);

    /**
     * Register shop specific configuration with given registry.
     *
     * @param key shop specific key
     * @param configuration configuration
     */
    void register(K key, C configuration);


}
