package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * Country DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CountryDTO extends Unique {

    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getCountryId();

    /**
     * Set pk value.
     *
     * @param countryId pk value.
     */
    void setCountryId(long countryId);

    /**
     * Country code. ISO
     *
     * @return country code.
     */
    String getCountryCode();

    /**
     * Set country code.
     *
     * @param countryCode country code.
     */
    void setCountryCode(String countryCode);

    /**
     * Get country name.
     *
     * @return country name.
     */
    String getName();

    /**
     * Set country name.
     *
     * @param name country name.
     */
    void setName(String name);

    /**
     * Get iso constry code.
     *
     * @return iso constry code.
     */
    String getIsoCode();

    /**
     * Set iso constry code.
     *
     * @param isoCode iso constry code.
     */
    void setIsoCode(final String isoCode);


}
