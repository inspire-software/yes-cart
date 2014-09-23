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

import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.*;

/**
 * User: denispavlov
 * Date: 16/01/2014
 * Time: 18:04
 */
public class ViewProductSkuInternalCommandImpl implements ShoppingCartCommand {

    private final int maxProductHistory;

    public ViewProductSkuInternalCommandImpl(final int maxProductHistory) {
        this.maxProductHistory = maxProductHistory;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final Object sku = parameters.get(getCmdKey());
            if (sku instanceof ProductSku) {
                updateViewedSku(shoppingCart, String.valueOf(((ProductSku) sku).getProduct().getProductId()));
            } else if (sku instanceof Product) {
                updateViewedSku(shoppingCart, String.valueOf(((Product) sku).getProductId()));
            } else if (sku instanceof String) {
                updateViewedSku(shoppingCart, (String) sku);
            }
        }
    }

    private void updateViewedSku(final ShoppingCart shoppingCart, final String productId) {

        List<String> skus = shoppingCart.getShoppingContext().getLatestViewedSkus();

        if (skus == null) {
            skus = new LinkedList<String>();
            shoppingCart.getShoppingContext().setLatestViewedSkus(skus);
        }

        if (!skus.contains(productId)) {
            if (skus.size() >= maxProductHistory) {
                // if maxed out remove the first element
                final List<String> last = new LinkedList<String>(skus.subList(1, skus.size()));
                skus.clear();
                skus.addAll(last);
            }
            skus.add(productId);
            shoppingCart.markDirty();
        }

    }

    /** {@inheritDoc} */
    @Override
    public String getCmdKey() {
        return CMD_INTERNAL_VIEWSKU;
    }
}
