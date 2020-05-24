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

import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.impl.DeliveryBucketImpl;
import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 29/12/2016
 * Time: 22:23
 */
public final class ShoppingCartUtils {

    public static final DeliveryBucket DEFAULT_DELIVERY_BUCKET = new DeliveryBucketImpl("", "");

    private ShoppingCartUtils() {
        // no instance
    }

    /**
     * Get immutable list of all cart items (products + gifts)
     *
     * @param items items
     * @param gifts gifts
     *
     * @return all items
     */
    public static List<CartItem> getCartItemImmutableList(final List<? extends CartItem> items,
                                                          final List<? extends CartItem> gifts) {

        final List<CartItem> immutableItems = new ArrayList<>(items.size());

        // first items (in the order they were added)
        addCartItemsToImmutableList(immutableItems, items);
        // gifts in the order promotions were applied
        addCartItemsToImmutableList(immutableItems, gifts);

        return Collections.unmodifiableList(immutableItems);
    }

    /**
     * Get immutable list of shipping items
     *
     * @param items items
     *
     * @return all items
     */
    public static List<CartItem> getShippingImmutableList(final List<? extends CartItem> items) {

        final List<CartItem> immutableItems = new ArrayList<>(items.size());

        addCartItemsToImmutableList(immutableItems, items);

        return Collections.unmodifiableList(immutableItems);
    }

    private static void addCartItemsToImmutableList(final List<CartItem> fullList,
                                                    final List<? extends CartItem> itemsToAdd) {

        for (CartItem item : itemsToAdd) {
            fullList.add(new ImmutableCartItemImpl(item));
        }

    }


    /**
     * Get immutable map of all cart items (products + gifts) buckets
     *
     * @param items items
     * @param gifts gifts
     *
     * @return all items
     */
    public static Map<DeliveryBucket, List<CartItem>> getCartItemImmutableMap(final List<? extends CartItem> items,
                                                                              final List<? extends CartItem> gifts) {

        final Map<DeliveryBucket, List<CartItem>> map = new LinkedHashMap<>(items.size() * 2);

        addCartItemsToImmutableMap(map, items);
        addCartItemsToImmutableMap(map, gifts);

        return map;
    }

    /**
     * Get immutable map of all shipping buckets
     *
     * @param items items
     *
     * @return all items
     */
    public static Map<DeliveryBucket, List<CartItem>> getShippingImmutableMap(final List<? extends CartItem> items) {

        final Map<DeliveryBucket, List<CartItem>> map = new LinkedHashMap<>(items.size() * 2);

        addCartItemsToImmutableMap(map, items);

        return map;
    }


    private static void addCartItemsToImmutableMap(final Map<DeliveryBucket, List<CartItem>> fullMap,
                                                   final List<? extends CartItem> itemsToAdd) {

        for (final CartItem item : itemsToAdd) {
            final DeliveryBucket itemBucket = item.getDeliveryBucket();
            final DeliveryBucket bucket = itemBucket != null ? itemBucket : DEFAULT_DELIVERY_BUCKET;
            List<CartItem> supplierItems = fullMap.computeIfAbsent(bucket, k -> new ArrayList<>());
            supplierItems.add(new ImmutableCartItemImpl(item));
        }

    }

    /**
     * Get delivery bucket for given item
     *
     * @param item item
     *
     * @return delivery bucket or null if none can be calculated
     */
    public static DeliveryBucket getDeliveryBucket(final CartItem item) {
        if (item.getSupplierCode() == null || item.getDeliveryGroup() == null) {
            return null;
        }
        return new DeliveryBucketImpl(item.getDeliveryGroup(), item.getSupplierCode());
    }

    /**
     * Get sum of quantities of products and gifts.
     *
     * @param items items
     * @param gifts gifts
     *
     * @return quantities of all items
     */
    public static int getCartItemsCount(final List<? extends CartItem> items,
                                        final List<? extends CartItem> gifts) {
        BigDecimal quantity = BigDecimal.ZERO;
        for (CartItem cartItem : items) {
            if (!cartItem.isNotSoldSeparately()) {
                quantity = quantity.add(cartItem.getQty());
            }
        }
        for (CartItem cartItem : gifts) {
            quantity = quantity.add(cartItem.getQty());
        }
        return quantity.intValue();
    }


    /**
     * Get total quantity of SKU.
     *
     * @param items     items
     * @param supplier  supplier
     * @param sku       sku
     *
     * @return quantities of all items
     */
    public static BigDecimal getProductSkuQuantity(final List<? extends CartItem> items,
                                                   final String supplier,
                                                   final String sku) {
        BigDecimal quantity = BigDecimal.ZERO;
        for (CartItem cartItem : items) {
            if (isCartItem(cartItem, supplier, sku)) {
                quantity = quantity.add(cartItem.getQty());
            }
        }
        return quantity;
    }

    /**
     * Check if cart item matches SKU by given supplier.
     *
     * @param item      item to check
     * @param supplier  supplier to match
     * @param skuCode   SKU to match
     *
     * @return true if supplier and SKU match exactly
     */
    public static boolean isCartItem(final CartItem item,
                                     final String supplier,
                                     final String skuCode) {

        return item.getProductSkuCode().equals(skuCode) &&
                ((item.getSupplierCode() == null && supplier == null)
                        || (item.getSupplierCode() != null && item.getSupplierCode().equals(supplier)));

    }

    /**
     * Check if cart item matches SKU by given supplier.
     *
     * @param item      item to check
     * @param supplier  supplier to match
     * @param skuCode   SKU to match
     * @param group     group to match
     *
     * @return true if supplier and SKU match exactly
     */
    public static boolean isCartItem(final CartItem item,
                                     final String supplier,
                                     final String skuCode,
                                     final String group) {

        return item.getProductSkuCode().equals(skuCode) &&
                ((item.getSupplierCode() == null && supplier == null)
                        || (item.getSupplierCode() != null && item.getSupplierCode().equals(supplier))) &&
                ((item.getItemGroup() == null && group == null)
                        || (item.getItemGroup() != null && item.getItemGroup().equals(group)));

    }

    /**
     * Check if cart item matches SKU by given supplier.
     *
     * @param item      item to check
     * @param bucket    bucket to match
     * @param skuCode   SKU to match
     *
     * @return true if supplier and SKU match exactly
     */
    public static boolean isCartItem(final CartItem item,
                                     final DeliveryBucket bucket,
                                     final String skuCode) {

        return item.getProductSkuCode().equals(skuCode) &&
                (bucket == null || bucket.equals(item.getDeliveryBucket()));

    }

    /**
     * Check if cart item matches SKU by given supplier.
     *
     * @param item      item to check
     * @param bucket    bucket to match
     * @param skuCode   SKU to match
     * @param group     group to match
     *
     * @return true if supplier and SKU match exactly
     */
    public static boolean isCartItem(final CartItem item,
                                     final DeliveryBucket bucket,
                                     final String skuCode,
                                     final String group) {

        return item.getProductSkuCode().equals(skuCode) &&
                (bucket == null || bucket.equals(item.getDeliveryBucket())) &&
                ((item.getItemGroup() == null && group == null)
                        || (item.getItemGroup() != null && item.getItemGroup().equals(group)));

    }


    /**
     * Get all supplier codes of products and gifts.
     *
     * @param items items
     * @param gifts gifts
     *
     * @return unique list of supplier codes
     */
    public static List<String> getCartItemsSuppliers(final List<? extends CartItem> items,
                                                     final List<? extends CartItem> gifts) {
        final Set<String> codes = new HashSet<>();
        for (CartItem cartItem : items) {
            if (cartItem.getSupplierCode() != null) {
                codes.add(cartItem.getSupplierCode());
            }
        }
        for (CartItem cartItem : gifts) {
            if (cartItem.getSupplierCode() != null) {
                codes.add(cartItem.getSupplierCode());
            }
        }
        return new ArrayList<>(codes);
    }


    /**
     * Get all supplier codes of products and gifts.
     *
     * @param items items
     * @param gifts gifts
     *
     * @return unique list of supplier codes
     */
    public static boolean isAllCarrierSlaSelected(final List<? extends CartItem> items,
                                                  final List<? extends CartItem> gifts,
                                                  final Map<String, Long> slaSelection) {
        for (CartItem cartItem : items) {
            if (cartItem.getSupplierCode() == null || !slaSelection.containsKey(cartItem.getSupplierCode())) {
                return false;
            }
        }
        for (CartItem cartItem : gifts) {
            if (cartItem.getSupplierCode() == null || !slaSelection.containsKey(cartItem.getSupplierCode())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all items in this cart have been assigned a bucket.
     * If not this indicated that order splitting command must be run
     * before shipping step.
     *
     * @param items items
     * @param gifts gifts
     *
     * @return true if all items and gifts have delivery bucket
     */
    public static boolean isAllCartItemsBucketed(final List<? extends CartItem> items,
                                                 final List<? extends CartItem> gifts) {
        for (CartItem cartItem : items) {
            if (cartItem.getSupplierCode() == null || cartItem.getDeliveryGroup() == null) {
                return false;
            }
        }
        for (CartItem cartItem : gifts) {
            if (cartItem.getSupplierCode() == null || cartItem.getDeliveryGroup() == null) {
                return false;
            }
        }
        return true;
    }

}
