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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.StateDTO;

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

    @DtoField(value = "displayName")
    private String displayName;

    /**
     * {@inheritDoc}
     */
    public long getStateId() {
        return stateId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return stateId;
    }

    /**
     * {@inheritDoc}
     */
    public void setStateId(final long stateId) {
        this.stateId = stateId;
    }

    /**
     * {@inheritDoc}
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String name) {
        this.name = name;
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
