package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CountryDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CountryDTOImpl implements CountryDTO {

    private static final long serialVersionUID = 20101108L;

    @DtoField(value = "countryId", readOnly = true)
    private long countryId;

    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "isoCode")
    private String isoCode;

    @DtoField(value = "name")
    private String name;


    /**
     * {@inheritDoc}
     */
    public long getCountryId() {
        return countryId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCountryId(final long countryId) {
        this.countryId = countryId;
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
    public String getIsoCode() {
        return isoCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setIsoCode(final String isoCode) {
        this.isoCode = isoCode;
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
