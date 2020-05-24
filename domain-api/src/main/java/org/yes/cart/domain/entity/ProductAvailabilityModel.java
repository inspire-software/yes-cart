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

import org.yes.cart.domain.i18n.I18NModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.SortedSet;

/**
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:08 PM
 */
public interface ProductAvailabilityModel {

    /**
     * @return supplier code for this model
     */
    String getSupplier();

    /**
     * @return true if this product is purchasable at minimum quantity.
     */
    boolean isAvailable();

    /**
     * Returns true only if there is actual stock for this product.
     * For perpetual, preorder and back order items this will return
     * false.
     *
     * @return true if product is in stock.
     */
    boolean isInStock();

    /**
     * @return product that is always available (e.g. digital download or service)
     */
    boolean isPerpetual();

    /**
     * Get product availability
     *
     * @return  Availability
     */
    int getAvailability();

    /**
     * @return default sku for multisku
     */
    String getDefaultSkuCode();

    /**
     * Returns sku code for the first available item. The order is:
     * default SKU if available first then other skus from getSkuCodes()
     *
     * @return the first available sku.
     */
    String getFirstAvailableSkuCode();

    /**
     * @return all sku codes
     */
    SortedSet<String> getSkuCodes();

    /**
     * @param skuCode code
     *
     * @return quantity that is available for customer to purchase.
     */
    BigDecimal getAvailableToSellQuantity(String skuCode);

    /**
     * Date when product is released. If this date is in the future this offer is PREORDER.
     *
     * @return release date
     */
    LocalDateTime getReleaseDate();

    /**
     * Get restock date. Specific date of the next delivery (if known)
     *
     * @return product restock date.
     */
    LocalDateTime getRestockDate();

    /**
     * Get restock note. Customer note to suggest of when stock will be available.
     * Used as guideline for orders that are on BACKORDER to encourage sales.
     *
     * @return product restock note.
     */
    I18NModel getRestockNote();

}
