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

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 * SKU/Warehouse is specific offer by a fulfilment center, which defines the
 * fulfilment options for a given SKU.
 */
public interface SkuWarehouse extends Auditable, Taggable {

    /**
     * Not available.
     */
    int AVAILABILITY_NA = 0;
    /**
     * When available on warehouse.
     */
    int AVAILABILITY_STANDARD = 1;
    /**
     * Available for backorder.
     */
    int AVAILABILITY_BACKORDER = 4;
    /**
     * Always available
     */
    int AVAILABILITY_ALWAYS = 8;
    /**
     * Only available as product information (cannot be purchased)
     */
    int AVAILABILITY_SHOWROOM = 16;

    /**
     * @return primary key
     */
    long getSkuWarehouseId();

    /**
     * Set primary key
     *
     * @param skuWarehouseId primary key
     */
    void setSkuWarehouseId(long skuWarehouseId);


    /**
     * Get the {@link Warehouse}.
     *
     * @return {@link Warehouse}
     */
    Warehouse getWarehouse();

    /**
     * Set {@link Warehouse}
     *
     * @param warehouse {@link Warehouse}
     */
    void setWarehouse(Warehouse warehouse);

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
     * Get the available quantity.
     *
     * @return available quantity.
     */
    BigDecimal getQuantity();

    /**
     * Set available quantity.
     *
     * @param quantity available quantity.
     */
    void setQuantity(BigDecimal quantity);

    /**
     * Get reserved quantity during payment transaction.
     *
     * @return reserved quantity during payment transaction.
     */
    BigDecimal getReserved();

    /**
     * Set reserved quantity during payment transaction.
     *
     * @param reserved reserved quantity during payment transaction.
     */
    void setReserved(BigDecimal reserved);

    /**
     * Returns true if product is enabled and now is within from/to date range.
     *
     * @param now    time now
     *
     * @return true if the product is available now
     */
    boolean isAvailable(LocalDateTime now);

    /**
     * Returns true if product is released and now is within from/to date range.
     *
     * @param now    time now
     *
     * @return true if the product is available now
     */
    boolean isReleased(LocalDateTime now);

    /**
     * Returns true if has quantity and quantity is larger than reserved.
     *
     * @param allowOrderMore    flag to signify that we can sell in principle (allow true means we will have enough
     *                          stock with backorder)
     *
     * @return true if there is quantity available to sell
     */
    boolean isAvailableToSell(boolean allowOrderMore);

    /**
     * Returns true if has quantity and quantity is larger than reserved.
     *
     * @param required          quantity
     * @param allowOrderMore    flag to signify that we can sell in principle (allow true means we will have enough
     *                          stock with backorder)
     *
     * @return true if there is quantity available to sell
     */
    boolean isAvailableToSell(BigDecimal required, boolean allowOrderMore);

    /**
     * Returns true if has enough quantity to allocate.
     *
     * @param required quantity
     *
     * @return true if there is quantity available to sell
     */
    boolean isAvailableToAllocate(BigDecimal required);

    /**
     * Returns quantity available for sale.
     *
     * @return quantity less reservations
     */
    BigDecimal getAvailableToSell();


    /**
     * Flag to denote if object is disabled on not.
     *
     * @return true if object is disabled
     */
    boolean isDisabled();

    /**
     * Flag to denote if object is disabled on not.
     *
     * @param disabled true if object is disabled
     */
    void setDisabled(boolean disabled);


    /**
     * Get start of product availability.
     * Null - product has not start date, means no limitation.
     *
     * @return start of product availability.
     */
    LocalDateTime getAvailablefrom();

    /**
     * Set start of product availability.
     *
     * @param availablefrom start of product availability.
     */
    void setAvailablefrom(LocalDateTime availablefrom);

    /**
     * Get end of product availability.
     * Null - product has not end date, means no limitation.
     *
     * @return end of product availability.
     */
    LocalDateTime getAvailableto();

    /**
     * Set end of product availability.
     *
     * @param availableto end of product availability.
     */
    void setAvailableto(LocalDateTime availableto);

    /**
     * Get product availability
     *
     * @return  Availability
     */
    int getAvailability();

    /**
     * Set product availability
     *
     * @param availability product
     */
    void setAvailability(int availability);

    /**
     * Get minimal quantity for order. E.g. 5.0 means customer can only buy 5 or more.
     *
     * @return minimal order quantity
     */
    BigDecimal getMinOrderQuantity();

    /**
     * @param minOrderQuantity minimal quantity for order.
     */
    void setMinOrderQuantity(BigDecimal minOrderQuantity);

    /**
     * Get maximum quantity for order. E.g. 5.0 means customer can only buy up to 5
     *
     * @return maximum order quantity
     */
    BigDecimal getMaxOrderQuantity();

    /**
     * @param maxOrderQuantity maximum quantity for order.
     */
    void setMaxOrderQuantity(BigDecimal maxOrderQuantity);

    /**
     * Get step quantity for order. E.g. 5.0 means customer can only buy in batches of 5 - 5, 10, 15
     * but not say 11.
     *
     * @return step order quantity
     */
    BigDecimal getStepOrderQuantity();

    /**
     * @param stepOrderQuantity step quantity for order.
     */
    void setStepOrderQuantity(BigDecimal stepOrderQuantity);

    /**
     * Get the featured flag for product.
     *
     * @return set featured flag.
     */
    Boolean getFeatured();

    /**
     * Set product featured flag.
     *
     * @param featured featured flag.
     */
    void setFeatured(Boolean featured);

    /**
     * Get product release date.
     * Null - product has no release date (alrady available).
     *
     * @return product release date.
     */
    LocalDateTime getReleaseDate();

    /**
     * Set product release date.
     *
     * @param releaseDate when product is to be released.
     */
    void setReleaseDate(LocalDateTime releaseDate);

    /**
     * Get restock date.
     *
     * @return product restock date.
     */
    LocalDateTime getRestockDate();

    /**
     * Set product restock date.
     *
     * @param restockDate when product is to be restocked.
     */
    void setRestockDate(LocalDateTime restockDate);

    /**
     * Get restock note.
     *
     * @return product restock note.
     */
    I18NModel getRestockNote();

    /**
     * Set product restock note (customer facing).
     *
     * @param restockNote when product is to be restocked.
     */
    void setRestockNote(I18NModel restockNote);

}
