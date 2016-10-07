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
public class VoAttribute {

    @DtoField(value = "attributeId", readOnly = true)
    private long attributeId;
    

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "mandatory")
    private boolean mandatory;

    @DtoField(value = "val")
    private String val;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "etypeId", readOnly=true)
    private long etypeId;

    @DtoField(value = "etypeName", readOnly=true)
    private String etypeName;


    @DtoField(value = "attributegroupId", readOnly=true)
    private long attributegroupId;

    @DtoField(value = "allowduplicate")
    private boolean allowduplicate;

    @DtoField(value = "allowfailover")
    private boolean allowfailover;


    @DtoField(value = "regexp")
    private String regexp;

    @DtoField(value = "validationFailedMessage", converter = "DisplayValues")
    private List<MutablePair<String, String>> validationFailedMessage;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "choiceData", converter = "DisplayValues")
    private List<MutablePair<String, String>> choiceData;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

    @DtoField(value = "store")
    private boolean store;

    @DtoField(value = "search")
    private boolean search;

    @DtoField(value = "primary")
    private boolean primary;

    @DtoField(value = "navigation")
    private boolean navigation;


    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public List<MutablePair<String, String>> getChoiceData() {
        return choiceData;
    }

    public void setChoiceData(final List<MutablePair<String, String>> choiceData) {
        this.choiceData = choiceData;
    }

    public List<MutablePair<String, String>> getValidationFailedMessage() {
        return validationFailedMessage;
    }

    public void setValidationFailedMessage(final List<MutablePair<String, String>> validationFailedMessage) {
        this.validationFailedMessage = validationFailedMessage;
    }

    public long getAttributegroupId() {
        return attributegroupId;
    }

    public void setAttributegroupId(final long attributegroupId) {
        this.attributegroupId = attributegroupId;
    }

    public long getAttributeId() {
        return this.attributeId;
    }

    public long getId() {
        return attributeId;
    }

    public void setAttributeId(final long attributeId) {
        this.attributeId = attributeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getVal() {
        return val;
    }

    public void setVal(final String val) {
        this.val = val;
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

    public long getEtypeId() {
        return etypeId;
    }

    public void setEtypeId(final long etypeId) {
        this.etypeId = etypeId;
    }

    public String getEtypeName() {
        return etypeName;
    }

    public void setEtypeName(final String etypeName) {
        this.etypeName = etypeName;
    }

    public boolean isAllowduplicate() {
        return allowduplicate;
    }

    public void setAllowduplicate(final boolean allowduplicate) {
        this.allowduplicate = allowduplicate;
    }

    public boolean isAllowfailover() {
        return allowfailover;
    }

    public void setAllowfailover(final boolean allowfailover) {
        this.allowfailover = allowfailover;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(final boolean store) {
        this.store = store;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(final boolean search) {
        this.search = search;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    public boolean isNavigation() {
        return navigation;
    }

    public void setNavigation(final boolean navigation) {
        this.navigation = navigation;
    }

}
