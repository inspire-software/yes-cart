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

package org.yes.cart.shoppingcart;

import java.io.Serializable;
import java.util.Map;

/**
 * Factory for shopping cart visitor events.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:25:25 PM
 */
public interface ShoppingCartCommandFactory extends ShoppingCartCommand, Serializable {

    /**
     * Execute specific command by key.
     *
     * @param key command key
     * @param shoppingCart the shopping cart
     * @param parameters parameters
     *
     * @throws IllegalArgumentException if key is invalid
     */
    void execute(String key, ShoppingCart shoppingCart, Map<String, Object> parameters) throws IllegalArgumentException;

}
