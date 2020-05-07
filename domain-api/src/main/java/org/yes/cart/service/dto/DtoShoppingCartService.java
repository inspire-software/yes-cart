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

package org.yes.cart.service.dto;

import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 15/03/2016
 * Time: 18:11
 */
public interface DtoShoppingCartService {

    /**
     * Create amendment shopping cart for given order ref.
     *
     * @param ref           order reference
     * @param recalculate   if this flag is set to true then cart is recalculates using current prices and offers,
     *                      otherwise the cart is using old prices
     *
     * @return shopping cart
     */
    ShoppingCart create(String ref,
                        boolean recalculate);

    /**
     * Get shopping cart by its GUID.
     *
     * @param guid  cart GUID
     *
     * @return shopping cart
     */
    ShoppingCart getById(String guid);

    /**
     * Remove SKU line from cart.
     *
     * @param guid      cart guid
     * @param supplier  supplier
     * @param sku       SKU to remove
     * @param group     SKU group
     */
    void removeLine(String guid,
                    String supplier,
                    String sku,
                    String group);

    /**
     * Update SKU line quantity (if sku did not exist creates one).
     *
     * @param guid      cart guid
     * @param supplier  supplier
     * @param sku       SKU to update
     * @param group     SKU group
     * @param quantity  quantity to set
     */
    void createLine(String guid,
                    String supplier,
                    String sku,
                    String group,
                    BigDecimal quantity);

    /**
     * Update SKU line quantity (if sku did not exist creates one).
     *
     * @param guid      cart guid
     * @param supplier  supplier
     * @param sku       SKU to update
     * @param group     SKU group
     * @param quantity  quantity to set
     */
    void updateLineQuantity(String guid,
                            String supplier,
                            String sku,
                            String group,
                            BigDecimal quantity);

    /**
     * Update SKU line quantity (if sku did not exist does not update).
     *
     * @param guid      cart guid
     * @param supplier  supplier
     * @param sku       SKU to update
     * @param offer     special price
     * @param auth      authorisation
     */
    void updateLinePrice(String guid,
                         String supplier,
                         String sku,
                         BigDecimal offer,
                         String auth);

    /**
     * Remove shopping cart by GUID.
     *
     * @param guid cart GUID
     */
    void remove(String guid);


}
