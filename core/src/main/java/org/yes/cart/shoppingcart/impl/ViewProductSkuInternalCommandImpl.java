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

import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 16/01/2014
 * Time: 18:04
 */
public class ViewProductSkuInternalCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private final ShopService shopService;
    private final int maxProductHistory;

    /**
     * Construct sku command.
     * @param registry shopping cart command registry
     * @param shopService shop service
     * @param maxProductHistory max product history to keep
     */
    public ViewProductSkuInternalCommandImpl(final ShoppingCartCommandRegistry registry,
                                             final ShopService shopService,
                                             final int maxProductHistory) {
        super(registry);
        this.shopService = shopService;
        this.maxProductHistory = maxProductHistory;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final Shop shop = this.shopService.getById(shoppingCart.getShoppingContext().getShopId());
            final int max = NumberUtils.toInt(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_SF_MAX_LAST_VIEWED_SKU), maxProductHistory);
            final Object skus = parameters.get(getCmdKey());
            if (skus instanceof Collection) {
                for (final Object sku : (Collection) skus) {
                    executeOneItem(shoppingCart, sku, max);
                }
            } else {
                executeOneItem(shoppingCart, skus, max);
            }
        }
    }

    private void executeOneItem(final MutableShoppingCart shoppingCart, final Object sku, final int max) {
        if (sku instanceof ProductSku) {
            updateViewedSku(shoppingCart, String.valueOf(((ProductSku) sku).getProduct().getProductId()), max);
        } else if (sku instanceof Product) {
            updateViewedSku(shoppingCart, String.valueOf(((Product) sku).getProductId()), max);
        } else if (sku instanceof String) {
            updateViewedSku(shoppingCart, (String) sku, max);
        }
    }

    private void updateViewedSku(final MutableShoppingCart shoppingCart, final String skuCode, final int max) {

        List<String> skus = shoppingCart.getShoppingContext().getLatestViewedSkus();

        if (skus == null) {
            skus = new LinkedList<>();
            shoppingCart.getShoppingContext().setLatestViewedSkus(skus);
        }

        if (!skus.contains(skuCode)) {
            if (skus.size() >= max) {
                // if maxed out remove the first element
                final List<String> last = new LinkedList<>(skus.subList(1, skus.size()));
                skus.clear();
                skus.addAll(last);
            }
            skus.add(skuCode);
            shoppingCart.markDirty();
        }

    }

    /** {@inheritDoc} */
    @Override
    public String getCmdKey() {
        return CMD_INTERNAL_VIEWSKU;
    }
}
