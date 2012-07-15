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

/**
 * TODO nice to have scheduler, that perform old wish list items clean up
 * <p/>
 * Shopper wish list item. Also reponsible to notification sheduling.
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
     * Get type of wsih list item.
     *
     * @return type of wsih list item
     */
    String getWlType();

    /**
     * Set type of wsih list item
     *
     * @param wlType type of wsih list item to set.
     */
    void setWlType(final String wlType);


}


