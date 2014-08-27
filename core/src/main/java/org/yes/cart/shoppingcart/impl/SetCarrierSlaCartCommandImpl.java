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
import org.yes.cart.util.ShopCodeContext;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetCarrierSlaCartCommandImpl  extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    public SetCarrierSlaCartCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_SETCARRIERSLA;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final String val = (String) parameters.get(getCmdKey());
            final Long slaPkvalue = val != null ? NumberUtils.createLong(val) : null;
            if (slaPkvalue == null || !slaPkvalue.equals(shoppingCart.getOrderInfo().getCarrierSlaId())) {
                ShopCodeContext.getLog(this).debug("Set carrier sla to {}", slaPkvalue);
                shoppingCart.getOrderInfo().setCarrierSlaId(slaPkvalue);
                if (slaPkvalue != null) {
                    final Long billingId = NumberUtils.createLong((String) parameters.get(CMD_SETCARRIERSLA_P_BILLING_ADDRESS));
                    final Long shippingId = NumberUtils.createLong((String) parameters.get(CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS));
                    shoppingCart.getOrderInfo().setBillingAddressId(billingId);
                    shoppingCart.getOrderInfo().setDeliveryAddressId(shippingId);
                } else {
                    shoppingCart.getOrderInfo().setBillingAddressId(null);
                    shoppingCart.getOrderInfo().setDeliveryAddressId(null);
                }
                recalculate(shoppingCart);
                markDirty(shoppingCart);
            }
        }
    }
}
