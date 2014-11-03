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

package org.yes.cart.domain.entity;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface State extends Auditable {

    /**
     * Get country code.
     *
     * @return country code.
     */
    String getCountryCode();

    /**
     * Set country code.
     *
     * @param countryCode code to set.
     */
    void setCountryCode(String countryCode);

    /**
     * Get state/area code.
     *
     * @return state/area code.
     */
    String getStateCode();

    /**
     * Set state/area code.
     *
     * @param stateCode code to set.
     */
    void setStateCode(String stateCode);

    /**
     * Get name.
     *
     * @return name.
     */
    String getName();

    /**
     * Set name.
     *
     * @param name name to set.
     */
    void setName(String name);

    /**
     * Get name.
     *
     * @return name.
     */
    String getDisplayName();

    /**
     * Set name.
     *
     * @param name name to set.
     */
    void setDisplayName(String name);

    /**
     * Get state pk value.
     *
     * @return state pk value.
     */
    long getStateId();

    /**
     * Set pk value.
     *
     * @param stateId pl value.
     */
    void setStateId(long stateId);

}
