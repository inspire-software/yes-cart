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

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ExpireCartCommandImpl  extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    private final ShopService shopService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    public ExpireCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                 final ShopService shopService) {
        super(registry);
        this.shopService = shopService;
    }

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_EXPIRE;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            final String shopCode = shoppingCart.getShoppingContext().getShopCode();
            final Shop current = shopService.getShopByCode(shopCode);

            shoppingCart.getShoppingContext().clearContext();
            setTaxOptions(current, null, null, null, null, shoppingCart.getShoppingContext());

            shoppingCart.getOrderInfo().clearInfo();
            setCustomerOptions(current, null, shoppingCart.getOrderInfo());

            if (shoppingCart.removeItemOffers()) {
                // Offers have to be removed from cart, since we may get stale prices
                // Offers assumed to be valid only for duration of cart validity
                recalculate(shoppingCart);
            }
            markDirty(shoppingCart);
        }
    }
}
