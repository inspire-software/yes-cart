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
package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class WarehouseEntity implements org.yes.cart.domain.entity.Warehouse, java.io.Serializable {

    private long warehouseId;
    private long version;

    private String code;
    private String name;
    private String displayNameInternal;
    private I18NModel displayName;
    private String description;
    private String countryCode;
    private String stateCode;
    private String city;
    private String postcode;

    private int defaultStandardStockLeadTime;
    private int defaultBackorderStockLeadTime;
    private boolean multipleShippingSupported;

    private Collection<ShopWarehouse> warehouseShop = new ArrayList<>(5);

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public WarehouseEntity() {
    }

    @Override
    public Collection<ShopWarehouse> getWarehouseShop() {
        return warehouseShop;
    }

    @Override
    public void setWarehouseShop(final Collection<ShopWarehouse> warehouseShop) {
        this.warehouseShop = warehouseShop;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplayNameInternal() {
        return displayNameInternal;
    }

    public void setDisplayNameInternal(final String displayNameInternal) {
        this.displayNameInternal = displayNameInternal;
        this.displayName = new StringI18NModel(displayNameInternal);
    }

    @Override
    public I18NModel getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(final I18NModel displayName) {
        this.displayName = displayName;
        this.displayNameInternal = displayName != null ? displayName.toString() : null;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getCountryCode() {
        return this.countryCode;
    }

    @Override
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String getStateCode() {
        return this.stateCode;
    }

    @Override
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public void setCity(final String city) {
        this.city = city;
    }

    @Override
    public String getPostcode() {
        return this.postcode;
    }

    @Override
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    @Override
    public int getDefaultStandardStockLeadTime() {
        return defaultStandardStockLeadTime;
    }

    @Override
    public void setDefaultStandardStockLeadTime(final int defaultStandardStockLeadTime) {
        this.defaultStandardStockLeadTime = defaultStandardStockLeadTime;
    }

    @Override
    public int getDefaultBackorderStockLeadTime() {
        return defaultBackorderStockLeadTime;
    }

    @Override
    public void setDefaultBackorderStockLeadTime(final int defaultBackorderStockLeadTime) {
        this.defaultBackorderStockLeadTime = defaultBackorderStockLeadTime;
    }

    @Override
    public boolean isMultipleShippingSupported() {
        return multipleShippingSupported;
    }

    @Override
    public void setMultipleShippingSupported(final boolean multipleShippingSupported) {
        this.multipleShippingSupported = multipleShippingSupported;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getWarehouseId() {
        return this.warehouseId;
    }

    @Override
    public long getId() {
        return this.warehouseId;
    }


    @Override
    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


