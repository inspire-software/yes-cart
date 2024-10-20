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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Default implementation of the set price command.
 * <p/>
 * User: denis
 * Date: Mar 11, 2016
 * Time: 4:17:10 PM
 */
public class SetSkuPriceEventCommandImpl extends AbstractCartCommandImpl {

    private static final long serialVersionUID = 20100122L;

    private static final Logger LOG = LoggerFactory.getLogger(SetSkuPriceEventCommandImpl.class);

    /**
     * Construct sku command.
     *
     * @param registry shopping cart command registry
     */
    public SetSkuPriceEventCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return CMD_SETPRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {

        if (parameters.containsKey(getCmdKey())) {

            final String skuCode = (String) parameters.get(getCmdKey());
            final String supplier = (String) parameters.get(CMD_P_SUPPLIER);
            final String price = (String) parameters.get(CMD_SETPRICE_P_PRICE);
            final String group = (String) parameters.get(CMD_P_ITEM_GROUP);

            BigDecimal offer;
            try {
                offer = new BigDecimal(price);
            } catch (Exception exp) {
                LOG.error("Can not set price {} for product sku dto with code {}", price, skuCode);
                return;
            }

            final String auth = (String) parameters.get(CMD_SETPRICE_P_AUTH);

            if (StringUtils.isBlank(auth)) {
                LOG.error("Can not set price {} for product sku dto with code {} because no auth code supplied", price, skuCode);
                return;
            }

            boolean matched = false;

            for (final CartItem item : shoppingCart.getCartItemList()) {

                if ((StringUtils.isNotBlank(group) && ShoppingCartUtils.isCartItem(item, supplier, skuCode, group))
                        || ShoppingCartUtils.isCartItem(item, supplier, skuCode)) {

                    matched = true;

                    if (MoneyUtils.isFirstBiggerThanSecond(item.getSalePrice(), offer)) {

                        if (!shoppingCart.setProductSkuOffer(supplier, skuCode, offer, group, auth)) {
                            LOG.warn("Can not set price to sku with code {} ", skuCode);
                        } else {

                            recalculate(shoppingCart);
                            markDirty(shoppingCart);

                        }

                    } else {
                        // Use case whereby we override the price upwards
                        if (!(shoppingCart.setProductSkuPrice(supplier, skuCode, offer, offer) &&
                                shoppingCart.setProductSkuOffer(supplier, skuCode, offer, group, auth))) {
                            LOG.warn("Can not set price to sku with code {} ", skuCode);
                        } else {

                            recalculate(shoppingCart);
                            markDirty(shoppingCart);

                        }

                    }

                }

            }
            if (!matched) {

                LOG.warn("Can not locate product sku dto with code {} in cart {}", skuCode, shoppingCart.getGuid());

            }

        }

    }

}
