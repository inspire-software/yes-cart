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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:29:32 PM
 *
 */
public class ShoppingCartCommandFactoryImpl implements ShoppingCartCommandFactory {

    private static final long serialVersionUID = 20100122L;

    private final ShoppingCartCommand[] commands;
    private final Map<String, ShoppingCartCommand> commandByKey = new HashMap<String, ShoppingCartCommand>();

    /**
     * IoC constructor.
     *
     * @param commands configured commands
     */
    public ShoppingCartCommandFactoryImpl(final List<ShoppingCartCommand> commands) {
        this.commands = commands.toArray(new ShoppingCartCommand[commands.size()]);
        for (final ShoppingCartCommand command : commands) {
            commandByKey.put(command.getCmdKey(), command);
        }
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

}
