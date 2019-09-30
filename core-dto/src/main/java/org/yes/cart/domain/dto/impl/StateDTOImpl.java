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
import org.yes.cart.domain.dto.StateDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class StateDTOImpl implements StateDTO {

    private static final long serialVersionUID = 20101108L;

    @DtoField(value = "stateId", readOnly = true)
    private long stateId;

    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "stateCode")
    private String stateCode;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;


    /**
     * {@inheritDoc}
     */
    @Override
    public long getStateId() {
        return stateId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return stateId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStateId(final long stateId) {
        this.stateId = stateId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStateCode() {
        return stateCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
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

    @Override
    public String toString() {
        return "StateDTOImpl{" +
                "stateId=" + stateId +
                ", countryCode='" + countryCode + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
