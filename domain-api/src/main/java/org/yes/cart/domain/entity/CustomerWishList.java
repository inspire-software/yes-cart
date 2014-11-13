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

package org.yes.cart.domain.entity;

import java.math.BigDecimal;

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
     * Product sku
     *
     * @return {@link ProductSku}
     */
    ProductSku getSkus();

    /**
     * Set {@link ProductSku}
     *
     * @param skus Product Sku
     */
    void setSkus(ProductSku skus);

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


}


