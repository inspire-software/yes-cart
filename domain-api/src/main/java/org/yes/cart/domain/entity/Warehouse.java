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
 * Warehouse entity. Each shop can have several warehouses.
 * Address part of warehouse used to:
 * <p/>
 * 1. Show only suitable delivery options to customer.
 * 2. Devivery planing in case of splited delivery or splited orders.
 */
public interface Warehouse extends Auditable {


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
    String getCode();

    /**
     * Set warehouse code.
     *
     * @param code warehouse code.
     */
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
     * @return coubtry.
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


}


