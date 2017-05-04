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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.WarehouseDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class WarehouseDTOImpl implements WarehouseDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "warehouseId", readOnly = true)
    private long warehouseId;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "stateCode")
    private String stateCode;

    @DtoField(value = "city")
    private String city;

    @DtoField(value = "postcode")
    private String postcode;

    @DtoField(value = "defaultStandardStockLeadTime")
    private int defaultStandardStockLeadTime;

    @DtoField(value = "defaultBackorderStockLeadTime")
    private int defaultBackorderStockLeadTime;

    @DtoField(value = "multipleShippingSupported")
    private boolean multipleShippingSupported;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;


    /** {@inheritDoc}*/
    public long getWarehouseId() {
        return warehouseId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return warehouseId;
    }

    /** {@inheritDoc}*/
    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }

    /** {@inheritDoc}*/
    public String getCode() {
        return code;
    }

    /** {@inheritDoc}*/
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc}*/
    public String getName() {
        return name;
    }

    /** {@inheritDoc}*/
    public void setName(final String name) {
        this.name = name;
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /** {@inheritDoc}*/
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}*/
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc}*/
    public String getCountryCode() {
        return countryCode;
    }

    /** {@inheritDoc}*/
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /** {@inheritDoc}*/
    public String getStateCode() {
        return stateCode;
    }

    /** {@inheritDoc}*/
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc}*/
    public String getCity() {
        return city;
    }

    /** {@inheritDoc}*/
    public void setCity(final String city) {
        this.city = city;
    }

    /** {@inheritDoc}*/
    public String getPostcode() {
        return postcode;
    }

    /** {@inheritDoc}*/
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    /** {@inheritDoc}*/
    public int getDefaultStandardStockLeadTime() {
        return defaultStandardStockLeadTime;
    }

    /** {@inheritDoc}*/
    public void setDefaultStandardStockLeadTime(final int defaultStandardStockLeadTime) {
        this.defaultStandardStockLeadTime = defaultStandardStockLeadTime;
    }

    /** {@inheritDoc}*/
    public int getDefaultBackorderStockLeadTime() {
        return defaultBackorderStockLeadTime;
    }

    /** {@inheritDoc}*/
    public void setDefaultBackorderStockLeadTime(final int defaultBackorderStockLeadTime) {
        this.defaultBackorderStockLeadTime = defaultBackorderStockLeadTime;
    }

    /** {@inheritDoc}*/
    public boolean isMultipleShippingSupported() {
        return multipleShippingSupported;
    }

    /** {@inheritDoc}*/
    public void setMultipleShippingSupported(final boolean multipleShippingSupported) {
        this.multipleShippingSupported = multipleShippingSupported;
    }

    @Override
    public String toString() {
        return "WarehouseDTOImpl{" +
                "warehouseId=" + warehouseId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
