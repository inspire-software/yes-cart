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

package org.yes.cart.service.domain;

import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * Order amendment service facilitates basic operations for order amendment.
 *
 * User: denispavlov
 * Date: 14/03/2016
 * Time: 18:08
 */
public interface CustomerOrderAmendmentService {

    /**
     * Create order amendment cart. This cart will contains all CustomerOrder items.
     *
     * @param ref order reference
     * @param recalculate flag to recalculate the shopping cart using currently active promotions,
     *                    which overwrites all promotions previously set on this cart.
     *
     * @return cart to be used for amending order
     */
    ShoppingCart createAmendmentCart(String ref, boolean recalculate);

    /**
     * Find cart by guid.
     *
     * @param guid cart GUID
     *
     * @return shopping cart or null if does not exist
     */
    ShoppingCart findByGuid(String guid);

    /**
     * Update cart item, which includes updating quantity, list, sale or final price.
     *
     * Final price is only applicable for {@link CartItem#isFixedPrice()} updates since recalculation
     * of cart will overwrite all final prices during recalculation.
     *
     * @param cartGuid shopping cart GUID
     * @param item cart item with updates
     */
    void updateCartItem(String cartGuid, CartItem item);

    /**
     * Create cart item.
     *
     * @param cartGuid shopping cart GUID
     * @param item cart item to create
     */
    void createCartItem(String cartGuid, CartItem item);

    /**
     * Delete shopping cart with given guid.
     *
     * @param guid shopping cart to delete
     */
    void deleteCart(String guid);

}
