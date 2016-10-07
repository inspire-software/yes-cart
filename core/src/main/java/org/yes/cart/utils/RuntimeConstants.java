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

package org.yes.cart.utils;

/**
 * Constant that can be pushed through during build time or in Spring context.
 *
 * User: denispavlov
 * Date: 02/12/2015
 * Time: 21:37
 */
public interface RuntimeConstants {

    String WEBAPP_SF_CONTEXT_PATH = "webapp.yes.context.path";
    String WEBAPP_SF_WAR_NAME = "webapp.yes.war.name";
    String WEBAPP_API_CONTEXT_PATH = "webapp.api.context.path";
    String WEBAPP_API_WAR_NAME = "webapp.api.war.name";
    String WEBAPP_ADMIN_CONTEXT_PATH = "webapp.admin.context.path";
    String WEBAPP_ADMIN_WAR_NAME = "webapp.admin.war.name";

    /**
     * Check if value exists for key.
     *
     * @param key key
     *
     * @return true if key exists and value is non blank string
     */
    boolean hasValue(String key);

    /**
     * Get constant by key.
     *
     * @param key key
     *
     * @return constant
     */
    String getConstant(String key);

}
