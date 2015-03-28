/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.web.support.util;

import org.slf4j.Logger;
import org.yes.cart.util.ShopCodeContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 15:12
 */
public class CommandUtils {

    /**
     * Temporally fields will be removed from parameter maps
     */
    private static Set<String> cmdKeys = new HashSet<String>();
    private static Set<String> cmdInternalKeys = new HashSet<String>();

    public void setCmdKeys(final List<String> cmdKeys) {
        CommandUtils.cmdKeys.addAll(cmdKeys);

        final Logger log = ShopCodeContext.getLog(this);
        for (final String cmd : cmdKeys) {
            log.info("Configured key to be command: {}", cmd);
        }

    }

    public void setCmdInternalKeys(final List<String> cmdKeys) {
        CommandUtils.cmdInternalKeys.addAll(cmdKeys);

        final Logger log = ShopCodeContext.getLog(this);
        for (final String cmd : cmdKeys) {
            log.info("Configured key to be internal command: {}", cmd);
        }

    }


    /**
     * Check if given key is a command key.
     *
     * @param key key
     *
     * @return true if this key represents a command
     */
    public static boolean isCommandKey(final String key) {
        return cmdKeys.contains(key);
    }

    /**
     * Check if given key is an internal command key.
     *
     * @param key key
     *
     * @return true if this key represents an internal command
     */
    public static boolean isInternalCommandKey(final String key) {
        return cmdInternalKeys.contains(key);
    }

}
