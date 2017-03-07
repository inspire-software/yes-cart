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

package org.yes.cart.shoppingcart.support.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.support.CommandConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 15:12
 */
public class CommandConfigImpl implements CommandConfig {

    private static final Logger LOG = LoggerFactory.getLogger(CommandConfigImpl.class);

    /**
     * Temporally fields will be removed from parameter maps
     */
    private final Set<String> cmdKeys = new HashSet<String>();
    private final Set<String> cmdInternalKeys = new HashSet<String>();

    public void setCmdKeys(final List<String> cmdKeys) {
        this.cmdKeys.addAll(cmdKeys);

        for (final String cmd : cmdKeys) {
            LOG.info("Configured key to be command: {}", cmd);
        }

    }

    public void setCmdInternalKeys(final List<String> cmdKeys) {
        this.cmdInternalKeys.addAll(cmdKeys);

        for (final String cmd : cmdKeys) {
            LOG.info("Configured key to be internal command: {}", cmd);
        }

    }


    /**
     * Check if given key is a command key.
     *
     * @param key key
     *
     * @return true if this key represents a command
     */
    public boolean isCommandKey(final String key) {
        return cmdKeys.contains(key);
    }

    /**
     * Check if given key is an internal command key.
     *
     * @param key key
     *
     * @return true if this key represents an internal command
     */
    public boolean isInternalCommandKey(final String key) {
        return cmdInternalKeys.contains(key);
    }

}
