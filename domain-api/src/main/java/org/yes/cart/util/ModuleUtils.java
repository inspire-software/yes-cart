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

package org.yes.cart.util;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: denispavlov
 * Date: 20/03/2018
 * Time: 19:26
 */
public class ModuleUtils {
    
    private static final Set<String> MODULES = new TreeSet<>();

    private ModuleUtils() {
        // no instance
    }

    /**
     * Retrieve registered modules.
     *
     * @return retrieve modules
     */
    public static Set<String> getRegisteredModules() {
        return Collections.unmodifiableSet(MODULES);
    }

    /**
     * Register module in Spring context.
     *
     * @param module module
     */
    public static void registerModule(final String module) {
        MODULES.add(module);
    }

}
