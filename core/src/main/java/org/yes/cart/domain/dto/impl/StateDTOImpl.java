package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
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

    /**
     * {@inheritDoc}
     */
    public long getStateId() {
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
    public void setName(final String name) {
        this.name = name;
    }
}
