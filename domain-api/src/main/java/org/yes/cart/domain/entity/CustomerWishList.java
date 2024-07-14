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

package org.yes.cart.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p/>
 * Shopper wish list item. Also responsible to notification scheduling.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerWishList extends Auditable {

    String SIMPLE_WISH_ITEM = "W";

    String SHOPPING_LIST_ITEM = "S";

    String MANAGED_LIST_ITEM = "M";

    String CART_SAVE_FOR_LATER = "C";

    String REMIND_WHEN_WILL_BE_AVAILABLE = "A";

    String REMIND_WHEN_PRICE_CHANGED = "P";

    String REMIND_WHEN_WILL_BE_IN_PROMO = "R";


    String PRIVATE = "P";

    String SHARED = "S";

    /**
     * Primary key value.
     *
     * @return key value.
     */
    long getCustomerwishlistId();

    /**
     * Set key value
     *
     * @param customerwishlistId value to set.
     */
    void setCustomerwishlistId(long customerwishlistId);


    /**
     * Get SKU code.
     *
     * @return sku
     */
    String getSkuCode();

    /**
     * Set SKU code.
     *
     * @param skuCode SKU code
     */
    void setSkuCode(String skuCode);


    /**
     * Get supplier.
     *
     * @return supplier
     */
    String getSupplierCode();

    /**
     * Set supplier.
     *
     * @param supplierCode supplier
     */
    void setSupplierCode(String supplierCode);

    /**
     * Get customer
     *
     * @return {@link Customer}
     */
    Customer getCustomer();

    /**
     * Set customer
     *
     * @param customer customer to set
     */
    void setCustomer(Customer customer);

    /**
     * Get type of wish list item.
     *
     * @return type of wish list item
     */
    String getWlType();

    /**
     * Set type of wish list item
     *
     * @param wlType type of wish list item to set.
     */
    void setWlType(final String wlType);

    /**
     * Defines visibility for this item. (i.e. can other customer view it?)
     *
     * @return visibility type
     */
    String getVisibility();

    /**
     * Defines visibility for this item. (i.e. can other customer view it?)
     *
     * @param  visibility type
     */
    void setVisibility(String visibility);

    /**
     * Get the space separated product tags. For example
     * wedding, alwayswanted etc.
     *
     * This tags should be used to separate items into wish list groups.
     *
     * @return space separated items tags
     */
    String getTag();

    /**
     * Set space separated product tags.
     *
     * @param tag space separated product tags.
     */
    void setTag(String tag);

    /**
     * Get notification email of the creator of this wishlist.
     *
     * @return notification email
     */
    String getNotificationEmail();

    /**
     * Set notification email of the creator of this wishlist.
     *
     * @param notificationEmail notification email
     */
    void setNotificationEmail(String notificationEmail);

    /**
     * Get last purchase date for this item
     *
     * @return last purchase date
     */
    LocalDate getLastPurchaseDate();

    /**
     * Set last purchase date for this item
     *
     * @param lastPurchaseDate last purchase date
     */
    void setLastPurchaseDate(LocalDate lastPurchaseDate);


    /**
     * Quantity in wish list.
     *
     * @return quantity of sku
     */
    BigDecimal getQuantity();

    /**
     * Quantity in wish list.
     *
     * @param quantity quantity of sku
     */
    void setQuantity(BigDecimal quantity);

    /**
     * Price for this item when it was added to the wish list.
     *
     * @return price of sku
     */
    BigDecimal getRegularPriceWhenAdded();

    /**
     * Price for this item when it was added to the wish list.
     *
     * @param listPriceWhenAdded price of sku
     */
    void setRegularPriceWhenAdded(BigDecimal listPriceWhenAdded);


    /**
     * Get currency code.
     *
     * @return currency code
     */
    String getRegularPriceCurrencyWhenAdded();

    /**
     * set currency code.
     *
     * @param regularPriceCurrencyWhenAdded curr code
     */
    void setRegularPriceCurrencyWhenAdded(String regularPriceCurrencyWhenAdded);

    /**
     * Wish list price change types
     */
    enum PriceChangeType {
        NOCHANGE, // No change
        ONSALE, // No change but it is on sale
        DECREASED, // price dropped
        INCRSEASED, // price increased
        DIFFCURRENCY, // wishlist is in different currency
        OFFLINE // product no longer has price in this shop
    }

    /**
     * Price change model for wish list to support price comparison
     */
    class PriceChange {

        private final PriceChangeType type;
        private final BigDecimal delta;

        public PriceChange(final PriceChangeType type, final BigDecimal delta) {
            this.type = type;
            this.delta = delta;
        }

        public PriceChangeType getType() {
            return type;
        }

        public BigDecimal getDelta() {
            return delta;
        }
    }

}


