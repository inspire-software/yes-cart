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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RemoveCouponEventCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    public RemoveCouponEventCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_REMOVECOUPON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            final Object coupon = parameters.get(getCmdKey());
            if (coupon instanceof String ) {
                final String couponCode = (String) coupon;
                if (StringUtils.isNotBlank(couponCode)) {
                    if (shoppingCart.removeCoupon(couponCode)) {
                        recalculate(shoppingCart);
                        markDirty(shoppingCart);
                    }
                }
            }
        }
    }
}
