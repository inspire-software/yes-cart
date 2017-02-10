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

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: denis
 */
public class SetOrderInfoDetailsCommandImpl extends AbstractCartCommandImpl  implements ShoppingCartCommand {

    private static final long serialVersionUID = 20161106L;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    public SetOrderInfoDetailsCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
    * @return command key
    */
    public String getCmdKey() {
       return CMD_SETORDERDETAILS;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final String values = (String) parameters.get(getCmdKey());
            if (values == null) {
                shoppingCart.getOrderInfo().setDetails(null);
                markDirty(shoppingCart);
            } else {
                final String[] keyAndValues = StringUtils.split(values, '|');
                if (keyAndValues != null) {
                    boolean modified = false;
                    for (final String keyAndValueRaw : keyAndValues) {
                        final String[] keyAndValue = StringUtils.split(keyAndValueRaw, ":", 2);
                        if (keyAndValue != null && keyAndValue.length == 2) {
                            shoppingCart.getOrderInfo().putDetail(keyAndValue[0], keyAndValue[1]);
                            modified = true;
                        }
                    }
                    if (modified) {
                        markDirty(shoppingCart);
                    }
                }
            }
        }
    }

}
