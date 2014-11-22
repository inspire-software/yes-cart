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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;
import org.yes.cart.util.ShopCodeContext;

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

    private ShoppingCartCommand[] commands;
    private final Map<String, ShoppingCartCommand> commandByKey = new HashMap<String, ShoppingCartCommand>();

    /** {@inheritDoc} */
    @Override
    public void registerCommand(final ShoppingCartCommand command) {

        final ShoppingCartCommand mapped = commandByKey.get(command.getCmdKey());
        if (mapped != command) {
            if (mapped != null) {
                ShopCodeContext.getLog(this).warn("Replacing command impl for: {} with {}", command.getCmdKey(), command);
            } else {
                ShopCodeContext.getLog(this).info("Adding command impl for: {} with {}", command.getCmdKey(), command);
            }
            commandByKey.put(command.getCmdKey(), command);
            commands = remapCommandChain(commandByKey.values());
        }
    }

    ShoppingCartCommand[] remapCommandChain(final Collection<ShoppingCartCommand> commands) {
        final List<ShoppingCartCommand> ordered = new ArrayList<ShoppingCartCommand>(commands);
        Collections.sort(ordered, new Comparator<ShoppingCartCommand>() {
            @Override
            public int compare(final ShoppingCartCommand cmd1, final ShoppingCartCommand cmd2) {
                return cmd1.getPriority() - cmd2.getPriority();
            }
        });
        return ordered.toArray(new ShoppingCartCommand[ordered.size()]);
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
