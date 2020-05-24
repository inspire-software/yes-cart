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

package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.*;

import java.util.*;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:29:32 PM
 *
 */
public class ShoppingCartCommandFactoryImpl implements ShoppingCartCommandFactory, ShoppingCartCommandRegistry {

    private static final long serialVersionUID = 20100122L;

    private static final Logger LOG = LoggerFactory.getLogger("CONFIG");

    private final ShoppingCartCommandConfigurationProvider configurationProvider;

    private ConfigurableShoppingCartCommand[] commands;
    private final Map<String, ConfigurableShoppingCartCommand> commandByKey = new HashMap<>();

    public ShoppingCartCommandFactoryImpl(final ShoppingCartCommandConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void registerCommand(final ConfigurableShoppingCartCommand command) {

        final ConfigurableShoppingCartCommand mapped = commandByKey.get(command.getCmdKey());
        if (mapped != command) {
            if (mapped != null) {
                LOG.warn("ShoppingCartCommandRegistry replacing command impl for: {} with {}", command.getCmdKey(), command);
            } else {
                LOG.debug("ShoppingCartCommandRegistry adding command impl for: {} with {}", command.getCmdKey(), command);
            }
            command.configure(this.configurationProvider);
            commandByKey.put(command.getCmdKey(), command);
            commands = remapCommandChain(commandByKey.values());
        }
    }

    ConfigurableShoppingCartCommand[] remapCommandChain(final Collection<ConfigurableShoppingCartCommand> commands) {
        final List<ConfigurableShoppingCartCommand> ordered = new ArrayList<>(commands);
        ordered.sort((cmd1, cmd2) -> cmd1.getPriority() - cmd2.getPriority());
        return ordered.toArray(new ConfigurableShoppingCartCommand[ordered.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        for (ShoppingCartCommand command : commands) {
            command.execute(shoppingCart, parameters);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final String key, final ShoppingCart shoppingCart, final Map<String, Object> parameters) throws IllegalArgumentException {
        if (commandByKey.containsKey(key)) {
            commandByKey.get(key).execute(shoppingCart, parameters);
        } else {
            throw new IllegalArgumentException("Command " + key + " is not mapped");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getCmdKey() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getPriority() {
        return 0;
    }
}
