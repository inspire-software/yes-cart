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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.ShopWarehouse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;

    @DtoCollection(
            value = "warehouseShop",
            dtoBeanKey = "org.yes.cart.domain.dto.ShopWarehouseDTO",
            entityGenericType = ShopWarehouse.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private List<ShopWarehouseDTO> warehouseShop;


    /** {@inheritDoc}*/
    @Override
    public long getWarehouseId() {
        return warehouseId;
    }

    /** {@inheritDoc}*/
    @Override
    public long getId() {
        return warehouseId;
    }

    /** {@inheritDoc}*/
    @Override
    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }

    /** {@inheritDoc}*/
    @Override
    public String getCode() {
        return code;
    }

    /** {@inheritDoc}*/
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc}*/
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc}*/
    @Override
    public void setName(final String name) {
        this.name = name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /** {@inheritDoc}*/
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}*/
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc}*/
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    /** {@inheritDoc}*/
    @Override
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /** {@inheritDoc}*/
    @Override
    public String getStateCode() {
        return stateCode;
    }

    /** {@inheritDoc}*/
    @Override
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc}*/
    @Override
    public String getCity() {
        return city;
    }

    /** {@inheritDoc}*/
    @Override
    public void setCity(final String city) {
        this.city = city;
    }

    /** {@inheritDoc}*/
    @Override
    public String getPostcode() {
        return postcode;
    }

    /** {@inheritDoc}*/
    @Override
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    /** {@inheritDoc}*/
    @Override
    public int getDefaultStandardStockLeadTime() {
        return defaultStandardStockLeadTime;
    }

    /** {@inheritDoc}*/
    @Override
    public void setDefaultStandardStockLeadTime(final int defaultStandardStockLeadTime) {
        this.defaultStandardStockLeadTime = defaultStandardStockLeadTime;
    }

    /** {@inheritDoc}*/
    @Override
    public int getDefaultBackorderStockLeadTime() {
        return defaultBackorderStockLeadTime;
    }

    /** {@inheritDoc}*/
    @Override
    public void setDefaultBackorderStockLeadTime(final int defaultBackorderStockLeadTime) {
        this.defaultBackorderStockLeadTime = defaultBackorderStockLeadTime;
    }

    /** {@inheritDoc}*/
    @Override
    public boolean isMultipleShippingSupported() {
        return multipleShippingSupported;
    }

    /** {@inheritDoc}*/
    @Override
    public void setMultipleShippingSupported(final boolean multipleShippingSupported) {
        this.multipleShippingSupported = multipleShippingSupported;
    }

    /** {@inheritDoc}*/
    @Override
    public List<ShopWarehouseDTO> getWarehouseShop() {
        return warehouseShop;
    }

    /** {@inheritDoc}*/
    @Override
    public void setWarehouseShop(final List<ShopWarehouseDTO> warehouseShop) {
        this.warehouseShop = warehouseShop;
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
