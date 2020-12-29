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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represent sku quantity on particular warehouse.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface SkuWarehouseDTO extends Identifiable, AuditInfoDTO {

    /**
     * Primary key.
     *
     * @return ph value.
     */
    long getSkuWarehouseId();

    /**
     * Set pk value.
     *
     * @param skuWarehouseId value to set
     */
    void setSkuWarehouseId(long skuWarehouseId);

    /**
     * Get sku code.
     *
     * @return sku code.
     */
    String getSkuCode();

    /**
     * Set sku code.
     *
     * @param skuCode sku code.
     */
    void setSkuCode(String skuCode);

    /**
     * Get sku name.
     *
     * @return sku name.
     */
    String getSkuName();

    /**
     * Set sku name
     *
     * @param skuName sku name.
     */
    void setSkuName(String skuName);

    /**
     * Get the warehouse id.
     *
     * @return warehouse id
     */
    long getWarehouseId();

    /**
     * Set warehouse id.
     *
     * @param warehouseId warehouse id
     */
    void setWarehouseId(long warehouseId);

    /**
     * Get warehouse code.
     *
     * @return warehouse code.
     */
    String getWarehouseCode();

    /**
     * Set warehouse code.
     *
     * @param warehouseCode warehouse code.
     */
    void setWarehouseCode(String warehouseCode);

    /**
     * Get warehouse name.
     *
     * @return warehouse name.
     */
    String getWarehouseName();

    /**
     * Set warehouse name.
     *
     * @param warehouseName warehouse name.
     */
    void setWarehouseName(String warehouseName);

    /**
     * Sku quantity.
     *
     * @return sku quantity.
     */
    BigDecimal getQuantity();

    /**
     * Set sku quantity
     *
     * @param quantity ku quantity.
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
     * @return availability
     */
    int getAvailability();

    /**
     * Set productavailability
     *
     * @param availability product
     */
    void setAvailability(int availability);


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
     * Get product release date.
     * Null - product has no release date (already available).
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
     * Get product tags.
     *
     * @return tag line.
     */
    String getTag();

    /**
     * Set product tag.
     *
     * @param tag product tag line
     */
    void setTag(String tag);

    /**
     * Get product release date.
     * Null - product has no re-stock date.
     *
     * @return product re-stock date.
     */
    LocalDateTime getRestockDate();

    /**
     * Set product re-stock date.
     *
     * @param releaseDate when product is to be re-stocked.
     */
    void setRestockDate(LocalDateTime releaseDate);

    /**
     * Re-stock notes.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getRestockNotes();

    /**
     * Re-stock notes
     *
     * @param notes localised locale => name pairs
     */
    void setRestockNotes(Map<String, String> notes);

}
