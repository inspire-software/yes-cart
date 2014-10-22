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

import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

public class SetAddressesCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20141022L;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    public SetAddressesCartCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_SETADDRESES;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            boolean changed = false;
            if (parameters.containsKey(CMD_SETADDRESES_P_BILLING_ADDRESS)) {
                final boolean notRequired = shoppingCart.getOrderInfo().isBillingAddressNotRequired();
                if (notRequired) {
                    if (shoppingCart.getOrderInfo().getBillingAddressId() != null) {
                        shoppingCart.getOrderInfo().setBillingAddressId(null);
                        changed = true;
                    }
                } else {
                    final Long billingId = NumberUtils.toLong((String) parameters.get(CMD_SETADDRESES_P_BILLING_ADDRESS), 0L);
                    if (billingId > 0L) {
                        if (!billingId.equals(shoppingCart.getOrderInfo().getBillingAddressId())) {
                            shoppingCart.getOrderInfo().setBillingAddressId(billingId);
                            changed = true;
                        }
                    } else if (shoppingCart.getOrderInfo().getBillingAddressId() != null) {
                        shoppingCart.getOrderInfo().setBillingAddressId(null);
                        changed = true;
                    }
                }
            }

            if (parameters.containsKey(CMD_SETADDRESES_P_DELIVERY_ADDRESS)) {
                final boolean notRequired = shoppingCart.getOrderInfo().isDeliveryAddressNotRequired();
                if (notRequired) {
                    if (shoppingCart.getOrderInfo().getDeliveryAddressId() != null) {
                        shoppingCart.getOrderInfo().setDeliveryAddressId(null);
                        changed = true;
                    }
                } else {
                    final Long deliveryId = NumberUtils.toLong((String) parameters.get(CMD_SETADDRESES_P_DELIVERY_ADDRESS), 0L);
                    if (deliveryId > 0L) {
                        if (!deliveryId.equals(shoppingCart.getOrderInfo().getDeliveryAddressId())) {
                            shoppingCart.getOrderInfo().setDeliveryAddressId(deliveryId);
                            changed = true;
                        }
                    } else if (shoppingCart.getOrderInfo().getDeliveryAddressId() != null) {
                        shoppingCart.getOrderInfo().setDeliveryAddressId(null);
                        changed = true;
                    }
                }
            }

            if (changed) {
                recalculate(shoppingCart);
                markDirty(shoppingCart);
            }
        }
    }
}
