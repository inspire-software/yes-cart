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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: inspiresoftware
 * Date: 01/03/2021
 * Time: 20:11
 */
public class ResetCouponsEventCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    public ResetCouponsEventCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return CMD_RESETCOUPONS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            final Object coupon = parameters.get(getCmdKey());
            if (addCouponCodeIfNecessary(shoppingCart, coupon)) {
                LOG.debug("[{}] Reset coupon codes {}", shoppingCart.getGuid(), coupon);
            }
        }
    }

    private boolean addCouponCodeIfNecessary(final MutableShoppingCart shoppingCart, final Object coupons) {

        if (coupons instanceof String ) {
            final String couponCodes = (String) coupons;
            if (StringUtils.isNotBlank(couponCodes)) {
                for (final String remove : shoppingCart.getCoupons()) {
                    shoppingCart.removeCoupon(remove);
                }
                final String[] codes = StringUtils.split(couponCodes, ',');
                for (final String add : codes) {
                    shoppingCart.addCoupon(add);
                }
                recalculate(shoppingCart);
                markDirty(shoppingCart);
                return true;
            }
        }

        return false;
    }
}
