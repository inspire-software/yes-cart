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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.matcher.NoopMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:54
 */
@Dto
public class VoFulfilmentCentreInfo {

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

    @DtoField(value = "forceBackorderDeliverySplit")
    private boolean forceBackorderDeliverySplit;

    @DtoField(value = "forceAllDeliverySplit")
    private boolean forceAllDeliverySplit;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

    @DtoCollection(
            value = "warehouseShop",
            dtoBeanKey = "VoFulfilmentCentreShopLink",
            entityGenericType = ShopWarehouseDTO.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<VoFulfilmentCentreShopLink> fulfilmentShops = new ArrayList<>();


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    public long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    public int getDefaultStandardStockLeadTime() {
        return defaultStandardStockLeadTime;
    }

    public void setDefaultStandardStockLeadTime(final int defaultStandardStockLeadTime) {
        this.defaultStandardStockLeadTime = defaultStandardStockLeadTime;
    }

    public int getDefaultBackorderStockLeadTime() {
        return defaultBackorderStockLeadTime;
    }

    public void setDefaultBackorderStockLeadTime(final int defaultBackorderStockLeadTime) {
        this.defaultBackorderStockLeadTime = defaultBackorderStockLeadTime;
    }

    public boolean isMultipleShippingSupported() {
        return multipleShippingSupported;
    }

    public void setMultipleShippingSupported(final boolean multipleShippingSupported) {
        this.multipleShippingSupported = multipleShippingSupported;
    }

    public boolean isForceBackorderDeliverySplit() {
        return forceBackorderDeliverySplit;
    }

    public void setForceBackorderDeliverySplit(final boolean forceBackorderDeliverySplit) {
        this.forceBackorderDeliverySplit = forceBackorderDeliverySplit;
    }

    public boolean isForceAllDeliverySplit() {
        return forceAllDeliverySplit;
    }

    public void setForceAllDeliverySplit(final boolean forceAllDeliverySplit) {
        this.forceAllDeliverySplit = forceAllDeliverySplit;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }

    public List<VoFulfilmentCentreShopLink> getFulfilmentShops() {
        return fulfilmentShops;
    }

    public void setFulfilmentShops(final List<VoFulfilmentCentreShopLink> fulfilmentShops) {
        this.fulfilmentShops = fulfilmentShops;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
