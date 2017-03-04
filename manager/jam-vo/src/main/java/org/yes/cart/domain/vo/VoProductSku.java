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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * User: denispavlov
 */
@Dto
public class VoProductSku {


    @DtoField(value = "skuId", readOnly = true)
    private long skuId;

    @DtoField(value = "guid")
    private String guid;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "manufacturerCode")
    private String manufacturerCode;

    @DtoField(value = "manufacturerPartCode")
    private String manufacturerPartCode;

    @DtoField(value = "supplierCode")
    private String supplierCode;

    @DtoField(value = "supplierCatalogCode")
    private String supplierCatalogCode;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "productId")
    private long productId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "barCode")
    private String barCode;

    @DtoField(value = "uri")
    private String uri;

    @DtoField(value = "title")
    private String title;

    @DtoField(value = "metakeywords")
    private String metakeywords;

    @DtoField(value = "metadescription")
    private String metadescription;

    @DtoField(value = "displayTitles", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayTitles;

    @DtoField(value = "displayMetakeywords", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayMetakeywords;

    @DtoField(value = "displayMetadescriptions", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayMetadescriptions;


    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(final long skuId) {
        this.skuId = skuId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getManufacturerPartCode() {
        return manufacturerPartCode;
    }

    public void setManufacturerPartCode(final String manufacturerPartCode) {
        this.manufacturerPartCode = manufacturerPartCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierCatalogCode() {
        return supplierCatalogCode;
    }

    public void setSupplierCatalogCode(final String supplierCatalogCode) {
        this.supplierCatalogCode = supplierCatalogCode;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(final String barCode) {
        this.barCode = barCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getMetakeywords() {
        return metakeywords;
    }

    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    public String getMetadescription() {
        return metadescription;
    }

    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    public List<MutablePair<String, String>> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(final List<MutablePair<String, String>> displayTitles) {
        this.displayTitles = displayTitles;
    }

    public List<MutablePair<String, String>> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(final List<MutablePair<String, String>> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    public List<MutablePair<String, String>> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(final List<MutablePair<String, String>> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }
}
