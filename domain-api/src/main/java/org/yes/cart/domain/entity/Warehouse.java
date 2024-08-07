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

import java.util.Collection;

/**
 * Warehouse entity. Each shop can have several warehouses.
 * Address part of warehouse used to:
 * <p/>
 * 1. Show only suitable delivery options to customer.
 * 2. Delivery planing in case of split delivery or split orders.
 */
public interface Warehouse extends Auditable, Codable {


    /**
     * Get primary key.
     *
     * @return primary key
     */
    long getWarehouseId();

    /**
     * Set primary key
     *
     * @param warehouseId primary key.
     */
    void setWarehouseId(long warehouseId);

    /**
     * Get warehouse code.
     *
     * @return warehouse code.
     */
    @Override
    String getCode();

    /**
     * Set warehouse code.
     *
     * @param code warehouse code.
     */
    @Override
    void setCode(String code);


    /**
     * Warehouse name.
     *
     * @return warehouse name.
     */
    String getName();

    /**
     * Set Warehouse name.
     *
     * @param name name of warehouse
     */
    void setName(String name);

    /**
     * Get name.
     *
     * @return localisable name of warehouse.
     */
    I18NModel getDisplayName();

    /**
     * Set name of warehouse.
     *
     * @param name localisable name.
     */
    void setDisplayName(I18NModel name);

    /**
     * @return description.
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description warehouse description.
     */
    void setDescription(String description);


    // address part of warehouse begin

    /**
     * Get country.
     *
     * @return country.
     */
    String getCountryCode();

    /**
     * Set country.
     *
     * @param countryCode country to set
     */
    void setCountryCode(String countryCode);

    /**
     * State or province code.
     *
     * @return state or province code
     */
    String getStateCode();

    /**
     * Set state or province.
     *
     * @param stateCode state.
     */
    void setStateCode(final String stateCode);

    /**
     * Get city.
     *
     * @return city
     */
    String getCity();

    /**
     * Set city
     *
     * @param city value to set
     */
    void setCity(String city);

    /**
     * Get postcode.
     *
     * @return post code
     */
    String getPostcode();

    /**
     * Set post code
     *
     * @param postcode value to set
     */
    void setPostcode(String postcode);

    // address part of warehouse end


    /**
     * Get shop warehouse relation.
     * @return  shop warehouse relation.
     */
    Collection<ShopWarehouse> getWarehouseShop();

    /**
     * Set shop warehouse relation.
     * @param warehouseShop  shop warehouse relation.
     */
    void setWarehouseShop(Collection<ShopWarehouse> warehouseShop);



    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     *
     * @return lead time
     */
    int getDefaultStandardStockLeadTime();

    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     * This is default (average time). Product specific lead times will override this.
     *
     * @param defaultStandardStockLeadTime lead time
     */
    void setDefaultStandardStockLeadTime(int defaultStandardStockLeadTime);



    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     * This is default (average time). Product specific lead times will override this.
     *
     * @return lead time
     */
    int getDefaultBackorderStockLeadTime();

    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     * This is default (average time). Product specific lead times will override this.
     *
     * @param defaultBackorderStockLeadTime lead time
     */
    void setDefaultBackorderStockLeadTime(int defaultBackorderStockLeadTime);

    /**
     * Determine if this warehouse supports multiple shipment or not.
     *
     * @return true if multiple shipment is supported
     */
    boolean isMultipleShippingSupported();

    /**
     * Determine if this warehouse supports multiple shipment or not.
     *
     * @param multipleShippingSupported multiple shipment
     */
    void setMultipleShippingSupported(boolean multipleShippingSupported);

    /**
     * Determine if this warehouse enforces backorder items to be tracked
     * by a separate delivery.
     *
     * @return true if each backorder item must be separate
     */
    boolean isForceBackorderDeliverySplit();

    /**
     * Determine if this warehouse enforces backorder items to be tracked
     * by a separate delivery.
     *
     * @param forceBackorderDeliverySplit true if each backorder item must be separate
     */
    void setForceBackorderDeliverySplit(boolean forceBackorderDeliverySplit);

    /**
     * Determine if this warehouse enforces all items to be tracked
     * by a separate delivery.
     *
     * @return true if each backorder item must be separate
     */
    boolean isForceAllDeliverySplit();

    /**
     * Determine if this warehouse enforces all items to be tracked
     * by a separate delivery.
     *
     * @param forceAllDeliverySplit true if each item must be separate
     */
    void setForceAllDeliverySplit(boolean forceAllDeliverySplit);


}


