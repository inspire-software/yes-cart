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
 * Date: 26/03/2018
 * Time: 18:12
 */
public interface ActiveConfiguration {

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
     * Get target object this configuration is set for (if applicable)
     *
     * @return targets
     */
    String getTarget();

}
