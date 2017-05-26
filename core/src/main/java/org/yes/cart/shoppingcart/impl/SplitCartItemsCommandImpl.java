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

import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.OrderSplittingStrategy;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SplitCartItemsCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    private final OrderSplittingStrategy orderSplittingStrategy;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param orderSplittingStrategy splitting strategy
     */
    public SplitCartItemsCommandImpl(final ShoppingCartCommandRegistry registry,
                                     final OrderSplittingStrategy orderSplittingStrategy) {
        super(registry);
        this.orderSplittingStrategy = orderSplittingStrategy;
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_SPLITCARTITEMS;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            boolean changed = performCartItemsSplitting(shoppingCart);

            if (changed) {
                recalculate(shoppingCart);
                markDirty(shoppingCart);
            }
        }
    }

    /**
     * Perform cart items splitting into delivery buckets appropriate to products supplier
     * ({@link org.yes.cart.domain.entity.Warehouse}) and product availability and stock.
     *
     * @param shoppingCart cart
     *
     * @return true if any of the items in the cart had been altered
     */
    protected boolean performCartItemsSplitting(final MutableShoppingCart shoppingCart) {

        boolean changed = false;
        final long shopId = shoppingCart.getShoppingContext().getCustomerShopId();

        final Map<String, Boolean> isMultiAllowed =
                this.orderSplittingStrategy.isMultipleDeliveriesAllowed(shopId, shoppingCart.getCartItemList());

        final Map<String, Boolean> singleSelected = new HashMap<String, Boolean>();
        for (final Map.Entry<String, Boolean> isAllowedForSupplier : isMultiAllowed.entrySet()) {
            singleSelected.put(isAllowedForSupplier.getKey(), !isAllowedForSupplier.getValue() || !shoppingCart.getOrderInfo().isMultipleDelivery());
        }

        // Ensure multi option is updated
        final Map<String, Boolean> existingAllowed = shoppingCart.getOrderInfo().getMultipleDeliveryAvailable();
        if (existingAllowed.size() != isMultiAllowed.size()) {
            // size does not match so must be different
            shoppingCart.getOrderInfo().setMultipleDeliveryAvailable(isMultiAllowed);
            changed = true;
        } else {
            for (final Map.Entry<String, Boolean> existingAllowedBySupplier : existingAllowed.entrySet()) {
                if (existingAllowedBySupplier.getValue().equals(isMultiAllowed.get(existingAllowedBySupplier.getKey()))) {
                    shoppingCart.getOrderInfo().setMultipleDeliveryAvailable(isMultiAllowed);
                    changed = true;
                    break; // At least one condition changed
                }
            }
        }

        // Set default supplier code to all items that are missing one
        for (final CartItem item : shoppingCart.getCartItemList()) {
            if (item.getDeliveryBucket() == null) {
                if (item.isGift()) {
                    shoppingCart.setGiftDeliveryBucket(item.getProductSkuCode(), ShoppingCartUtils.DEFAULT_DELIVERY_BUCKET);
                } else {
                    shoppingCart.setProductSkuDeliveryBucket(item.getProductSkuCode(), ShoppingCartUtils.DEFAULT_DELIVERY_BUCKET);
                }
                changed = true;
            }
        }

        final Map<DeliveryBucket, List<CartItem>> cartBuckets =
                this.orderSplittingStrategy.determineDeliveryBuckets(shopId, shoppingCart.getCartItemList(), singleSelected);

        for (final Map.Entry<DeliveryBucket, List<CartItem>> cartBucket : cartBuckets.entrySet()) {

            for (final CartItem item : cartBucket.getValue()) {

                if (item.isGift()) {
                    if (shoppingCart.setGiftDeliveryBucket(item.getProductSkuCode(), cartBucket.getKey())) {
                        changed = true;
                    }
                } else {
                    if (shoppingCart.setProductSkuDeliveryBucket(item.getProductSkuCode(), cartBucket.getKey())) {
                        changed = true;
                    }
                }

            }

        }

        return changed;
    }

}
