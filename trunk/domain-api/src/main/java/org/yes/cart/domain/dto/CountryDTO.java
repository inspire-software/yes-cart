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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Country DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CountryDTO extends Identifiable {

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
     * Get country name.
     *
     * @return country name.
     */
    String getDisplayName();

    /**
     * Set country name.
     *
     * @param name country name.
     */
    void setDisplayName(String name);

    /**
     * Get iso country code.
     *
     * @return iso country code.
     */
    String getIsoCode();

    /**
     * Set iso country code.
     *
     * @param isoCode iso country code.
     */
    void setIsoCode(final String isoCode);


}
